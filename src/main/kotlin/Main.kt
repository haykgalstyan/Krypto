import java.io.File
import kotlin.system.exitProcess

const val ARG_MODE = 0
const val ARG_KEY_FILE = 1
const val ARG_INPUT_FILE = 2
const val ARG_OUTPUT_FILE = 3
const val ARG_AAD_FILE = 4

const val MODE_KEYGEN = "keygen"
const val MODE_XOR = "xor"
const val MODE_ENCRYPT = "encrypt"
const val MODE_DECRYPT = "decrypt"
val modes = setOf(MODE_KEYGEN, MODE_XOR, MODE_ENCRYPT, MODE_DECRYPT)


fun main(args: Array<String>) {
    checkArgsOrExit(args)

    val cipher = CipherFactory().aeadCipher()
    val mode = args[ARG_MODE]

    when (mode) {
        MODE_KEYGEN -> {
            val k = File(args[ARG_KEY_FILE])
            generateKeyAndExit(cipher, k)
        }

        MODE_XOR -> {
            val k1 = File(args[ARG_KEY_FILE])
            val k2 = File(args[ARG_INPUT_FILE])
            val o = File(args[ARG_OUTPUT_FILE])
            xorAndExit(k1, k2, o)
        }

        else -> {
            val k = File(args[ARG_KEY_FILE])
            val i = File(args[ARG_INPUT_FILE])
            val o = File(args[ARG_OUTPUT_FILE])
            val ad = File(args[ARG_AAD_FILE])
            when (mode) {
                MODE_ENCRYPT -> tryEncryptAndExit(cipher, i, o, k, ad)
                MODE_DECRYPT -> tryDecryptAndExit(cipher, i, o, k, ad)
            }
        }
    }

    exitProcess(0)
}


fun checkArgsOrExit(args: Array<String>) {
    val mode = args.getOrNull(ARG_MODE) ?: run {
        exitError("Invalid args, see usages.")
    }
    if (!modes.contains(mode)) {
        exitError("Invalid mode: $mode")
    }
    when (mode) {
        MODE_KEYGEN -> if (args.size < 2) exitError("Invalid arguments for $MODE_KEYGEN")
        MODE_XOR -> if (args.size < 3) exitError("Invalid arguments for $MODE_XOR")
        MODE_ENCRYPT,
        MODE_DECRYPT -> if (args.size < 4) exitError("Invalid arguments for $MODE_ENCRYPT/$MODE_DECRYPT")
    }
}


fun generateKeyAndExit(cipher: AeadCipher, keyFile: File) {
    val keyBytes = cipher.generateKey()
    keyFile.writeBytes(keyBytes)
    exitProcess(0)
}


fun xorAndExit(keyFile1: File, keyFile2: File, o: File) {
    val k1 = keyFile1.readBytes()
    val k2 = keyFile2.readBytes()
    if (k1.size != k2.size) exitError("XOR keys must have the same length")

    val ko = k1 xor k2
    o.writeBytes(ko)
    exitProcess(0)
}


fun tryEncryptAndExit(cipher: AeadCipher, inputFile: File, outputFile: File, keyFile: File, aadFile: File) {
    try {
        val plainText = inputFile.readBytes()
        val key = keyFile.readBytes()
        val aad = aadFile.readBytes()
        val cipherMessage = cipher.encrypt(plainText, key, aad)
        outputFile.writeBytes(cipherMessage)
    } catch (e: Exception) {
        System.err.println("Error encrypting: ${e.message}")
        exitProcess(1)
    }
}


fun tryDecryptAndExit(cipher: AeadCipher, inputFile: File, outputFile: File, keyFile: File, aadFile: File) {
    try {
        val plainText = cipher.decrypt(inputFile.readBytes(), keyFile.readBytes(), aadFile.readBytes())
        outputFile.writeBytes(plainText)
    } catch (e: Exception) {
        System.err.println("Error decrypting: ${e.message}")
        exitProcess(1)
    }
}


fun exitError(message: String) {
    System.err.println(message)
    printHelp()
    exitProcess(1)
}


fun printHelp() {
    println("Usage:")
    println("\tModes: ${modes.joinToString("/")}")
    println("\tGenerate key: $MODE_KEYGEN keyFile")
    println("\tEncrypt/Decrypt: $MODE_ENCRYPT/$MODE_DECRYPT keyFile inputFile outputFile aadFile")
    println("\tXOR keys: $MODE_XOR keyFileA keyFileB outputFile")
}