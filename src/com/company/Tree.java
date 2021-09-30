package com.company;

import java.util.*;
import java.util.stream.Collectors;

public class Tree {
    private ArrayList<ArrayList<String>> receipts;
    private TreeSet<String> itemTreeSet;
    private int minSupportThreshold;
    private Node root;
    private LinkedHashMap<String, LinkedHashMap<ArrayList<Node>, Integer>> conditonalPB;
    private LinkedHashMap<String, TreeMap<String, Integer>> conditionalFPTree;
    private LinkedHashMap<String, TreeMap<String, Double>> generatedRules;
    private HashMap<String, Integer> FList;
    int ruleCount;


    public Tree(ArrayList<ArrayList<String>> receipts, TreeSet<String> itemTreeSet, int minSupportThreshold) {
        FPG fpg = new FPG(receipts, itemTreeSet, minSupportThreshold);
        this.minSupportThreshold = minSupportThreshold;
        this.receipts = fpg.getFPDP();
        this.itemTreeSet = itemTreeSet;
        this.root = new Node(null);
        conditonalPB = new LinkedHashMap<>();
        conditionalFPTree = new LinkedHashMap<>();
        generatedRules = new LinkedHashMap<>();
        constructFPTree();
        LinkedHashMap<String, Integer> reversedFList;
        reversedFList = fpg.getReversedFList();
        generateConditionalPatternBase(reversedFList);
        generateConditionalFPTree();
        FList = fpg.getFList();
        generateRules();
        printRules();
    }

    private void constructFPTree() {
        for (ArrayList<String> receipt : receipts) {
            root.add(receipt, root);
        }
    }

    public void generateConditionalPatternBase(LinkedHashMap<String, Integer> reversedFList) {
        for (String item : reversedFList.keySet()) {
            for (Node lastNode : root.getUniquePaths().get(item)) {
                if (!(lastNode.getParent().getItem() == null)) {
                    ArrayList<Node> nodePathTemp = lastNode.generatePathToRoot();
                    Collections.reverse(nodePathTemp);
                    ArrayList<Node> nodePath = (ArrayList<Node>) nodePathTemp.clone();

                    Node lastL = nodePath.get(nodePathTemp.size() - 1);

                    if (nodePath.contains(lastL)) {
                        nodePath.remove(lastL);
                    }

                    if (conditonalPB.get(item) == null) {
                        LinkedHashMap temp = new LinkedHashMap<ArrayList<Node>, Integer>();
                        temp.put(nodePath, 0);
                        conditonalPB.put(item, temp);
                    }

                    conditonalPB.get(item).put(nodePath, lastL.frequency);
                    LinkedHashMap<ArrayList<Node>, Integer> sortedConditionalPB;
                    sortedConditionalPB = conditonalPB.get(item).entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

                    conditonalPB.put(item, sortedConditionalPB);
                    lastNode.generatePathToRoot().clear();
                }
            }
        }
    }


    public void generateConditionalFPTree() {
        for (String item : conditonalPB.keySet()) {
            for (LinkedHashMap<ArrayList<Node>, Integer> nodes : conditonalPB.values()) {
                if (conditonalPB.get(item).equals(nodes)) {

                    TreeMap<String, Integer> conditions = new TreeMap<>();
                    for (Map.Entry<ArrayList<Node>, Integer> entry : nodes.entrySet()) {
                        for (Node items : entry.getKey()) {

                            if (!conditions.containsKey(items.item)) {
                                conditions.put(items.item, entry.getValue());
                            } else if (conditions.containsKey(items.item)) {
                                conditions.merge(items.item, entry.getValue(), Integer::sum);
                            }
                        }
                    }

                    for (String itemToCheck : List.copyOf(conditions.keySet())) {
                        if (minSupportThreshold > conditions.get(itemToCheck)) {
                            conditions.remove(itemToCheck);
                        } else {
                            conditionalFPTree.put(item, conditions);
                        }
                    }

                }

            }
        }
    }

    private void generateRules() {
        for (String item : conditionalFPTree.keySet()) {
            for (TreeMap<String, Integer> rules : conditionalFPTree.values()) {
                if (conditionalFPTree.get(item).equals(rules)) {
                    TreeMap<String, Double> someRules = new TreeMap<>();
                    for (Map.Entry<String, Integer> entry : rules.entrySet()) {
                        double confidence = generateConfidence(item, entry.getValue());
                        someRules.put(entry.getKey(), confidence);
                    }
                    generatedRules.put(item, someRules);
                }
            }

        }
    }


    private double generateConfidence(String key, Integer value) {
        double ruleFrequency = (double) value;
        double itemFrequency = (double) FList.get(key);
        double confidence = ruleFrequency / itemFrequency;
        return confidence;
    }

    private void printRules() {
        System.out.println("======== Generated Rules ========");
        for (String item : generatedRules.keySet()) {
            for (TreeMap<String, Double> rules : generatedRules.values()) {
                if (generatedRules.get(item).equals(rules)) {
                    for (Map.Entry<String, Double> entry : rules.entrySet()) {
                        System.out.println(item + " -> " + entry.getKey());
                        System.out.println("Count: " + getRuleCount(item) + " | Confidence: " + entry.getValue() + "\n");
                    }
                }
            }
        }
    }

    private int getRuleCount(String item) {
        for (Map.Entry<String, TreeMap<String, Integer>> rules : conditionalFPTree.entrySet()) {
            if (rules.getKey().equalsIgnoreCase(item)) {
                for (Map.Entry<String, Integer> val : conditionalFPTree.get(item).entrySet()) {
                    ruleCount = val.getValue();
                }
            }
        }
        return ruleCount;
    }

}





