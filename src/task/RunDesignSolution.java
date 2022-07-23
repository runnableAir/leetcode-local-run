package task;

import array.Array;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.*;

/**
 * 运行leetcode的“实现类"题目。
 * <p>
 * 运行方式：输入一系列方法名称表示调用顺序、结合给定的参数进行测试。
 * 每组测试共两行输入：
 * <li>1.调用顺序：     {@code ["A", "B", "C", ..., "X"]}</li>
 * <li>2.相应的参数顺序：{@code [[p1, p2, p3], [p4], [p5, p6], ..., []]}</li>
 * </p>
 * <p>
 * 调用方式：
 * 通过 {@link #setIn(InputStream)} 从输入流中读取程序的输入内容，
 * 再通过 {@link  #run(Class)} 方法运行程序；
 * </p>
 * 在执行之前，可以通过 {@link #setOutPrefer(int)} 设置输出的内容格式，传入的参数有两种，分别是
 * {@link #LINE_BY_LINE} 和 {@link #ARRAY_LIKED}
 * 默认情况下是以”数组“的形式展示（即和leetcode一致）
 */
public class RunDesignSolution {

    /** 输出模式1：段落型，即每行一个输出结果 */
    public static final int LINE_BY_LINE = 0;

    /** 输出模式2：数组型，以数组形式展示 */
    public static final int ARRAY_LIKED = 1;

    /** 当前输出模式，默认为{@link #LINE_BY_LINE} */
    private int outputMode = LINE_BY_LINE;

    /** 当前设置的多组样例，每组样例有两个 {@link Array} 对象，分别存储调用方法的顺序和参数顺序 */
    private final List<List<Array>> inputs;

    /** 输出流缓存，保存程序输出内容 */
    private final ByteArrayOutputStream bos = new ByteArrayOutputStream();

    /** 重定向程序的输出流 */
    private final PrintStream out = new PrintStream(bos, true);

    private final Map<String, RunSolution> map = new HashMap<>();

    /* 私有化 */
    private RunDesignSolution(List<List<Array>> inputs) {
        this.inputs = inputs;
    }

    /**
     * 从指定输入流中读取程序的方法调用序列和传入的参数序列，并构建 {@link RunDesignSolution} 对象
     *
     * @param in 输入流
     * @return 包含特定方法调用序列和参数序列的 {@link RunDesignSolution} 对象
     * @throws IOException 读取异常
     */
    public static RunDesignSolution setIn(InputStream in) throws IOException {
        List<List<Array>> inputs = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line;
        while ((line = reader.readLine()) != null) {
            Array methods = new Array(line);
            line = reader.readLine();
            Array parameters = new Array(line);
            inputs.add(Arrays.asList(methods, parameters));
        }
        return new RunDesignSolution(inputs);
    }

    /**
     * 设置的输出方式：{@link #LINE_BY_LINE} 或 {@link #ARRAY_LIKED}
     *
     * @param mode {@link #LINE_BY_LINE} 或 {@link #ARRAY_LIKED}
     * @return 当前 {@link RunDesignSolution} 对象
     */
    public RunDesignSolution setOutPrefer(int mode) {
        outputMode = mode;
        return this;
    }

    /**
     * 从指定的 {@link Class} 构建程序运行对象，并按当前设置的调用方法序列和参数序列运行
     *
     * @param target 目标源 Class 对象
     * @throws InvocationTargetException 调用异常，通常为程序代码错误导致
     * @throws NoSuchMethodException 调用方法不存在，可能由于方法名称错误或参数列表不一致
     * @throws InstantiationException 实例化异常，可能由于找不到构造方法，请检查参数列表是否一致
     * @throws IllegalAccessException 访问异常，可能由于修饰符不是public导致的
     */
    public void run(Class<?> target) throws InvocationTargetException, NoSuchMethodException, InstantiationException,
            IllegalAccessException {
        for (List<Array> in : inputs) {
            // 调用方法的顺序
            Array totalInvoke = in.get(0);
            // 方法所需的参数
            Array invokeParameters = in.get(1);
            // 第一个调用的是构造方法，则根据相应的参数，初始化类对象
            List<String> initialParameters = invokeParameters.get(0).asArray().convertToStringList();
            Object obj = newInstance(target, initialParameters);
            out.println("null"); // 构造方法没有返回值
            // 处理后续的调用方法
            for (int i = 1; i < invokeParameters.length; ++i) {
                String methodName = totalInvoke.get(i).asString();
                Array parameters = invokeParameters.get(i).asArray();
                invoke(obj, methodName, parameters);
            }
            output();
        }
    }

    /**
     * 在指定的对象中调用指定方法
     *
     * @param obj 指定的对象
     * @param methodName 调用方法的名称
     * @param parameters 参数列表，由 {@link Array} 对象存储
     * @throws InvocationTargetException 调用异常，通常为程序代码错误导致
     * @throws IllegalAccessException 访问异常，可能由于修饰符不是public导致的
     * @throws NoSuchMethodException 指定方法不存在
     */
    private void invoke(Object obj, String methodName, Array parameters) throws InvocationTargetException,
            IllegalAccessException, NoSuchMethodException {
        // convert
        List<String> inputLines = parameters.convertToStringList();
        RunSolution runSolution;
        if (map.containsKey(methodName)) {
            runSolution = map.get(methodName);
        } else {
            runSolution = RunSolution.setTarget(methodName, obj).setOut(out);
            map.put(methodName, runSolution);
        }
        runSolution.run(inputLines);
    }

    /**
     * 从指定的 {@link Class} 实例化对象。该方法认为该对象只有一个有参构造方法。
     *
     * @param srcClass 指定的 Class 对象
     * @param parameters 用于提供构造方法参数的数组 {@link Array}，具体元素的值取决于构造方法的参数列表
     * @return 指定 Class 对象的实例
     * @throws InvocationTargetException 构造方法不存在
     * @throws InstantiationException 实例创建失败
     * @throws IllegalAccessException 非法访问
     */
    private Object newInstance(Class<?> srcClass, List<String> parameters) throws InvocationTargetException,
            InstantiationException, IllegalAccessException {
        // get
        Constructor<?>[] constructors = srcClass.getDeclaredConstructors();
        Constructor<?> con = constructors[0];
        con.setAccessible(true);
        // call
        Type[] types = con.getGenericParameterTypes();
        Object[] args = RunSolution.stringsToParameters(parameters, types);
        return con.newInstance(args);
    }


    /**
     * @return 返回当前缓存的程序输出结果，文本格式由 {@link #outputMode} 决定
     */
    private String getResult() {
        if (outputMode == ARRAY_LIKED) {
            String s = bos.toString();
            StringJoiner joiner = new StringJoiner(",", "[", "]");
            for (String item : s.split(System.lineSeparator())) {
                joiner.add(item);
            }
            return joiner.toString();
        }
        return bos.toString();
    }

    /**
     * 输出缓存的内容，并清空缓存
     */
    private void output() {
        System.out.println(getResult());
        bos.reset();
    }

}
