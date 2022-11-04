interface AeadCipher {

    fun generateKey(): ByteArray

    fun encrypt(
        plainText: ByteArray,
        key: ByteArray,
        aad: ByteArray?,
    ): ByteArray

    fun decrypt(
        cipherMessage: ByteArray,
        key: ByteArray,
        aad: ByteArray?,
    ): ByteArray
}