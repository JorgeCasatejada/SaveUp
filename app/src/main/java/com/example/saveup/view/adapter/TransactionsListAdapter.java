package com.example.saveup.view.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.saveup.R;
import com.example.saveup.model.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TransactionsListAdapter extends RecyclerView.Adapter<TransactionsListAdapter.TransactionViewHolder> {

    private final OnItemClickListener listener;
    private final Context context;
    private List<Transaction> transactionsList;

    public TransactionsListAdapter(Context context, OnItemClickListener listener) {
        this.transactionsList = new ArrayList<>();
        this.listener = listener;
        this.context = context;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setTransactionsList(List<Transaction> transactionsList) {
        this.transactionsList = transactionsList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.line_recycler_view_transaction, parent, false);
        return new TransactionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        // Extrae de la lista el elemento indicado por posición
        Transaction transaction = transactionsList.get(position);
        Log.i("TransactionListAdapter", "Visualiza elemento: " + transaction);
        // llama al método de nuestro holder para asignar valores a los componentes
        // además, pasamos el listener del evento onClick
        holder.assignComponentsValues(context, transaction, listener);
    }

    @Override
    public int getItemCount() {
        return transactionsList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(Transaction item);
    }

    public static class TransactionViewHolder extends RecyclerView.ViewHolder {

        private final CardView cardView;
        private final TextView title;
        private final TextView description;
        private final TextView value;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.titleTransaction);
            description = itemView.findViewById(R.id.descriptionTransaction);
            value = itemView.findViewById(R.id.valueTransaction);
            cardView = itemView.findViewById(R.id.cardView);
        }

        public static String cutString(String str, int len) {
            if (str == null) return "";
            if (str.length() <= len) return str;
            return str.substring(0, len) + "...";
        }

        public void assignComponentsValues(final Context context, final Transaction transaction, final OnItemClickListener listener) {
            title.setText(cutString(transaction.getName(), 10));
            description.setText(cutString(transaction.getDescription(), 32));
            value.setText(String.format(Locale.getDefault(), "%.2f €", transaction.getSignedValue()));

            itemView.setOnClickListener(v -> listener.onItemClick(transaction));
            updateColor(context, transaction);
        }

        private void updateColor(Context context, Transaction transaction) {
            int isExpense = context.getColor(R.color.redExpense);
            int isIncome = context.getColor(R.color.greenIncome);
            if (transaction.isExpense()) {
                cardView.setCardBackgroundColor(isExpense);
            } else {
                cardView.setCardBackgroundColor(isIncome);
            }
        }
    }

}
