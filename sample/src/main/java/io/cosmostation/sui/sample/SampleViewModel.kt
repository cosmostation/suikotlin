package io.cosmostation.sui.sample

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.cosmostation.suikotlin.SuiClient
import io.cosmostation.suikotlin.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

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

    private val _transactions = MutableLiveData<List<String>>()
    val transactions: LiveData<List<String>> get() = _transactions

    private val _transactionDetails = MutableLiveData<List<String>>()
    val transactionDetails: LiveData<List<String>> get() = _transactionDetails

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
            _objectDetails.postValue(SuiClient.instance.getObjectDetails(objects))
        }
    }

    fun getTransactionDetails() = viewModelScope.launch {
        _transactions.value?.let { digests ->
            _transactionDetails.postValue(SuiClient.instance.getTransactionDetails(digests))
        }
    }

    fun getTransactions(address: String) = viewModelScope.launch {
        val digests = mutableListOf<String>()
        _transactions.value?.forEach { digests.add(it) }

        val toDigest = SuiClient.instance.getTransactions(TransactionQuery.ToAddress(address))
        toDigest?.data?.forEach { digests.add(it) }

        val fromDigest = SuiClient.instance.getTransactions(TransactionQuery.FromAddress(address))
        fromDigest?.data?.forEach { digests.add(it) }

        _transactions.postValue(digests)
    }

    fun transferObject(objectInfo: SuiObjectInfo, receiver: String, sender: String) =
        viewModelScope.launch {
            val transfer =
                SuiClient.instance.transferObject(objectInfo.objectId, receiver, sender, 100)
            transfer?.let { transferTxBytes ->
                _keyPair.value?.let { keyPair ->
                    val txBytes = Base64.getDecoder().decode(transferTxBytes.txBytes)
                    val signedTxBytes = SuiClient.instance.sign(keyPair, txBytes)
                    val executeResult = SuiClient.instance.executeTransaction(
                        txBytes, signedTxBytes, keyPair
                    )
                    _toastMessage.postValue(Gson().toJson(executeResult))
                }
            }

        }
}