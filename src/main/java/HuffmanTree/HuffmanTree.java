package HuffmanTree;

import MyException.ParaIllegalException;
import lombok.Data;

import java.util.*;

// 实现参照 http://baijiahao.baidu.com/s?id=1651316704300729180&wfr=spider&for=pc
@Data
public class HuffmanTree { // 左0右1

    Node huffmanTree;
    // 要为非叶子结点赋予一个唯一的info , 初定方案：从256开始赋值

    public Map<Byte,String> encode(Map<Byte, Integer> frequencyForByte) {
        PriorityQueue<Node> priorityQueue = new PriorityQueue<>();

        // 1. 添加所有元素到优先队列中
        for (Byte b : frequencyForByte.keySet()) {
            priorityQueue.add(new Node(b , frequencyForByte.get(b)));
        }

        // 2. 选取最小的两个节点，取消
        while (priorityQueue.size() != 1) {
            Node node1 = priorityQueue.poll();
            Node node2 = priorityQueue.poll();
            // 3. 合并之后放入队列中
            priorityQueue.add(new Node(node1, node2, node1.freq + node2.freq));
        }

        this.huffmanTree = priorityQueue.poll();

        //4.选取最小的一个进行编码
        return encodeReal(this.huffmanTree);
    }

    // 5. 实现编码的方法
    private Map<Byte,String> encodeReal(Node root) {
        Map<Byte, String> encodingForByte = new HashMap<>();
        encodeByte(root, "", encodingForByte);
        return encodingForByte;
    }

    private void encodeByte(Node node, String encoding, Map<Byte, String> encodingByte) {
        // 6. 如果是叶子，放进来字节还有相应的编码
        if (node.isLeaf) {
            encodingByte.put(node.info, encoding);
            return;
        }

        // 7. 左右递归编码
        encodeByte(node.left, encoding + '0', encodingByte);
        encodeByte(node.right, encoding + '1', encodingByte);
    }

    public Node getHuffmanTree() {
        return this.huffmanTree;
    }

    public static ArrayList<Node> preOrderTraversal(Node root) { // 前序遍历
        ArrayList<Node> res = new ArrayList<Node>();

        if (root == null)
            return res;
        Stack<Node> nodeStack = new Stack<Node>();
        nodeStack.push(root);
        while (!nodeStack.empty()) {
            Node node = nodeStack.pop();
            res.add(node);
            if (node.getLeft() != null) {
                nodeStack.push(node.getLeft());
            }
            if (node.getRight() != null) {
                nodeStack.push(node.getRight());
            }
        }
        return res;
    }

    public static ArrayList<Node> inOrderTraversal(Node root) {
        ArrayList<Node> res = new ArrayList<Node>();
        if (root == null) {
            return res;
        }

        Node pres = root;
        Stack<Node> nodeStack = new Stack<Node>();

        while (pres != null || !nodeStack.empty()) {
            while (pres != null) {
                nodeStack.push(pres);
                pres = pres.getLeft();
            }

            pres = nodeStack.pop();
            res.add(pres);
            pres = pres.getRight();
        }
        return res;
    }

    // 由前序遍历和中序遍历还原哈夫曼树
    public static Node createTreeByPreOrderAndInOrder(ArrayList<Node> preOrder, ArrayList<Node> inOrder) throws ParaIllegalException {

        if (preOrder.size()==0 || inOrder.size()==0 || preOrder.size() != inOrder.size()) {
            throw new ParaIllegalException("\n" +
                    "Node createTreeByPreOrderAndInOrder(ArrayList<Node> preOrder, ArrayList<Node> inOrder)中" +
                    "preOrder或者inOrder大小==0，或者preOrder和inOrder的大小不相等"
            );
        }

        Node root = preOrder.get(0); // 前序遍历第一个为哈夫曼树的根节点
        int rootIndex = inOrder.indexOf(root); // 根节点在中序遍历中的位置

        ArrayList<Node> leftChildInOrder = new ArrayList(inOrder.subList(0, rootIndex)); // 左子树的中序遍历  0 ~ rootIndex-1
        ArrayList<Node> rightChildInOrder = new ArrayList(inOrder.subList(rootIndex, inOrder.size())); // 右子树的中序遍历 rootIndex ~ end

        // 确认左右子树的前序遍历
        ArrayList<Node> leftChildPreOrder = new ArrayList<Node>();
        ArrayList<Node> rightChildPreOrder = new ArrayList<Node>();

        PriorityQueue<Integer> leftPreOrderQueue = new PriorityQueue<>();
        for (Node node : leftChildInOrder) {
            int nodeIndex = preOrder.indexOf(node);
            leftPreOrderQueue.add(nodeIndex);
        }
        while (!leftPreOrderQueue.isEmpty()) {
            int index = leftPreOrderQueue.poll();
            leftChildPreOrder.add(preOrder.get(index));
        }

        PriorityQueue<Integer> rightPreOrderQueue = new PriorityQueue<>();
        for (Node node : rightChildInOrder) {
            int nodeIndex = preOrder.indexOf(node);
            rightPreOrderQueue.add(nodeIndex);
        }
        while (!rightPreOrderQueue.isEmpty()) {
            int index = rightPreOrderQueue.poll();
            rightChildPreOrder.add(preOrder.get(index));
        }

        Node leftChild = createTreeByPreOrderAndInOrder(leftChildPreOrder, rightChildInOrder);
        Node rightChild = createTreeByPreOrderAndInOrder(rightChildPreOrder, rightChildInOrder);
        root.setLeft(leftChild);
        root.setRight(rightChild);
        return root;
    }

}
