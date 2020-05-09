package com.company;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Main {
    static final String ORIGINAL_OUTPUT = "files/output-mada.dat";
    static final String ORIGINAL_HUFFMAN = "files/dec_tab-mada.txt";
    static final String STANDARD_OUTPUT = "files/output.dat";
    static final String STANDARD_HUFFMAN = "files/dec_tab.txt";
    static final String STANDARD_INPUT = "files/input.txt";

    SortedMap<Integer, String> huffmanMap = new TreeMap<>(Integer::compareTo);

    public static void main(String[] args) {
        Main m = new Main();
        try {
            int[] occ = m.countAsciiOccurencesInFile(STANDARD_INPUT);
            List<HuffmanNode> huffmanNodes = m.compressedOccurences(occ);
            HuffmanNode rootNode= m.createHuffmanTree(huffmanNodes);
            m.treePostorder(rootNode, "","");
            m.writeToTextFile(STANDARD_HUFFMAN, m.createHuffmanString());
            m.encodeFile(STANDARD_INPUT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void encodeFile(String filePath){
        char[] fileAsCharArray = readFromTextFile(filePath).toCharArray();

        StringBuilder encodedString = new StringBuilder();

        for(char c: fileAsCharArray){
            encodedString.append(huffmanMap.get((int) c));
        }

        encodedString.append("1");

        while (encodedString.length()%8 != 0){
            encodedString.append("0");
        }

        byte[] encodedMessage = new byte[encodedString.length()/8];

        for (int i = 0; i < encodedString.length()/8; i++) {
            String substring = encodedString.substring( i*8, ((i+1)*8));
//            System.out.println(substring);
//            System.out.println(Integer.parseInt(substring, 2));
            encodedMessage[i] = (byte) Integer.parseInt(substring, 2);
        }

//        for (byte b : encodedMessage) {
//            System.out.println(b);
//        }

        try {
            writeFile(STANDARD_OUTPUT, encodedMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    String createHuffmanString(){
        StringBuilder string = new StringBuilder();
        for (int index : huffmanMap.keySet()) {
            string.append(String.format("%s:%s-", index, huffmanMap.get(index)));
        }

        string = new StringBuilder(string.substring(0, string.length() - 1));

        return string.toString();
    }

    void treePostorder(HuffmanNode node, String binarySoFar, String binaryAdded) {
        if(node == null){
            return;
        }
        binarySoFar += binaryAdded;

        treePostorder(node.left, binarySoFar, "0");
        treePostorder(node.right, binarySoFar, "1");

        if(node.characterAsInt >= 0){
//            System.out.println(String.format("%s:&s:%s",(char) node.characterAsInt, node.characterAsInt, binarySoFar ));
            huffmanMap.put(node.characterAsInt, binarySoFar);
        }
    }

    List<HuffmanNode> compressedOccurences(int[] occurencesArray) {
        List<HuffmanNode> huffmanNodes = new ArrayList<>();
        for (int i = 0; i < occurencesArray.length; i++) {
            if(occurencesArray[i] > 0){
                HuffmanNode node = new HuffmanNode( occurencesArray[i] , i);
                huffmanNodes.add(node);
            }
        }

        return huffmanNodes;
    }

    HuffmanNode createHuffmanTree(List<HuffmanNode> nodes) {
        if(nodes.size() == 1){
            return nodes.get(0);
        } else if(nodes.size() == 0){
            throw new Error("List is empty!");
        } else {

            HuffmanNode node1 = nodes.stream().min(HuffmanNode::compareTo).get();
            nodes.remove(node1);

            HuffmanNode node2 =  nodes.stream().min(HuffmanNode::compareTo).get();
            nodes.remove(node2);

            HuffmanNode newNode = new HuffmanNode(node1.value + node2.value, node2, node1);
            nodes.add(newNode);

            return createHuffmanTree(nodes);
        }
    }

    int[] countAsciiOccurencesInFile(String filePath) throws Exception {
        int[] asciiOccurences = new int[128];

        String file = readFromTextFile(filePath);
        if (file != null) {
            char[] chars = file.toCharArray();
            for (char c: chars) {
               asciiOccurences[(int) c]++;
            }

            return asciiOccurences;
        } else {
            throw new FileNotFoundException();
        }
    }

    void writeFile(String filePath, byte[] out) throws IOException {
        FileOutputStream fos = new FileOutputStream(filePath);
        fos.write(out);
        fos.close();
    }

    byte[] readFile(String filePath) throws IOException {
        File file = new File(filePath);
        byte[] bFile = new byte[(int) file.length()];
        FileInputStream fis = new FileInputStream(file);
        fis.read(bFile);
        fis.close();

        return bFile;
    }

    String readFromTextFile(String path) {
        try {
            return Files.readString(Paths.get(path));
        } catch (IOException e){
            e.printStackTrace();
        }

        return null;
    }

    void writeToTextFile(String path, String content) {
        try {
            Path outPath = Paths.get(path);
            Files.writeString(outPath, content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
