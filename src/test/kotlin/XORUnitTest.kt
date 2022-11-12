package galstyan.hayk.app

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import xor

class XORUnitTest {

    private val k1 = listOf(
        0x4a, 0xc5, 0xfb, 0x5f, 0xe8, 0xfb, 0x8a, 0x2c,
        0xdb, 0x5c, 0xdf, 0x9a, 0x91, 0x5c, 0xaa, 0xa2,
    ).map { it.toByte() }.toByteArray()

    private val k2 = listOf(
        0xa6, 0xc8, 0xdd, 0x7e, 0x06, 0x7a, 0xbf, 0x62,
        0x7b, 0xa4, 0x31, 0x1c, 0x38, 0xff, 0x88, 0x56,
    ).map { it.toByte() }.toByteArray()


    @Test
    fun `assert symmetry`() {
        assert((k1 xor k2).contentEquals(k2 xor k1))
    }

    @Test
    fun `assert fails on different lengths`() {
        assertThrows<IllegalArgumentException> {
            k1.xor(byteArrayOf(0.toByte()))
        }
    }

    @Test
    fun `assert zeros`() {
        val k0 = ByteArray(16) { 0x00 }
        assert((k1 xor k0).contentEquals(k1))
        assert((k2 xor k0).contentEquals(k2))
    }

    @Test
    fun `assert xor and back works`() {
        val k3 = k1 xor k2

        assert((k1 xor k2).contentEquals(k3))
        assert((k2 xor k3).contentEquals(k1))
        assert((k3 xor k1).contentEquals(k2))
    }
}