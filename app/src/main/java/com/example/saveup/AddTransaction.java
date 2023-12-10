package com.example.saveup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.example.saveup.databinding.ActivityAddTransactionBinding;
import com.example.saveup.databinding.ContentScrollingBinding;
import com.example.saveup.model.Category;
import com.example.saveup.model.Transaction;
import com.example.saveup.ui.MainScreenFragment;
import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddTransaction extends AppCompatActivity {

    // Constants
    public static final String CREATED_TRANSACTION = "created_transaction";
    public static final String DETAILS_TRANSACTION = "details_transaction";
    public static final String OLD_MODIFIED_TRANSACTION = "old_modified_transaction";
    public static final String NEW_MODIFIED_TRANSACTION = "new_modified_transaction";
    public static final String MODE = "action_mode";
    public static final int MODE_ADD = 1;
    public static final int MODE_DELETE = 2;
    public static final int MODE_MODIFY = 3;
    public static final int FROM_MODE_ADD = 1;
    public static final int FROM_MODE_DETAILS = 2;

    // View Binding
    private ActivityAddTransactionBinding binding;
    private ContentScrollingBinding contentBinding;

    // Transaction Details
    private Transaction transactionDetails = null;

    // Main fab clicked
    private boolean clicked = false;

    // Categories
    private String[] categories; // Categorias de la transacción
    private ArrayAdapter<String> categoryAdapter;

    // Date Format
    private SimpleDateFormat sdf;

    // Animations
    private Animation rotateOpen;
    private Animation rotateClose;
    private Animation fromBottom;
    private Animation toBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddTransactionBinding.inflate(getLayoutInflater());
        contentBinding = binding.contentScrolling;
        setContentView(binding.getRoot());

        initializeVariables();

        // Check if coming from add or details
        Intent intent = getIntent();
        int mode = intent.getIntExtra(MainScreenFragment.ACTIVITY_MODE, FROM_MODE_ADD);
        if (mode == FROM_MODE_ADD) {
            showAddMode();
        } else if (mode == FROM_MODE_DETAILS) {
            transactionDetails = intent.getParcelableExtra(MainScreenFragment.TRANSACTION_DETAILS);
            showDetailsMode(transactionDetails);
        }

        setClickListeners();
    }

    private void setClickListeners() {
        // Add Button Click Listener
        contentBinding.btAdd.setOnClickListener(v -> {
            Transaction transaction = validateTransactionData();
            if (transaction != null) {
                Intent data = new Intent();
                data.putExtra(CREATED_TRANSACTION, transaction);
                data.putExtra(MODE, MODE_ADD);
                setResult(Activity.RESULT_OK, data);
                finish();
            }
        });

        // Cancel Button Click Listeners
        View.OnClickListener cancelClickListener = v -> {
            setResult(Activity.RESULT_CANCELED);
            finish();
        };
        contentBinding.btCancel.setOnClickListener(cancelClickListener);
        contentBinding.btClose.setOnClickListener(cancelClickListener);
        contentBinding.btCloseDetails.setOnClickListener(cancelClickListener);

        // Delete Button Click Listener
        contentBinding.deleteFab.setOnClickListener(v -> {
            Intent data = new Intent();
            data.putExtra(DETAILS_TRANSACTION, transactionDetails);
            data.putExtra(MODE, MODE_DELETE);
            setResult(Activity.RESULT_OK, data);
            finish();
        });

        // Edit Button Click Listener
        contentBinding.editFab.setOnClickListener(v -> {
            enableEditFields();
            setVisibility();
        });

        // Save Changes Button Click Listener
        contentBinding.btSaveChangues.setOnClickListener(v -> {
            Transaction transaction = validateTransactionData();
            if (transaction != null) {
                Intent data = new Intent();
                data.putExtra(NEW_MODIFIED_TRANSACTION, transaction);
                data.putExtra(OLD_MODIFIED_TRANSACTION, transactionDetails);
                data.putExtra(MODE, MODE_MODIFY);
                setResult(Activity.RESULT_OK, data);
                finish();
            }
        });

        // Primary Fab Button Click Listener
        contentBinding.primaryFab.setOnClickListener(v -> {
            setVisibility();
            setAnimation();
            clicked = !clicked;
        });
    }

    private void enableEditFields() {
        // Enable modification
        setEnabledEditFields(true);
        loadCategories();
        contentBinding.etTransactionTitle.requestFocus();

        // Visibility
        contentBinding.cancelAddLayout.setVisibility(View.GONE);
        contentBinding.saveChanguesLayout.setVisibility(View.VISIBLE);
        contentBinding.fabContainer.setVisibility(View.GONE);
        contentBinding.btCloseDetails.setVisibility(View.GONE);
    }

    private void setVisibility() {
        int visibility = clicked ? View.GONE : View.VISIBLE;
        contentBinding.editFab.setVisibility(visibility);
        contentBinding.deleteFab.setVisibility(visibility);
    }

    private void setAnimation() {
        if (!clicked) {
            contentBinding.editFab.startAnimation(fromBottom);
            contentBinding.deleteFab.startAnimation(fromBottom);
            contentBinding.primaryFab.setAnimation(rotateOpen);
        } else {
            contentBinding.editFab.startAnimation(toBottom);
            contentBinding.deleteFab.startAnimation(toBottom);
            contentBinding.primaryFab.setAnimation(rotateClose);
        }
    }

    private void showAddMode() {
        // Visibility
        contentBinding.cancelAddLayout.setVisibility(View.VISIBLE);
        contentBinding.saveChanguesLayout.setVisibility(View.GONE);
        contentBinding.fabContainer.setVisibility(View.GONE);
        contentBinding.btCloseDetails.setVisibility(View.GONE);
    }

    private void showDetailsMode(Transaction transaction) {
        // Visibility
        contentBinding.cancelAddLayout.setVisibility(View.GONE);
        contentBinding.saveChanguesLayout.setVisibility(View.GONE);
        contentBinding.fabContainer.setVisibility(View.VISIBLE);
        contentBinding.btCloseDetails.setVisibility(View.VISIBLE);

        // Add data
        contentBinding.etTransactionTitle.setText(transaction.getName());
        contentBinding.etTransactionQuantity.setText(String.valueOf(transaction.getValue()));

        contentBinding.rbExpense.setChecked(transaction.isExpense());
        contentBinding.rbIncome.setChecked(!transaction.isExpense());

        contentBinding.autocompleteCategory.setText(categoryAdapter.getItem(Category.getIndex(transaction.getCategory())));
        contentBinding.etTransactionDate.setText(sdf.format(transaction.getDate()));
        contentBinding.etDescription.setText(transaction.getDescription());

        // Disable modification
        setEnabledEditFields(false);
    }

    private void setEnabledEditFields(boolean state) {
        contentBinding.etTransactionTitle.setEnabled(state);
        contentBinding.etTransactionQuantity.setEnabled(state);
        contentBinding.rbExpense.setEnabled(state);
        contentBinding.rbIncome.setEnabled(state);
        contentBinding.autocompleteCategory.setEnabled(state);
        contentBinding.autocompleteCategory.setFocusableInTouchMode(state);
        contentBinding.etTransactionDate.setEnabled(state);
        contentBinding.etDescription.setEnabled(state);
    }

    private void initializeVariables() {
        // FAB Animations
        rotateOpen = AnimationUtils.loadAnimation(this, R.anim.rotate_open_anim);
        rotateClose = AnimationUtils.loadAnimation(this, R.anim.rotate_close_anim);
        fromBottom = AnimationUtils.loadAnimation(this, R.anim.from_bottom_anim);
        toBottom = AnimationUtils.loadAnimation(this, R.anim.to_bottom_anim);

        // Date
        sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        contentBinding.etTransactionDate.setText(sdf.format(new Date()));
        contentBinding.etTransactionDate.addTextChangedListener(new ValidationTextWatcher(contentBinding.datePickerLayout));
        contentBinding.etTransactionDate.setOnFocusChangeListener((view, hasFocus) ->
                validateFocusField(contentBinding.datePickerLayout, hasFocus));

        // Title
        contentBinding.etTransactionTitle.addTextChangedListener(new ValidationTextWatcher(contentBinding.outlinedTextFieldTitle));
        contentBinding.etTransactionTitle.setOnFocusChangeListener((view, hasFocus) ->
                validateFocusField(contentBinding.outlinedTextFieldTitle, hasFocus));

        // Quantity
        contentBinding.etTransactionQuantity.addTextChangedListener(new ValidationTextWatcher(contentBinding.outlinedTextFieldQuantity));
        contentBinding.etTransactionQuantity.setOnFocusChangeListener((view, hasFocus) ->
                validateFocusField(contentBinding.outlinedTextFieldQuantity, hasFocus));

        // Categories
        loadCategories();
    }

    private void loadCategories() {
        categories = Category.enumToStringArray();
        categoryAdapter = new ArrayAdapter<>(this, R.layout.list_item, categories);
        contentBinding.autocompleteCategory.setAdapter(categoryAdapter);

        contentBinding.autocompleteCategory.addTextChangedListener(new ValidationTextWatcher(contentBinding.menuCategory));
        contentBinding.autocompleteCategory.setOnFocusChangeListener((view, hasFocus) ->
                validateFocusField(contentBinding.menuCategory, hasFocus));
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
        String name = validateTextField(contentBinding.outlinedTextFieldTitle);
        if (name == null) return null;

        String txValue = validateTextField(contentBinding.outlinedTextFieldQuantity);
        if (txValue == null) return null;
        double value = Double.parseDouble(txValue);
        value = Math.round(value * 100.0) / 100.0; // Redondeo a 2 decimales

        boolean isExpense = contentBinding.rbExpense.isChecked();

        String txCategory = validateTextField(contentBinding.menuCategory);
        if (txCategory == null) return null;
        Category cat = Category.valueOf(txCategory);

        Date date = validateDate();
        if (date == null) return null;

        String description = contentBinding.etDescription.getText().toString();

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
            date = sdf.parse(contentBinding.etTransactionDate.getText().toString());
        } catch (ParseException e) {
            contentBinding.datePickerLayout.setError(getResources().getString(R.string.errFechaInvalida));
            contentBinding.etTransactionDate.requestFocus();
            return null;
        }
        return date;
    }

    private class ValidationTextWatcher implements TextWatcher {
        private final TextInputLayout etLayout;

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