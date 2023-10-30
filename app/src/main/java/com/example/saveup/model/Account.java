package com.example.saveup.model;

import android.util.Log;

import java.util.ArrayList;
import java.util.Locale;

public class Account {

    private String ID;
    private String userName;
    private String email;
    private String password;
    private TransactionManager transactionManager;
    private double balance;

    public Account(String ID, String email, String password) {
        this.ID = ID;
        this.email = email;
        this.password = password;
        this.transactionManager = new TransactionManager(new ArrayList<>());
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
        return transactionManager.getTransactionsList();
    }

    public Account setTransactionsList(ArrayList<Transaction> transactionsList) {
        transactionManager.setTransactionsList(transactionsList);
        setBalance(transactionManager.getBalance());
        return this;
    }

    public double getBalance() {
        return balance;
    }

    private Account setBalance(double balance) {
        this.balance = balance;
        return this;
    }

    public void addTransaction(Transaction transaction) {
        transactionManager.addTransaction(transaction);
        setBalance(transactionManager.getBalance());
    }

    public String getStrBalance(){
        return String.format(Locale.getDefault(),"%.2f", getBalance());
    }
}
