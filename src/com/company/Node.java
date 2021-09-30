package com.company;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Node {
    ArrayList<Node> children;
    String item;
    int frequency;
    Node parent;
    LinkedHashMap<String, ArrayList<Node>> unique;
    static public ArrayList<Node> pathToRootNode = new ArrayList<>();

    public Node(String item) {
        this.item = item;
        this.children = new ArrayList<>();
        this.frequency = 0;
        unique = new LinkedHashMap<>();
    }

    public void increaseFrequency() {
        this.frequency++;
    }

    public boolean add(ArrayList<String> receipt, Node currentNode) {
        String item;

        if (receipt.size() == 0) {
            return true;
        } else {
            item = receipt.get(0);
        }

        Node child = null;

        // Recursively goes through and fills in the tree.
        for (Node scanner : currentNode.getChildren()) {
            String currentNodeItem = scanner.item;

            if (currentNodeItem.equals(item)) {
                child = scanner;
                child.increaseFrequency();
                break;
            }

        }

        while (child == null) {
            child = new Node(item);
            child.parent = currentNode;
            currentNode.children.add(child);
            child.increaseFrequency();
            ArrayList<Node> treePath;
            treePath = unique.get(item);

            if (treePath == null) {
                treePath = new ArrayList<>();
            }
            treePath.add(child);

            unique.put(item, treePath);
        }

        receipt.remove(item);
        return add(receipt, child);
    }

    private ArrayList<Node> getChildren() {
        return this.children;
    }

    public LinkedHashMap<String, ArrayList<Node>> getUniquePaths() {
        return unique;
    }

    public Node getParent() {
        return this.parent;
    }
    public String getItem() {
        return this.item;
    }

    ArrayList<Node> generatePathToRoot(){
        if(parent == null) {
            return pathToRootNode;
        } else {

            pathToRootNode.size();

            pathToRootNode.add(this);
            return parent.generatePathToRoot();
        }
    }

}
