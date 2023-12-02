package com.example.saveup.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.saveup.AddTransaction;
import com.example.saveup.R;
import com.example.saveup.TransactionsListAdapter;
import com.example.saveup.databinding.FragmentMainScreenBinding;
import com.example.saveup.model.Account;
import com.example.saveup.model.Transaction;
import com.example.saveup.model.TransactionManager;

// Fragmento para la pantalla principal
public class MainScreenFragment extends Fragment {

    // Constants for intent extras
    public static final String TRANSACTION_DETAILS = "transaction_details";
    public static final String ACTIVITY_MODE = "activity_mode";
    private static final int MODE_ADD = 1;
    private static final int MODE_DETAILS = 2;
    public static final int INTENT_ADD_TRANSACTION = 1;
    private static final String ACCOUNT = "Account";

    // View binding
    private FragmentMainScreenBinding binding;

    // User account
    private Account account;

    // TransactionsListAdapter
    private TransactionsListAdapter ltAdapter;

    // Expense/Income filter
    private int appliedFilter;


    public static MainScreenFragment newInstance(Account account) {
        MainScreenFragment fragment = new MainScreenFragment();
        Bundle args = new Bundle();
        //Esto no tiene mucha ciencia -> Clave, valor.
        args.putParcelable(ACCOUNT, account);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            account = getArguments().getParcelable(ACCOUNT);
        }
    }

    /* Al crear la vista, cargamos los valores necesarios */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the fragment layout using View Binding
        binding = FragmentMainScreenBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        initializeUI();
        setUpRecyclerView();
        setClickListeners();
        updateColor();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(resultCode, resultCode, data);
        if (requestCode == INTENT_ADD_TRANSACTION && resultCode == Activity.RESULT_OK) {
            handleTransactionResult(data);
        }
    }

    private void initializeUI() {
        // RecyclerTransactions
        binding.recyclerTransactions.setHasFixedSize(true);

        // Balance
        binding.etBalance.setText(account.getStrBalance());

        // Expense/Income filter
        appliedFilter = 0;
    }

    private void setUpRecyclerView() {
        binding.recyclerTransactions.setLayoutManager(new LinearLayoutManager(getContext()));
        ltAdapter = new TransactionsListAdapter(requireContext(), account.getTransactionsList(), this::clickOnItem);
        binding.recyclerTransactions.setAdapter(ltAdapter);
    }

    private void setClickListeners() {
        binding.fabAdd.setOnClickListener(v -> {
            Intent intentAddTransaction = new Intent(getActivity(), AddTransaction.class);
            intentAddTransaction.putExtra(ACTIVITY_MODE, MODE_ADD);
            startActivityForResult(intentAddTransaction, INTENT_ADD_TRANSACTION);
        });

        binding.toggleButton.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                handleToggleFilter(checkedId);
            }
        });
    }

    private void handleToggleFilter(int checkedId) {
        if (checkedId == R.id.filterIncome) {
            appliedFilter = TransactionManager.FILTER_INCOMES;
        } else if (checkedId == R.id.filterExpense) {
            appliedFilter = TransactionManager.FILTER_EXPENSES;
        } else if (checkedId == R.id.filterAll) {
            appliedFilter = TransactionManager.FILTER_ALL;
        }
        ltAdapter.setTransactionsList(account.getFilteredTransactionsList(appliedFilter));
    }

    private void handleTransactionResult(Intent data) {
        int mode = data.getIntExtra(AddTransaction.MODE, 0);
        switch (mode) {
            case AddTransaction.MODE_ADD:
                account.addTransaction(data.getParcelableExtra(AddTransaction.CREATED_TRANSACTION));
                break;
            case AddTransaction.MODE_DELETE:
                account.removeTransaction(data.getParcelableExtra(AddTransaction.DETAILS_TRANSACTION));
                break;
            case AddTransaction.MODE_MODIFY:
                Transaction transactionOld = data.getParcelableExtra(AddTransaction.OLD_MODIFIED_TRANSACTION);
                Transaction transactionNew = data.getParcelableExtra(AddTransaction.NEW_MODIFIED_TRANSACTION);
                account.modifyTransaction(transactionOld, transactionNew);
                break;
        }

        binding.etBalance.setText(account.getStrBalance());
        ltAdapter.setTransactionsList(account.getFilteredTransactionsList(appliedFilter));
        updateColor();
    }

    private void updateColor() {
        int color;
        if (account.getBalance() > 0)
            color = getResources().getColor(R.color.greenBalance);
        else
            color = getResources().getColor(R.color.redBalance);
        binding.outlinedTextFieldBalance.setBoxBackgroundColor(color);
    }

    private void clickOnItem(Transaction transaction) {
        //Mostar activity_add_transaction con 3 botones (cancelar, modificar, eliminar)
        Intent intentAddTransaction = new Intent(getActivity(), AddTransaction.class);
        intentAddTransaction.putExtra(ACTIVITY_MODE, MODE_DETAILS);
        intentAddTransaction.putExtra(TRANSACTION_DETAILS, transaction);
        startActivityForResult(intentAddTransaction, INTENT_ADD_TRANSACTION);
    }

}
