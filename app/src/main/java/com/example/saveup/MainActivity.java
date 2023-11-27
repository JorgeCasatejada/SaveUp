package com.example.saveup;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.saveup.model.Account;
import com.example.saveup.model.Category;
import com.example.saveup.model.Transaction;
import com.example.saveup.ui.GroupsFragment;
import com.example.saveup.ui.MainScreenFragment;
import com.example.saveup.ui.ProfileFragment;
import com.example.saveup.ui.StatisticsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends AppCompatActivity {
    private FirebaseUser activeUser;
    private Account account;
    private BottomNavigationView bottomNavigation;
    private Fragment selectedFragment;

    private void inicializarVariables() {
        activeUser = FirebaseAuth.getInstance().getCurrentUser();
        account = new Account("1", activeUser.getDisplayName(), activeUser.getEmail(), "pass")
                .setTransactionsList(loadTransactions());

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
                Toast.makeText(this, "An error occurred", Toast.LENGTH_LONG).show();
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

    private ArrayList<Transaction> loadTransactions() {
        ArrayList<Transaction> transactionsList = new ArrayList<>();
        transactionsList.add(new Transaction(false, "Gasto 1", 1.12345, Category.REGALOS, new Date(2023 - 1900, 0, 1), ""));
        transactionsList.add(new Transaction(false, "Gasto 2", 2.05, Category.ALIMENTACION, new Date(2023 - 1900, 1, 1), ""));
        transactionsList.add(new Transaction(false, "Gasto 3", 3, Category.OCIO, new Date(2023 - 1900, 2, 1), ""));
        transactionsList.add(new Transaction(true, "Gasto 4", 4.00, Category.TECNOLOGIA, new Date(2023 - 1900, 3, 1), ""));
        transactionsList.add(new Transaction(false, "Gasto 5", 5.99, Category.TRANSPORTE, new Date(2023 - 1900, 4, 1), ""));
        transactionsList.add(new Transaction(false, "Gasto 6", 6.995, Category.OCIO, new Date(2023 - 1900, 5, 1), ""));
        transactionsList.add(new Transaction(false, "Gasto 7", 7, Category.EDUCACION, new Date(2023 - 1900, 6, 1), ""));
        transactionsList.add(new Transaction(false, "Gasto 8", 8, Category.VIVIENDA, new Date(2023 - 1900, 7, 1), ""));
        transactionsList.add(new Transaction(false, "Gasto 9", 10.10, Category.TRANSPORTE, new Date(2023 - 1900, 8, 1), ""));
        transactionsList.add(new Transaction(true, "Gasto 10", 10.10, Category.OCIO, new Date(2023 - 1900, 9, 1), ""));
        transactionsList.add(new Transaction(false, "Gasto 11", 10.10, Category.OCIO, new Date(2023 - 1900, 9, 1), ""));
        transactionsList.add(new Transaction(true, "Gasto 12", 10.10, Category.VIVIENDA, new Date(2023 - 1900, 11, 1), ""));
        transactionsList.add(new Transaction(false, "Gasto 13", 10.10, Category.VIVIENDA, new Date(2023 - 1900, 11, 1), ""));
        return transactionsList;
    }

}