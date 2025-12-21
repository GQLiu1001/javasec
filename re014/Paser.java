package re014;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Paser {
    /**
     * 字节数组转Integer（默认大端序）
     * 
     * @param bytes 4字节的字节数组
     * @return 解析后的Integer
     * @throws IllegalArgumentException 字节数组长度非4时抛出
     */
    public Integer parseBytesToInt(byte[] bytes) {
        return parseBytesToInt(bytes, ByteOrder.BIG_ENDIAN);
    }

    /**
     * 字节数组转Integer（指定字节序）
     * 
     * @param bytes 4字节的字节数组
     * @param order 字节序（BIG_ENDIAN/ LITTLE_ENDIAN）
     * @return 解析后的Integer
     */
    public Integer parseBytesToInt(byte[] bytes, ByteOrder order) {
        // 校验字节数组长度（int占4字节）
        if (bytes == null || bytes.length != 4) {
            throw new IllegalArgumentException("字节数组必须为4字节");
        }
        // 包装字节数组为ByteBuffer，指定字节序
        ByteBuffer buffer = ByteBuffer.wrap(bytes).order(order);
        // 转换为int
        return buffer.getInt();
    }

    // 正则模式：匹配正负整数（分组捕获数字部分）
    private static final Pattern INT_PATTERN = Pattern.compile("-?\\d+");

    /**
     * 提取字符串中的所有整数
     * 
     * @param s 混合字符串（如"abc123def-456g789"）
     * @return 提取的整数列表
     */
    public List<Integer> parseString(String s) {
        List<Integer> result = new ArrayList<>();
        if (s == null || s.isEmpty()) {
            return result;
        }

        Matcher matcher = INT_PATTERN.matcher(s);
        // 遍历所有匹配的结果
        while (matcher.find()) {
            String numStr = matcher.group(); // 获取匹配的字符串
            try {
                result.add(Integer.parseInt(numStr));
            } catch (NumberFormatException e) {
                // 超出int范围的数字跳过（可选）
                System.out.println("数字超出int范围：" + numStr);
            }
        }
        return result;
    }

    /**
     * 解析URL查询参数为Map
     * @param url 完整URL
     * @return 解析后的参数键值对
     */
    public Map<String, String> parseUrl(String url) {
        Map<String, String> paramMap = new HashMap<>();
        if (url == null || url.isEmpty()) {
            return paramMap;
        }

        // 1. 切割?，获取查询参数部分
        int queryStart = url.indexOf("?");
        if (queryStart == -1) {
            return paramMap; // 无查询参数
        }
        String queryStr = url.substring(queryStart + 1);
        if (queryStr.isEmpty()) {
            return paramMap;
        }

        // 2. 按&分割多个参数
        String[] paramArr = queryStr.split("&");
        for (String param : paramArr) {
            if (param.isEmpty()) {
                continue; // 跳过空参数（如&&）
            }
            // 3. 按=分割键值对（处理无值的情况，如a=&b=1）
            int equalIndex = param.indexOf("=");
            if (equalIndex == -1) {
                paramMap.put(param, ""); // 无值的参数，值为空字符串
            } else {
                String key = param.substring(0, equalIndex);
                String value = param.substring(equalIndex + 1);
                // 可选：URL解码（处理%20等转义字符）
                // value = java.net.URLDecoder.decode(value, StandardCharsets.UTF_8);
                paramMap.put(key, value);
            }
        }
        return paramMap;
    }
}
