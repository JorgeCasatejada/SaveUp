package com.example.saveup.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    /**
     * Crea un mapa con clave Category y valor Double que contiene el total para la categoría
     * @param year año
     * @param areExpenses indica si se obtiene el total para ingreso (false) o gasto (true)
     * @return un mapa conteniendo el total
     */
    public Map<Category, Double> getCategories(int year, boolean areExpenses) {
        return getTransactionsList().stream()
                .filter(t -> t.getDate().getYear() + 1900 == year && t.isExpense() == areExpenses)
                .collect(Collectors.groupingBy(Transaction::getCategory,
                        Collectors.mapping(Transaction::getValue,
                                Collectors.summingDouble(Double::doubleValue))));
    }

    /**
     * Crea un mapa con clave Integer que es el mes del año y valor lista de Transaction que son las
     * transacciones de dicho mes
     * @param year año
     * @return un mapa conteniendo las transacciones de un año agrupadas por mes
     */
    public Map<Integer, List<Transaction>> getGroupedTransactions(int year) {
        return getTransactionsList().stream().filter(t -> t.getDate().getYear() + 1900 == year)
                .collect(Collectors.groupingBy(t -> t.getDate().getMonth()));
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
