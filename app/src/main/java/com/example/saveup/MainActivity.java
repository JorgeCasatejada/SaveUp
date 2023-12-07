package com.example.saveup;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.saveup.model.Account;
import com.example.saveup.model.Transaction;
import com.example.saveup.model.firestore.FireTransaction;
import com.example.saveup.ui.GroupsFragment;
import com.example.saveup.ui.MainScreenFragment;
import com.example.saveup.ui.ProfileFragment;
import com.example.saveup.ui.StatisticsFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    private FirebaseUser activeUser;
    private FirebaseFirestore db;
    private Account account;
    private BottomNavigationView bottomNavigation;
    private Fragment selectedFragment;

    private void inicializarVariables() {
        activeUser = FirebaseAuth.getInstance().getCurrentUser();
        if (activeUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            Toast.makeText(this, getResources().getString(R.string.errMessage), Toast.LENGTH_LONG).show();
        }
        account = new Account("1", activeUser.getDisplayName(), activeUser.getEmail(), "pass");

        db = FirebaseFirestore.getInstance();
        Task<QuerySnapshot> t = db.collection("users").document(activeUser.getUid()).collection("transactions")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d("Firestore", "User documents retrieved successfully");
                            QuerySnapshot collection = task.getResult();
                            loadTransactions(collection);
                        } else {
                            Log.d("Firestore", "Error getting User documents: ", task.getException());
                        }
                    }
                });


        selectedFragment = MainScreenFragment.newInstance(account);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setSelectedItemId(R.id.mnItmBalance);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inicializarVariables();
        if (savedInstanceState != null) {
            int mnItemSelected = savedInstanceState.getInt("SelectedMenu");
            switchSelectedFragment(mnItemSelected);
            if (selectedFragment == null) {
                Toast.makeText(this, getResources().getString(R.string.errMessage), Toast.LENGTH_LONG).show();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_container, selectedFragment).commit();

        bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                switchSelectedFragment(itemId);
                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_container, selectedFragment).commit();
                    return true;
                }
                return false;
            }
        });
    }

    private void switchSelectedFragment(int mnItmID) {
        selectedFragment = null;
        if (mnItmID == R.id.mnItmGroups) {
            selectedFragment = GroupsFragment.newInstance(account);
        } else if (mnItmID == R.id.mnItmBalance) {
            selectedFragment = MainScreenFragment.newInstance(account);
        } else if (mnItmID == R.id.mnItmStatistics) {
            selectedFragment = StatisticsFragment.newInstance(account);
        } else if (mnItmID == R.id.mnItmProfile) {
            selectedFragment = ProfileFragment.newInstance(account);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        int mnItemSelected = bottomNavigation.getSelectedItemId();
        outState.putInt("SelectedMenu", mnItemSelected);
    }

    private void loadTransactions(QuerySnapshot collection) {
        ArrayList<Transaction> transactionsList = new ArrayList<>();
        for (QueryDocumentSnapshot document : collection) {
            Log.d("Firestore", "Transaction: " + document.getId() + " => " + document.getData());
            FireTransaction fireTransaction = document.toObject(FireTransaction.class);
            transactionsList.add(new Transaction(fireTransaction));
        }
        account.setTransactionsList(transactionsList);
        Log.d("loadTransactions", account.getTransactionsList().toString());
        switchSelectedFragment(bottomNavigation.getSelectedItemId());
        getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_container, selectedFragment).commit();
        Log.d("loadTransactions", "Fragment replaced");
    }

//    private ArrayList<Transaction> loadTransactions() {
//        ArrayList<Transaction> transactionsList = new ArrayList<>();
//        transactionsList.add(new Transaction(false, "Gasto 1", 1.12345, Category.REGALOS, new Date(2023 - 1900, 0, 1), ""));
//        transactionsList.add(new Transaction(false, "Gasto 2", 2.05, Category.ALIMENTACION, new Date(2023 - 1900, 1, 1), ""));
//        transactionsList.add(new Transaction(false, "Gasto 3", 3, Category.OCIO, new Date(2023 - 1900, 2, 1), ""));
//        transactionsList.add(new Transaction(true, "Gasto 4", 4.00, Category.TECNOLOGIA, new Date(2023 - 1900, 3, 1), ""));
//        transactionsList.add(new Transaction(false, "Gasto 5", 5.99, Category.TRANSPORTE, new Date(2023 - 1900, 4, 1), ""));
//        transactionsList.add(new Transaction(false, "Gasto 6", 6.995, Category.OCIO, new Date(2023 - 1900, 5, 1), ""));
//        transactionsList.add(new Transaction(false, "Gasto 7", 7, Category.EDUCACION, new Date(2023 - 1900, 6, 1), ""));
//        transactionsList.add(new Transaction(false, "Gasto 8", 8, Category.VIVIENDA, new Date(2023 - 1900, 7, 1), ""));
//        transactionsList.add(new Transaction(false, "Gasto 9", 10.10, Category.TRANSPORTE, new Date(2023 - 1900, 8, 1), ""));
//        transactionsList.add(new Transaction(true, "Gasto 10", 10.10, Category.OCIO, new Date(2023 - 1900, 9, 1), ""));
//        transactionsList.add(new Transaction(false, "Gasto 11", 10.10, Category.OCIO, new Date(2023 - 1900, 9, 1), ""));
//        transactionsList.add(new Transaction(true, "Gasto 12", 10.10, Category.VIVIENDA, new Date(2023 - 1900, 11, 1), ""));
//        transactionsList.add(new Transaction(false, "Gasto 13", 10.10, Category.VIVIENDA, new Date(2023 - 1900, 11, 1), ""));
//        return transactionsList;
//    }

}