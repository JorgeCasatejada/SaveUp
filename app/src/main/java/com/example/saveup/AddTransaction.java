package com.example.saveup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.saveup.model.Category;
import com.example.saveup.model.Transaction;
import com.example.saveup.ui.MainScreenFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddTransaction extends AppCompatActivity {

    public static final String CREATED_EXPENSE = "created_expense";
    public static final String DETAILS_TRANSACTION = "details_transaction";
    public static final String OLD_MODIFIED_TRANSACTION = "old_modified_transaction";
    public static final String NEW_MODIFIED_TRANSACTION = "new_modified_transaction";
    public static final String MODE = "action_mode";
    public static final int MODE_ADD = 1;
    public static final int MODE_DELETE = 2;
    public static final int MODE_MODIFY = 3;
    private String[] categories; // Categorias de la transacción
    private AutoCompleteTextView autocompleteCategory;
    private TextInputLayout autocompleteCategoryLayout;
    private Button buttonCancel;
    private Button buttonClose;
    private Button buttonAdd;
    private Button buttonModify;
    private Button buttonDelete;
    private Button buttonSaveChangues;
    private TextInputEditText etTitle;
    private TextInputLayout etTitleLayout;
    private TextInputEditText etValue;
    private TextInputLayout etValueLayout;
    private TextInputEditText etDate;
    private TextInputLayout etDateLayout;
    private TextInputEditText etDescription;
    private ArrayAdapter<String> categoryAdapter;
    private RadioButton rbExpense;
    private RadioButton rbIncome;
    private SimpleDateFormat sdf;
    private Transaction transactionDetails = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        initializeVariables();

        //Mirar si viene de añadir o de detalles
        Intent intent = getIntent();
        int mode = intent.getIntExtra(MainScreenFragment.ACTIVITY_MODE, 1);
        if (mode == 1){
            showAddMode();
        } else if (mode ==2) {
            transactionDetails = intent.getParcelableExtra(MainScreenFragment.TRANSACTION_DETAILS);
            showDetailsMode(transactionDetails);
        }

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Transaction transaction = validateTransactionData();
                if (transaction != null) {
                    Intent data = new Intent();
                    data.putExtra(CREATED_EXPENSE, transaction);
                    data.putExtra(MODE, MODE_ADD);
                    setResult(Activity.RESULT_OK, data);
                    finish();
                }
            }
        });

        View.OnClickListener cancelClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        };

        buttonCancel.setOnClickListener(cancelClickListener);
        buttonClose.setOnClickListener(cancelClickListener);

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent();
                data.putExtra(DETAILS_TRANSACTION, transactionDetails);
                data.putExtra(MODE, MODE_DELETE);
                setResult(Activity.RESULT_OK, data);
                finish();
            }
        });

        buttonModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etTitle.setEnabled(true);
                etValue.setEnabled(true);
                rbExpense.setEnabled(true);
                rbIncome.setEnabled(true);
                rbExpense.setEnabled(true);
                autocompleteCategory.setEnabled(true);
                autocompleteCategory.setFocusableInTouchMode(true);
                etDate.setEnabled(true);
                etDescription.setEnabled(true);
                loadCategories();
                etTitle.requestFocus();

                buttonDelete.setVisibility(View.GONE);
                buttonModify.setVisibility(View.GONE);
                buttonCancel.setVisibility(View.GONE);
                buttonClose.setVisibility(View.VISIBLE);
                buttonSaveChangues.setVisibility(View.VISIBLE);
            }
        });

        buttonSaveChangues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Transaction transaction = validateTransactionData();
                if (transaction != null) {
                    Intent data = new Intent();
                    data.putExtra(NEW_MODIFIED_TRANSACTION, transaction);
                    data.putExtra(OLD_MODIFIED_TRANSACTION, transactionDetails);
                    data.putExtra(MODE, MODE_MODIFY);
                    setResult(Activity.RESULT_OK, data);
                    finish();
                }
            }
        });
    }

    private void showAddMode() {
        //Visibilidad
        buttonCancel.setVisibility(View.VISIBLE);
        buttonAdd.setVisibility(View.VISIBLE);

        buttonClose.setVisibility(View.GONE);
        buttonModify.setVisibility(View.GONE);
        buttonDelete.setVisibility(View.GONE);
        buttonSaveChangues.setVisibility(View.GONE);
    }

    private void showDetailsMode(Transaction transaction) {
        //Visibilidad
        buttonCancel.setVisibility(View.GONE);
        buttonAdd.setVisibility(View.GONE);
        buttonSaveChangues.setVisibility(View.GONE);

        buttonClose.setVisibility(View.VISIBLE);
        buttonModify.setVisibility(View.VISIBLE);
        buttonDelete.setVisibility(View.VISIBLE);

        //Añadir datos
        etTitle.setText(transaction.getName());
        etValue.setText(String.valueOf(transaction.getValue()));
        if (transaction.isExpense()){
            rbExpense.setChecked(true);
            rbIncome.setChecked(false);
        } else {
            rbExpense.setChecked(false);
            rbIncome.setChecked(true);
        }
        autocompleteCategory.setText(categoryAdapter.getItem(Category.getIndex(transaction.getCategory())));
        etDate.setText(sdf.format(transaction.getDate()));
        etDescription.setText(transaction.getDescription());

        //Que no se puedan modificar
        etTitle.setEnabled(false);
        etValue.setEnabled(false);
        rbExpense.setEnabled(false);
        rbIncome.setEnabled(false);
        rbExpense.setEnabled(false);
        autocompleteCategory.setEnabled(false);
        autocompleteCategory.setFocusableInTouchMode(false);
        etDate.setEnabled(false);
        etDescription.setEnabled(false);
    }

    private void initializeVariables() {
        buttonCancel = findViewById(R.id.btCancel);
        buttonClose = findViewById(R.id.btClose);
        buttonModify = findViewById(R.id.btModify);
        buttonDelete = findViewById(R.id.btDelete);
        buttonAdd = findViewById(R.id.btAdd);
        buttonSaveChangues = findViewById(R.id.btSaveChangues);
        etTitle = findViewById(R.id.etExpenseTitle);
        etTitleLayout = findViewById(R.id.outlinedTextFieldTitle);
        etValue = findViewById(R.id.etExpenseQuantity);
        etValueLayout = findViewById(R.id.outlinedTextFieldQuantity);
        etDate = findViewById(R.id.etExpenseDate);
        sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        etDate.setText(sdf.format(new Date()));
        etDateLayout = findViewById(R.id.datePickerLayout);
        etDescription = findViewById(R.id.etDescription);
        rbExpense = findViewById(R.id.rb_gasto);
        rbIncome = findViewById(R.id.rb_ingreso);
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

        loadCategories();

//        // Experimento fallido, lo dejo por aqui para acordarnos de volver a intentar implementarlo en un futuro
//        MaterialDatePicker datePicker =
//                MaterialDatePicker.Builder.datePicker()
//                        .setTitleText("Select transaction date")
//                        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
//                        .setInputMode(MaterialDatePicker.INPUT_MODE_TEXT)
//                        .build();
//        datePicker.show(supportFragmentManager, "tag");
    }

    private void loadCategories() {
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