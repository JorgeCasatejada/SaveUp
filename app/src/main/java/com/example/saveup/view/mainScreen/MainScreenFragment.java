package com.example.saveup.view.mainScreen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.saveup.model.firestore.FireGoal;
import com.example.saveup.view.statistics.LimitsGoalsFragment;
import com.example.saveup.viewModel.MainViewModel;
import com.example.saveup.R;
import com.example.saveup.view.adapter.TransactionsListAdapter;
import com.example.saveup.databinding.FragmentMainScreenBinding;
import com.example.saveup.model.Account;
import com.example.saveup.model.Notifications;
import com.example.saveup.model.Transaction;
import com.example.saveup.model.TransactionManager;

import java.util.Date;
import java.util.List;
import java.util.Locale;

// Fragmento para la pantalla principal
public class MainScreenFragment extends Fragment {

    // Constants for intent extras
    public static final String TRANSACTION_DETAILS = "transaction_details";
    public static final String ACTIVITY_MODE = "activity_mode";
    public static final int INTENT_ADD_TRANSACTION = 1;
    private static final int MODE_ADD = 1;
    private static final int MODE_DETAILS = 2;
    private static final String ACCOUNT = "Account";

    // View binding
    private FragmentMainScreenBinding binding;

    private MainViewModel viewModel;

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
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        if (!viewModel.getShowedMainTransactions().isInitialized()) {
            viewModel.getUserTransactions();
            binding.recyclerTransactions.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            viewModel.filterTransactions(appliedFilter);
        }

        if (!viewModel.getMonthlyLimit().isInitialized()) {
            viewModel.getLimit();
        }

        if (!viewModel.getGoal().isInitialized()) {
            viewModel.getCurrentGoal();
        }

        setUpRecyclerView();
        initializeUI();
        setClickListeners();

        checkGoal();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel.getShowedMainTransactions().observe(getViewLifecycleOwner(), new Observer<List<Transaction>>() {
            @Override
            public void onChanged(List<Transaction> transactions) {
                binding.progressBar.setVisibility(View.GONE);
                binding.recyclerTransactions.setVisibility(View.VISIBLE);
                ltAdapter.setTransactionsList(transactions);
            }
        });
        viewModel.getBalance().observe(getViewLifecycleOwner(), new Observer<Double>() {
            @Override
            public void onChanged(Double aDouble) {
                String balance = String.format(Locale.getDefault(), "%.2f", aDouble);
                binding.etBalance.setText(balance);
                updateColor(aDouble);

                checkLimit();
                checkGoal();
            }
        });
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
        binding.etBalance.setText(viewModel.getStrBalance());
        updateColor(viewModel.getBalance().getValue());

