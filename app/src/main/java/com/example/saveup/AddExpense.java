package com.example.saveup;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.example.saveup.databinding.ActivityAddExpenseBinding;
import com.example.saveup.model.Category;
import com.example.saveup.model.Transaction;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AddExpense extends AppCompatActivity {

    public static final String CREATED_EXPENSE = "created_expense";

    private ActivityAddExpenseBinding binding;

    private String[] categories; // Categorias de la transacción
    private AutoCompleteTextView autocompleteCategory;
    private Button buttonCancel;
    private Button buttonAdd;
    private TextInputEditText etTitle;
    private TextInputEditText etValue;
    private TextInputEditText etDate;
    private TextInputEditText etDescription;
    private ArrayAdapter<String> categoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAddExpenseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initializeVariables();

        autocompleteCategory.setAdapter(categoryAdapter);

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Category cat = Category.valueOf(autocompleteCategory.getText().toString());

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Date date = null;
                try {
                    date = sdf.parse(etDate.getText().toString());
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

                Transaction transaction = new Transaction(true, etTitle.getText().toString(),
                        Double.parseDouble(String.valueOf(etValue.getText()).toString()), cat, date,
                        etDescription.getText().toString());
                //Paso el modo de apertura
                Intent intent=new Intent (AddExpense.this, MainActivity.class);
                intent.putExtra(CREATED_EXPENSE, transaction);

                startActivity(intent);
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentAddExpense = new Intent(AddExpense.this, MainActivity.class);
                startActivity(intentAddExpense);
            }
        });
    }

    private void initializeVariables() {
        categories = Category.enumToStringArray();
        autocompleteCategory = findViewById(R.id.autocompleteCategory);
        buttonCancel = findViewById(R.id.tbCancel);
        buttonAdd = findViewById(R.id.tbAdd);
        etTitle = findViewById(R.id.etExpenseTitle);
        etValue = findViewById(R.id.etExpenseQuantity);
        etDate = findViewById(R.id.etExpenseDate);
        etDescription = findViewById(R.id.etDescription);

        categoryAdapter = new ArrayAdapter<>(this, R.layout.list_item, categories);
    }
}