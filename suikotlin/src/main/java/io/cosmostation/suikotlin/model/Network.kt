package io.cosmostation.suikotlin.model

sealed class Network(val name: String, val rpcUrl: String, val faucetUrl: String) {
    class Devnet : Network("Devnet", "https://sui-devnet-kr-1.cosmostation.io", "https://faucet.devnet.sui.io")

    class Testnet : Network("Testnet", "https://rpc-sui-testnet.cosmostation.io", "https://faucet.testnet.sui.io")
    class Localnet : Network("Localhost", "http://127.0.0.1:9000", "http://127.0.0.1:9000")
}