package io.cosmostation.suikotlin.model

sealed class Network(val name: String, val rpcUrl: String, val faucetUrl: String) {
    class Devnet : Network("Devnet", "https://fullnode.devnet.sui.io/", "https://faucet.devnet.sui.io")

    class Testnet : Network("Testnet", "https://fullnode.testnet.sui.io/", "https://faucet.testnet.sui.io")

    class Localnet : Network("Localhost", "http://127.0.0.1:9000", "http://127.0.0.1:9000")

    class Custom(name: String, rpcUrl: String) : Network(name, rpcUrl, "")
}