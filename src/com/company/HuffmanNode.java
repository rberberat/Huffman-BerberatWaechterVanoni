package com.company;

import java.util.Comparator;

public class HuffmanNode implements Comparable<HuffmanNode> {
    int value;
    int characterAsInt = -1;
    HuffmanNode left = null;
    HuffmanNode right = null;

    HuffmanNode(int value) {
        this.value = value;
    }

    public HuffmanNode(int value, int characterAsInt) {
        this.value = value;
        this.characterAsInt = characterAsInt;
    }

    public HuffmanNode(int value, HuffmanNode left, HuffmanNode right) {
        this.value = value;
        this.left = left;
        this.right = right;
    }

    @Override
    public int compareTo(HuffmanNode o) {
        return this.value - o.value;
    }

    @Override
    public String toString() {
        return "HuffmanNode{" + "value=" + value + ", characterAsInt=" + characterAsInt + ", left=" + left + ", right="
                + right + '}';
    }
}
