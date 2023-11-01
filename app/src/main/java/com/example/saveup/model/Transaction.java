package com.example.saveup.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Date;
import java.util.Locale;

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

    protected Transaction(Parcel in) {
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

    public Transaction(boolean isExpense, String name, double value, String description) {
        this.name = name;
        this.value = value;
        this.description = description;
        this.isExpense = isExpense;
        this.category = Category.OTROS;
        this.date = new Date(1);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeByte((byte) (isExpense ? 1 : 0));
        parcel.writeString(name);
        parcel.writeDouble(value);
        parcel.writeString(category.name());
        parcel.writeLong(date.getTime());
        parcel.writeString(description);
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

    public String getStrSignedValue() {
        return String.format(Locale.getDefault(), "%.2f", getSignedValue());
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "isExpense=" + isExpense +
                ", name='" + name + '\'' +
                ", value=" + value +
                ", category=" + category +
                ", date=" + date +
                ", description='" + description + '\'' +
                '}';
    }
}
