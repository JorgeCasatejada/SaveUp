package com.example.saveup.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.example.saveup.model.firestore.FireTransaction;

import java.util.Date;
import java.util.Objects;

public class Transaction implements Parcelable {
    public static final Creator<Transaction> CREATOR = new Creator<Transaction>() {
        @Override
        public Transaction createFromParcel(Parcel in) {
            return new Transaction(in);
        }

        @Override
        public Transaction[] newArray(int size) {
            return new Transaction[size];
        }
    };
    private final boolean isExpense; //true = gasto
    private final String name;
    private final double value;
    private final Category category;
    private final Date date;
    private final String description;
    private String transactionID;

    protected Transaction(Parcel in) {
        transactionID = in.readString();
        isExpense = in.readByte() != 0;
        name = in.readString();
        value = in.readDouble();
        category = Category.valueOf(in.readString());
        date = new Date(in.readLong());
        description = in.readString();
    }

    public Transaction(boolean isExpense, String name, double value, Category category, Date date, String description) {
        this.isExpense = isExpense;
        this.name = name;
        this.value = value;
        this.category = category;
        this.date = date;
        this.description = description;
    }

    public Transaction(FireTransaction fireTransaction) {
        this.transactionID = fireTransaction.getTransactionID();
        this.isExpense = fireTransaction.isExpense;
        this.name = fireTransaction.getName();
        this.value = fireTransaction.getValue();
        this.category = fireTransaction.getCategory();
        this.date = fireTransaction.getDate();
        this.description = fireTransaction.getDescription();
    }

    public FireTransaction toFirestore() {
        return new FireTransaction(
                this.transactionID,
                this.isExpense,
                this.name,
                this.value,
                this.category,
                this.date,
                this.description
        );
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(transactionID);
        parcel.writeByte((byte) (isExpense ? 1 : 0));
        parcel.writeString(name);
        parcel.writeDouble(value);
        parcel.writeString(category.name());
        parcel.writeLong(date.getTime());
        parcel.writeString(description);
    }

    public String getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }

    public boolean isExpense() {
        return isExpense;
    }

    public String getName() {
        return name;
    }

    public double getValue() {
        return value;
    }

    public Category getCategory() {
        return category;
    }

    public Date getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public double getSignedValue() {
        if (isExpense()) return (-1) * getValue();
        return getValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return isExpense == that.isExpense && Double.compare(that.value, value) == 0 && Objects.equals(name, that.name) && category == that.category && Objects.equals(date, that.date) && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isExpense, name, value, category, date, description);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionID='" + transactionID + '\'' +
                ", isExpense=" + isExpense +
                ", name='" + name + '\'' +
                ", value=" + value +
                ", category=" + category +
                ", date=" + date +
                ", description='" + description + '\'' +
                '}';
    }
}
