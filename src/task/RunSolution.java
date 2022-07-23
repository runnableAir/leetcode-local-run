package task;

import input.TestUtil;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 运行leetcode提交的Solution。不支持题目类型为”设计类“的Solution。
 * <p>
 *    通过静态方法 <code>build()</code> 指定提交的类和主方法，构建 <code>RunSolution</code> 的对象，
 *    调用run方法读取标准输入的内容，自动按照主方法的参数顺序解析每行输入的参数，
 *    然后调用主方法，在控制台输出返回的结果。
 * </p>
 */
public class RunSolution {

    /** 需要调用的方法对象 */
    private final Method method;

    private final Class<?> solutionClass;

    /** Solution的实例对象 */
    private Object solution;

    /** 为运行时提供方法所需参数的一个输入流，输入流会被逐行按参数列表的参数顺序进行解析 */
    private InputStream in = System.in;

    /** 为运行时提供方法返回值的输出流，每个返回值占一行 */
    private PrintStream out = System.out;

    /** 当前调用方法需要传入的参数个数 */
    private final int paramCount;

    private final boolean isRecycledUse;

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
     * 根据相应的Solution的Class对象，以及已知的调用方法名称，创建一个RunSolution对象
     *
     * @param methodName 需要指定一个方法名称
     * @param solutionClass 作为Solution类的Class对象
     * @return <code>RunSolution</code> Object
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
     * 根据现成的Solution实例，以及已知的调用方法名称，创建一个RunSolution对象
     *
     * @param methodName 需要指定一个方法名称
     * @param solution Solution的实例
     * @param isRecycledUse 是否固定运行时的实例对象
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
     * 根据现成的Solution实例，以及已知的调用方法名称，创建一个RunSolution对象，
     * 这样做等同于调用 {@code setTarget(methodName, object, false)}，
     * 返回的RunSolution对象将会一直持有当前Solution的实例
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
     * 为运行时提供方法所需参数的一个输入流，输入流会被逐行按参数列表的参数顺序进行解析
     *
     * @param in 输入流
     * @return 当前RunSolution对象
     */
    public RunSolution setIn(InputStream in) {
        this.in = in;
        return this;
    }

    public RunSolution setOut(PrintStream out) {
        this.out = out;
        return this;
    }

    /**
     * run!!
     *
     * @throws IOException 读取发生异常，检查input文件是否按参数列表顺序进行输入
     * @throws InvocationTargetException 调用方法时产生的异常
     * @throws IllegalAccessException 方法访问异常
     */
    public void run() throws Exception {
        BufferedReader in = new BufferedReader(new InputStreamReader(this.in));
        List<String> inputLines;
        while ((inputLines = collectInput(in, paramCount)).size() > 0) {
            run(inputLines);
            if (!isRecycledUse) {
                Constructor<?> constructor = solutionClass.getDeclaredConstructor();
                constructor.setAccessible(true);
                solution = constructor.newInstance();
            }
        }
    }

    /**
     * 从现成的输入内容中解析参数，输入内容是一个List对象，存储的每个字符串表示一行内容
     * 将每一行内容按方法声明的参数顺序进行解析，然后带着这些参数执行相应的方法
     *
     * @param inputLines 输入的内容，一行一个字符串
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
     * @return 用于打印输出的字符串
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

    private List<String> collectInput(BufferedReader in, int limit) throws IOException {
        List<String> ret = new ArrayList<>();
        String line = null;
        while (ret.size() < limit && (line = in.readLine()) != null) {
            ret.add(line);
        }
        if (line == null) {
            in.close();
        }
        return ret;
    }

}
