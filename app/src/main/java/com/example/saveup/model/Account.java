package com.example.saveup.model;

import java.util.ArrayList;

public class Account {

    private String ID;
    private String userName;
    private String email;
    private String password;
    private ArrayList<Transaction> transactionsList;
    private double balance;

    public Account(String ID, String email, String password) {
        this.ID = ID;
        this.email = email;
        this.password = password;
        this.transactionsList = new ArrayList<>();
        this.balance = 0;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getUserName() {
        return userName;
    }

    public Account setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ArrayList<Transaction> getTransactionsList() {
        return transactionsList;
    }

    public Account setTransactionsList(ArrayList<Transaction> transactionsList) {
        this.transactionsList = transactionsList;
        calculateBalance();
        return this;
    }

    public double getBalance() {
        return balance;
    }

    public Account setBalance(double balance) {
        this.balance = balance;
        return this;
    }

    public void calculateBalance(){
        double balance = 0;
        for (Transaction t: transactionsList) {
            balance += t.getValue();
        }
        setBalance(balance);
    }

    public void addTransaction(Transaction transaction) {
        transactionsList.add(transaction);
    }
}
