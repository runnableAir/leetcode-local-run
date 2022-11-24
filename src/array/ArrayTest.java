package array;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class ArrayTest {

    @Test
    public void testNormalArray00() {
        String arrayString = "[ 123, 45, 6 ]";
        System.out.println("arrayString = \n" + arrayString);
        Array array = new Array(arrayString);
        for (int i = 0; i < array.length; i++) {
            System.out.printf("array.get(%d) = %s\n", i, array.get(i));
        }
        List<String> excepted = Arrays.asList("123", "45", "6");
        for (int i = 0; i < array.length; i++) {
            String actual = array.get(i).toString();
            Assert.assertEquals(String.format("array.get(%d) != %s ", i, actual), excepted.get(i), actual);
        }
    }

    /**
     * 测试对包含换行、缩进的一维数组文本解析是否正确
     */
    @Test
    public void testNormalArray01() {
        String arrayString = "[\n\t1, \n\n\t2, \n\t3\n]";
        System.out.println("arrayString = \n" + arrayString);
        Array array = new Array(arrayString);
        for (int i = 0; i < array.length; i++) {
            System.out.printf("array.get(%d) = %s\n", i, array.get(i));
        }
        List<String> excepted = Arrays.asList("1", "2", "3");
        for (int i = 0; i < array.length; i++) {
            String actual = array.get(i).toString();
            Assert.assertEquals(String.format("array.get(%d) != %s ", i, actual), excepted.get(i), actual);
        }
    }

    /**
     * 测试对包含换行、缩进的二维数组文本解析是否正确
     */
    @Test
    public void test2dArray() {
        String arrayString = "[\n\t[1, 2, 3], \n\t[4, 5, 6], \n\t\"demo air\"\n]";
        System.out.println("arrayString = \n" + arrayString);
        Array array = new Array(arrayString);
        for (int i = 0; i < array.length; i++) {
            System.out.printf("array.get(%d) = %s\n", i, array.get(i));
        }
        List<String> excepted = Arrays.asList("[1, 2, 3]", "[4, 5, 6]", "\"demo air\"");
        for (int i = 0; i < array.length; i++) {
            String actual = array.get(i).toString();
            Assert.assertEquals(String.format("array.get(%d) != %s ", i, actual), excepted.get(i), actual);
        }
    }
}
