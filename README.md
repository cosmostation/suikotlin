# sui kotlin

Android SDK for Sui

## Requirement

* Android API 26
* Kotlin

## Dependency

```
implementation 'com.github.cosmostation:suikotlin:0.3.0'
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
suspend fun getObjectsByOwner
```

### Get object details from sui objects
```kotlin
suspend fun getObjectDetails
```

### Get transactions
```kotlin
suspend fun getTransactions
```

### Get transaction details from transaction digests
```kotlin
suspend fun getTransactionDetails
```

### Fetch custom request(support sui json-rpc specs.)
```kotlin
suspend fun fetchCustomRequest(requests: JsonRpcRequest): JsonRpcResponse
```

### Fetch custom request list(support sui json-rpc specs.) : Batch not working
```kotlin
suspend fun fetchCustomRequests(requests: List<JsonRpcRequest>): List<JsonRpcResponse>
```

### Faucet
```kotlin
suspend fun faucet
```

### Transfer object
```kotlin
suspend fun transferObject
```

### Move Call
```kotlin
suspend fun moveCall
```

### Transfer sui
```kotlin
suspend fun transferSui
```

### Execute signed transaction
```kotlin
suspend fun executeTransaction
```
