package io.cosmostation.suikotlin.key

import com.develop.mnemonic.MnemonicUtils
import io.cosmostation.suikotlin.model.EdDSAKeyPair
import net.i2p.crypto.eddsa.EdDSAEngine
import net.i2p.crypto.eddsa.EdDSAPrivateKey
import net.i2p.crypto.eddsa.EdDSAPublicKey
import net.i2p.crypto.eddsa.Utils
import net.i2p.crypto.eddsa.spec.EdDSANamedCurveTable
import net.i2p.crypto.eddsa.spec.EdDSAParameterSpec
import net.i2p.crypto.eddsa.spec.EdDSAPrivateKeySpec
import net.i2p.crypto.eddsa.spec.EdDSAPublicKeySpec
import java.security.MessageDigest
import java.security.Signature
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object SuiKey {
    private const val MAC_SECRET_KEY = "ed25519 seed"
    private const val HMAC_SHA512_ALGORITHM_KEY = "HmacSHA512"
    private const val MESSAGE_DIGEST_ALGORITHM_SHA3_256 = "SHA3-256"
    private val SUI_HD_PATH = listOf(44, 784, 0, 0, 0)
    private const val SUI_ADDRESS_LENGTH = 40

    fun getSuiAddress(mnemonic: String, path: List<Int> = SUI_HD_PATH): String {
        val keyPair = getKeyPair(mnemonic, path)
        val digest: MessageDigest = MessageDigest.getInstance(MESSAGE_DIGEST_ALGORITHM_SHA3_256)
        val digestBytes: ByteArray = digest.digest(ByteArray(1) + keyPair.publicKey.abyte)
        val hex = Utils.bytesToHex(digestBytes)
        return "0x${hex.substring(0, SUI_ADDRESS_LENGTH)}"
    }

    fun getSuiAddress(keyPair: EdDSAKeyPair): String {
        val digest: MessageDigest = MessageDigest.getInstance(MESSAGE_DIGEST_ALGORITHM_SHA3_256)
        val digestBytes: ByteArray = digest.digest(ByteArray(1) + keyPair.publicKey.abyte)
        val hex = Utils.bytesToHex(digestBytes)
        return "0x${hex.substring(0, SUI_ADDRESS_LENGTH)}"
    }

    fun getKeyPair(mnemonic: String, path: List<Int> = SUI_HD_PATH): EdDSAKeyPair {
        val seedBytes: ByteArray = MnemonicUtils.generateSeed(mnemonic, "")
        var pair = shaking(seedBytes, MAC_SECRET_KEY.toByteArray())
        path.forEach {
            val buffer = ByteArray(size = 4)
            for (i in 0..3) buffer[i] = ((0x80000000 + it) shr ((3 - i) * 8) and 0xff).toByte()
            val pathBuffer = ByteArray(size = 1) + pair.first + buffer
            pair = shaking(pathBuffer, pair.second)
        }

        val keySpecs = EdDSANamedCurveTable.getByName(EdDSANamedCurveTable.ED_25519);
        val privateKeySpec = EdDSAPrivateKeySpec(pair.first, keySpecs)
        val pubKeySpec = EdDSAPublicKeySpec(privateKeySpec.a, keySpecs)
        val publicKey = EdDSAPublicKey(pubKeySpec)
        val privateKey = EdDSAPrivateKey(privateKeySpec)
        return EdDSAKeyPair(privateKey, publicKey)
    }

    fun sign(keyPair: EdDSAKeyPair, data: ByteArray): ByteArray {
        val spec: EdDSAParameterSpec = EdDSANamedCurveTable.getByName(EdDSANamedCurveTable.ED_25519)
        val signature: Signature = EdDSAEngine(MessageDigest.getInstance(spec.hashAlgorithm))
        signature.initSign(keyPair.privateKey)
        signature.update(data)
        return signature.sign()
    }

    private fun shaking(key: ByteArray, chain: ByteArray): Pair<ByteArray, ByteArray> {
        val mac = Mac.getInstance(HMAC_SHA512_ALGORITHM_KEY)
        val secretKeySpec = SecretKeySpec(chain, HMAC_SHA512_ALGORITHM_KEY)
        mac.init(secretKeySpec)
        val result = mac.doFinal(key)
        return Pair(result.sliceArray(0..31), result.sliceArray(32..63))
    }
}