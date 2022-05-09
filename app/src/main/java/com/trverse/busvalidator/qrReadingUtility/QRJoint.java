package com.trverse.busvalidator.qrReadingUtility;

public class QRJoint {
    static StringBuffer completeBuffer = new StringBuffer();

    public static String getCompleteQRCode(byte[] buffer, int size) {
        if (buffer == null || buffer.length <= 0 || size <= 0) {
            return null;
        }
        byte[] realData = new byte[size];
        System.arraycopy(buffer, 0, realData, 0, size);
        completeBuffer.append(ByteUtil.byteArrayToHexString(realData));
        if (completeBuffer.length() <= 0 || !completeBuffer.toString().contains("0D")) {
            return null;
        }
        StringBuffer stringBuffer = completeBuffer;
        String qrcode = stringBuffer.substring(0, stringBuffer.indexOf("0D"));
        StringBuffer stringBuffer2 = completeBuffer;
        stringBuffer2.delete(0, stringBuffer2.length());
        return qrcode;
    }
}