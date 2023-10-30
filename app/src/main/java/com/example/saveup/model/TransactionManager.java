package com.example.saveup.model;

import androidx.recyclerview.widget.SortedList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

public class TransactionManager {

    private double balance;
    private ArrayList<Transaction> transactionsList;

    public TransactionManager(ArrayList<Transaction> transactionsList) {
        setTransactionsList(transactionsList);
    }

    public ArrayList<Transaction> getTransactionsList() {
        return transactionsList;
    }

    public TransactionManager setTransactionsList(ArrayList<Transaction> transactionsList) {
        this.transactionsList = transactionsList;
        Collections.sort(transactionsList, Collections.reverseOrder(Comparator.comparing(Transaction::getDate)));
        this.balance = reCalculateBalance();
        return this;
    }

    public double getBalance() {
        return balance;
    }

    private TransactionManager setBalance(double balance) {
        this.balance = balance;
        return this;
    }

    private double reCalculateBalance(){
        double balance = 0;
        for (Transaction t: transactionsList) {
            balance += t.getSignedValue();
        }
        return balance;
    }

    public void addTransaction(Transaction transaction){
        transactionsList.add(transaction);
        Collections.sort(transactionsList, Collections.reverseOrder(Comparator.comparing(Transaction::getDate)));
        balance += transaction.getSignedValue();
    }
}
