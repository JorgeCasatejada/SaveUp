package com.example.saveup.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class TransactionManager implements Parcelable {

    public static final Creator<TransactionManager> CREATOR = new Creator<TransactionManager>() {
        @Override
        public TransactionManager createFromParcel(Parcel in) {
            return new TransactionManager(in);
        }

        @Override
        public TransactionManager[] newArray(int size) {
            return new TransactionManager[size];
        }
    };
    private double balance;
    private ArrayList<Transaction> transactionsList;

    public TransactionManager(ArrayList<Transaction> transactionsList) {
        setTransactionsList(transactionsList);
    }

    protected TransactionManager(Parcel in) {
        balance = in.readDouble();
        transactionsList = in.createTypedArrayList(Transaction.CREATOR);
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

    private double reCalculateBalance() {
        double balance = 0;
        for (Transaction t : transactionsList) {
            balance += t.getSignedValue();
        }
        return balance;
    }

    public void addTransaction(Transaction transaction) {
        transactionsList.add(transaction);
        Collections.sort(transactionsList, Collections.reverseOrder(Comparator.comparing(Transaction::getDate)));
        balance += transaction.getSignedValue();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeDouble(balance);
        parcel.writeList(transactionsList);
    }
}
