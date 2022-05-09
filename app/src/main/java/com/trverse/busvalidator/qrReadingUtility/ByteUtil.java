package com.trverse.busvalidator.qrReadingUtility;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import kotlin.UByte;

public class ByteUtil {
    public static byte[] addBytes(byte srcBytes, byte desBytes) {
        return new byte[]{srcBytes, desBytes};
    }

    public static byte[] addBytes(byte[] srcBytes, byte[] desBytes) {
        if (!notNull(srcBytes) && !notNull(desBytes)) {
            return null;
        }
        if (notNull(srcBytes) && !notNull(desBytes)) {
            return srcBytes;
        }
        if (!notNull(srcBytes) && notNull(desBytes)) {
            return desBytes;
        }
        byte[] returnArray = new byte[(srcBytes.length + desBytes.length)];
        System.arraycopy(srcBytes, 0, returnArray, 0, srcBytes.length);
        System.arraycopy(desBytes, 0, returnArray, srcBytes.length, desBytes.length);
        return returnArray;
    }

    public static byte[] addBytes(byte[] srcBytes, byte desByte) {
        return addBytes(srcBytes, new byte[]{desByte});
    }

    public static byte[] insertByte(byte[] srcBytes, byte desByte, int index) {
        if (!notNull(srcBytes)) {
            return new byte[]{desByte};
        }
        byte[] desByteArray = {desByte};
        int srcLength = srcBytes.length;
        if (srcLength <= index) {
            return addBytes(srcBytes, desByteArray);
        }
        byte[] returnArray = new byte[(srcBytes.length + 1)];
        System.arraycopy(srcBytes, 0, returnArray, 0, index);
        System.arraycopy(desByteArray, 0, returnArray, index, desByteArray.length);
        System.arraycopy(srcBytes, index, returnArray, index + 1, srcLength - index);
        return returnArray;
    }

    public static byte[] insertBytes(byte[] srcBytes, byte[] desBytes, int index) {
        if (!notNull(srcBytes) && !notNull(desBytes)) {
            return null;
        }
        if (notNull(srcBytes) && !notNull(desBytes)) {
            return srcBytes;
        }
        if (!notNull(srcBytes) && notNull(desBytes)) {
            return desBytes;
        }
        int srcLength = srcBytes.length;
        if (srcLength <= index) {
            return addBytes(srcBytes, desBytes);
        }
        byte[] returnArray = new byte[(srcBytes.length + desBytes.length)];
        System.arraycopy(srcBytes, 0, returnArray, 0, index);
        System.arraycopy(desBytes, 0, returnArray, index, desBytes.length);
        System.arraycopy(srcBytes, index, returnArray, desBytes.length + index, srcLength - index);
        return returnArray;
    }

    public static boolean notNull(byte[] bytes) {
        if (bytes == null || bytes.length <= 0) {
            return false;
        }
        return true;
    }

    public static byte[] removeNULLByte(byte[] bytes) {
        if (!notNull(bytes)) {
            return null;
        }
        int copyEndIndex = 0;
        int i = bytes.length - 1;
        while (true) {
            if (i >= 0) {
                if (bytes[i] != 0) {
                    copyEndIndex = i;
                    break;
                }
                i--;
            } else {
                break;
            }
        }
        if (copyEndIndex <= 0) {
            return null;
        }
        byte[] returnBytes = new byte[(copyEndIndex + 1)];
        System.arraycopy(bytes, 0, returnBytes, 0, copyEndIndex + 1);
        return returnBytes;
    }

    public static boolean byteArrayIsNull(byte[] bytes) {
        if (!notNull(bytes)) {
            return true;
        }
        if (0 >= bytes.length || bytes[0] != 0) {
            return false;
        }
        return true;
    }

    public static byte naByte(byte src) {
        return (byte) (src ^ -1);
    }

