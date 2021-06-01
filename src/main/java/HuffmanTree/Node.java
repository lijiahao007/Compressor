package HuffmanTree;

import lombok.Data;

@Data
public class Node implements Comparable<Node> {
    byte info; // 这个info 应该要在所有Node中唯一
    int freq; //频率
    boolean isLeaf; // 是否叶节点
    Node left;
    Node right;

    public Node(byte info, int freq) {
        this.info = info;
        this.freq = freq;
        isLeaf = true;
        left = null;
        right = null;
    }

    public Node(Node left, Node right, int freq) {
        this.left = left;
        this.right = right;
        this.freq = freq;
        isLeaf = false;
    }

    @Override
    public int compareTo(Node o) {
        return this.freq - o.freq;
    }
}
