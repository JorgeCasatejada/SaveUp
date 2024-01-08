package com.example.saveup.view.statistics

import android.graphics.Color
import android.graphics.DashPathEffect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.saveup.R
import com.example.saveup.databinding.FragmentGraphsBinding
import com.example.saveup.model.Category
import com.example.saveup.viewModel.MainViewModel
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
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
import java.util.Date
import java.util.Locale
import java.util.Objects
import java.util.function.Consumer
import java.util.stream.Collectors
import java.util.stream.IntStream

class GraphsFragment : Fragment() {
    private var _binding: FragmentGraphsBinding? = null
    private val binding get() = _binding!!

    private var viewModel: MainViewModel? = null

    private var showExpenses = true
    private var yearToShow = 0
    private var yearsAdapter: ArrayAdapter<Int>? = null
    private lateinit var years: List<Int>
    private var totalBalance = 0.0

    private var minBalance = Float.MAX_VALUE
    private var maxBalance = Float.MIN_VALUE

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGraphsBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        if (!viewModel!!.allUserTransactions.isInitialized) {
            viewModel!!.getUserTransactions()
        }

        initializeVariables()

        return binding.root
    }

    private fun initializeVariables() {
        // Botones de filtros
        binding.selectExpense.setOnClickListener {
            showExpenses = true
            createPieChart()
        }
        binding.selectIncome.setOnClickListener {
            showExpenses = false
            createPieChart()
        }

        // Filtro de años
        years =
            IntStream.range(1899, Date().year + 1900 + 1).boxed().sorted(Comparator.reverseOrder())
                .collect(Collectors.toList())
        yearToShow = years[0]
        yearsAdapter = ArrayAdapter(requireContext(), R.layout.list_item, years)
        binding.autocompleteYear.setAdapter(yearsAdapter)
        binding.autocompleteYear.setText(
            String.format(
                Locale.getDefault(),
                "%04d",
                yearsAdapter!!.getItem(0)
            ), false
        )
        binding.autocompleteYear.setOnItemClickListener { _, _, position, _ ->
            val item: Any? = yearsAdapter!!.getItem(position)
            if (item != null) {
                yearToShow = item as Int
            }
            createLineChartBalance()
            createLineChartIncomeExpense()
            createPieChart()
        }

        // Creación de los gráficos
        createLineChartBalance()
        createLineChartIncomeExpense()
        createPieChart()
    }

    private fun createLineChartBalance() {
        // Datos (x e y)
        val values = resources.getStringArray(R.array.months)
        val map = viewModel?.groupedTransactionsByYear(yearToShow)!!

        // Ejes y líneas
        val zeroLine = LimitLine(0f, "")
        zeroLine.lineWidth = 2f
        zeroLine.lineColor = Color.BLACK

        val xAxis = binding.graphs.lineChartBalance.xAxis
        xAxis.axisLineWidth = 2f
        xAxis.labelCount = 11
        xAxis.labelRotationAngle = 45f
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.valueFormatter = IndexAxisValueFormatter(values)

        val yAxis = binding.graphs.lineChartBalance.axisLeft
        yAxis.axisLineWidth = 2f
        yAxis.axisLineColor = Color.BLACK
        yAxis.addLimitLine(zeroLine)
        yAxis.setDrawLimitLinesBehindData(true)

        // Cálculo de datos
        val entries: MutableList<Entry> = ArrayList()
        var balance = 0f

        var minBalance = Float.MAX_VALUE
        var maxBalance = Float.MIN_VALUE

        for (month in 0..11) {
            val transactions = map.getOrDefault(month, ArrayList())
            for (transaction in transactions) {
                balance += transaction.signedValue.toFloat()
            }

            if (balance < minBalance) {
                minBalance = balance
            } else if (balance > maxBalance) {
                maxBalance = balance
            }

            entries.add(Entry(month.toFloat(), balance))
        }

        // Configuración
        val dataset = LineDataSet(entries, "Balance")
        dataset.color = Color.CYAN
        dataset.lineWidth = 2f

        binding.graphs.lineChartBalance.isDragEnabled = false
        binding.graphs.lineChartBalance.setScaleEnabled(false)

        val description = Description()
        description.text = resources.getString(R.string.totalBalance)
        description.textSize = 15f
        description.setPosition(550f, 90f)

        binding.graphs.lineChartBalance.description = description
        binding.graphs.lineChartBalance.axisRight.setDrawLabels(false)

        binding.graphs.lineChartBalance.axisRight.setDrawGridLines(false)
        binding.graphs.lineChartBalance.axisLeft.setDrawGridLines(false)
        binding.graphs.lineChartBalance.xAxis.setDrawGridLines(false)

        // Adición de datos
        val lineData = LineData(dataset)
        binding.graphs.lineChartBalance.data = lineData

        // Límites
        yAxis.axisMaximum = maxBalance + 70f
        yAxis.axisMinimum = minBalance - 70f

        // Animación
        binding.graphs.lineChartBalance.animateXY(1000, 1000)
    }

    private fun createLineChartIncomeExpense() {
        // Datos (x e y)
        val values = resources.getStringArray(R.array.months)
        val map = viewModel?.groupedTransactionsByYear(yearToShow)!!

        // Ejes y líneas
        val zeroLine = LimitLine(0f, "")
        zeroLine.lineWidth = 2f
        zeroLine.lineColor = Color.BLACK

        val xAxis = binding.graphs.lineChartIncomeExpense.xAxis
        xAxis.axisLineWidth = 2f
        xAxis.labelCount = 11
        xAxis.labelRotationAngle = 45f
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.valueFormatter = IndexAxisValueFormatter(values)

        val yAxis = binding.graphs.lineChartIncomeExpense.axisLeft
        yAxis.axisLineWidth = 2f
        yAxis.axisLineColor = Color.BLACK
        yAxis.addLimitLine(zeroLine)
        yAxis.setDrawLimitLinesBehindData(true)

        // Configuración
        binding.graphs.lineChartIncomeExpense.isDragEnabled = false
        binding.graphs.lineChartIncomeExpense.setScaleEnabled(false)

        val description = Description()
        description.text = resources.getString(R.string.incomeExpense)
        description.textSize = 15f
        description.setPosition(550f, 90f)

        binding.graphs.lineChartIncomeExpense.description = description
        binding.graphs.lineChartIncomeExpense.axisRight.setDrawLabels(false)

        binding.graphs.lineChartIncomeExpense.axisRight.setDrawGridLines(false)
        binding.graphs.lineChartIncomeExpense.axisLeft.setDrawGridLines(false)
        binding.graphs.lineChartIncomeExpense.xAxis.setDrawGridLines(false)

        // Adición de datos
        val pairDatasets = calculateDatasetsIncomesAndExpenses()

        val lineData = LineData(calculateDatasetLimit())
        lineData.addDataSet(pairDatasets.first)
        lineData.addDataSet(pairDatasets.second)
        binding.graphs.lineChartIncomeExpense.data = lineData

        // Límites
        yAxis.axisMaximum = maxBalance + 70f
        yAxis.axisMinimum = minBalance - 70f

        // Leyenda
        val dashedArray = floatArrayOf(8f, 8f)
        val nonDashedArray = floatArrayOf(0f, 0f)
        val legendEntryLimit = LegendEntry("Límite", Legend.LegendForm.LINE, 20f, 2f, DashPathEffect(dashedArray, 10f), Color.RED)
        val legendEntryExpenses = LegendEntry("Gastos", Legend.LegendForm.LINE, 20f, 2f, DashPathEffect(nonDashedArray, 10f), Color.RED)
        val legendEntryIncomes = LegendEntry("Ingresos", Legend.LegendForm.LINE, 20f, 2f, DashPathEffect(nonDashedArray, 10f), Color.GREEN)
        val entries = arrayOf(legendEntryLimit, legendEntryExpenses, legendEntryIncomes)
        val legend = binding.graphs.lineChartIncomeExpense.legend
        legend.setCustom(entries)

        // Animación
        binding.graphs.lineChartIncomeExpense.animateXY(1000, 1000)
    }

    private fun calculateDatasetLimit(): LineDataSet {
        var limit = viewModel!!.monthlyLimit.value ?: return LineDataSet(listOf(), "")
        if (limit < 0) limit *= -1
        val entries: MutableList<Entry> = ArrayList()

        for (month in 0..11) {
            entries.add(Entry(month.toFloat(), limit.toFloat()))
        }

        // Cálculo de nuevos mínimos o máximos
        if (limit < minBalance) {
            minBalance = limit.toFloat()
        } else if (limit > maxBalance) {
            maxBalance = limit.toFloat()
        }

        val datasetLimit = LineDataSet(entries, "Límite")
        datasetLimit.color = Color.RED
        datasetLimit.lineWidth = 2f
        datasetLimit.circleColors = listOf(Color.RED)
        datasetLimit.enableDashedLine(10f, 10f, 10f)
        datasetLimit.setDrawValues(false)
        datasetLimit.setDrawCircles(false)

        return datasetLimit
    }

    private fun calculateDatasetsIncomesAndExpenses(): Pair<LineDataSet, LineDataSet> {
        val map = viewModel?.groupedTransactionsByYear(yearToShow)!!

        val entriesExpenses: MutableList<Entry> = ArrayList()
        val entriesIncomes: MutableList<Entry> = ArrayList()
        for (month in 0 .. 11) {
            var balanceExpenses = 0f
            var balanceIncomes = 0f
            val transactions = map.getOrDefault(month, ArrayList())
            for (transaction in transactions) {
                if (transaction.isExpense) {
                    balanceExpenses += transaction.value.toFloat()
                } else {
                    balanceIncomes += transaction.value.toFloat()
                }
            }

            // Cálculo de nuevos mínimos o máximos
            if (balanceExpenses < minBalance) {
                minBalance = balanceExpenses
            } else if (balanceExpenses > maxBalance) {
                maxBalance = balanceExpenses
            }

            if (balanceIncomes < minBalance) {
                minBalance = balanceIncomes
            } else if (balanceIncomes > maxBalance) {
                maxBalance = balanceIncomes
            }

            entriesExpenses.add(Entry(month.toFloat(), balanceExpenses))
            entriesIncomes.add(Entry(month.toFloat(), balanceIncomes))
        }

        val datasetExpenses = LineDataSet(entriesExpenses, "Gastos")
        datasetExpenses.color = Color.RED
        datasetExpenses.lineWidth = 2f
        datasetExpenses.circleColors = listOf(Color.BLUE)
        datasetExpenses.setDrawCircles(false)

        val datasetIncomes = LineDataSet(entriesIncomes, "Ingresos")
        datasetIncomes.color = Color.GREEN
        datasetIncomes.lineWidth = 2f
        datasetIncomes.circleColors = listOf(Color.GREEN)
        datasetIncomes.setDrawCircles(false)


        return Pair(datasetExpenses, datasetIncomes)
    }

    private fun createPieChart() {
        // Datos
        val categories = ArrayList<PieEntry>()
        val map = viewModel?.groupedCategories(yearToShow, showExpenses)!!
        val categoriesToShow = ArrayList(map.keys)

        // Cálculo de datos
        map.keys.forEach(Consumer { category: Category ->
            categories.add(
                PieEntry(
                    Objects.requireNonNull(map[category]).toString().toFloat(),
                    category.toString()
                )
            )
        })

        totalBalance = 0.0
        for (key in map.keys) {
            totalBalance += map.getOrDefault(key, 0.0)
        }

        // Configuración
        // Colores
        val colors = resources.getIntArray(R.array.pieChartColorsHexCode)
        val pieDataSet =
            PieDataSet(categories, resources.getString(R.string.labelTransactionCategory))
        pieDataSet.setColors(*colors)
        pieDataSet.valueTextColor = Color.BLACK
        pieDataSet.valueTextSize = 15f

        // Diseño del gráfico
        binding.graphs.pieChart.description.isEnabled = false
        binding.graphs.pieChart.centerText = resources.getString(
            R.string.centerText, String.format(Locale.getDefault(), "%.2f", totalBalance),
            resources.getString(R.string.total)
        )
        binding.graphs.pieChart.setCenterTextSize(14f)
        binding.graphs.pieChart.setEntryLabelTextSize(0f)
        binding.graphs.pieChart.setEntryLabelColor(Color.BLACK)
        binding.graphs.pieChart.holeRadius = 40f
        binding.graphs.pieChart.transparentCircleRadius = 45f

        // Leyenda
        val legend = binding.graphs.pieChart.legend
        legend.verticalAlignment = Legend.LegendVerticalAlignment.CENTER
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
        legend.form = Legend.LegendForm.CIRCLE
        legend.orientation = Legend.LegendOrientation.VERTICAL
        legend.setDrawInside(false)
        legend.xEntrySpace = 5f
        legend.yEntrySpace = 5f
        legend.yOffset = 100f
        legend.isWordWrapEnabled = false

        // Listeners
        binding.graphs.pieChart.setOnChartValueSelectedListener(object :
            OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry, h: Highlight) {
                binding.graphs.pieChart.centerText = resources.getString(
                    R.string.centerText, String.format(
                        Locale.getDefault(), "%.2f", e.y
                    ),
                    categoriesToShow[pieDataSet.getEntryIndex(e)]
                )
            }

            override fun onNothingSelected() {
                binding.graphs.pieChart.centerText = resources.getString(
                    R.string.centerText, String.format(
                        Locale.getDefault(), "%.2f", totalBalance
                    ),
                    resources.getString(R.string.total)
                )
            }
        })

        // Adición de datos
        val pieData = PieData(pieDataSet)
        binding.graphs.pieChart.data = pieData

        // Animación
        binding.graphs.pieChart.animateXY(1000, 1000)
    }

    companion object {

        @JvmStatic
        fun newInstance(): GraphsFragment {
            return GraphsFragment()
        }
    }
}