package org.beanio.internal.util;


public class ReplicatorTest {

    private static Replicator replicator;
    
    /**
     * @param args
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static void main(String[] args) {
        
        TreeNode a = new TreeNode();
        a.setName("a");
        
        replicator = new Replicator();
        
        TreeNode a1 = createNode("a1");
        TreeNode a2 = createNode("a2");
        TreeNode a3 = createNode("a3");
        TreeNode a4 = createNode("a4");
        TreeNode a5 = createNode("a5");
        TreeNode a6 = createNode("a6");
        TreeNode a7 = createNode("a7");

        a.add(a1);
        a.add(a2);
        a.add(a3);
        a1.add(a4);
        a1.add(a5);
        a2.add(a6);
        a2.add(a7);

        print(a, "");
        
        System.out.println("====================");

        TreeNode clone = null;
        for (int i=0; i<1000; i++) {
             clone = replicator.replicate(a);
        }
        
        long start = System.currentTimeMillis();
        for (int i=0; i<10; i++) {
             clone = replicator.replicate(a);
        }
        long elapsed = System.currentTimeMillis() - start;
        System.out.println(elapsed + "ms");
        
        a.setName("b");
        a1.setName("b1");
        a2.setName("b2");
        a3.setName("b3");
        a4.setName("b4");
        a5.setName("b5");
        a6.setName("b6");
        a7.setName("b7");
        
        print(clone, "");
        
        print(a, "");
        
    }
    
    @SuppressWarnings("rawtypes")
    private static TreeNode createNode(String name) {
        TreeNode<TreeNode> node = new TreeNode<>();
        node.setName(name);
        replicator.register(node);
        return node;
    }

    private static void print(TreeNode<?> node, String indent) {
        System.out.println(indent + node.getName());
        indent = "  " + indent;
        for (TreeNode<?> n : node.getChildren()) {
            print(n, indent);
        }
    }
}
