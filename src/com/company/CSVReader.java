package com.company;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CSVReader {
    private ArrayList<Integer> memberIDList;
    private TreeSet<Integer> memberIDTreeSet;
    private Hashtable<String, Integer> frequentItemSet;
    private ArrayList<Member> members = new ArrayList<>();
    private ArrayList<String> allDates;
    private ArrayList<String> allItems;
    private TreeSet<String> itemTreeSet;
    File file = new File("Groceries_dataset.csv");


    public CSVReader() throws IOException {
        allDates = new ArrayList<>();


        memberIDList = new ArrayList<>();
        memberIDTreeSet = new TreeSet<>();
        allItems = new ArrayList<>();
        itemTreeSet = new TreeSet<>();
        frequentItemSet = new Hashtable<>();

        readCSV();
    }

    private void readCSV() throws IOException {
        String line = "";
        BufferedReader reader = new BufferedReader(new FileReader(file));
        while ((line = reader.readLine()) != null) {
            if (!line.contains("Member_number") || !line.contains("itemDescription")) {
                String REGEX = "^(\".*?\"|.*?),(\".*?\"|.*?),(\".*?\"|.*?)$";
                Pattern pattern = Pattern.compile(REGEX);
                Matcher matcher = pattern.matcher(line);
                matcher.find();

                int memberID = Integer.parseInt(matcher.group(1));
                String transactionDate = matcher.group(2);
                String item = matcher.group(3);

                allItems.add(item);
                itemTreeSet.add(item);
                allDates.add(transactionDate);
                memberIDList.add(memberID);
                memberIDTreeSet.add(memberID);

            }
        }
        reader.close();
        generateTransactions();
    }

    private void generateTransactions() {
        for (int memberID : memberIDTreeSet) {
                Member currentMember = new Member(memberID);
                TreeSet<String> memberPurchaseDates = getMemberPurchaseDates(memberID);

                for (String date : memberPurchaseDates) {
                    // Creates a receipt in the "Member.java" class for the current member.
                    currentMember.receipts.put(date, fillReceipt(memberID, date));
                }
                members.add(currentMember);
            }
        }

    private ArrayList<String> fillReceipt(int memberID, String date) {
        ArrayList<String> receiptItems = new ArrayList<>();
        for (int i = 0; i < allDates.size(); i++) {
            if (memberID == memberIDList.get(i) && allDates.get(i).equalsIgnoreCase(date)) {
                receiptItems.add(allItems.get(i));
            }
        }
        return receiptItems;
    }


    private TreeSet<String> getMemberPurchaseDates(int memberID) {
        TreeSet<String> memberPurchaseDates = new TreeSet<>();
        for (int i = 0; i < allDates.size(); i++) {
            if (memberID == memberIDList.get(i)) {
                memberPurchaseDates.add(allDates.get(i));
            }
        }
        return memberPurchaseDates;
    }

    // Getters
    public ArrayList<String> getAllDates() {
        return allDates;
    }

    public ArrayList<String> getAllItems() {
        return allItems;
    }

    public TreeSet<String> getItemTreeSet() {
        return itemTreeSet;
    }

    public ArrayList<Member> getMembers() {
        return members;
    }
}

