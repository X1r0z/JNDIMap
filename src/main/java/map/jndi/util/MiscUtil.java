package map.jndi.util;

import java.util.Arrays;
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

    public static String tryBase64UrlDecode(String encText) {
        try {
            // 判断字符串是否使用 Base64 URL 编码
            // 方法: 先 decode 再重新 encodeToString, 判断两者是否相等
            byte[] decBytes = Base64.getUrlDecoder().decode(encText);
            String reEncText = Base64.getUrlEncoder().encodeToString(decBytes);

            if (reEncText.equals(encText)) {
                // Base64 URL 编码

                // 判断 Base64 URL 解码结果是否属于纯文本内容
                // 方法: 先将 byte[] 转成 String (标准化), 然后 getBytes 重新获取 byte[], 判断两者是否相等
                String decText = new String(decBytes);
                byte[] reDecBytes = decText.getBytes();

                if (Arrays.equals(reDecBytes, decBytes)) {
                    // 纯文本内容
                    return decText;
                } else {
                    // 非纯文本内容
                    return encText;
                }
            } else {
                // 非 Base64 URL 编码
                return encText;
            }
        } catch (Exception e) {
            // 非 Base64 URL 编码
            return encText;
        }
    }
}
