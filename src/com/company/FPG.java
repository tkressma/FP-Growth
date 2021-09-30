package com.company;

import java.util.*;
import java.util.stream.Collectors;

/*
    This class generates the support of each item, creates an F-List
    of those items based on support value, and creates an FPDP by
    sorting the frequent items in each receipt based on the F-List.
    Read more here: http://athena.ecs.csus.edu/~associationcw/FpGrowth.html
    */

public class FPG {
    private ArrayList<ArrayList<String>> receipts;
    private TreeSet<String> itemTreeSet;
    private CSVReader parser;
    private HashMap<String, Integer> FList;
    private LinkedHashMap<String, Integer> reversedFList = new LinkedHashMap<>();
    private ArrayList<String> reversedFListItems;
    private int minSupportThreshold;

    public FPG(ArrayList<ArrayList<String>> receipts, TreeSet<String> itemTreeSet, int minSupportThreshold) {
        this.minSupportThreshold = minSupportThreshold;
        this.receipts = receipts;
        this.itemTreeSet = itemTreeSet;

        generateItemSupportCount(minSupportThreshold);
        generateFPDP();
    }

    // Retrieves the support of each item and generates the F-List
    private void generateItemSupportCount(int minSupportThreshold) {
        TreeMap<String, Integer> supportValues = new TreeMap<>();

        for (ArrayList<String> receipt : receipts) {
            for (String item : itemTreeSet) {
                // If an item hasn't been counted yet, create the key in the TreeMap for that item and set support value to 1. Else, increase the value for that item.
                if (receipt.contains(item) && !supportValues.containsKey(item)) {
                    supportValues.put(item, 1);
                } else if (receipt.contains(item) && supportValues.containsKey(item)) {
                    supportValues.put(item, supportValues.get(item) + 1);
                }
            }
        }
        buildFList(supportValues, minSupportThreshold);
    }

    // Creates the F-list in which frequent items are sorted in the descending order based on their support. K is item, V is support value.
    private void buildFList(TreeMap<String, Integer> supportValues, int minSupportThreshold) {
        for (String key : List.copyOf(supportValues.keySet())) {
            if (supportValues.get(key) < minSupportThreshold) {
                supportValues.remove(key);
            }

            // Uses Java 8 to sort the supportValues TreeMap by value in DESCENDING order.
            FList = supportValues.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        }

        System.out.println("Generated F-List: " + FList.toString());
        buildReverseFList(FList);
    }

    private void buildReverseFList(HashMap<String, Integer> FList) {
        reversedFListItems = new ArrayList<>(FList.keySet());
        Collections.reverse(reversedFListItems);

        for (String item : reversedFListItems) {
            reversedFList.put(item, FList.get(item));
        }

        System.out.println("Reversed F-List: " + reversedFList);
    }

    private void generateFPDP() {
        ArrayList<ArrayList<String>> itemsToBeSorted = new ArrayList<>();
        for (ArrayList<String> currentReceipt : receipts) {
            ArrayList<String> itemList = new ArrayList<>();
            for (String item : itemTreeSet) {
                if (currentReceipt.contains(item)) {
                    currentReceipt.remove(item);
                    itemList.add(item);
                    if (!FList.containsKey(item)) {
                        itemList.remove(item);
                    }
                }

            }
            if (!(itemList.size() == 0)) {
                itemsToBeSorted.add(itemList);
            }
        }
        sortItemsBySupport(itemsToBeSorted);

        // Clears all receipts of the unsorted items and fills them with their items sorted in descending order based on their support.
        receipts.clear();
        receipts.addAll(itemsToBeSorted);
        //System.out.println("List of receipts after FPDP: " + receipts);
        //System.out.println("Size of receipts after FPDP: " + receipts.size());
    }

    private ArrayList<ArrayList<String>> sortItemsBySupport(ArrayList<ArrayList<String>> itemsToBeSorted) {
        for (ArrayList<String> receipt : itemsToBeSorted) {

            HashMap<String, Integer> temp = new LinkedHashMap<>();
            for (String item : receipt) {
                temp.put(item, FList.get(item));
            }
            receipt.clear();

            HashMap<String, Integer> sortedTemp = temp.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

            receipt.addAll(sortedTemp.keySet());

        }
        return itemsToBeSorted;
    }

    public ArrayList<ArrayList<String>> getFPDP() {
        return receipts;
    }

    public LinkedHashMap<String, Integer> getReversedFList() {
        return reversedFList;
    }

    public HashMap<String, Integer> getFList() {
        return FList;
    }



}

