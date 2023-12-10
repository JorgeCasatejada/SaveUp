package com.example.saveup;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.saveup.model.Account;
import com.example.saveup.repositorios.TransactionsRepository;
import com.example.saveup.ui.GroupsFragment;
import com.example.saveup.ui.MainScreenFragment;
import com.example.saveup.ui.ProfileFragment;
import com.example.saveup.ui.statistics.StatisticsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;


public class MainActivity extends AppCompatActivity {
    MainViewModel viewModel;
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

        selectedFragment = MainScreenFragment.newInstance(account);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setSelectedItemId(R.id.mnItmBalance);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TransactionsRepository repo = new TransactionsRepository();
        MainViewModelProviderFactory viewModelProviderFactory = new MainViewModelProviderFactory(repo);
        viewModel = new ViewModelProvider(this, viewModelProviderFactory).get(MainViewModel.class);

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

}