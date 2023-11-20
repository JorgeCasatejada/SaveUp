package com.example.saveup;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.saveup.model.Account;
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

        getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_container, selectedFragment).commit();

        bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                selectedFragment = null;
                if (itemId == R.id.mnItmGroups) {
                    selectedFragment = GroupsFragment.newInstance(account);
                } else if (itemId == R.id.mnItmBalance) {
                    selectedFragment = MainScreenFragment.newInstance(account);
                } else if (itemId == R.id.mnItmStatistics) {
                    selectedFragment = StatisticsFragment.newInstance(account);
                } else if (itemId == R.id.mnItmProfile) {
                    selectedFragment = ProfileFragment.newInstance(account);
                }
                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_container, selectedFragment).commit();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        // Do nothing
    }

    private ArrayList<Transaction> loadTransactions() {
        ArrayList<Transaction> transactionsList = new ArrayList<>();
        transactionsList.add(new Transaction(false, "Gasto 1", 1.12345, "Una dola,"));
        transactionsList.add(new Transaction(false, "Gasto 2", 2.05, "tela catola,"));
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