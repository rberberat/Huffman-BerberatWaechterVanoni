package com.company;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class HuffmanCodec {
    static final String ORIGINAL_OUTPUT = "files/output-mada.dat";
    static final String ORIGINAL_HUFFMAN = "files/dec_tab-mada.txt";
    static final String ORIGINAL_INPUT = "files/input-mada.txt";
    static final String STANDARD_OUTPUT = "files/output.dat";
    static final String STANDARD_HUFFMAN = "files/dec_tab.txt";
    static final String STANDARD_INPUT = "files/input.txt";

    SortedMap<Integer, String> huffmanMap = new TreeMap<>(Integer::compareTo);

    public static void main(String[] args) {
        HuffmanCodec m = new HuffmanCodec();

        // Takes input.txt-file, and creates dec_tab.txt and output.dat (all in files-folder)
        try {
            m.encodeInputText(STANDARD_INPUT, STANDARD_OUTPUT, STANDARD_HUFFMAN);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Takes output-mada.dat and dec_tab.txt and created input-mada.txt (all in files)
        try {
            m.decodeOutputText(ORIGINAL_INPUT, ORIGINAL_OUTPUT, ORIGINAL_HUFFMAN);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void decodeOutputText(String inputFile, String outputFile, String decTabFile) throws Exception {
        readHuffmanMapFromFile(decTabFile);
        HuffmanNode rootNode = new HuffmanNode();
        for (Integer key : huffmanMap.keySet()) {
            buildHuffmanTreeFromMap(rootNode, huffmanMap.get(key), key);
        }
        byte[] encodedFile = readFile(outputFile);
        String fileString = turnByteArrayToBinaryString(encodedFile);
        writeToTextFile(inputFile, decodeBinaryString(fileString, rootNode));
    }

    void encodeInputText(String inputFile, String outputFile, String decTabFile) throws Exception {
        int[] occ = countAsciiOccurencesInFile(inputFile);
        List<HuffmanNode> huffmanNodes = compressedOccurences(occ);
        huffmanMap.clear();
        HuffmanNode rootNode = createHuffmanTree(huffmanNodes);
        treePostorder(rootNode, "", "");
        writeToTextFile(decTabFile, createHuffmanString());
        encodeFile(inputFile, outputFile);
    }

    private String decodeBinaryString(String binaryString, HuffmanNode huffmanTree) {
        String result = "";
        do {
            HuffmanNode node = getFirstLeafNodeFromString(binaryString, huffmanTree);
            result += (char) node.characterAsInt;
            binaryString = binaryString.substring(node.binaryRepresentationOfNode.length());
        } while (binaryString.length() > 0);

        return result;
    }

    private HuffmanNode getFirstLeafNodeFromString(String binaryString, @NotNull HuffmanNode node) {
        if (node.isLeafNode()) {
            return node;
        }

        if (binaryString.charAt(0) == '0') {
            binaryString = binaryString.substring(1);
            return getFirstLeafNodeFromString(binaryString, node.left);
        } else if (binaryString.charAt(0) == '1') {
            binaryString = binaryString.substring(1);
            return getFirstLeafNodeFromString(binaryString, node.right);
        }

        throw new Error("You shouldn't be here");
    }

    @NotNull private String turnByteArrayToBinaryString(@NotNull byte[] byteArray) {
        StringBuilder result = new StringBuilder();
        for (Byte b : byteArray) {
            result.append(String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0'));
        }

        int lastIndexOfOne = result.lastIndexOf("1");
        return result.toString().substring(0, lastIndexOfOne);
    }

    // Takes the Huffman-Map and creates a representation of its tree.
    private void buildHuffmanTreeFromMap(HuffmanNode node, @NotNull String binaryString, int finalValue) {
        if (binaryString.length() == 0) {
            node.characterAsInt = finalValue;
            return;
        }

        if (binaryString.charAt(0) == '0') {
            if (node.left == null) {
                node.left = new HuffmanNode();
                node.left.binaryRepresentationOfNode = node.binaryRepresentationOfNode + "0";
            }
            binaryString = binaryString.substring(1);
            buildHuffmanTreeFromMap(node.left, binaryString, finalValue);
        } else if (binaryString.charAt(0) == '1') {
            if (node.right == null) {
                node.right = new HuffmanNode();
                node.right.binaryRepresentationOfNode = node.binaryRepresentationOfNode + "1";
            }
            binaryString = binaryString.substring(1);
            buildHuffmanTreeFromMap(node.right, binaryString, finalValue);
        }
    }

    // Reads a given dec_tab-file and fills the Huffman-Map with the given codings.
    private void readHuffmanMapFromFile(String filepath) {
        huffmanMap.clear();

        String decoderString = readFromTextFile(filepath);
        String[] decoderArray = decoderString.split("-");
        for (String part : decoderArray) {
            String[] split = part.split(":");
            huffmanMap.put(Integer.parseInt(split[0]), split[1]);
        }
    }

    private void encodeFile(String inputPath, String outputPath) {
        char[] fileAsCharArray = readFromTextFile(inputPath).toCharArray();

        StringBuilder encodedString = new StringBuilder();

        for (char c : fileAsCharArray) {
            encodedString.append(huffmanMap.get((int) c));
        }

        encodedString.append("1");

        while (encodedString.length() % 8 != 0) {
            encodedString.append("0");
        }

        byte[] encodedMessage = new byte[encodedString.length() / 8];

        for (int i = 0; i < encodedString.length() / 8; i++) {
            String substring = encodedString.substring(i * 8, ((i + 1) * 8));
            encodedMessage[i] = (byte) Integer.parseInt(substring, 2);
        }

        try {
            writeFile(outputPath, encodedMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // Creates the final decoder-String that gets saved into the dec_tab-file
    @NotNull private String createHuffmanString() {
        StringBuilder string = new StringBuilder();
        for (int index : huffmanMap.keySet()) {
            string.append(String.format("%s:%s-", index, huffmanMap.get(index)));
        }

        string = new StringBuilder(string.substring(0, string.length() - 1));

        return string.toString();
    }

    // Fills the huffmanMap with values. Keys are the Integer-represantion of a given Character, Values represent the binary "path" through the tree.
    private void treePostorder(HuffmanNode node, String binarySoFar, String binaryAdded) {
        if (node == null) {
            return;
        }
        binarySoFar += binaryAdded;

        treePostorder(node.left, binarySoFar, "0");
        treePostorder(node.right, binarySoFar, "1");

        if (node.characterAsInt >= 0) {
            huffmanMap.put(node.characterAsInt, binarySoFar);
        }
    }

    // removes all occurences from the Array, where the value is 0 (Character doesn't appear in text)
    @NotNull private List<HuffmanNode> compressedOccurences(@NotNull int[] occurencesArray) {
        List<HuffmanNode> huffmanNodes = new ArrayList<>();
        for (int i = 0; i < occurencesArray.length; i++) {
            if (occurencesArray[i] > 0) {
                HuffmanNode node = new HuffmanNode(occurencesArray[i], i);
                huffmanNodes.add(node);
            }
        }

        return huffmanNodes;
    }

    private HuffmanNode createHuffmanTree(@NotNull List<HuffmanNode> nodes) {
        if (nodes.size() == 1) {
            return nodes.get(0);
        } else if (nodes.size() == 0) {
            throw new Error("List is empty!");
        } else {

            HuffmanNode node1 = nodes.stream().min(HuffmanNode::compareTo).get();
            nodes.remove(node1);

            HuffmanNode node2 = nodes.stream().min(HuffmanNode::compareTo).get();
            nodes.remove(node2);

            HuffmanNode newNode = new HuffmanNode(node1.value + node2.value, node2, node1);
            nodes.add(newNode);

            return createHuffmanTree(nodes);
        }
    }

    // Creates int-Array, where the position in the Array represents a char (as an int value)
    // and the stored value is the amount of occurences of the given char in the provided File.
    @NotNull private int[] countAsciiOccurencesInFile(String filePath) throws Exception {
        int[] asciiOccurences = new int[128];

        String file = readFromTextFile(filePath);
        if (file != null) {
            char[] chars = file.toCharArray();
            for (char c : chars) {
                asciiOccurences[(int) c]++;
            }

            return asciiOccurences;
        } else {
            throw new FileNotFoundException();
        }
    }

    // writeFile-methode von Prof. Vogt
    private void writeFile(String filePath, byte[] out) throws IOException {
        FileOutputStream fos = new FileOutputStream(filePath);
        fos.write(out);
        fos.close();
    }

    // readFile-methode von Prof. Vogt
    @NotNull private byte[] readFile(String filePath) throws IOException {
        File file = new File(filePath);
        byte[] bFile = new byte[(int) file.length()];
        FileInputStream fis = new FileInputStream(file);
        fis.read(bFile);
        fis.close();

        return bFile;
    }

    @Nullable private String readFromTextFile(String path) {
        try {
            return Files.readString(Paths.get(path));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void writeToTextFile(String path, String content) {
        try {
            Path outPath = Paths.get(path);
            Files.writeString(outPath, content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
