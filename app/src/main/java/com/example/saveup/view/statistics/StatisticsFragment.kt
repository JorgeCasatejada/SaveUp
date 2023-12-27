package com.example.saveup.view.statistics

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.saveup.R
import com.example.saveup.databinding.FragmentStatisticsBinding
import com.example.saveup.model.Account

class StatisticsFragment : Fragment() {
    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!

    private var account: Account? = null

    val ACTIVITY_MODE = "activity_mode"
    val INTENT_LIMITS = 1
    val MODE_LIMIT = 1
    val MODE_GOAL = 2
    private val ACCOUNT = "Account"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            account = requireArguments().getParcelable(ACCOUNT)
        }
    }

    private fun pasarALimitesMensuales() {
        val intentMonthlyLimits = Intent(activity, MonthlyLimit::class.java)
        intentMonthlyLimits.putExtra(ACTIVITY_MODE, MODE_LIMIT)
        startActivityForResult(intentMonthlyLimits, INTENT_LIMITS)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        //initializeVariables()

        loadMenu()

        showGraphs()

        return binding.root
    }

    private fun loadMenu() {
        binding.navViewStatistics.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.mnItmGraphs -> {
                    showGraphs()
                }

                R.id.mnItmLimits -> {
                    showLimits()
                }
            }
            true
        }
    }

    private fun showGraphs() {
        val graphsFragment = GraphsFragment.newInstance(account)
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_statistics, graphsFragment)
            .commit()
    }

    private fun showLimits() {
        val limitsFragment = LimitsFragment.newInstance(account)
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_statistics, limitsFragment)
            .commit()
    }

    companion object {
        private const val ACCOUNT = "Account"

        @JvmStatic
        fun newInstance(account: Account?): StatisticsFragment {
            val fragment = StatisticsFragment()
            val args = Bundle()
            args.putParcelable(ACCOUNT, account)
            fragment.arguments = args
            return fragment
        }
    }
}