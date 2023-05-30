package io.cosmostation.suikotlin

import com.develop.mnemonic.MnemonicUtils
import com.google.gson.Gson
import io.cosmostation.suikotlin.api.ApiService
import io.cosmostation.suikotlin.api.FaucetService
import io.cosmostation.suikotlin.key.SuiKey
import io.cosmostation.suikotlin.model.*
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.math.BigInteger
import java.security.Security
import java.util.*

class SuiClient {
    companion object {
        val instance = SuiClient()

        fun initialize() {
            setupBouncyCastle()
        }

        fun configure(network: Network) {
            instance.currentNetwork = network
        }

        private fun setupBouncyCastle() {
            val provider = Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) ?: return

            if (provider::class.java == BouncyCastleProvider::class.java) {
                return
            }

            Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME)
            Security.insertProviderAt(BouncyCastleProvider(), 1)
        }
    }

    var currentNetwork: Network = Network.Devnet()

    fun generateMnemonic(): String = MnemonicUtils.generateMnemonic()

    fun getAddress(mnemonic: String) = SuiKey.getSuiAddress(mnemonic)

    fun getAddress(keyPair: EdDSAKeyPair) = SuiKey.getSuiAddress(keyPair)

    fun getKeyPair(mnemonic: String) = SuiKey.getKeyPair(mnemonic)

    fun getKeyPairByPrivateKey(privateKeyHex: String) = SuiKey.getKeyPairByPrivateKey(privateKeyHex)

    fun sign(keyPair: EdDSAKeyPair, data: ByteArray) = SuiKey.sign(keyPair, data)

    suspend fun dryRunTransactionBlock(txBytes: String): Any? {
        val request = JsonRpcRequest("sui_dryRunTransactionBlock", listOf(txBytes))
        return ApiService.create().postJsonRpcRequest(request).body()?.result
    }

    suspend fun getObjectsByOwner(address: String, options: SuiObjectDataOptions? = SuiObjectDataOptions(showContent = true)): List<SuiObjectInfo> {
        val request = JsonRpcRequest("suix_getOwnedObjects", listOf(address, SuiObjectResponseQuery(null, options)))
        val result = ApiService.create().postJsonRpcRequest(request).body()?.result
        return Gson().fromJson(Gson().toJson(result), SuiObjectDataResult::class.java).data.map { it.data }
    }
    
    suspend fun getMultiObjectsById(objectIds: List<String?>, options: SuiObjectDataOptions? = SuiObjectDataOptions(showContent = true)): List<SuiObjectInfo> {
        val request = JsonRpcRequest("sui_multiGetObjects", listOf(objectIds, options))
        val result = ApiService.create().postJsonRpcRequest(request).body()
        return Gson().fromJson(Gson().toJson(result), SuiMultiObjectInfo::class.java).result.map { it.data }
    }

    suspend fun getTransactions(transactionQuery: TransactionQuery, nextOffset: String? = null, limit: Int? = null, descending: Boolean = false, options: SuiTransactionBlockResponseOptions? = SuiTransactionBlockResponseOptions(showInput = true, showEffects = true, showBalanceChanges = true)): List<SuiTransaction> {
        val request = JsonRpcRequest("suix_queryTransactionBlocks", listOf(SuiTransactionQueryFilter(transactionQuery, options), nextOffset, limit, descending))
        val result = ApiService.create().postJsonRpcRequest(request).body()?.result
        return Gson().fromJson(Gson().toJson(result), SuiTransactionDataResult::class.java).data
    }

    suspend fun getTransactionDetails(digests: List<String>): List<String>? {
        val request = digests.map { JsonRpcRequest("sui_multiGetTransactionBlocks", listOf(it)) }
        return ApiService.create().postJsonRpcRequests(request).body()?.map { Gson().toJson(it.result) }?.toList()
    }

    suspend fun fetchCustomRequest(requests: JsonRpcRequest) = ApiService.create().postJsonRpcRequest(requests).body()

    suspend fun fetchCustomRequests(requests: List<JsonRpcRequest>) = ApiService.create().postJsonRpcRequests(requests).body()

    suspend fun faucet(address: String) = FaucetService.create().faucet(FixedAmountRequest(Recipient(address))).body()

    suspend fun transferSui(objectId: String, receiver: String, sender: String, gasBudget: BigInteger, amount: BigInteger): SuiWrappedTxBytes? {
        val params = mutableListOf(sender, objectId, gasBudget.toString(), receiver, amount.toString())
        val result = ApiService.create().postJsonRpcRequest(JsonRpcRequest("unsafe_transferSui", params)).body()?.result
        return Gson().fromJson(Gson().toJson(result), SuiWrappedTxBytes::class.java)
    }

    suspend fun pay(objectIds: List<String>, receivers: List<String>, sender: String, gasBudget: BigInteger, gasObjectId: String? = null, amounts: List<BigInteger>): SuiWrappedTxBytes? {
        val params: MutableList<Any?> = mutableListOf(sender, objectIds, receivers, amounts.map { it.toString() }, gasObjectId, gasBudget.toString())
        val result = ApiService.create().postJsonRpcRequest(JsonRpcRequest("unsafe_pay", params)).body()?.result
        return Gson().fromJson(Gson().toJson(result), SuiWrappedTxBytes::class.java)
    }

    suspend fun paySui(objectIds: List<String>, receivers: List<String>, sender: String, gasBudget: BigInteger, amounts: List<BigInteger>): SuiWrappedTxBytes? {
        val params: MutableList<Any?> = mutableListOf(sender, objectIds, receivers, amounts.map { it.toString() }, gasBudget.toString())
        val result = ApiService.create().postJsonRpcRequest(JsonRpcRequest("unsafe_paySui", params)).body()?.result
        return Gson().fromJson(Gson().toJson(result), SuiWrappedTxBytes::class.java)
    }

    suspend fun transferObject(objectId: String, receiver: String, sender: String, gasBudget: BigInteger, gasObjectId: String? = null): SuiWrappedTxBytes? {
        val params = mutableListOf(sender, objectId, gasObjectId, gasBudget.toString(), receiver)
        val result = ApiService.create().postJsonRpcRequest(JsonRpcRequest("unsafe_transferObject", params)).body()?.result
        return Gson().fromJson(Gson().toJson(result), SuiWrappedTxBytes::class.java)
    }

    suspend fun moveCall(sender: String, packageObjectId: String, module: String, function: String, typeArguments: List<String> = listOf(), arguments: List<String> = listOf(), gasPayment: String? = null, gasBudget: BigInteger): SuiWrappedTxBytes? {
        val params = mutableListOf(sender, packageObjectId, module, function, typeArguments, arguments, gasPayment, gasBudget.toString())
        val result = ApiService.create().postJsonRpcRequest(JsonRpcRequest("unsafe_moveCall", params)).body()?.result
        return Gson().fromJson(Gson().toJson(result), SuiWrappedTxBytes::class.java)
    }

    suspend fun executeTransaction(txBytes: ByteArray, signedBytes: ByteArray, keyPair: EdDSAKeyPair, responseOptions: SuiTransactionBlockResponseOptions? = SuiTransactionBlockResponseOptions(showInput = true, showEffects = true, showBalanceChanges = true)): Any? {
        val params = mutableListOf(Base64.getEncoder().encodeToString(txBytes), listOf(Base64.getEncoder().encodeToString(byteArrayOf(0x00) + signedBytes + keyPair.publicKey.abyte)), responseOptions, "WaitForLocalExecution")
        return ApiService.create().postJsonRpcRequest(JsonRpcRequest("sui_executeTransactionBlock", params)).body()?.result
    }
}
