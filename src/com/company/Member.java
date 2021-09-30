package com.company;

import java.util.ArrayList;
import java.util.HashMap;

// This class includes the member ID and the respective receipts of that member.
public class Member {

    // Member ID of customer
    private int memberID;
    // All of the receipts from this customer. Key is Date and Value is List of all items on that date.
    public HashMap<String, ArrayList<String>> receipts;

    public Member(int memberID) {
        this.memberID = memberID;
        this.receipts = new HashMap<>();
    }

    // Returns all of the member's receipts
    public ArrayList<ArrayList<String>> getAllReceiptsForCurrentMember() {
        // Holds all of the member's receipts and the items in them
        ArrayList<ArrayList<String>> receiptLog = new ArrayList<>();
            // Searches for a receipt on a certain date
            for (String date : receipts.keySet()) {
                // Adds the receipt to the receipt log of the member
                receiptLog.add(receipts.get(date));
            }
            // Return all of the member's receipts in a list
            return receiptLog;
    }

}

