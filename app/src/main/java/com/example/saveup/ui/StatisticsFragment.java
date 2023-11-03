package com.example.saveup.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.saveup.R;
import com.example.saveup.model.Account;
import com.example.saveup.model.Transaction;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StatisticsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatisticsFragment extends Fragment {

    private static final String ACCOUNT = "Account";
    private Account account;
    private View root;

    private FloatingActionButton shareFab;

    public static StatisticsFragment newInstance(Account account) {
        StatisticsFragment fragment = new StatisticsFragment();
        Bundle args = new Bundle();
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
        shareFab = root.findViewById(R.id.shareFab);
        shareFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareStatistics();
            }
        });
    }

    public void shareStatistics(){
        /* es necesario hacer un intent con la constate ACTION_SEND */
        /*Llama a cualquier app que haga un envío*/
        Intent itSend = new Intent(Intent.ACTION_SEND);
        /* vamos a enviar texto plano */
        itSend.setType("text/plain");
        // itSend.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{para});
        itSend.putExtra(Intent.EXTRA_SUBJECT, "Registro de gastos e ingresos");
        StringBuilder stringBuilder = new StringBuilder("Historial de gastos / ingresos\n");
        for (Transaction transaction : account.getTransactionsList()) {
            if (transaction.isExpense()) {
                stringBuilder.append("-");
            } else {
                stringBuilder.append("+");
            }

            double value = transaction.getValue();
            value = round(value, 2);
            stringBuilder.append(value).append("€").append(" ").append("|").append(" ");

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy",
                    Locale.getDefault());
            String date = simpleDateFormat.format(transaction.getDate());
            stringBuilder.append(transaction.getName()).append(" ").append("|").append(" ")
                    .append(date).append("\n\r").append("\n\r");
        }
        stringBuilder.append("------------------------------------\n");
        stringBuilder.append("Balance Total: ").append(round(account.getBalance(), 2)).append("€");
        itSend.putExtra(Intent.EXTRA_TEXT, stringBuilder.toString());

        Intent shareIntent=Intent.createChooser(itSend, null);

        startActivity(shareIntent);

    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_statistics, container, false);

        initializeVariables();

        return root;
    }
}