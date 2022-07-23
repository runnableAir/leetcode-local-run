package input;

import input.tree.binary.TreeNode;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Air
 */
public class TestUtil {
    public static final String NORMAL_PARSE_INT_ONE_DIMENSION_ARR = "\\[((\\d+)*(,\\d+)*)\\]";


    /**
     * 像python一样对字符串切片，返回获得的子串
     *
     * @param s     a target string
     * @param start index of begin(inclusive)
     * @param end   index of end(exclusive)
     * @return a string by sliced from start to the end(exclusive)
     */
    public static String slice(String s, int start, int end) {
        return s.substring(start, end < 0 ? s.length() + end : end);
    }


    /**
     * 根据给定的下标（从0开始，负数表示是倒数）将字符串截取后分割，返回分割后的字符串数组
     *
     * @param s     要操作的字符串
     * @param start 截取的起始下标，从0开始，inclusive（包含当前字符）
     * @param end   截取的截止下标，exclusive（不包含当前字符）
     * @param regex 匹配分割字符串的正则表达式
     * @return a array contains string from splitting
     */
    public static String[] split(String s, int start, int end, String regex) {
        return slice(s, start, end).split(regex);
    }


    /**
     * 解析字符串得到java的int数组对象
     *
     * @param arrString 一个字符串
     * @return int数组
     */
    public static int[] stringToIntegerArray(String arrString) {
        return stringToIntegerArray(arrString, 1, -1);
    }


    /**
     * 通过解析字符串的【某一个子串】得到java的int数组对象
     *
     * @param arrString 一个字符串
     * @param begin 子串起始下标
     * @param end 子串结束下标 （不包括结束下标字符）
     * @return int数组
     */
    public static int[] stringToIntegerArray(String arrString, int begin, int end) {
        if (arrString.isEmpty()) {
            return new int[0];
        }
        arrString = arrString.trim();
        String[] spilt = split(arrString, begin, end, ",\\s{0,}");
        int n = spilt.length;
        int[] res = new int[n];
        for (int i = 0; i < n; ++i) {
            res[i] = Integer.parseInt(spilt[i]);
        }
        return res;
    }


    /**
     * 通过解析字符串得到java的int【二维】数组对象
     *
     * @param arr2String 一个表示二维数组的字符串
     * @return int数组
     */
    public static int[][] stringToInteger2dArray(String arr2String) {
        arr2String = arr2String.trim();
        if (arr2String.equals("[]")) {
            return new int[0][];
        }
        String[] arrStrings = split(arr2String, 2, -2, "\\],\\s{0,}\\[");
        int n = arrStrings.length;
        int[][] res = new int[n][];
        for (int i = 0; i < n; ++i) {
            res[i] = stringToIntegerArray(arrStrings[i], 0, arrStrings[i].length());
        }
        return res;
    }

    public static List<Integer> stringToIntegerArrayList(String s) {
        int[] array = stringToIntegerArray(s);
        List<Integer> list = new ArrayList<>();
        for (int i : array) {
            list.add(i);
        }
        return list;
    }

    public static List<List<Integer>> stringToInt2dList(String s) {
        int[][] arrays = stringToInteger2dArray(s);
        if (arrays.length == 0) {
            return new ArrayList<>();
        }
        List<List<Integer>> list = new ArrayList<>();
        for (int[] array : arrays) {
            List<Integer> ll = new ArrayList<>();
            for (int i : array) {
                ll.add(i);
            }
            list.add(ll);
        }
        return list;
    }

    public static List<String> stringToStringArrayList(String s) {
        s = s.trim();
        if (s.length() == 0) {
            return new ArrayList<>();
        }
        String[] array = slice(s, 2, -2).split("\",\\s*\"");
        return new ArrayList<>(Arrays.asList(array));
    }

    /**
     * 将 {@linkplain List} 转为字符串
     *
     * @param list 指定的 {@linkplain List} 对象
     * @return 字符串
     */
    public static String listToString(List<?> list) {
        StringJoiner joiner = new StringJoiner(",", "[", "]");
        for (Object o : list) {
            joiner.add(String.valueOf(o));
        }
        return joiner.toString();
    }

    public static String integerArrayToString(int[] nums, int length) {
        if (length == 0) {
            return "[]";
        }

        StringBuilder result = new StringBuilder();
        for(int index = 0; index < length; index++) {
            int number = nums[index];
            result.append(number).append(",");
        }
        return "[" + result.substring(0, result.length() - 1) + "]";
    }

    public static String integerArrayToString(int[] nums) {
        return integerArrayToString(nums, nums.length);
    }

    public static String integer2dArrayToString(int[][] nums) {
        if (nums.length == 0) {
            return "[]";
        }
        StringBuilder result = new StringBuilder();
        for (int[] num : nums) {
            String array = integerArrayToString(num);
            result.append(array).append(",");
        }
        return "[" + result.substring(0, result.length() - 1) + "]";
    }

    public static String stringArrayToString(String[] stringArr) {
        if (stringArr.length == 0) {
            return "[]";
        }
        StringBuilder result = new StringBuilder();
        for (String s : stringArr) {
            result.append(s).append(",");
        }
        return "[" + result.substring(0, result.length() - 1) + "]";
    }

    public static String stringToString(String input) {
        input = input.trim();
        if (input.length() == 0) {
            return "";
        }
        return input.charAt(0) == '"' ? slice(input, 1, -1) : input;
    }

    public static int stringToInteger(String input) {
        input = input.trim();
        if (input.length() == 0) {
            throw new IllegalArgumentException("number字符串为空");
        }
        return Integer.parseInt(input);
    }

