package io.cosmostation.suikotlin.model

sealed class Network(val name: String, val rpcUrl: String) {
    class Devnet : Network("Devnet", "https://fullnode.devnet.sui.io")
    class Testnet : Network("Testnet", "https://fullnode.testnet.sui.io")
    class Localnet : Network("Localhost", "http://127.0.0.1:9000")
}