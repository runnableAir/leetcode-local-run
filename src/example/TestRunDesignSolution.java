package example;

import task.RunDesignSolution;

import java.util.Map;
import java.util.TreeMap;

public class TestRunDesignSolution {
    public static void main(String[] args) throws Exception {
        RunDesignSolution
                .setIn(SummaryRanges.class.getResourceAsStream("design-solution.in"))
                .setOutPrefer(RunDesignSolution.ARRAY_LIKED)
                .run(SummaryRanges.class);
    }
}

/**
 * Your SummaryRanges object will be instantiated and called as such:
 * SummaryRanges obj = new SummaryRanges();
 * obj.addNum(val);
 * int[][] param_2 = obj.getIntervals();
 */
@SuppressWarnings("unused")
class SummaryRanges {
    private final TreeMap<Integer, Integer> treeMap;
    private static final int MIN = (int) -1e5;
    private static final int MAX = (int) 1e5;

    public SummaryRanges() {
        treeMap = new TreeMap<>();
    }

    public void addNum(int val) {
        if (treeMap.containsKey(val)) {
            return;
        }
        Map.Entry<Integer, Integer> lower = treeMap.lowerEntry(val);
        Map.Entry<Integer, Integer> higher = treeMap.higherEntry(val);
        int leftMin = MIN, leftMax = MIN;
        int rightMin = MAX, rightMax = MAX;
        if (lower != null) {
            leftMin = lower.getKey();
            leftMax = lower.getValue();
        }
        if (higher != null) {
            rightMin = higher.getKey();
            rightMax = higher.getValue();
        }
        // 单独成组
        if (val > leftMax + 1 && val < rightMin - 1) {
            treeMap.put(val, val);
            return;
        }
        // 合并
        boolean joinLeft = val == leftMax + 1;
        boolean joinRight = val == rightMin - 1;
        if (joinLeft && joinRight) {
            treeMap.put(leftMin, rightMax);
            treeMap.remove(rightMin);
        } else if (joinRight) {
            treeMap.put(val, rightMax);
            treeMap.remove(rightMin);
        } else if (joinLeft) {
            treeMap.put(leftMin, val);
        }
    }

    public int[][] getIntervals() {
        int[][] ret = new int[treeMap.size()][2];
        int idx = 0;
        for (Map.Entry<Integer, Integer> entry : treeMap.entrySet()) {
            ret[idx][0] = entry.getKey();
            ret[idx][1] = entry.getValue();
            idx++;
        }
        return ret;
    }
}


