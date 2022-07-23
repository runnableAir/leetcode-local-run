package array;

import java.util.regex.Pattern;

public class ArrayItem {
    private final String src;
    private String asString;

    public ArrayItem(String src) {
        this.src = src.trim();
    }

    public int asInt() {
        return Integer.parseInt(asString());
    }

    @SuppressWarnings("unused")
    public long asLong() {
        return Long.parseLong(asString());
    }

    @SuppressWarnings("unused")
    public double asDouble() {
        return Double.parseDouble(asString());
    }

    public String asString() {
        if (asString != null) {
            return asString;
        }
        int l = 0;
        int r = src.length();
        if (src.indexOf('"') == 0) {
            ++l;
            r = src.indexOf('"', l);
            if (r != src.length() - 1) {
                throw new ParsingFormattedException("\"不匹配：" + src + "，位置：" + r);
            }
        }
        asString = src.substring(l, r);
        return asString;
    }

    public Array asArray() {
        if (!isArray()) {
            return new Array("[" + src + "]");
        }
        return new Array(src);
    }

    public boolean isInteger() {
        Pattern pattern = Pattern.compile("^-?[1-9]\\d*$");
        return pattern.matcher(asString).find();
    }

    public boolean isArray() {
        return src.charAt(0) == '[' && src.charAt(src.length() - 1) == ']';
    }

    public String getSrc() {
        return src;
    }

    @Override
    public String toString() {
        return src;
    }
}
