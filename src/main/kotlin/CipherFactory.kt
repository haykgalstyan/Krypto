class CipherFactory {
    fun aeadCipher(): AeadCipher = CipherImpl()
}