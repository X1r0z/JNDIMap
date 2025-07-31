package map.jndi.util;

import map.jndi.Main;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class MiscUtil {
    private static final Map<String, Set<String>> fileToClassNames = new HashMap<>();

    static {
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            URL resource = classLoader.getResource("classNames");
            Path dirPath = Paths.get(resource.toURI());

            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath, "*.txt")) {
                for (Path filePath : stream) {
                    String fileName = filePath.getFileName().toString();
                    String key = fileName.substring(0, fileName.lastIndexOf('.'));

                    try (InputStream is = classLoader.getResourceAsStream("classNames/" + fileName);
                         BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

                        Set<String> classNames = reader.lines()
                                .map(String::trim)
                                .filter(line -> !line.isEmpty())
                                .collect(Collectors.toSet());

                        fileToClassNames.put(key, classNames);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getClassName() {
        if (Main.config.fakeClassName) {
            return getRandClassName();
        } else {
            return getRandStr(8);
        }
    }

    public static String getRandStr(int length) {
        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String dicts = upper + lower + digits;

        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        sb.append(upper.charAt(random.nextInt(upper.length())));

        for (int i = 0; i < length - 1; i ++) {
            int index = random.nextInt(dicts.length());
            sb.append(dicts.charAt(index));
        }

        return sb.toString();
    }

    public static String getRandClassName() {
        while (!fileToClassNames.isEmpty()) {
            Random random = new Random();

            List<String> keys = new ArrayList<>(fileToClassNames.keySet());
            String randomKey = keys.get(random.nextInt(keys.size()));
            Set<String> classNames = fileToClassNames.get(randomKey);

            if (classNames == null || classNames.isEmpty()) {
                fileToClassNames.remove(randomKey);
                continue;
            }

            List<String> classNameList = new ArrayList<>(classNames);
            String selectedClassName = classNameList.get(random.nextInt(classNameList.size()));
            classNames.remove(selectedClassName);

            if (classNames.isEmpty()) {
                fileToClassNames.remove(randomKey);
            }

            return selectedClassName;
        }

        return null;
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

    public static String encodeUnicode(String str) {
        StringBuilder sb = new StringBuilder();
        for (char c : str.toCharArray()) {
            sb.append("\\u").append(String.format("%04X", (int) c));
        }
        return sb.toString();
    }
}
