package com.example.saveup;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.saveup.model.Transaction;

import java.util.List;
import java.util.Locale;

public class TransactionsListAdapter extends RecyclerView.Adapter<TransactionsListAdapter.TransactionViewHolder> {

    public static final int APPEND = 0;
    public static final int CHANGE_TRANSACTION_LIST = 1;

    private List<Transaction> transactionsList;
    private final OnItemClickListener listener;
    private Context context;

    public TransactionsListAdapter(Context context, List<Transaction> transactionsList, OnItemClickListener listener) {
        this.transactionsList = transactionsList;
        this.listener = listener;
        this.context = context;
    }

    public void updateData(int flag) {
        if (flag == APPEND) { //append
            notifyItemInserted(getItemCount());
            notifyItemRangeChanged(0, getItemCount());
        } else {
            notifyDataSetChanged();
        }
    }

    public void setTransactionsList(List<Transaction> transactionsList) {
        this.transactionsList = transactionsList;
        updateData(CHANGE_TRANSACTION_LIST);
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
        Log.i("Lista", "Visualiza elemento: " + transaction);
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

        private final LinearLayout recyclerLineLayout;

        private final CardView cardView;
        private final TextView title;
        private final TextView description;
        private final TextView value;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerLineLayout = itemView.findViewById(R.id.recyclerLineLayout);
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
            if (transaction.getValue() < 0) {
                recyclerLineLayout.setBackground(Drawable.createFromPath("@android:color/holo_red_light"));
            } else {
                recyclerLineLayout.setBackground(Drawable.createFromPath("@android:color/holo_green_light"));
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(transaction);
                }
            });
            updateColor(context, transaction);
        }

        private void updateColor(Context context, Transaction transaction) {
            int isExpense = context.getColor(R.color.redExpense);
            int isIncome = context.getColor(R.color.greenIncome);
            if (transaction.isExpense()){
                cardView.setCardBackgroundColor(isExpense);
            } else {
                cardView.setCardBackgroundColor(isIncome);
            }
        }
    }

}
