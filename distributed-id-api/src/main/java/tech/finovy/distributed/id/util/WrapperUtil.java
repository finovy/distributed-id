package tech.finovy.distributed.id.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Ryan Luo
 * @Date: 2023/8/15 18:52
 */
public class WrapperUtil{
        public static List<String> wrapperList(List<Long> t, String prefix, Integer length) {
            List<String> ids = new ArrayList<>();
            t.forEach(v -> ids.add(join(v, prefix, length)));
            return ids;
        }

        public static String wrapper(Long id, String prefix, Integer length) {
            return join(id, prefix, length);
        }

        private static String join(Long id, String prefix, Integer length) {
            if (length != null && length > 0) {
                // 减去前缀长度
                int targetLength = length - prefix.length();
                final int diff = targetLength - (id + "").length();
                if (diff > 0) {
                    StringBuilder padded = new StringBuilder(targetLength);
                    while (padded.length() < diff) {
                        // 在字符串前面补充零
                        padded.insert(0, '0');
                    }
                    return prefix + padded + id;
                }
            }
            return prefix + id;
        }
}
