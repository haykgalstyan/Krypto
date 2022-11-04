package galstyan.hayk.app

import CipherFactory
import org.junit.jupiter.api.Test
import javax.crypto.AEADBadTagException

class AeadCipherUnitTest {

    private val cipherFactory = CipherFactory()


    @Test
    fun `Test encrypt encrypts`() {
        val aeadCipher = cipherFactory.aeadCipher()
        val key = aeadCipher.generateKey()
        val cipherMessageBytes = aeadCipher.encrypt(PLAIN_TEXT_BYTES, key, null)
        val cipherMessage = cipherMessageBytes.decodeToString()

        assert(!cipherMessage.contains(PLAIN_TEXT))
    }


    @Test
    fun `Test decryption decrypts`() {
        val aeadCipher = cipherFactory.aeadCipher()
        val key = aeadCipher.generateKey()

        val cipherMessageBytes = aeadCipher.encrypt(PLAIN_TEXT_BYTES, key, null)

        val plainTextBytes = aeadCipher.decrypt(cipherMessageBytes, key, null)
        val plainText = plainTextBytes.decodeToString()

        assert(plainText == PLAIN_TEXT)
    }


    @Test
    fun `Test wrong AAD won't decrypt`() {
        val aeadCipher = cipherFactory.aeadCipher()
        val key = aeadCipher.generateKey()

        val cipherMessageBytesWithAad = aeadCipher.encrypt(PLAIN_TEXT_BYTES, key, AAD_BYTES)

        var exception: Exception? = null
        try {
            aeadCipher.decrypt(cipherMessageBytesWithAad, key, null)
        } catch (e: Exception) {
            exception = e
        }

        assert(exception is AEADBadTagException)
    }


    @Test
    fun `Dest decryption decrypts with AAD`() {
        val aeadCipher = cipherFactory.aeadCipher()
        val key = aeadCipher.generateKey()

        val cipherMessageBytes = aeadCipher.encrypt(PLAIN_TEXT_BYTES, key, AAD_BYTES)

        val plainTextBytes = aeadCipher.decrypt(cipherMessageBytes, key, AAD_BYTES)
        val plainText = plainTextBytes.decodeToString()
        assert(plainText == PLAIN_TEXT)
    }


    companion object {
        private const val PLAIN_TEXT = "Top secret message content nobody should see."
        private val PLAIN_TEXT_BYTES = PLAIN_TEXT.encodeToByteArray()

        private const val AAD = """
            {
                "id":"secret-message",
                "v":"1"
            }
        """
        private val AAD_BYTES = AAD.encodeToByteArray()
    }
}