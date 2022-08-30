# leetcode-local-run

这是一个可以让自己在本地 IDE 模拟 `leetcode` 刷题过程的 demo。

## 前言

虽然在本地测试 [**leetcode**](https://leetcode.cn/problems/) 代码并不困难，但好奇心驱使着我，于是就有了这个 demo。

在 `leetcode` 进行刷题时，会提供一个类的模板，让我们实现特定的方法

例如：

```java
class Solution {
    public int[] twoSum(int[] nums, int target) {
        // ...
    }
}
```

除此之外，控制台中的样例输入，通常是**一行对应一个参数**。在上面的例子中，它的输入样例长这样，

```
[2,7,11,15]
9
```

分别对应 `towSum` 方法中的两个参数 `nums` 和 `target` 。

特别的，还有一些设计类的题目，它在控制台读取的输入和刚才的有些不太一样。

例如：[208.实现（Trie）前缀树](https://leetcode.cn/problems/implement-trie-prefix-tree/)

* 模板

```java
class Trie {

    public Trie() {

    }
  
    public void insert(String word) {

    }
  
    public boolean search(String word) {

    }
  
    public boolean startsWith(String prefix) {

    }
}

/**
 * Your Trie object will be instantiated and called as such:
 * Trie obj = new Trie();
 * obj.insert(word);
 * boolean param_2 = obj.search(word);
 * boolean param_3 = obj.startsWith(prefix);
 */
```

* 输入

```
["Trie","insert","search","search","startsWith","insert","search"]
[[],["apple"],["apple"],["app"],["app"],["app"],["app"]]
```

输入一共两行，分为两个**数组**，第一个是需要调用的**方法**，按调用顺序排列，第二个是相应方法需要的**参数**。
**注意：第二个数组中，每个元素都是数组，代表的是参数列表**。

> 要想在本地运行代码，需要解决的问题是**输入读取、参数转换、执行方法**这个过程。

## 使用方法

对于第一种类型的题目，需要使用 `RunSolution` 类。

首先，调用 `build` 构建对象，通过 `setIn` 设置读取的输入流，最后调用 `run` 执行代码。

`build` 需要的参数有：

* `methodName`: 调用的方法名称
* `srcClass`: 运行代码的类的 `Class` 对象

### 示例

```java
import task.RunSolution;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class TestNormalSolution {
    public static void main(String[] args) throws Exception {
        Class<?> srcClass = Solution.class; // 指定运行时创建的实例的Class对象
        String methodName = "towSum"; // 指定实例调用的方法 
        InputStream in = System.in; // 创建运行时读取的输入流
        RunSolution.build(methodName, srcClass).setIn(in).run();
    }
}

// 编译器会认为该类没有被使用，使用该注解可以忽视掉警告
@SuppressWarnings("unused")
class Solution {
    public int[] twoSum(int[] nums, int target) {
        int n = nums.length;
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < n; ++i) {
            if (map.containsKey(target - nums[i])) {
                return new int[] {map.get(target - nums[i]), i};
            }
            map.put(nums[i], i);
        }
        return new int[2];
    }
}
```

如果不设置 `setIn` ，则默认从 `System.in` 中读取。

---

对于第二种类型的题目，需要使用 `RunDesignSolution` 类。

它与前者的特殊之处在于，它是需要先指明输入的样例，然后按照输入的指令，执行传入的 `Class` 。

首先，通过静态 `setIn` 设置读取的输入，获得 `RunDesignSolution` 对象，然后调用对象的 `run` 方法并传入一个 `Class` 对象。

在执行之前可以通过 `setOutPrefer` 设置运行结果的输出样式：

* `LINE_BY_LINE`: 逐行输出每个方法调用的返回值
* `ARRAY_LIKED`: 以数组的形式组织每个方法调用的返回值。

### 示例

```java
import task.RunDesignSolution;

public class TestRunDesignSolution {
    public static void main(String[] args) throws Exception {
        RunDesignSolution
                .setIn(Sytem.in)
                .setOutPrefer(RunDesignSolution.ARRAY_LIKED)
                .run(Trie.class);
    }
}

class Trie {

    public Trie() {
        // ...
    }

    public void insert(String word) {
        // ...
    }

    public boolean search(String word) {
        // ...
    }

    public boolean startsWith(String prefix) {
        // ...
    }
}
```