        // Expense/Income filter
        appliedFilter = TransactionManager.FILTER_ALL;
        viewModel.setFilter(appliedFilter);
    }

    private void setUpRecyclerView() {
        binding.recyclerTransactions.setLayoutManager(new LinearLayoutManager(getContext()));
        ltAdapter = new TransactionsListAdapter(requireContext(), this::clickOnItem);
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
        viewModel.setFilter(appliedFilter);
        viewModel.filterTransactions(appliedFilter);
    }

    private void handleTransactionResult(Intent data) {
        int mode = data.getIntExtra(AddTransaction.MODE, 0);
        switch (mode) {
            case AddTransaction.MODE_ADD:
                viewModel.addTransaction(data.getParcelableExtra(AddTransaction.CREATED_TRANSACTION));
//                account.addTransaction(data.getParcelableExtra(AddTransaction.CREATED_TRANSACTION));
                break;
            case AddTransaction.MODE_DELETE:
                viewModel.removeTransaction(data.getParcelableExtra(AddTransaction.DETAILS_TRANSACTION));
//                account.removeTransaction(data.getParcelableExtra(AddTransaction.DETAILS_TRANSACTION));
                break;
            case AddTransaction.MODE_MODIFY:
                Transaction transactionOld = data.getParcelableExtra(AddTransaction.OLD_MODIFIED_TRANSACTION);
                Transaction transactionNew = data.getParcelableExtra(AddTransaction.NEW_MODIFIED_TRANSACTION);
                viewModel.modifyTransaction(transactionOld, transactionNew);
                break;
        }

//        viewModel.filterTransactions(appliedFilter);
//        binding.etBalance.setText(account.getStrBalance());
//        ltAdapter.setTransactionsList(account.getFilteredTransactionsList(appliedFilter));
//        updateColor();
    }

    private void updateColor(Double balance) {
        int color;
        if (balance > 0)
            color = getResources().getColor(R.color.greenBalance);
        else
            color = getResources().getColor(R.color.redBalance);
        binding.outlinedTextFieldBalance.setBoxBackgroundColor(color);
    }

    private void clickOnItem(Transaction transaction) {
        // Mostar activity_add_transaction con 3 botones (cancelar, modificar, eliminar)
        Intent intentAddTransaction = new Intent(getActivity(), AddTransaction.class);
        intentAddTransaction.putExtra(ACTIVITY_MODE, MODE_DETAILS);
        intentAddTransaction.putExtra(TRANSACTION_DETAILS, transaction);
        startActivityForResult(intentAddTransaction, INTENT_ADD_TRANSACTION);
    }

    private void checkLimit() {
        Double expenses = viewModel.getMonthlyExpenses();
        Double limit = viewModel.getMonthlyLimit().getValue();
        if (expenses != null && limit != null) {
            if (expenses >= limit) {
                notifyLimitExceeded();
            }
        }
    }

    private void checkGoal() {
        Double balance = viewModel.getBalance().getValue();
        FireGoal goal = viewModel.getGoal().getValue();
        Date currentDate = new Date();
        if (balance != null) {
            if (goal != null) {
                Double objectiveBalance = goal.getObjectiveBalance();
                Date finalDate = goal.getFinalDate();
                if (objectiveBalance != null) {
                    if (currentDate.compareTo(finalDate) < 0) { // Comprobar si ha llegado a la meta
                        if (balance >= objectiveBalance) {
                            notifyGoalReached();
                        }
                    } else { // Comprobar si se ha pasado la fecha de la meta
                        if (balance < objectiveBalance) {
                            Log.d("goal", "checkGoal");
                            notifyGoalNotReached();
                        }
                    }
                }
            }
        }
    }

    private void notifyLimitExceeded() {
        Notifications.simpleNotification(requireActivity(),
                "¡Atención! Ha Excedido Su Límite Mensual",
                "El límite mensual que ha creado de " + viewModel.getMonthlyLimit().getValue()
                        + " € ha sido excedido, ahora mismo sus gastos mensuales son "
                        + viewModel.getMonthlyExpenses() + "€",
                com.google.android.material.R.drawable.navigation_empty_icon,
                LimitsGoalsFragment.LIMIT_REACHED_NOTIFICATION_ID);
    }

    private void notifyGoalReached() {
        FireGoal goal = viewModel.getGoal().getValue();

        if (goal == null) return;

        String name = goal.getName();
        Double value = goal.getObjectiveBalance();
        Date date = goal.getFinalDate();

        String title = "¡Atención! Ha Llegado a su meta " + name + "\n";
        StringBuilder builder = new StringBuilder();
        if (value != null) {
            builder.append("Ha llegado a ").append(value).append(" €").append("\n");
        }
        if (date != null) {
            String dateFormatted = date.getDate() + "/" + date.getMonth() + 1 + "/" + (date.getYear() + 1900);
            builder.append("Antes del ").append(dateFormatted);
        }
        Notifications.simpleNotification(requireActivity(),
                title,
                builder.toString(),
                com.google.android.material.R.drawable.navigation_empty_icon,
                LimitsGoalsFragment.GOAL_REACHED_NOTIFICATION_ID);
    }

    private void notifyGoalNotReached() {
        FireGoal goal = viewModel.getGoal().getValue();

        if (goal == null) return;

        String name = goal.getName();
        Double value = goal.getObjectiveBalance();
        Date date = goal.getFinalDate();

        String title = "¡Atención! Su meta " + name + " ha expirado\n";
        StringBuilder builder = new StringBuilder();
        if (value != null) {
            builder.append("No ha llegado a ").append(value).append(" €").append("\n");
        }
        if (date != null) {
            String dateFormatted = date.getDate() + "/" + date.getMonth() + 1 + "/" + (date.getYear() + 1900);
            builder.append("Antes del ").append(dateFormatted);
        }
        Notifications.simpleNotification(requireActivity(),
                title,
                builder.toString(),
                com.google.android.material.R.drawable.navigation_empty_icon,
                LimitsGoalsFragment.GOAL_NOT_REACHED_NOTIFICATION_ID);
    }

}
