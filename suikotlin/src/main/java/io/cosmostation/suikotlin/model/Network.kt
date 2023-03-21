package io.cosmostation.suikotlin.model

sealed class Network(val name: String, val rpcUrl: String, val faucetUrl: String) {
    //    https://rpc-office.cosmostation.io/sui-dev-testnet/
    class Devnet :
        Network("Devnet", "https://explorer-rpc.devnet.sui.io", "https://faucet.devnet.sui.io")

    class Testnet : Network("Testnet", "https://wave3-rpc.testnet.sui.io", "https://faucet.testnet.sui.io")
    class Localnet : Network("Localhost", "http://127.0.0.1:9000", "http://127.0.0.1:9000")
}