    public static byte[] naBytes(byte[] src) {
        byte[] returnBytes = null;
        if (notNull(src)) {
            returnBytes = new byte[src.length];
            for (int i = 0; i < src.length; i++) {
                returnBytes[i] = naByte(src[i]);
            }
        }
        return returnBytes;
    }

    public static byte[] removeFFByte(byte[] bytes) {
        if (!notNull(bytes)) {
            return null;
        }
        int copyEndIndex = 0;
        int i = bytes.length;
        while (true) {
            i--;
            if (i < bytes.length) {
                if (bytes[i] != -1) {
                    copyEndIndex = i;
                    break;
                }
            } else {
                break;
            }
        }
        if (copyEndIndex <= 0) {
            return null;
        }
        byte[] returnBytes = new byte[(copyEndIndex + 1)];
        System.arraycopy(bytes, 0, returnBytes, 0, copyEndIndex + 1);
        return returnBytes;
    }

    public static byte[] removeNULLByte(byte[] bytes, int equalPartsNumber) {
        int copyCopyEndIndex = getLastNotNullIndex(bytes, 0, equalPartsNumber, bytes.length);
        if (copyCopyEndIndex <= 0) {
            return null;
        }
        byte[] returnBytes = new byte[(copyCopyEndIndex + 1)];
        System.arraycopy(bytes, 0, returnBytes, 0, copyCopyEndIndex + 1);
        return returnBytes;
    }

    private static int getLastNotNullIndex(byte[] bytes, int startIndex, int equalPartsNumber, int loopNumber) {
        if (!notNull(bytes) || startIndex >= bytes.length) {
            return 0;
        }
        int forPartNumber = loopNumber / equalPartsNumber;
        int remain = loopNumber % equalPartsNumber;
        if (forPartNumber <= 0) {
            int copyEndIndex = startIndex;
            int i = 0;
            while (i < loopNumber && bytes[i + startIndex] != 0) {
                copyEndIndex = i + startIndex;
                i++;
            }
            return copyEndIndex;
        }
        for (int i2 = 0; i2 < forPartNumber; i2++) {
            if (bytes[(startIndex + equalPartsNumber) - 1] == 0) {
                return getLastNotNullIndex(bytes, startIndex, equalPartsNumber, equalPartsNumber - 1);
            }
            startIndex = ((i2 + 1) * equalPartsNumber) - 1;
            if (i2 == forPartNumber - 1 && remain > 0) {
                return getLastNotNullIndex(bytes, startIndex, equalPartsNumber, remain);
            }
        }
        return 0;
    }

