package utility

import java.lang.StringBuilder
import kotlin.experimental.and
import android.text.TextUtils
import java.lang.IllegalArgumentException


object ByteUtils {
    fun bytesToHexString(src: ByteArray?, len: Int): String? {
        val string = StringBuilder("")
        if (src?.isEmpty() == true) {
            return null
        } else {
            for (i in 0 until len) {
                val v = (src?.get(i) ?: 0) and 0xFF.toByte()
                val hv = Integer.toHexString(v.toInt())
                if (hv.length < 2) {
                    string.append(0)
                } else {
                    string.append(hv)
                }
            }
        }
        return string.toString()
    }


    fun bytearrayToHexString(b: ByteArray, leng: Int): String {
        val strbuf = StringBuffer()
        for (i in 0 until leng) {
            strbuf.append("0123456789ABCDEF"[((b[i] and 0xf0.toByte() shr 4).toByte()).toInt()])
            strbuf.append("0123456789ABCDEF"[(b[i] and 0x0f)])
            strbuf.append(" ")

        }
        return strbuf.toString()
    }

    /**
     * åå…­è¿›åˆ¶ä¸²è½¬åŒ–ä¸ºbyteæ•°ç»„
     *
     * @param hex
     * @return the array of byte
     */
    @Throws(IllegalArgumentException::class)
    fun hexStringToByteArray(hex: String): ByteArray {
        require(hex.length % 2 == 0)
        val arr = hex.toCharArray()
        val b = ByteArray(hex.length / 2)
        var i = 0
        var j = 0
        val l = hex.length
        while (i < l) {
            val swap = "" + arr[i++] + arr[i]
            val byteint = swap.toInt(16) and 0xFF
            b[j] = byteint.toByte()
            i++
            j++
        }
        return b
    }

    /**
     * copy byte æ•°ç»„
     *
     * @param srcBytes è¢«copy çš„å­—èŠ‚æ•°ç»„
     * @param start    ã€€å¼€å§‹ä½ç½®
     * @param length   ã€€é•¿åº¦
     * @return byte[] è¿”å›žæ‹·è´åŽçš„æ•°ç»„
     */
    fun copyBytes(srcBytes: ByteArray, start: Int, length: Int): ByteArray? {
        if (start >= 0 && length > 0) {
            if (length <= srcBytes.size - start) {
                //copy array
                val returnBytes = ByteArray(length)
                System.arraycopy(srcBytes, start, returnBytes, 0, length)
                return returnBytes
            }
        }
        return null
    }

    /**
     * ä¸¤ä¸ªbyte æ•°ç»„å åŠ . å°† desBytes æ·»åŠ åˆ° srcBytes
     *
     * @param srcBytes è¢«æ·»åŠ çš„byte sæ•°ç»„
     * @param desBytes ã€€æ·»åŠ çš„byte æ•°ç»„
     * @return byte[]ã€€è¿”å›žæ·»åŠ åŽçš„æ•°ç»„
     */
    fun addBytes(srcBytes: ByteArray?, desBytes: ByteArray?): ByteArray? {
        if (srcBytes == null || desBytes == null) {
            return null
        }
        // copy array
        val returnArray = ByteArray(srcBytes.size + desBytes.size)
        System.arraycopy(srcBytes, 0, returnArray, 0, srcBytes.size)
        System.arraycopy(desBytes, 0, returnArray, srcBytes.size, desBytes.size)
        return returnArray
    }

    /**
     * å°†byte æ•°ç»„ è½¬åŒ–æˆ hex string
     *
     * @param src
     * @return String
     */
    fun byteArrayToHexString(src: ByteArray?): String? {
        val stringBuilder = StringBuilder("")
        if (src == null || src.size <= 0) {
            return null
        }
        for (i in src.indices) {
            val v = (src[i] and 0xFF.toByte()).toInt()
            val hv = Integer.toHexString(v)
            if (hv.length < 2) {
                stringBuilder.append(0)
            }
            stringBuilder.append(hv)
        }
        return stringBuilder.toString().toUpperCase()
    }

    /**
     * å°†åå…­è¿›åˆ¶çš„å­—ç¬¦ä¸²ï¼Œè½¬åŒ–ä¸ºæ™®é€šå­—ç¬¦ä¸²ã€‚
     *
     * @param hexStr
     * @return String
     */
    //     * ä¾èµ–äº†{@link DatatypeConverter#parseHexBinary(String)}æ–¹æ³•ï¼Œå› æ­¤éœ€è¦å¯¼å…¥æ­¤åŒ…ï¼Œå¦åˆ™ä¸èƒ½ç¼–è¯‘é€šè¿‡
    fun hexStringToString(hexStr: String): String? {
        if (!TextUtils.isEmpty(hexStr)) {
//            byte[] hexBytes = DatatypeConverter.parseHexBinary(hexStr);
            val hexBytes = hexStringToByteArray(hexStr)
            return String(hexBytes)
        }
        return null
    }

    infix fun Byte.shl(that: Int): Int = this.toInt().shl(that)
    infix fun Int.shl(that: Byte): Int = this.shl(that.toInt()) // Not necessary in this case because no there's (Int shl Byte)
    infix fun Byte.shl(that: Byte): Int = this.toInt().shl(that.toInt()) // Not necessary in this case because no there's (Byte shl Byte)

    infix fun Byte.and(that: Int): Int = this.toInt().and(that)
    infix fun Int.and(that: Byte): Int = this.and(that.toInt()) // Not necessary in this case because no there's (Int and Byte)
    infix fun Byte.and(that: Byte): Int = this.toInt().and(that.toInt())

    fun hexToString(hex: String): String? {
        val sb = StringBuilder()
        val hexData = hex.toCharArray()
        var count = 0
        while (count < hexData.size - 1) {
            val firstDigit = Character.digit(hexData[count], 16)
            val lastDigit = Character.digit(hexData[count + 1], 16)
            val decimal = firstDigit * 16 + lastDigit
            sb.append(decimal.toChar())
            count += 2
        }
        return sb.toString()
    }
}