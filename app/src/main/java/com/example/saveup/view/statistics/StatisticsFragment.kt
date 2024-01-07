package com.example.saveup.view.statistics

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.saveup.R
import com.example.saveup.databinding.FragmentStatisticsBinding
import com.example.saveup.viewModel.MainViewModel
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.Locale

class StatisticsFragment : Fragment() {
    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!

    private var viewModel: MainViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)

        loadMenu()

        showGraphs()

        // Botón FAB
        binding.shareFab.setOnClickListener { shareStatistics() }

        return binding.root
    }

    private fun shareStatistics() {

        val itSend = Intent(Intent.ACTION_SEND)
        itSend.type = "text/plain"
        // itSend.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{para});
        itSend.putExtra(Intent.EXTRA_SUBJECT, "Registro de gastos e ingresos")
        val stringBuilder = StringBuilder("Historial de gastos / ingresos\n")
        for (transaction in viewModel?.allUserTransactions?.value!!) {
            if (transaction.isExpense) {
                stringBuilder.append("-")
            } else {
                stringBuilder.append("+")
            }
            val value = transaction.value
            stringBuilder.append(String.format(Locale.getDefault(), "%.2f", value)).append("€")
                .append(" ").append("|").append(" ")
            val simpleDateFormat = SimpleDateFormat(
                "dd/MM/yyyy",
                Locale.getDefault()
            )
            val date = simpleDateFormat.format(transaction.date)
            stringBuilder.append(transaction.name).append(" ").append("|").append(" ")
                .append(date).append("\n\r").append("\n\r")
        }
        stringBuilder.append("------------------------------------\n")
        stringBuilder.append("Balance Total: ")
            .append(viewModel?.balance?.value?.let { round(it, 2) }).append("€")
        itSend.putExtra(Intent.EXTRA_TEXT, stringBuilder.toString())
        val shareIntent = Intent.createChooser(itSend, null)
        startActivity(shareIntent)
    }

    private fun round(value: Double, places: Int): Double {
        require(places >= 0)
        var bd = BigDecimal.valueOf(value)
        bd = bd.setScale(places, RoundingMode.HALF_UP)
        return bd.toDouble()
    }

    private fun loadMenu() {
        binding.navViewStatistics.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.mnItmGraphs -> {
                    showGraphs()
                }

                R.id.mnItmLimits -> {
                    showLimitsGoals()
                }
            }
            true
        }
    }

    private fun showGraphs() {
        val graphsFragment = GraphsFragment.newInstance()
        childFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_statistics, graphsFragment)
            .commit()
    }

    private fun showLimitsGoals() {
        val limitsGoalsFragment = LimitsGoalsFragment.newInstance()
        childFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_statistics, limitsGoalsFragment)
            .commit()
    }

    companion object {

        @JvmStatic
        fun newInstance(): StatisticsFragment {
            return StatisticsFragment()
        }
    }
}