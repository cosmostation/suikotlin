package io.cosmostation.suikotlin

import android.app.Application
import com.develop.mnemonic.MnemonicUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.cosmostation.suikotlin.api.ApiService
import io.cosmostation.suikotlin.api.FaucetService
import io.cosmostation.suikotlin.key.SuiKey
import io.cosmostation.suikotlin.model.*
import io.cosmostation.suikotlin.secure.CipherHelper
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Security
import java.util.*

class SuiClient {
    companion object {
        val instance = SuiClient()

        fun initialize(applicationContext: Application) {
            CipherHelper.init(applicationContext)
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

    fun sign(keyPair: EdDSAKeyPair, data: ByteArray) = SuiKey.sign(keyPair, data)

    suspend fun getObjectsByOwner(address: String): List<SuiObjectInfo>? {
        val request = JsonRpcRequest("sui_getObjectsOwnedByAddress", listOf(address))
        val result = ApiService.create().postJsonRpcRequest(request).body()?.result
        return Gson().fromJson<List<SuiObjectInfo>>(
            Gson().toJson(result), object : TypeToken<ArrayList<SuiObjectInfo>>() {}.type
        )
    }

    suspend fun getObjectDetails(objectInfos: List<SuiObjectInfo>): List<String>? {
        val request = objectInfos.map { JsonRpcRequest("sui_getObject", listOf(it.objectId)) }
        return ApiService.create().postJsonRpcRequests(request).body()
            ?.map { Gson().toJson(it.result) }?.toList()
    }

    suspend fun getTransactions(
        transactionQuery: TransactionQuery,
        nextOffset: String? = null,
        limit: Int? = null,
        descending: Boolean = false
    ): SuiTransactionDigest? {
        val request = JsonRpcRequest(
            "sui_getTransactions", listOf(transactionQuery, nextOffset, limit, descending)
        )
        val result = ApiService.create().postJsonRpcRequest(request).body()?.result
        return Gson().fromJson(Gson().toJson(result), SuiTransactionDigest::class.java)
    }

    suspend fun getTransactionDetails(digests: List<String>): List<String>? {
        val request = digests.map { JsonRpcRequest("sui_getTransaction", listOf(it)) }
        return ApiService.create().postJsonRpcRequests(request).body()
            ?.map { Gson().toJson(it.result) }?.toList()
    }

    suspend fun fetchCustomRequest(requests: JsonRpcRequest) =
        ApiService.create().postJsonRpcRequest(requests).body()

    suspend fun fetchCustomRequests(requests: List<JsonRpcRequest>) =
        ApiService.create().postJsonRpcRequests(requests).body()

    suspend fun faucet(address: String) =
        FaucetService.create().faucet(FixedAmountRequest(Recipient(address))).body()

    suspend fun transferSui(
        objectId: String, receiver: String, sender: String, gasBudget: Int, amount: Int? = null
    ): SuiWrappedTxBytes? {
        val params = mutableListOf(sender, objectId, gasBudget, receiver)
        amount?.let { params.add(it) }
        val result = ApiService.create().postJsonRpcRequest(
            JsonRpcRequest("sui_transferSui", params)
        ).body()?.result
        return Gson().fromJson(Gson().toJson(result), SuiWrappedTxBytes::class.java)
    }

    suspend fun transferObject(
        objectId: String,
        receiver: String,
        sender: String,
        gasBudget: Int,
        gasObjectId: String? = null
    ): SuiWrappedTxBytes? {
        val params = mutableListOf(sender, objectId, gasObjectId, gasBudget, receiver)
        val result = ApiService.create().postJsonRpcRequest(
            JsonRpcRequest("sui_transferObject", params)
        ).body()?.result
        return Gson().fromJson(Gson().toJson(result), SuiWrappedTxBytes::class.java)
    }

    suspend fun moveCall(
        sender: String,
        packageObjectId: String,
        module: String,
        function: String,
        typeArguments: List<String> = listOf(),
        arguments: List<String> = listOf(),
        gasPayment: String? = null,
        gasBudget: Int
    ): SuiWrappedTxBytes? {
        val params = mutableListOf(
            sender,
            packageObjectId,
            module,
            function,
            typeArguments,
            arguments,
            gasPayment,
            gasBudget
        )
        val result = ApiService.create().postJsonRpcRequest(
            JsonRpcRequest("sui_moveCall", params)
        ).body()?.result
        return Gson().fromJson(Gson().toJson(result), SuiWrappedTxBytes::class.java)
    }

    suspend fun executeTransaction(
        txBytes: ByteArray, signedBytes: ByteArray, keyPair: EdDSAKeyPair
    ): Any? {
        val params = mutableListOf(
            Base64.getEncoder().encodeToString(txBytes),
            "ED25519",
            Base64.getEncoder().encodeToString(signedBytes),
            Base64.getEncoder().encodeToString(keyPair.publicKey.abyte),
            "WaitForLocalExecution"
        )
        return ApiService.create()
            .postJsonRpcRequest(JsonRpcRequest("sui_executeTransaction", params)).body()?.result
    }
}