    public static byte[] removeAllNullBytes(byte[] bytes) {
        if (!notNull(bytes)) {
            return null;
        }
        int newArrayIndex = 0;
        byte[] copyBytes = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] != 0) {
                copyBytes[newArrayIndex] = bytes[i];
                newArrayIndex++;
            }
        }
        return removeNULLByte(copyBytes);
    }

    public static String asciiToHex(String str) {
        char[] chars = str.toCharArray();
        StringBuffer hex = new StringBuffer();
        for (char hexString : chars) {
            hex.append(Integer.toHexString(hexString));
        }
        return hex.toString();
    }

    public static String byteArrayToHexArray(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        if (notNull(bytes)) {
            for (byte byteToHexString : bytes) {
                sb.append(byteToHexString(byteToHexString));
            }
        }
        return sb.toString();
    }

    public static String hexStringToASCII(String hex) {
        StringBuilder sb = new StringBuilder();
        StringBuilder temp = new StringBuilder();
        for (int i = 0; i < hex.length() - 1; i += 2) {
            int decimal = Integer.parseInt(hex.substring(i, i + 2), 16);
            sb.append((char) decimal);
            temp.append(decimal);
        }
        return sb.toString();
    }

    public static int hexToInt(String hex) {
        if (hex == null || GPIOControl.GPIO_DIRECTION_OUT_STR.equals(hex)) {
            return -1;
        }
        return Integer.parseInt(hex, 16) & 255;
    }

    public static byte[] copyBytes(byte[] srcBytes, int start, int length) {
        if (!notNull(srcBytes) || start < 0 || length <= 0 || length > srcBytes.length - start) {
            return null;
        }
        byte[] returnBytes = new byte[length];
        System.arraycopy(srcBytes, start, returnBytes, 0, length);
        return returnBytes;
    }

    public static byte[] copyAllBytes(byte[] srcBytes) {
        return copyBytes(srcBytes, 0, srcBytes.length);
    }

    public static byte[] xorItself(byte[] bytes) throws Exception {
        if (notNull(bytes)) {
            byte[] returnBytes = new byte[bytes.length];
            for (int i = 0; i < bytes.length; i++) {
                returnBytes[i] = (byte) (bytes[i] ^ UByte.MAX_VALUE);
            }
            return returnBytes;
        }
        throw new Exception("the byte array is null");
    }

    public static byte xorItself(byte bte) throws Exception {
        return (byte) (bte ^ UByte.MAX_VALUE);
    }

    public static int xor(byte[] bytes, int xor) throws Exception {
        if (notNull(bytes)) {
            int xorInt = xor;
            for (byte b : bytes) {
                xorInt ^= b;
            }
            return xorInt;
        }
        throw new Exception("the byte array is null");
    }

    public static int xor(byte[] bytes, byte xor) throws Exception {
        if (notNull(bytes)) {
            int xorInt = xor;
            for (byte b : bytes) {
                xorInt ^= b;
            }
            return xorInt;
        }
        throw new Exception("the byte array is null");
    }

    public static int symbolBytesToInt(byte[] bytes) {
        int returnV = 0;
        if (notNull(bytes)) {
            int i = 4;
            if (bytes.length <= 4) {
                i = bytes.length;
            }
            int fori = i;
            for (int i2 = 0; i2 < fori; i2++) {
                returnV += bytes[i2] << ((i2 + 1) * 2);
            }
        }
        return returnV;
    }

    public static boolean verifyCode(String hexData, String hexCode) {
        if (hexData == null || hexData.length() <= 0) {
            return false;
        }
        int fori = hexData.length() % 2 == 0 ? hexData.length() / 2 : (hexData.length() / 2) + 1;
        int xor = 0;
        for (int i = 0; i < fori; i++) {
            xor ^= hexToInt(hexData.substring(i * 2, (i * 2) + 2));
        }
        if (xor == hexToInt(hexCode)) {
            return true;
        }
        return false;
    }

    public static int createCode(byte[] bytes, int cmd) {
        if (notNull(bytes)) {
            return createCode(new String(bytes), cmd);
        }
        throw new NullPointerException(" the bytes is null");
    }

    public static int createCode(String hexData, int cmd) {
        if (hexData == null || hexData.length() <= 0) {
            throw new NullPointerException("Cards is null");
        }
        int fori = hexData.length() % 2 == 0 ? hexData.length() / 2 : (hexData.length() / 2) + 1;
        int xor = cmd;
        for (int i = 0; i < fori; i++) {
            xor ^= hexToInt(hexData.substring(i * 2, (i * 2) + 2));
        }
        return xor;
    }

    public static byte intToByte(int value) throws Exception {
        if (value <= 255) {
            return (byte) value;
        }
        throw new Exception("can not convert int value to byte when int value is greater than 255");
    }

    public static byte intToByteTwo(int value) {
        return new Integer(value & 255).byteValue();
    }

    public static char intStringToChar(String intStr) {
        if (!notNull(intStr)) {
            intStr = "0";
        }
        return (char) new Integer(Integer.parseInt(intStr)).byteValue();
    }

    public static char hexStringToChar(String hexStr) {
        if (!notNull(hexStr)) {
            hexStr = "00";
        }
        return (char) hexStringToByte(hexStr);
    }

    public static String byteArrayToHex(byte[] b) {
        if (b != null) {
            String hs = GPIOControl.GPIO_DIRECTION_OUT_STR;
            for (byte b2 : b) {
                String stmp = Integer.toHexString(b2 & UByte.MAX_VALUE);
                if (stmp.length() == 1) {
                    hs = hs + "0" + stmp;
                } else {
                    hs = hs + stmp;
                }
            }
            return hs.toUpperCase();
        }
        throw new IllegalArgumentException("Argument b ( byte array ) is null! ");
    }

    public static final String byteToHex(byte b) {
        StringBuilder sb = new StringBuilder();
        String stmp = Integer.toHexString(b & UByte.MAX_VALUE);
        if (stmp.length() == 1) {
            sb.append("0");
            sb.append(stmp);
        } else {
            sb.append(stmp);
        }
        return sb.toString().toUpperCase();
    }

    public static String byteToHexString(byte bte) {
        return byteToHex(bte).toUpperCase();
    }

    public static String intToHexString(int intValue) {
        try {
            return byteArrayToHexString(intToByteArray(intValue));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String intToHexStringSingle(int intValue) {
        try {
            return byteToHexString(intToByte(intValue));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean notNull(List<?> list) {
        if (list == null || list.size() <= 0) {
            return false;
        }
        return true;
    }

    public static String createCode(String hexData, String hexCmd) {
        try {
            return byteToHexString(intToByteTwo(createCode(hexData, hexToInt(hexCmd))));
        } catch (Exception e) {
            e.printStackTrace();
            return GPIOControl.GPIO_DIRECTION_OUT_STR;
        }
    }

    public static byte[] intToByteArray(int res) {
        byte[] targets = new byte[4];
        targets[3] = (byte) (res & 255);
        targets[2] = (byte) ((res >> 8) & 255);
        targets[1] = (byte) ((res >> 16) & 255);
        targets[0] = (byte) (res >>> 24);
        return targets;
    }

    public static int byteArrayToIntHighToLow(byte[] b) {
        int returnValue = 0;
        if (notNull(b)) {
            if (b.length > 4) {
                return 0;
            }
            for (int k = 0; k < b.length; k++) {
                returnValue += (b[k] & UByte.MAX_VALUE) << (((b.length - 1) - k) * 8);
            }
        }
        return returnValue;
    }

    public static int byteArrayToIntLowToHigh(byte[] b) {
        int returnValue = 0;
        if (notNull(b)) {
            if (b.length > 4) {
                return 0;
            }
            for (int i = 0; i < b.length; i++) {
                returnValue += (b[i] & UByte.MAX_VALUE) << (i * 8);
            }
        }
        return returnValue;
    }

    public static int byteToInt(byte b) {
        return b & UByte.MAX_VALUE;
    }

    public static char intToChar(int val) {
        return (char) (val & 255);
    }

    public static char intToChar(String hexStr) {
        if (!notNull(hexStr)) {
            return 0;
        }
        new Integer(hexToInt(hexStr)).byteValue();
        return 0;
    }

    public static byte[] reverseByteArray(byte[] srcBytes) {
        if (!notNull(srcBytes)) {
            return null;
        }
        byte[] returnBytes = new byte[srcBytes.length];
        for (int i = srcBytes.length - 1; i >= 0; i--) {
            returnBytes[(returnBytes.length - 1) - i] = srcBytes[i];
        }
        return returnBytes;
    }

    public static String reverseHexString(String hexStr) {
        if (!notNull(hexStr)) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = (hexStr.length() / 2) - 1; i >= 0; i--) {
            sb.append(hexStr.substring(i * 2, (i * 2) + 2));
        }
        return sb.toString();
    }

    public static boolean notNull(String string) {
        if (string == null || string.length() <= 0) {
            return false;
        }
        return true;
    }

    public static boolean notNull(Map map) {
        if (map == null || map.size() <= 0) {
            return false;
        }
        return true;
    }

    public static String hexStringToString(String hexStr) {
        if (notNull(hexStr)) {
            return new String(hexStringToByteArray(hexStr));
        }
        return null;
    }

    public static final byte[] hexStringToByteArray(String hex) throws IllegalArgumentException {
        if (hex.length() % 2 == 0) {
            char[] arr = hex.toCharArray();
            byte[] b = new byte[(hex.length() / 2)];
            int i = 0;
            int j = 0;
            int l = hex.length();
            while (i < l) {
                StringBuilder sb = new StringBuilder();
                sb.append(GPIOControl.GPIO_DIRECTION_OUT_STR);
                int i2 = i + 1;
                sb.append(arr[i]);
                sb.append(arr[i2]);
                b[j] = new Integer(Integer.parseInt(sb.toString(), 16) & 255).byteValue();
                i = i2 + 1;
                j++;
            }
            return b;
        }
        throw new IllegalArgumentException();
    }

    public static final byte hexStringToByte(String hex) throws IllegalArgumentException {
        if (hex.length() % 2 == 0) {
            return new Integer(Integer.parseInt(hex, 16) & 255).byteValue();
        }
        throw new IllegalArgumentException();
    }

    public static String byteArrayToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder(GPIOControl.GPIO_DIRECTION_OUT_STR);
        if (src == null || src.length <= 0) {
            return null;
        }
        for (byte b : src) {
            String hv = Integer.toHexString(b & 255);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString().toUpperCase();
    }

    public static byte[] charArrayToByteArray(char[] chars) {
        byte[] reBytes = null;
        if (notNull(chars)) {
            reBytes = new byte[chars.length];
            for (int i = 0; i < chars.length; i++) {
                reBytes[i] = (byte) chars[i];
            }
        }
        return reBytes;
    }

    public static byte charToByte(char chr) {
        Charset cs = Charset.forName("UTF-8");
        CharBuffer cb = CharBuffer.allocate(1);
        cb.put(chr);
        cb.flip();
        return cs.encode(cb).array()[0];
    }

    public static boolean notNull(char[] objs) {
        if (objs == null || objs.length <= 0) {
            return false;
        }
        return true;
    }

    public static char[] byteArrayToCharArray(byte[] bytes) {
        char[] reChars = null;
        if (notNull(bytes)) {
            reChars = new char[bytes.length];
            for (int i = 0; i < bytes.length; i++) {
                reChars[i] = (char) bytes[i];
            }
        }
        return reChars;
    }

    public static char byteToChar(byte bte) {
        Charset cs = Charset.forName("UTF-8");
        ByteBuffer bb = ByteBuffer.allocate(1);
        bb.put(bte);
        bb.flip();
        return cs.decode(bb).array()[0];
    }

    public static char byteToOxChar(byte bte) {
        return (char) bte;
    }

    public static byte charToOxByte(char car) {
        return (byte) car;
    }

    public static boolean compareString(String one, String two) {
        if (!notNull(one) && !notNull(two)) {
            return true;
        }
        if (!notNull(one) || !notNull(two)) {
            return false;
        }
        return one.equals(two);
    }

    public static boolean compareBytes(byte[] oneBytes, byte[] twoBytes) {
        return Arrays.equals(oneBytes, twoBytes);
    }

    public static boolean replaceBytes(byte[] srcBytes, byte[] newBytes, int startIndex) {
        if (!notNull(srcBytes)) {
            printLog("the src bytes is null");
            return false;
        } else if (!notNull(newBytes)) {
            printLog("the new bytes is null, nothing to replace");
            return false;
        } else if (newBytes.length + startIndex > srcBytes.length) {
            printLog("replace length is bigger than src bytes length");
            return false;
        } else {
            System.arraycopy(newBytes, 0, srcBytes, startIndex, newBytes.length);
            return true;
        }
    }

    private static void printLog(String msg) {
    }
}