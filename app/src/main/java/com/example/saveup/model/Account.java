package com.example.saveup.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Account implements Parcelable {

    public static final Creator<Account> CREATOR = new Creator<Account>() {
        @Override
        public Account createFromParcel(Parcel in) {
            return new Account(in);
        }

        @Override
        public Account[] newArray(int size) {
            return new Account[size];
        }
    };
    private final TransactionManager transactionManager;
    private String ID;
    private String userName;
    private String email;
    private String password;
    private double balance;
    private final CollectionReference bd = FirebaseFirestore.getInstance()
            .collection("users")
            .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
            .collection("transactions");

    public Account(String ID, String userName, String email, String password) {
        this.ID = ID;
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.transactionManager = new TransactionManager(new ArrayList<>());
        this.balance = 0;
    }

    protected Account(Parcel in) {
        ID = in.readString();
        userName = in.readString();
        email = in.readString();
        password = in.readString();
        transactionManager = in.readParcelable(TransactionManager.class.getClassLoader());
        balance = in.readDouble();
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

    public ArrayList<Transaction> getFilteredTransactionsList(int filter) {
        return transactionManager.getFilteredTransactionsList(filter);
    }

    public double getBalance() {
        return balance;
    }

    private Account setBalance(double balance) {
        this.balance = balance;
        return this;
    }

    public void addTransaction(Transaction transaction) {
        DocumentReference docRef = bd.document();
        transaction.setTransactionID(docRef.getId());
        transactionManager.addTransaction(transaction);
        setBalance(transactionManager.getBalance());
        docRef.set(transaction.toFirestore());
    }

    public void removeTransaction(Transaction transaction) {
        transactionManager.removeTransaction(transaction);
        setBalance(transactionManager.getBalance());
        bd.document(transaction.getTransactionID()).delete();
    }

    public void modifyTransaction(Transaction transactionOld, Transaction transactionNew) {
        transactionNew.setTransactionID(transactionOld.getTransactionID());
        transactionManager.removeTransaction(transactionOld);
        transactionManager.addTransaction(transactionNew);
        setBalance(transactionManager.getBalance());
        bd.document(transactionNew.getTransactionID())
                .set(transactionNew.toFirestore());
    }

    public String getStrBalance() {
        return String.format(Locale.getDefault(), "%.2f", getBalance());
    }

    public Map<Category, Double> getCategories(int year, boolean areExpenses) {
        return transactionManager.getCategories(year, areExpenses);
    }

    public Map<Integer, List<Transaction>> getGroupedTransactions(int year) {
        return transactionManager.getGroupedTransactions(year);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(ID);
        parcel.writeString(userName);
        parcel.writeString(email);
        parcel.writeString(password);
        parcel.writeParcelable(transactionManager, 1);
        parcel.writeDouble(balance);
    }
}
