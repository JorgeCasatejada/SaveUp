package com.example.saveup;

import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.example.saveup.databinding.ActivityAddExpenseBinding;

public class AddExpense extends AppCompatActivity {

    private ActivityAddExpenseBinding binding;

    private String[] categories; // Categorias de la transacción
    private AutoCompleteTextView autocompleteCategory;
    private ArrayAdapter<String> categoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAddExpenseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initializeElements();

        autocompleteCategory.setAdapter(categoryAdapter);
    }

    private void initializeElements() {
        categories = getResources().getStringArray(R.array.categories);
        Log.d("Categories", categories[0]);
        autocompleteCategory = findViewById(R.id.autocompleteCategory);
        categoryAdapter = new ArrayAdapter<>(this, R.layout.list_item, categories);
    }
}