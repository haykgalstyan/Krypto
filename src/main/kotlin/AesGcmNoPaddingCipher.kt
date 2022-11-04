import java.security.Key
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec


/***
 * AEAD Cipher
 * AES encryption with GCM mode. (AES128_GCM) (AES-GCM)
 *
 * Terminology
 * iv: Initialization Vector
 * aad: Additional Authentication Data or Associated data
 */
class AesGcmNoPaddingCipher {

    companion object {
        private const val KEY_LENGTH_BYTES = 16
        private const val IV_LENGTH_BYTES = 12
        private const val TAG_LENGTH_BYTES = 16
        private const val ALGORITHM_KEY = "AES"
        private const val ALGORITHM_CIPHER = "AES/GCM/NoPadding"
    }

    fun generateKey() = randomBytes(KEY_LENGTH_BYTES)

    fun encrypt(
        plainText: ByteArray,
        key: ByteArray,
        aad: ByteArray?
    ): ByteArray {
        val iv = generateIv()
        val cipher = encryptionCipher(key, iv, aad)

        val cipherText = cipher.doFinal(plainText)
        val cipherMessage = cipher.iv + cipherText

        return cipherMessage
    }

    fun decrypt(
        cipherMessage: ByteArray,
        key: ByteArray,
        aad: ByteArray?
    ): ByteArray {
        val cipher = decryptionCipher(key, cipherMessage, aad)

        val plainText: ByteArray =
            cipher.doFinal(cipherMessage, IV_LENGTH_BYTES, cipherMessage.size - IV_LENGTH_BYTES)

        return plainText
    }


    private fun generateIv() = randomBytes(IV_LENGTH_BYTES)

    private fun encryptionCipher(
        key: ByteArray,
        iv: ByteArray,
        aad: ByteArray?,
    ): Cipher {
        val cipher = Cipher.getInstance(ALGORITHM_CIPHER)
        val params = GCMParameterSpec(TAG_LENGTH_BYTES * 8, iv)
        cipher.init(Cipher.ENCRYPT_MODE, key.toKey(), params)
        aad?.let { cipher.updateAAD(it) }
        return cipher
    }

    private fun decryptionCipher(
        key: ByteArray,
        cipherMessage: ByteArray,
        aad: ByteArray?,
    ): Cipher {
        val cipher = Cipher.getInstance(ALGORITHM_CIPHER)
        val params = GCMParameterSpec(TAG_LENGTH_BYTES * 8, cipherMessage, 0, IV_LENGTH_BYTES)
        cipher.init(Cipher.DECRYPT_MODE, key.toKey(), params)
        aad?.let { cipher.updateAAD(it) }
        return cipher
    }

    private fun ByteArray.toKey(): Key {
        return SecretKeySpec(this, ALGORITHM_KEY)
    }

    private fun randomBytes(length: Int): ByteArray {
        val random = SecureRandom()
        val bytes = ByteArray(length)
        random.nextBytes(bytes)
        return bytes
    }
}