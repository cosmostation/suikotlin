package io.cosmostation.suikotlin.model

import net.i2p.crypto.eddsa.EdDSAPrivateKey
import net.i2p.crypto.eddsa.EdDSAPublicKey

data class EdDSAKeyPair(val privateKey: EdDSAPrivateKey, val publicKey: EdDSAPublicKey)