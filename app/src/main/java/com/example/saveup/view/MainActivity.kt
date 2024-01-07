package com.example.saveup.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.saveup.R
import com.example.saveup.model.repository.TransactionsRepository
import com.example.saveup.view.group.GroupsFragment
import com.example.saveup.view.login.LoginActivity
import com.example.saveup.view.mainScreen.MainScreenFragment
import com.example.saveup.view.profile.ProfileFragment
import com.example.saveup.view.statistics.StatisticsFragment
import com.example.saveup.viewModel.MainViewModel
import com.example.saveup.viewModel.MainViewModelProviderFactory
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainActivity : AppCompatActivity() {

    lateinit var viewModel: MainViewModel
    private var activeUser: FirebaseUser? = null
    private lateinit var bottomNavigation: BottomNavigationView
    private var selectedFragment: Fragment? = null
    private fun inicializarVariables() {
        activeUser = FirebaseAuth.getInstance().currentUser
        if (activeUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            Toast.makeText(this, resources.getString(R.string.errMessage), Toast.LENGTH_LONG).show()
        }
        selectedFragment = MainScreenFragment.newInstance()
        bottomNavigation = findViewById(R.id.bottom_navigation)
        bottomNavigation.selectedItemId = R.id.mnItmBalance
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val repo = TransactionsRepository()
        val viewModelProviderFactory = MainViewModelProviderFactory(repo)
        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(
            MainViewModel::class.java
        )
        inicializarVariables()
        if (savedInstanceState != null) {
            val mnItemSelected = savedInstanceState.getInt("SelectedMenu")
            switchSelectedFragment(mnItemSelected)
            if (selectedFragment == null) {
                Toast.makeText(this, resources.getString(R.string.errMessage), Toast.LENGTH_LONG)
                    .show()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_fragment_container, selectedFragment!!).commit()
        bottomNavigation.setOnItemSelectedListener(NavigationBarView.OnItemSelectedListener { item ->
            val itemId = item.itemId
            switchSelectedFragment(itemId)
            if (selectedFragment != null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_fragment_container, selectedFragment!!).commit()
                return@OnItemSelectedListener true
            }
            false
        })
    }

    private fun switchSelectedFragment(mnItmID: Int) {
        selectedFragment = null
        when (mnItmID) {
            R.id.mnItmGroups -> {
                selectedFragment = GroupsFragment.newInstance()
            }

            R.id.mnItmBalance -> {
                selectedFragment = MainScreenFragment.newInstance()
            }

            R.id.mnItmStatistics -> {
                selectedFragment = StatisticsFragment.newInstance()
            }

            R.id.mnItmProfile -> {
                selectedFragment = ProfileFragment.newInstance()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val mnItemSelected = bottomNavigation.selectedItemId
        outState.putInt("SelectedMenu", mnItemSelected)
    }
}