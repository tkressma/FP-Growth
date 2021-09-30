package com.company;

import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeSet;

public class Main {

	public static void main(String[] args) throws IOException {
		CSVReader parser = new CSVReader();
		ArrayList<Member> members = parser.getMembers();
		Receipts completedReceipts = new Receipts(members);
		int  minSupportThreshold = 104;
		ArrayList<ArrayList<String>> receipts = completedReceipts.getAllMemberReceipts();
		TreeSet<String> itemTreeSet = parser.getItemTreeSet();
		Tree fpt = new Tree(receipts, itemTreeSet, minSupportThreshold);
    }
}
