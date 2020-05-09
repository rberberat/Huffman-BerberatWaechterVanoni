package com.company;

import java.util.Comparator;

public class HuffmanNode implements Comparable<HuffmanNode> {
    int value;
    int characterAsInt = -1;
    HuffmanNode left;
    HuffmanNode right;

    HuffmanNode(int value) {
        this.value = value;
        left = null;
        right = null;
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

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getCharacterAsInt() {
        return characterAsInt;
    }

    public void setCharacterAsInt(char characterAsInt) {
        this.characterAsInt = characterAsInt;
    }

    public HuffmanNode getLeft() {
        return left;
    }

    public void setLeft(HuffmanNode left) {
        this.left = left;
    }

    public HuffmanNode getRight() {
        return right;
    }

    public void setRight(HuffmanNode right) {
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
