package example;

import task.RunSolution;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class TestNormalSolution {
    public static void main(String[] args) throws Exception {
        InputStream in = TestNormalSolution.class.getResourceAsStream("normal-solution.in");
        RunSolution.build("twoSum", Solution.class).setIn(in).run();
    }
}

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
