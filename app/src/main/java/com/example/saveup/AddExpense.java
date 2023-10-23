package com.example.saveup;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.example.saveup.databinding.ActivityAddExpenseBinding;

public class AddExpense extends AppCompatActivity {

    private ActivityAddExpenseBinding binding;

    private String[] categories; // Categorias de la transacción
    private AutoCompleteTextView autocompleteCategory;
    private Button buttonCancel;
    private ArrayAdapter<String> categoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAddExpenseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initializeVariables();

        autocompleteCategory.setAdapter(categoryAdapter);

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentAddExpense = new Intent(AddExpense.this, MainActivity.class);
                startActivity(intentAddExpense);
            }
        });
    }

    private void initializeVariables() {
        categories = getResources().getStringArray(R.array.categories);
        autocompleteCategory = findViewById(R.id.autocompleteCategory);
        buttonCancel = findViewById(R.id.tbCancel);

        categoryAdapter = new ArrayAdapter<>(this, R.layout.list_item, categories);
    }
}