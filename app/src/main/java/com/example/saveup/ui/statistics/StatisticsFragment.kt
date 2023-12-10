package com.example.saveup.ui.statistics

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.example.saveup.MonthlyLimit
import com.example.saveup.R
import com.example.saveup.databinding.FragmentStatisticsBinding
import com.example.saveup.model.Account
import com.example.saveup.model.Category
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Objects
import java.util.function.Consumer
import java.util.stream.Collectors
import java.util.stream.IntStream

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