package task;

import input.TestUtil;
import input.tree.binary.TreeNode;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * RunSolution对象用于运行指定类的某个方法并获取运行的结果。
 * <p>
 * 它从控制台，或其他输入流中，读取测试样例 (方法运行需要的参数) 后，传递给运行的方法。
 * 在这个过程中会根据方法的参数列表，逐一解析、转化参数内容，得到对应类型的值。
 * <p>
 * 输入的参数是以字符串存储的，其文本必须是以”leetcode“风格进行书写。<br>
 * <ul>
 *     <li>单个参数占一行</li>
 *     <li>字符串要用""包裹</li>
 *     <li>数组、列表用"<code>[</code>"和"<code>]</code>"进行包裹，嵌套时注意括号成对出现</li>
 * </ul>
 * 该类可以说是专门为运行leetcode代码的工具类。<br>
 * 你需要先将leetcode代码 (当然了，只能是java语言的) 对应的类 (Solution) 编写在本地ide，然后使用.class获取到该类Class对象，
 * 接着通过<code>RunSolution.build()</code>来创建RunSolution对象，该方法需要接受两个参数: Class对象和需要运行的方法名称，
 * 然后调用RunSolution的 {@link #run()} 方法运行即可。<br>
 * 在调用之前，还可以调用 {@link #setIn(InputStream)} 设置读取的输入流。
 * <p>
 * 默认情况下，程序会不断地从控制台中读取参数，每次参数输入完毕，会自动运行一次方法，并输出结果到控制台中。
 * 如果设置了文件输入流，程序则在所有输入读取完毕后退出。
 */
public class RunSolution {

    /** 需要调用的方法 */
    private final Method method;

    /** 当前Solution的Class对象 */
    private final Class<?> solutionClass;

    /** Solution的实例 */
    private Object solution;

    /** 读取参数时的输入流 */
    private InputStream in = System.in;

    /** 输出运行结果的输出流 */
    private PrintStream out = System.out;

    /** 当前调用方法需要传入的参数个数，取决于当前设置的方法 */
    private final int paramCount;

    /** 设置是否所有运行都重复使用同一个实例 */
    private final boolean isRecycledUse;

    /** 存储从输入流读取的字符串 */
    private final List<String> inputLines = new ArrayList<>();

    /* 私有化构造方法 */
    private RunSolution(Method m, Object solution, boolean isRecycledUse) {
        this.method = m;
        this.isRecycledUse = isRecycledUse;
        this.solution = solution;
        this.solutionClass = solution.getClass();
        paramCount = method.getParameterCount();
        method.setAccessible(true);
    }

    /**
     * 根据指定类的Class对象，以及指定的方法名称，创建一个RunSolution实例。
     * <p>
     * 指定的Class对象实例在每次运行方法时都会重新创建。
     *
     * @param methodName 指定的方法名称
     * @param solutionClass 指定类的Class对象
     * @return <code>RunSolution</code> 实例
     *
     * @throws NoSuchMethodException 方法找不到，或许是名称输入错误了
     */
    public static RunSolution build(String methodName, Class<?> solutionClass)
            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Constructor<?> constructor = solutionClass.getDeclaredConstructor();
        constructor.setAccessible(true);
        Object o = constructor.newInstance();
        return setTarget(methodName, o, false);
    }

    /**
     * 根据指定类的实例，以及指定的方法名称，创建一个RunSolution实例。
     * <p>
     * 如果参数 <code>isRecycledUse</code> 是 false，则多次方法运行时使用的都是新的对象，否则都是同一个对象。
     *
     * @param methodName 指定的方法名称
     * @param solution 指定类的实例
     * @param isRecycledUse 是否重复使用实例对象
     * @return <code>RunSolution</code> Object
     * @throws NoSuchMethodException 方法找不到，或许是名称输入错误了
     */
    public static RunSolution setTarget(String methodName, Object solution, boolean isRecycledUse)
            throws NoSuchMethodException {
        Class<?> srcClass = solution.getClass();
        Method method = getMethod(srcClass, methodName);
        if (method == null) {
            throw new NoSuchMethodException("can not find method named: " + methodName);
        }
        return new RunSolution(method, solution, isRecycledUse);
    }

    /**
     * 根据指定类的实例，以及指定的方法名称，创建一个RunSolution实例。
     * <p>
     * 指定类的实例对象在每次运行方法时都会重新创建。
     *
     * @param methodName 需要指定一个方法名称
     * @param solution Solution的实例
     * @return <code>RunSolution</code> Object
     * @throws NoSuchMethodException 方法找不到，或许是名称输入错误了
     */
    public static RunSolution setTarget(String methodName, Object solution) throws NoSuchMethodException {
        return setTarget(methodName, solution, true);
    }

    /**
     * 设置输入流，从中读取参数
     *
     * @param in 输入流
     * @return 当前对象
     */
    public RunSolution setIn(InputStream in) {
        this.in = in;
        return this;
    }

    /**
     * 设置输出流，运行结果输出到该流中
     * @param out 输出流
     * @return 当前对象
     */
    public RunSolution setOut(PrintStream out) {
        this.out = out;
        return this;
    }

    /**
     * run!!
     * <p>
     * 该方法调用后，会不断从输入流 (默认在控制台) 中读取参数，读取的参数顺序要与方法参数列表中的参数顺序一致，
     * 每读取足够的参数后就运行一次，并将结果输出到的输出流中 (默认在控制台)。
     *
     * @throws IOException 读取发生异常，检查input文件是否按参数列表顺序进行输入
     * @throws InvocationTargetException 调用方法时产生的异常
     * @throws IllegalAccessException 方法访问异常
     */
    public void run() throws Exception {
        BufferedReader in = new BufferedReader(new InputStreamReader(this.in));
        while (collectInput(in) >= paramCount) {
            run(inputLines);
            if (!isRecycledUse) {
                Constructor<?> constructor = solutionClass.getDeclaredConstructor();
                constructor.setAccessible(true);
                solution = constructor.newInstance();
            }
        }
        in.close();
    }

    /**
     * 使用指定的参数来运行，而不是从输入流中获取。
     *
     * @param inputLines 包含所有所需参数的list，参数出现的顺序要与方法参数列表中的参数顺序对应
     * @throws InvocationTargetException 调用方法时产生的异常
     * @throws IllegalAccessException 方法访问异常
     */
    protected void run(List<String> inputLines)
            throws InvocationTargetException, IllegalAccessException {
        // get parameters including generic type
        Type[] types = method.getGenericParameterTypes();
        Object[] args = stringsToParameters(inputLines, types);
        output(method.invoke(solution, args));
    }

    private void output(Object o) {
        out.println(toReadable(o));
    }

    /**
     * 将方法返回的值转为”可读“的字符串，用于打印输出。
     * 主要避免toString()返回对象地址，无法阅读。
     *
     * @param o 传入的值
     * @return 转化后得到的字符串
     */
    protected static String toReadable(Object o) {
        if (o == null) {
            return "null";
        }
        // 打印数组
        if (o instanceof int[]) {
            return TestUtil.integerArrayToString((int[]) o);
        } else if (o instanceof int[][]) {
            return TestUtil.integer2dArrayToString((int[][]) o);
        } else if (o instanceof String[]) {
            return TestUtil.stringArrayToString((String[]) o);
        } else if (o instanceof List) {
            return TestUtil.listToString((List<?>) o);
        } else if (o instanceof TreeNode) {
            return TestUtil.treeNodeToString((TreeNode) o);
        }
        return String.valueOf(o);
    }

    protected static Object[] stringsToParameters(List<String> items, Type[] paramTypes) {
        int n = paramTypes.length;
        Object[] args = new Object[n];
        for (int i = 0; i < n; ++i) {
            args[i] = parse(paramTypes[i].getTypeName(), items.get(i));
        }
        return args;
    }

    /**
     * 将当前读取的行转为指定类型的参数
     *
     * @param typeName 类型名称
     * @param line 行
     * @return 返回需要的参数
     */
    protected static Object parse(String typeName, String line) {
        Object ret = null;
        switch (typeName) {
            case "int":
                ret = TestUtil.stringToInteger(line);
                break;
            case "int[]":
                ret = TestUtil.stringToIntegerArray(line);
                break;
            case "int[][]":
                ret = TestUtil.stringToInteger2dArray(line);
                break;
            case "java.util.List<java.lang.Integer>":
                ret = TestUtil.stringToIntegerArrayList(line);
                break;
            case "java.util.List<java.util.List<java.lang.Integer>>":
                ret = TestUtil.stringToInt2dList(line);
                break;
            case "java.lang.String":
                ret = TestUtil.stringToString(line);
                break;
            case "java.lang.String[]":
                ret = TestUtil.getStringArr(line);
                break;
            case "java.util.List<java.lang.String>":
                ret = TestUtil.stringToStringArrayList(line);
                break;
            case "input.tree.binary.TreeNode":
                ret = TestUtil.stringToTreeNode(line);
                break;
        }
        return ret;
    }

    /**
     * 通过反射， 找到指定名称的方法对象
     * 当该方法没有被重载时，才可能得到想要的结果...
     *
     * @param srcClass 反射来源
     * @param name 方法名称
     * @return 返回的方法，如果没有，则返回null
     */
    private static Method getMethod(Class<?> srcClass, String name) {
        Method[] declaredMethods = srcClass.getDeclaredMethods();
        for (Method method : declaredMethods) {
            if (name.equals(method.getName())) {
                return method;
            }
        }
        return null;
    }

    private int collectInput(BufferedReader in) throws IOException {
        inputLines.clear();
        String line;
        // 读取足够的参数后停止，或者EOF异常
        while (inputLines.size() < paramCount && (line = in.readLine()) != null) {
            if (line.isEmpty()) {
                continue;
            }
            inputLines.add(line);
        }
        return inputLines.size();
    }

}
