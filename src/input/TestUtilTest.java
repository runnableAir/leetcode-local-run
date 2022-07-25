package input;

import input.tree.binary.TreeNode;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestUtilTest {

    @Test
    public void treeNodeToString() {
        String str = "[1,2,3,null,null,null,4]";
        TreeNode treeNode = TestUtil.stringToTreeNode(str);
        System.out.println(TestUtil.treeNodeToString(treeNode));
    }

    @Test
    public void treeNodeToString1() {
        String str = "[5,2,4,null,1,3]";
        TreeNode treeNode = TestUtil.stringToTreeNode(str);
        System.out.println(TestUtil.treeNodeToString(treeNode));
    }
}