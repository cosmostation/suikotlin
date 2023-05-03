package io.cosmostation.sui.sample

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import io.cosmostation.suikotlin.SuiClient
import io.cosmostation.suikotlin.model.EdDSAKeyPair
import io.cosmostation.suikotlin.model.SuiObjectInfo
import io.cosmostation.suikotlin.model.SuiTransaction
import io.cosmostation.suikotlin.model.TransactionQuery
import kotlinx.coroutines.launch
import java.math.BigInteger
import java.util.*

class SampleViewModel : ViewModel() {
    private val _mnemonic = MutableLiveData<String>()
    val mnemonic: LiveData<String> get() = _mnemonic

    private val _keyPair = MutableLiveData<EdDSAKeyPair>()
    val keyPair: LiveData<EdDSAKeyPair> get() = _keyPair

    private val _address = MutableLiveData<String>()
    val address: LiveData<String> get() = _address

    private val _objectInfos = MutableLiveData<List<SuiObjectInfo>>()
    val objectInfos: LiveData<List<SuiObjectInfo>> get() = _objectInfos

    private val _objectDetails = MutableLiveData<List<String>>()
    val objectDetails: LiveData<List<String>> get() = _objectDetails

    private val _transactions = MutableLiveData<List<SuiTransaction>>()
    val transactions: LiveData<List<SuiTransaction>> get() = _transactions

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> get() = _toastMessage

    fun loadMnemonic(value: String) = viewModelScope.launch {
        try {
            _keyPair.postValue(SuiClient.instance.getKeyPair(value))
            _address.postValue(SuiClient.instance.getAddress(value))
            _mnemonic.postValue(value)
        } catch (e: Exception) {
            _toastMessage.postValue("Mnemonic load error.")
        }
    }

    fun faucet(address: String) = viewModelScope.launch {
        SuiClient.instance.faucet(address)
    }

    fun getObjectsByOwner(address: String) = viewModelScope.launch {
        _objectInfos.postValue(SuiClient.instance.getObjectsByOwner(address))
    }

    fun getObjectsDetails() = viewModelScope.launch {
        _objectInfos.value?.let { objects ->
            val multiObjects = SuiClient.instance.getMultiObjectDetail(objects.map { it.objectId })
            _toastMessage.postValue(Gson().toJson(multiObjects))
        }
    }

    fun getTransactions(address: String) = viewModelScope.launch {
        val digests = mutableListOf<SuiTransaction>()
        _transactions.value?.forEach { digests.add(it) }

        val toDigest = SuiClient.instance.getTransactions(TransactionQuery.ToAddress(address))
        digests.addAll(toDigest)
        val fromDigest = SuiClient.instance.getTransactions(TransactionQuery.FromAddress(address))
        digests.addAll(fromDigest)

        _transactions.postValue(digests)
    }

    fun transferObject(objectInfo: SuiObjectInfo, receiver: String, sender: String) = viewModelScope.launch {
        val transfer = SuiClient.instance.transferSui(
            objectInfo.objectId, receiver, sender, BigInteger("10000"), BigInteger("10000000")
        )
//            val transfer = SuiClient.instance.moveCall(
//                _address.value!!, "0x2", "devnet_nft", "mint", listOf(), listOf(
//                    "Example NFT",
//                    "Example NFT",
//                    "ipfs://bafkreibngqhl3gaa7daob4i2vccziay2jjlp435cf66vhono7nrvww53ty"
//                ), null, 10000
//            )
//            val transfer =
//                SuiClient.instance.transferObject(objectInfo.objectId, receiver, sender, 100)
        transfer?.let { transferTxBytes ->
            _keyPair.value?.let { keyPair ->
                val txBytes = Base64.getDecoder().decode(transferTxBytes.txBytes)
                val intentMessage = byteArrayOf(0, 0, 0) + txBytes
                val signedTxBytes = SuiClient.instance.sign(keyPair, intentMessage)
                val executeResult = SuiClient.instance.executeTransaction(
                    txBytes, signedTxBytes, keyPair
                )
                _toastMessage.postValue(Gson().toJson(executeResult))
            }
        }

    }
}