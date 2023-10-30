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
import com.example.saveup.MainActivity;
import com.example.saveup.R;
import com.example.saveup.TransactionsListAdapter;
import com.example.saveup.model.Account;
import com.example.saveup.model.Transaction;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

// Fragmento para la pantalla principal
public class MainScreenFragment extends Fragment {

    private View root;

    public static final int INTENT_ADD_TRANSACTION = 1;
    private View mainLayout;
    private RecyclerView transactionsListView;
    private TextInputEditText etBalance;
    private Account account;
    private FloatingActionButton fabAdd;

    private void initializeVariables() {
        mainLayout = root.findViewById(R.id.mainLayout);
        transactionsListView = root.findViewById(R.id.recyclerTransactions);
        transactionsListView.setHasFixedSize(true);
        etBalance = root.findViewById(R.id.etBalance);
        fabAdd = root.findViewById(R.id.fabAdd);

        account = new Account("1", "prueba@gmail.com", "pass")
                .setTransactionsList(loadTransactions());
        etBalance.setText(account.getStrBalance());
    }

    public static MainScreenFragment newInstance() {
        MainScreenFragment fragment = new MainScreenFragment();
        Bundle args = new Bundle();
        //Esto no tiene mucha ciencia -> Clave, valor.
//        args.putString(ARGUMENTO_PELI, argumentoPeli);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                startActivityForResult(intentAddTransaction, INTENT_ADD_TRANSACTION);
            }
        });

        TransactionsListAdapter lpAdapter = new TransactionsListAdapter(account.getTransactionsList(),
                new TransactionsListAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Transaction transaction) {
                        clickOnItem(transaction);
                    }
                });
        transactionsListView.setAdapter(lpAdapter);

        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(resultCode, resultCode, data);
        if(requestCode == INTENT_ADD_TRANSACTION){
            if(resultCode == Activity.RESULT_OK){
                Transaction transaction = data.getParcelableExtra(AddTransaction.CREATED_EXPENSE);
                account.addTransaction(transaction);
                etBalance.setText(account.getStrBalance());
                ((TransactionsListAdapter)transactionsListView.getAdapter()).updateData(TransactionsListAdapter.APPEND);
            }
        }
    }

    private void clickOnItem(Transaction transaction) {
        showSnackBar("Ha hecho click en la transaccion: " + transaction.getName());
    }

    private void showSnackBar(String text){
        Snackbar.make(mainLayout, text, Snackbar.LENGTH_LONG).show();
    }

    private ArrayList<Transaction> loadTransactions() {
        ArrayList<Transaction> transactionsList = new ArrayList<>();
        transactionsList.add(new Transaction(false, "Gasto 1", 1.12345, "Una dola,"));
        transactionsList.add(new Transaction(false,"Gasto 2", 2.05, "tela catola,"));
        transactionsList.add(new Transaction(false, "Gasto 3", 3, "quila, quilete,"));
        transactionsList.add(new Transaction(true, "Gasto 4", 4.0000, "estaba la reina"));
        transactionsList.add(new Transaction(false, "Gasto 5", 5.99, "en su gabinete,"));
        transactionsList.add(new Transaction(false, "Gasto 6", 6.995, "vino Gil,"));
        transactionsList.add(new Transaction(false, "Gasto 7", 7, "apagó el candil,"));
        transactionsList.add(new Transaction(false, "Gasto 8", 8, "candil candilón,"));
        transactionsList.add(new Transaction(false, "Gasto 9", 1000.10, " las veinte que las veinte son."));
        transactionsList.add(new Transaction(true, "Gasto 10", 1000.10, "cuenta las  que  veinte son."));
        transactionsList.add(new Transaction(false, "Gasto 11", 1000.10, " las veinte que las veinte ."));
        transactionsList.add(new Transaction(true, "Gasto 12", 1000.10, "cuenta las veinte que las  son."));
        transactionsList.add(new Transaction(false, "Gasto 13", 1000.10, "veinte son."));
        return transactionsList;
    }
}
