package com.example.saveup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.saveup.model.Category;
import com.example.saveup.model.Transaction;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddTransaction extends AppCompatActivity {

    public static final String CREATED_EXPENSE = "created_expense";

    private String[] categories; // Categorias de la transacción
    private AutoCompleteTextView autocompleteCategory;
    private TextInputLayout autocompleteCategoryLayout;
    private Button buttonCancel;
    private Button buttonAdd;
    private TextInputEditText etTitle;
    private TextInputLayout etTitleLayout;
    private TextInputEditText etValue;
    private TextInputLayout etValueLayout;
    private TextInputEditText etDate;
    private TextInputLayout etDateLayout;
    private TextInputEditText etDescription;
    private ArrayAdapter<String> categoryAdapter;
    private RadioButton rbExpense;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        initializeVariables();

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Transaction transaction = validateTransactionData();
                if (transaction != null) {
                    Intent data = new Intent();
                    data.putExtra(CREATED_EXPENSE, transaction);
                    setResult(Activity.RESULT_OK, data);
                    finish();
                }
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });
    }

    private void initializeVariables() {
        buttonCancel = findViewById(R.id.tbCancel);
        buttonAdd = findViewById(R.id.tbAdd);
        etTitle = findViewById(R.id.etExpenseTitle);
        etTitleLayout = findViewById(R.id.outlinedTextFieldTitle);
        etValue = findViewById(R.id.etExpenseQuantity);
        etValueLayout = findViewById(R.id.outlinedTextFieldQuantity);
        etDate = findViewById(R.id.etExpenseDate);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        etDate.setText(sdf.format(new Date()));
        etDateLayout = findViewById(R.id.datePickerLayout);
        etDescription = findViewById(R.id.etDescription);
        rbExpense = findViewById(R.id.rb_gasto);
        etTitle.addTextChangedListener(new ValidationTextWatcher(etTitleLayout));
        etTitle.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                validateFocusField(etTitleLayout, hasFocus);
            }
        });
        etValue.addTextChangedListener(new ValidationTextWatcher(etValueLayout));
        etValue.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                validateFocusField(etValueLayout, hasFocus);
            }
        });
        etDate.addTextChangedListener(new ValidationTextWatcher(etDateLayout));
        etDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                validateFocusField(etDateLayout, hasFocus);
            }
        });

        categories = Category.enumToStringArray();
        categoryAdapter = new ArrayAdapter<>(this, R.layout.list_item, categories);
        autocompleteCategory = findViewById(R.id.autocompleteCategory);
        autocompleteCategory.setAdapter(categoryAdapter);
        autocompleteCategoryLayout = findViewById(R.id.menuCategory);

        autocompleteCategory.addTextChangedListener(new ValidationTextWatcher(autocompleteCategoryLayout));
        autocompleteCategory.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                validateFocusField(autocompleteCategoryLayout, hasFocus);
            }
        });

//        // Experimento fallido, lo dejo por aqui para acordarnos de volver a intentar implementarlo en un futuro
//        MaterialDatePicker datePicker =
//                MaterialDatePicker.Builder.datePicker()
//                        .setTitleText("Select transaction date")
//                        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
//                        .setInputMode(MaterialDatePicker.INPUT_MODE_TEXT)
//                        .build();
//        datePicker.show(supportFragmentManager, "tag");
    }

    private boolean validateFocusField(TextInputLayout component, boolean hasFocus) {
        if (!hasFocus) {
            if (component.getEditText().getText().toString().isEmpty()) {
                component.setError(getResources().getString(R.string.errCampoVacio));
                return false;
            }
        } else {
            component.setError(null);
        }
        return true;
    }

    private Transaction validateTransactionData() {
        String name = validateTextField(etTitleLayout);
        if (name == null) return null;

        String txValue = validateTextField(etValueLayout); // Falta validación adicional (decimales, etc.)
        if (txValue == null) return null;
        double value = Double.parseDouble(txValue);

        boolean isExpense = rbExpense.isChecked();

        String txCategory = validateTextField(autocompleteCategoryLayout);
        if (txCategory == null) return null;
        Category cat = Category.valueOf(txCategory);

        Date date = validateDate();
        if (date == null) return null;

        String description = etDescription.getText().toString();

        // SE ME AÑADEN DEL REVES LOS COSTES, CHEQUEAR ESO
        Transaction transaction = new Transaction(isExpense, name, value, cat, date, description);
        return transaction;
    }

    private String validateTextField(TextInputLayout etLayout) {
        String str = etLayout.getEditText().getText().toString().trim();
        if (str.isEmpty()) {
            etLayout.setError(getResources().getString(R.string.errCampoVacio));
            etLayout.getEditText().requestFocus();
            return null;
        }
        return str;
    }

    private Date validateDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        sdf.setLenient(false);
        Date date;
        try {
            date = sdf.parse(etDate.getText().toString());
        } catch (ParseException e) {
            etDateLayout.setError(getResources().getString(R.string.errFechaInvalida));
            etDate.requestFocus();
            return null;
        }
        return date;
    }

    private class ValidationTextWatcher implements TextWatcher {
        private TextInputLayout etLayout;
        private ValidationTextWatcher(TextInputLayout etLayout) {
            this.etLayout = etLayout;
        }
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }
        public void afterTextChanged(Editable editable) {
            etLayout.setError(null);
        }
    }

}