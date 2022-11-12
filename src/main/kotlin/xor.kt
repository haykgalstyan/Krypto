import java.lang.IllegalArgumentException
import kotlin.experimental.xor

infix fun ByteArray.xor(byteArray: ByteArray): ByteArray {
    if (this.size != byteArray.size) {
        throw IllegalArgumentException("XOR byte arrays must have the same length.")
    }

    val result = ByteArray(this.size)
    for (i in this.indices) result[i] = this[i] xor byteArray[i]
    return result
}
