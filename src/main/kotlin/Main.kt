import java.io.File
import java.lang.Exception
import kotlin.system.exitProcess

const val ARG_MODE = 0
const val ARG_KEY_FILE = 1
const val ARG_INPUT_FILE = 2
const val ARG_OUTPUT_FILE = 3
const val ARG_AAD_FILE = 4

const val MODE_KEYGEN = "keygen"
const val MODE_ENCRYPT = "encrypt"
const val MODE_DECRYPT = "decrypt"


fun main(args: Array<String>) {
    val mode = getModeOrExit(args)
    val cipher = CipherFactory().aeadCipher()

    when (mode) {
        MODE_KEYGEN -> {
            val k = File(args[ARG_KEY_FILE])
            generateKeyAndExit(cipher, k)
        }

        else -> {
            checkEncryptDecryptArgsOrExit(args)
            val i = File(args[ARG_INPUT_FILE])
            val o = File(args[ARG_OUTPUT_FILE])
            val k = File(args[ARG_KEY_FILE])
            val ad = File(args[ARG_AAD_FILE])
            when (mode) {
                MODE_ENCRYPT -> tryEncryptAndExit(cipher, i, o, k, ad)
                MODE_DECRYPT -> tryDecryptAndExit(cipher, i, o, k, ad)
            }
        }
    }

    exitProcess(0)
}


fun getModeOrExit(args: Array<String>): Any {
    val mode = args.getOrNull(ARG_MODE) ?: run {
        exitError("Invalid arguments")
    }
    if (!setOf(MODE_KEYGEN, MODE_ENCRYPT, MODE_DECRYPT).contains(mode)) {
        exitError("Invalid mode: $mode")
    }
    return mode
}


fun checkArgsOrExit(args: Array<String>) {
    when (args[ARG_MODE]) {
        MODE_KEYGEN -> if (args.size < 2) {
            exitError("Invalid arguments for keygen")
        }

        MODE_ENCRYPT,
        MODE_DECRYPT -> {
            if (args.size < 4) {
                exitError("Invalid arguments for encrypt/decrypt")
            }
        }
    }
}


fun checkEncryptDecryptArgsOrExit(args: Array<String>) {
    if (args.size < 4) {
        exitError("Invalid arguments for encrypt/decrypt")
    }
}


fun generateKeyAndExit(cipher: AeadCipher, keyFile: File) {
    val keyBytes = cipher.generateKey()
    keyFile.writeBytes(keyBytes)
    println(keyBytes.toString())
    println(keyBytes.decodeToString())
    println("Generated key length: ${keyBytes.size}")
    println("Generated key length toString: ${keyBytes.toString().length}")
    println("Generated key length decodeToString: ${keyBytes.decodeToString().length}")
    exitProcess(0)
}


fun tryEncryptAndExit(cipher: AeadCipher, inputFile: File, outputFile: File, keyFile: File, aadFile: File) {
    try {
        val plainText = inputFile.readBytes()
        val key = keyFile.readBytes()
        val aad = aadFile.readBytes()
        println("Encrypt: ${plainText.decodeToString()}, key: ${key.decodeToString()}, aad: ${aad.decodeToString()}")
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
    println("Usages: ")
    println("\tModes: ${setOf(MODE_KEYGEN, MODE_ENCRYPT, MODE_DECRYPT).joinToString("/")}")
    println("\tGenerate key: $MODE_KEYGEN keyFile")
    println("\tEncrypt/Decrypt: $MODE_ENCRYPT/$MODE_ENCRYPT keyFile inputFile outputFile aadFile)")
}