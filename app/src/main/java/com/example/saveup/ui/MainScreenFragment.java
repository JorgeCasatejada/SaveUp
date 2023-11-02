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
import androidx.recyclerview.widget.RecyclerView;

import com.example.saveup.AddTransaction;
import com.example.saveup.R;
import com.example.saveup.TransactionsListAdapter;
import com.example.saveup.model.Account;
import com.example.saveup.model.Transaction;
import com.example.saveup.model.TransactionManager;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

// Fragmento para la pantalla principal
public class MainScreenFragment extends Fragment {

    public static final String TRANSACTION_DETAILS = "transaction_details";
    public static final String ACTIVITY_MODE = "activity_mode";
    private static final int MODE_ADD = 1;
    private static final int MODE_DETAILS = 2;
    public static final int INTENT_ADD_TRANSACTION = 1;
    private static final String ACCOUNT = "Account";
    private Account account;
    private View root;
    private View mainLayout;
    private RecyclerView transactionsListView;
    private TransactionsListAdapter ltAdapter;
    private TextInputEditText etBalance;
    private MaterialButtonToggleGroup toggleButton;
    private FloatingActionButton fabAdd;
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

    private void initializeVariables() {
        mainLayout = root.findViewById(R.id.mainLayout);
        transactionsListView = root.findViewById(R.id.recyclerTransactions);
        transactionsListView.setHasFixedSize(true);
        etBalance = root.findViewById(R.id.etBalance);
        toggleButton = root.findViewById(R.id.toggleButton);
        fabAdd = root.findViewById(R.id.fabAdd);
        etBalance.setText(account.getStrBalance());
        appliedFilter = 0;
    }

    /* Al crear la vista, cargamos los valores necesarios */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Mostramos el fragmento en el contenedor
        root = inflater.inflate(R.layout.fragment_main_screen, container, false);

        initializeVariables();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        transactionsListView.setLayoutManager(layoutManager);

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentAddTransaction = new Intent(getActivity(), AddTransaction.class);
                intentAddTransaction.putExtra(ACTIVITY_MODE, MODE_ADD);
                startActivityForResult(intentAddTransaction, INTENT_ADD_TRANSACTION);
            }
        });

        ltAdapter = new TransactionsListAdapter(account.getTransactionsList(),
                new TransactionsListAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Transaction transaction) {
                        clickOnItem(transaction);
                    }
                });
        transactionsListView.setAdapter(ltAdapter);

        toggleButton.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if (isChecked) {
                    if (checkedId == R.id.filterIncome) {
                        appliedFilter = TransactionManager.FILTER_INCOMES;
                    } else if (checkedId == R.id.filterExpense) {
                        appliedFilter = TransactionManager.FILTER_EXPENSES;
                    } else if (checkedId == R.id.filterAll) {
                        appliedFilter = TransactionManager.FILTER_ALL;
                    }
                    ltAdapter.setTransactionsList(
                            account.getFilteredTransactionsList(appliedFilter));
                }
            }
        });

        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(resultCode, resultCode, data);
        if (requestCode == INTENT_ADD_TRANSACTION) {
            if (resultCode == Activity.RESULT_OK) {
                int mode = data.getIntExtra(AddTransaction.MODE, 0);
                if (mode == AddTransaction.MODE_ADD) {
                    Transaction transactionAdd = data.getParcelableExtra(AddTransaction.CREATED_EXPENSE);
                    account.addTransaction(transactionAdd);
                } else if (mode == AddTransaction.MODE_DELETE) {
                    Transaction transactionRemove = data.getParcelableExtra(AddTransaction.DETAILS_TRANSACTION);
                    account.removeTransaction(transactionRemove);
                } else if (mode == AddTransaction.MODE_MODIFY) {
                    Transaction transactionOld = data.getParcelableExtra(AddTransaction.OLD_MODIFIED_TRANSACTION);
                    Transaction transactionNew = data.getParcelableExtra(AddTransaction.NEW_MODIFIED_TRANSACTION);
                    account.modifyTransaction(transactionOld, transactionNew);
                }


                etBalance.setText(account.getStrBalance());
//                ((TransactionsListAdapter) transactionsListView.getAdapter()).updateData(TransactionsListAdapter.APPEND);
                ltAdapter.setTransactionsList(
                        account.getFilteredTransactionsList(appliedFilter));
            }
        }
    }

    private void clickOnItem(Transaction transaction) {
        //Mostar activity_add_transaction con 3 botones (cancelar, modificar, eliminar)
        Intent intentAddTransaction = new Intent(getActivity(), AddTransaction.class);
        intentAddTransaction.putExtra(ACTIVITY_MODE, MODE_DETAILS);
        intentAddTransaction.putExtra(TRANSACTION_DETAILS, transaction);
        startActivityForResult(intentAddTransaction, INTENT_ADD_TRANSACTION);
    }

    private void showSnackBar(String text) {
        Snackbar.make(mainLayout, text, Snackbar.LENGTH_LONG).show();
    }

}
