package com.example.saveup;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.saveup.model.Account;
import com.example.saveup.model.Transaction;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private View mainLayout;
    private RecyclerView transactionsListView;
    private TextView txBalance;
    private Account account;
    private FloatingActionButton fabAdd;

    private void initializeVariables() {
        mainLayout = findViewById(R.id.mainLayout);
        transactionsListView = findViewById(R.id.recyclerTransactions);
        transactionsListView.setHasFixedSize(true);
        txBalance = findViewById(R.id.txBalance);
        fabAdd = findViewById(R.id.fabAdd);

        account = new Account("1", "prueba@gmail.com", "pass")
                .setTransactionsList(loadTransactions());
        txBalance.setText(String.format(Locale.getDefault(),"%.2f €", account.getBalance()));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeVariables();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        transactionsListView.setLayoutManager(layoutManager);

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentAddExpense = new Intent(MainActivity.this, AddExpense.class);
                startActivity(intentAddExpense);
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
    }

    private void clickOnItem(Transaction transaction) {
        Snackbar.make(mainLayout,
                "Ha hecho click en la transaccion: " + transaction.getName(),
                Snackbar.LENGTH_LONG).show();
    }

    private ArrayList<Transaction> loadTransactions() {
        ArrayList<Transaction> transactionsList = new ArrayList<>();
        transactionsList.add(new Transaction(1.12345, "Gasto 1", "Una dola,"));
        transactionsList.add(new Transaction(2.05, "Gasto 2", "tela catola,"));
        transactionsList.add(new Transaction(3, "Gasto 3", "quila, quilete,"));
        transactionsList.add(new Transaction(-4.0000, "Gasto 4", "estaba la reina"));
        transactionsList.add(new Transaction(5.99, "Gasto 5", "en su gabinete,"));
        transactionsList.add(new Transaction(6.995, "Gasto 6", "vino Gil,"));
        transactionsList.add(new Transaction(7, "Gasto 7", "apagó el candil,"));
        transactionsList.add(new Transaction(8, "Gasto 8", "candil candilón,"));
        transactionsList.add(new Transaction(1000.10, "Gasto 9", " las veinte que las veinte son."));
        transactionsList.add(new Transaction(-1000.10, "Gasto 10", "cuenta las  que  veinte son."));
        transactionsList.add(new Transaction(1000.10, "Gasto 11", " las veinte que las veinte ."));
        transactionsList.add(new Transaction(-1000.10, "Gasto 12", "cuenta las veinte que las  son."));
        transactionsList.add(new Transaction(1000.10, "Gasto 13", "veinte son."));
        return transactionsList;
    }
}