    public static TreeNode stringToTreeNode(String input) {
        input = input.trim();
        if (input.charAt(0) == '[' && input.charAt(input.length() - 1) == ']') {
            input = input.substring(1, input.length() - 1);
        }
        if (input.length() == 0) {
            return null;
        }

        String[] parts = input.split(",");
        String item = parts[0];
        TreeNode root = new TreeNode(Integer.parseInt(item));
        Queue<TreeNode> nodeQueue = new LinkedList<>();
        nodeQueue.add(root);

        int index = 1;
        while(!nodeQueue.isEmpty()) {
            TreeNode node = nodeQueue.remove();

            if (index == parts.length) {
                break;
            }

            item = parts[index++];
            item = item.trim();
            if (!item.equals("null")) {
                int leftNumber = Integer.parseInt(item);
                node.left = new TreeNode(leftNumber);
                nodeQueue.add(node.left);
            }

            if (index == parts.length) {
                break;
            }

            item = parts[index++];
            item = item.trim();
            if (!item.equals("null")) {
                int rightNumber = Integer.parseInt(item);
                node.right = new TreeNode(rightNumber);
                nodeQueue.add(node.right);
            }
        }
        return root;
    }

    public static String treeNodeToString(TreeNode root) {
        if (root == null) {
            return "[]";
        }

        StringBuilder output = new StringBuilder();
        Queue<TreeNode> nodeQueue = new LinkedList<>();
        nodeQueue.add(root);
        while(!nodeQueue.isEmpty()) {
            TreeNode node = nodeQueue.remove();

            if (node == null) {
                output.append("null, ");
                continue;
            }

            output.append(node.val).append(", ");
            nodeQueue.add(node.left);
            nodeQueue.add(node.right);
        }
        return "[" + output.substring(0, output.length() - 2) + "]";
    }

    // TODO 重构下面的工具方法，整理注释文档


    /**
     * return a char[] from a string parsed design-solution.in a specific Regex string
     *
     * @param arrString a string which means an array
     * @param regex      a specific Regx string to be used to parse strings
     * @return an char[] corresponding the result
     */
    public static char[] getCharArr(String arrString, String regex) {
        Pattern pat = Pattern.compile(regex);
        Matcher matcher = pat.matcher(arrString);
        char[] res = null;
        while (matcher.find()) {
            String[] split = matcher.group(1).split(",");
            res = new char[split.length];
            for (int i = 0; i < split.length; i++) {
                res[i] = split[i].substring(1, split[i].length() - 1).toCharArray()[0];
            }
        }
        return res;
    }

    public static String[] getStringArr(String arrString) {
        return split(arrString, 2, -2, "\",\"");
    }

    public static char[] getCharArr(String arrString, int start, int end) {
        String[] arr = split(arrString, start, end, "\",\"");
        char[] res = new char[arr.length];
        for (int i = 0; i < arr.length; i++) {
            res[i] = arr[i].toCharArray()[0];
        }
        return res;
    }

    public static char[][] getCharArr2(String arrString) {
        String[] split = split(arrString, 2, -2, "\\],\\[");
        char[][] res = new char[split.length][];
        for (int i = 0; i < split.length; i++) {
            res[i] = getCharArr(split[i], 1, -1);
        }
        return res;
    }



    /**
     * return an String[] from a string parsed design-solution.in a specific Regx string
     *
     * @param arrString a string which means an array
     * @param regx      a specific Regx string to be used to parse strings
     * @return an String[] corresponding the result
     */
    public static String[] getStringArr(String arrString, String regx) {
        Pattern pat = Pattern.compile(regx);
        Matcher matcher = pat.matcher(arrString);
        if (matcher.find()) {
            String group = matcher.group(1);
            return group.substring(1, group.length() - 1).split("\",\\s*?\"");
        }
        return new String[0];
    }



    /**
     * return an int[][] from a string parsed design-solution.in a specific Regx string
     *
     * @param arr2String a string which means an array
     * @param regx      a specific Regx string to be used to parse strings
     * @return an int[][] corresponding the result
     */
    public static int[][] stringToInteger2dArray(String arr2String, String regx) {
        int n = getArr2Size(arr2String);
        return stringToInteger2dArray(arr2String, n, regx);
    }

    /**
     * return an int[][] from a string parsed design-solution.in a specific Regx string
     *
     * @param arrString a string which means an array
     * @param n         a integer value to decide the length of a array int[][]
     * @return an int[][] corresponding the result
     */
    public static int[][] stringToInteger2dArray(String arrString, int n) {
        return stringToInteger2dArray(arrString, n, NORMAL_PARSE_INT_ONE_DIMENSION_ARR);
    }

    /**
     * return an int[][] , which the length is determined as a Integer value<code>n</code>,
     * from a string parsed design-solution.in a specific Regx string
     *
     * @param arrString a string which means an array
     * @param regx      a specific Regx string to be used to parse strings
     * @param n         a integer value to decide the length of a array int[][]
     * @return an int[][] corresponding the result
     */
    public static int[][] stringToInteger2dArray(String arrString, int n, String regx) {
        int[][] res = new int[n][];
        Pattern pat = Pattern.compile(regx);
        Matcher matcher = pat.matcher(arrString);
        int i = 0;
        while (matcher.find()) {
            String g = matcher.group(1);
            String[] split = g.split(",");
            int len = split.length;
            res[i] = new int[len];
            for (int j = 0; j < split.length; j++) {
                if (!split[j].isEmpty()) {
                    res[i][j] = Integer.parseInt(split[j]);
                }
            }
            i++;
        }
        return res;
    }

    /**
     * guess it how long, just it.
     *
     * @param arrString a string means a array
     * @return a integer value of the length
     */
    private static int getArr2Size(String arrString) {
        return arrString.split("(])(,)(\\[)").length;
    }


}
