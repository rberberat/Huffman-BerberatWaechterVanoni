package com.company;

// Custom Class to create a Binary-Tree for the Huffman Encoding-Strategy
public class HuffmanNode implements Comparable<HuffmanNode> {
    int value = 0;
    int characterAsInt = -1;
    String binaryRepresentationOfNode = "";
    HuffmanNode left = null;
    HuffmanNode right = null;

    HuffmanNode(){};

    public HuffmanNode(int value, int characterAsInt) {
        this.value = value;
        this.characterAsInt = characterAsInt;
    }

    public HuffmanNode(int value, HuffmanNode left, HuffmanNode right) {
        this.value = value;
        this.left = left;
        this.right = right;
    }

    boolean isLeafNode() {
        return left == null && right == null;
    }

    @Override
    public int compareTo(HuffmanNode o) {
        return this.value - o.value;
    }

    @Override
    public String toString() {
        return "HuffmanNode{" + "value=" + value + ", characterAsInt=" + characterAsInt
                + ", binaryRepresentationOfNode='" + binaryRepresentationOfNode + '\'' + ", left=" + left + ", right="
                + right + '}';
    }
}
