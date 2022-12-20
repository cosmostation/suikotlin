# sui kotlin

Android SDK for Sui

## Requirement

* Android API 26
* Kotlin

## Dependency

```
implementation 'com.github.cosmostation:suikotlin:0.0.2'
```


## Initialize

Initialize for security.

```kotlin
SuiClient.initialize(applicationContext)
```

## API

Using api like below.
```kotlin
SuiCLient.instance.{API}
```

### Generate new mnemonic
```kotlin
fun generateMnemonic(): String
```

### Get address from mnemonic
```kotlin
fun getAddress(mnemonic: String): String
```

### Get address from KeyPair
```kotlin
fun getAddress(keyPair: EdDSAKeyPair): String
```

### Get KeyPair from mnemonic
```kotlin
fun getKeyPair(mnemonic: String): EdDSAKeyPair
```

### Sign data bytearray with KeyPair
```kotlin
fun sign(keyPair: EdDSAKeyPair, data: ByteArray): ByteArray
```

### Get objects by address
```kotlin
suspend fun getObjectsByOwner(address: String): List<SuiObjectInfo>?
```

### Get object details from sui objects
```kotlin
//@TODO parse string
suspend fun getObjectDetails(objectInfos: List<SuiObjectInfo>): List<String>?
```

### Get transactions
```kotlin
suspend fun getTransactions(
    transactionQuery: TransactionQuery,
    nextOffset: String? = null,
    limit: Int? = null,
    descending: Boolean = false
): SuiTransactionDigest?
```

### Get transaction details from transaction digests
```kotlin
suspend fun getTransactionDetails(digests: List<String>): List<String>?
```

### Fetch custom request(support sui json-rpc specs.)
```kotlin
suspend fun fetchCustomRequest(requests: JsonRpcRequest): JsonRpcResponse
```

### Fetch custom request list(support sui json-rpc specs.)
```kotlin
suspend fun fetchCustomRequests(requests: List<JsonRpcRequest>): List<JsonRpcResponse>
```

### Faucet
```kotlin
//@TODO parse string 
suspend fun faucet(address: String): Any
```

### Transfer sui object
```kotlin
suspend fun transferObject(
    objectId: String, receiver: String, sender: String, gasBudget: Int, amount: Int? = null
): SuiWrappedTxBytes?
```

### Execute signed transaction
```kotlin
//@TODO parse string 
suspend fun executeTransaction(
    txBytes: ByteArray, signedBytes: ByteArray, keyPair: EdDSAKeyPair
): Any?
```
