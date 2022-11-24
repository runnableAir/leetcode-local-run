package array;

import java.util.ArrayList;
import java.util.List;

public class Array {
    private final List<ArrayItem> items;
    public int length;

    public Array(String str) {
        String src = str.trim();
        items = new ArrayList<>();
        check(src);
        parse(src);
    }

    /* 对传入的参数进行检查 */
    private static void check(String str) {
        // 检查两侧是否为[]
        if (str.charAt(0) != '[' || str.charAt(str.length() - 1) != ']') {
            throw new ParsingFormattedException("无效的array");
        }
        // 对'[' 计数
        int leftBraceCnt = 0;
        int begin = 0;
        for (int i = 0; i < str.length(); ++i) {
            char c = str.charAt(i);
            if (c == '[') {
                if (leftBraceCnt == 1) begin = i;
                ++leftBraceCnt;
            } else if (c == ']') {
                --leftBraceCnt;
                if (leftBraceCnt < 0) {
                    throw new ParsingFormattedException(info(str, i, ']'));
                }
            }
        }
        if (leftBraceCnt > 0) {
            throw new ParsingFormattedException(info(str, begin, '['));
        }
    }

    private static String info(String s, int pos, char c) {
        StringBuilder sb = new StringBuilder();
        sb.append(System.lineSeparator()).append(s).append(System.lineSeparator());
        for (int k = 0; k < pos; ++k) {
            sb.append(' ');
        }
        sb.append('^')
                .append(System.lineSeparator())
                .append("多余的" + "'")
                .append(c).append("'")
                .append(", 位置: ").append(pos);
        return sb.toString();
    }

    private void parse(String str) {
        // 去除换行和缩进，避免解析失误
        str = str.replaceAll("[\t\n]", "");
        int idx = 0;
        int n = str.length();
        while (++idx < n) {
            // 跳过元素之间的空格
            while (idx < n && str.charAt(idx) == ' ') ++idx;
            int begin = idx;
            if (str.charAt(idx) == '[') {
                int cnt = 1;
                while (++idx < n && cnt > 0) {
                    char c = str.charAt(idx);
                    if (c == ']') --cnt;
                    else if (c == '[') ++cnt;
                }
            } else {
                // 匹配元素，元素之间由 ',' 隔开
                // 忽略最后的 ']'
                while (idx < n - 1 && str.charAt(idx) != ',') ++idx;
            }
            if (begin < idx) {
                // 确保存在元素
                items.add(new ArrayItem(str.substring(begin, idx)));
            }
        }
        length = items.size();
    }

    public ArrayItem get(int index) {
        return items.get(index);
    }

    /**
     * @return 以 {@code List} 形式返回当前 {@code Array} 对象存储的一系列元素. 元素的值是它一开始被解析时的文本
     */
    public List<String> convertToStringList() {
        List<String> ret = new ArrayList<>();
        for (int i = 0; i < length; ++i) {
            ret.add(items.get(i).getSrc());
        }
        return ret;
    }

    @Override
    public String toString() {
        return "Array {\n" +
                "\titems=" + items +
                ",\n\tlength=" + length +
                "\n}";
    }
}
