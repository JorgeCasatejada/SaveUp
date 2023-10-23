package com.example.saveup.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

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

    private final double value;
    private final String name;
    private final String description;

    public Transaction(double value, String name, String description) {
        this.value = value;
        this.name = name;
        this.description = description;
    }


    protected Transaction(Parcel in) {
        value = in.readDouble();
        name = in.readString();
        description = in.readString();
    }

    public double getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeDouble(value);
        parcel.writeString(name);
        parcel.writeString(description);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "value=" + value +
                ", name='" + name + '\'' +
                '}';
    }
}
