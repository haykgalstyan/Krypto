class CipherImpl : AeadCipher {

    private val cipher = AesGcmNoPaddingCipher()

    override fun generateKey() = cipher.generateKey()

    override fun encrypt(
        plainText: ByteArray,
        key: ByteArray,
        aad: ByteArray?
    ) = cipher.encrypt(plainText, key, aad)

    override fun decrypt(
        cipherMessage: ByteArray,
        key: ByteArray,
        aad: ByteArray?
    ) = cipher.decrypt(cipherMessage, key, aad)

}