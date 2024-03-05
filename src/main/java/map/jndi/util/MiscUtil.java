package map.jndi.util;

import java.util.Base64;
import java.util.Random;

public class MiscUtil {
    public static String getRandStr(int length){
        String dicts = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i ++) {
            int index = random.nextInt(dicts.length());
            sb.append(dicts.charAt(index));
        }

        return "Exploit_" + sb;
    }

    public static String tryBase64UrlDecode(String s) {
        try {
            String plainText = new String(Base64.getUrlDecoder().decode(s));
            String encText = Base64.getUrlEncoder().encodeToString(plainText.getBytes());

            if (encText.equals(s)) {
                return plainText;
            } else {
                return s;
            }
        } catch (Exception e) {
            return s;
        }
    }
}
