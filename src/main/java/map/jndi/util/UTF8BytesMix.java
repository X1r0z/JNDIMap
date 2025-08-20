package map.jndi.util;

public class UTF8BytesMix {

    public static int type = 3;

    public byte[] originalBytes;
    public byte[] resultBytes = new byte[0];
    public int index = 0;

    final static byte TC_CLASSDESC = (byte) 0x72;
    final static byte TC_PROXYCLASSDESC = (byte) 0x7d;
    final static byte TC_STRING = (byte) 0x74;
    final static byte TC_REFERENCE = (byte) 0x71;

    final static byte BYTE = (byte) 0x42;
    final static byte CHAR = (byte) 0x43;
    final static byte DOUBLE = (byte) 0x44;
    final static byte FLOAT = (byte) 0x46;
    final static byte INTEGER = (byte) 0x49;
    final static byte LONG = (byte) 0x4a;
    final static byte OBJECT_L = (byte) 0x4c;
    final static byte SHORT = (byte) 0x53;
    final static byte BOOLEAN = (byte) 0x5a;
    final static byte ARRAY = (byte) 0x5b;

    public UTF8BytesMix(byte[] originalBytes) {
        this.originalBytes = originalBytes;
    }

    public byte[] mix() {
        while (index < originalBytes.length) {
            byte b = originalBytes[index];
            byteAdd(b);

            if (b == TC_CLASSDESC) {
                changeClassDesc();
            } else if (b == TC_PROXYCLASSDESC) {
                changeProxyClassDesc();
            } else if (b == TC_STRING) {
                changeString();
            }

            index++;
        }

        return resultBytes;
    }

    public void changeProxyClassDesc() {
        int interfaceCount = ((originalBytes[index + 1] & 0xFF) << 24) |
                ((originalBytes[index + 2] & 0xFF) << 16) |
                ((originalBytes[index + 3] & 0xFF) << 8) |
                (originalBytes[index + 4] & 0xFF);

        if (interfaceCount > 0xff || interfaceCount < 0x00) return;

        for (int i = 0; i < 4; i++) {
            byteAdd(originalBytes[index + 1]);
            index++;
        }

        int length = ((originalBytes[index + 1] & 0xFF) << 8) | (originalBytes[index + 2] & 0xFF);
        byte[] originalValue = new byte[length];
        System.arraycopy(originalBytes, index + 3, originalValue, 0, length);

        index += 3 + length;
        encode(originalValue, type);
        index--;
    }


    public boolean changeClassDesc() {
        boolean isClassDesc = changeString();
        if (!isClassDesc) {
            return false;
        }
        index++;

        // SerialVersionUID + ClassDescFlags
        byte[] serialVersionUID = new byte[9];
        System.arraycopy(originalBytes, index, serialVersionUID, 0, 9);

        for (int i = 0; i < serialVersionUID.length; i++) {
            byteAdd(serialVersionUID[i]);
        }
        index += 9;

        // Field Count
        byte[] fieldCount = new byte[2];
        System.arraycopy(originalBytes, index, fieldCount, 0, 2);

        for (int i = 0; i < fieldCount.length; i++) {
            byteAdd(fieldCount[i]);
        }
        int fieldCounts = ((fieldCount[0] & 0xFF) << 8) | (fieldCount[1] & 0xFF);
        index += 2;

        for (int i = 0; i < fieldCounts; i++) {
            // FieldName
            if (originalBytes[index] == BYTE
                    || originalBytes[index] == CHAR
                    || originalBytes[index] == DOUBLE
                    || originalBytes[index] == FLOAT
                    || originalBytes[index] == INTEGER
                    || originalBytes[index] == LONG
                    || originalBytes[index] == OBJECT_L
                    || originalBytes[index] == SHORT
                    || originalBytes[index] == BOOLEAN
                    || originalBytes[index] == ARRAY) {
                byteAdd(originalBytes[index]);
                index++;

                int fieldLength = ((originalBytes[index] & 0xFF) << 8) | (originalBytes[index + 1] & 0xFF);
                byte[] originalFieldName = new byte[fieldLength];
                System.arraycopy(originalBytes, index + 2, originalFieldName, 0, fieldLength);
                index += 2 + fieldLength;
                encode(originalFieldName, type);
            }

            // Class Name
            if (originalBytes[index] == TC_STRING) {
                byteAdd(originalBytes[index]);
                index++;

                int classLength = ((originalBytes[index] & 0xFF) << 8) | (originalBytes[index + 1] & 0xFF);
                byte[] originalClassName = new byte[classLength];
                System.arraycopy(originalBytes, index + 2, originalClassName, 0, classLength);
                index += 2 + classLength;
                encode(originalClassName, type);
            } else if (originalBytes[index] == TC_REFERENCE) {
                byte[] reference = new byte[5];
                System.arraycopy(originalBytes, index, reference, 0, 5);

                for (int j = 0; j < reference.length; j++) {
                    byteAdd(reference[j]);
                }
                index += 5;
            }
        }

        index--;
        return true;
    }

    public boolean changeString() {
        int length = ((originalBytes[index + 1] & 0xFF) << 8) | (originalBytes[index + 2] & 0xFF);
        if (length > 0xff || length < 0x00) return false;

        byte[] originalValue = new byte[length];
        System.arraycopy(originalBytes, index + 3, originalValue, 0, length);

        if (!isByteVisible(originalValue)) return false;

        index += 3 + length;
        encode(originalValue, type);
        index--;

        return true;
    }

    public void encode(byte[] originalValue, int type) {
        if (type == 3) {
            // 3 byte format: 1110xxxx 10xxxxxx 10xxxxxx
            int newLength = originalValue.length * 3;

            byteAdd((byte) ((newLength >> 8) & 0xFF));
            byteAdd((byte) (newLength & 0xFF));

            for (int i = 0; i < originalValue.length; i++) {
                char c = (char) originalValue[i];
                byteAdd((byte) (0xE0 | ((c >> 12) & 0x0F)));
                byteAdd((byte) (0x80 | ((c >> 6) & 0x3F)));
                byteAdd((byte) (0x80 | ((c >> 0) & 0x3F)));
            }
        } else {
            // 2 byte format: 110xxxxx 10xxxxxx
            int newLength = originalValue.length * 2;

            byteAdd((byte) ((newLength >> 8) & 0xFF));
            byteAdd((byte) (newLength & 0xFF));

            for (int i = 0; i < originalValue.length; i++) {
                char c = (char) originalValue[i];
                byteAdd((byte) (0xC0 | ((c >> 6) & 0x1F)));
                byteAdd((byte) (0x80 | ((c >> 0) & 0x3F)));
            }
        }
    }

    public boolean isByteVisible(byte[] bytes) {
        for (byte b : bytes) {
            if (b < 32 || b > 126) {
                return false;
            }
        }
        return true;
    }

    public void byteAdd(byte b) {
        byte[] newBytes = new byte[resultBytes.length + 1];
        System.arraycopy(resultBytes, 0, newBytes, 0, resultBytes.length);
        newBytes[resultBytes.length] = b;
        resultBytes = newBytes;
    }
}
