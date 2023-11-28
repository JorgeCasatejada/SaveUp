package com.example.saveup.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.Range;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ToggleButton;

import androidx.fragment.app.Fragment;

import com.example.saveup.R;
import com.example.saveup.model.Account;
import com.example.saveup.model.Category;
import com.example.saveup.model.Transaction;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.IntUnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StatisticsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatisticsFragment extends Fragment {

    private static final String ACCOUNT = "Account";
    private Account account;
    private View root;
    private Button buttonFilterExpense;
    private Button buttonFilterIncome;
    private boolean showExpenses = true;
    private int yearToShow;
    private FloatingActionButton shareFab;

    private LineChart lineChart;
    private PieChart pieChart;

    private List<Integer> years;

    private ArrayAdapter<Integer> yearsAdapter;

    private AutoCompleteTextView autocompleteYear;

    private TextInputLayout autocompleteYearLayout;

    private double totalBalance;

    public static StatisticsFragment newInstance(Account account) {
        StatisticsFragment fragment = new StatisticsFragment();
        Bundle args = new Bundle();
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

    private void initializeVariables() {
        buttonFilterExpense = root.findViewById(R.id.selectExpense);
        buttonFilterIncome = root.findViewById(R.id.selectIncome);

        System.out.println(buttonFilterExpense);
        buttonFilterExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showExpenses = true;
                createPieChart();
            }
        });
        buttonFilterIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showExpenses = false;
                createPieChart();
            }
        });


        lineChart = root.findViewById(R.id.lineChart);
        pieChart = root.findViewById(R.id.pieChart);

        shareFab = root.findViewById(R.id.shareFab);
        shareFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareStatistics();
            }
        });

        years = IntStream.range(1899, new Date().getYear() + 1900 + 1).boxed().sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());

        yearToShow = years.get(0);

        createLineChart();
        createPieChart();

        yearsAdapter = new ArrayAdapter<>(this.getContext(), R.layout.list_item, years);
        autocompleteYear = root.findViewById(R.id.autocompleteYear);
        autocompleteYear.setAdapter(yearsAdapter);
        autocompleteYear.setText(yearsAdapter.getItem(0).toString(), false);
        autocompleteYearLayout = root.findViewById(R.id.menuCategory);

        autocompleteYear.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                yearToShow = yearsAdapter.getItem(position);
                createLineChart();
                createPieChart();
            }
        });
    }

    private void createLineChart() {
        String[] values = new String[] {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                "Julio", "Agosto", "Septiempre", "Octubre", "Noviembre", "Diciembre"};

        Map<Integer, List<Transaction>> map = account.getGroupedTransactions(yearToShow);

        lineChart.setDragEnabled(false);
        lineChart.setScaleEnabled(false);

        LimitLine zeroLine = new LimitLine(0f, "");
        zeroLine.setLineWidth(2f);

        Description description = new Description();
        description.setText("Ingresos / Gastos");
        description.setTextSize(15f);
        description.setPosition(550f, 100f);
        lineChart.setDescription(description);
        lineChart.getAxisRight().setDrawLabels(false);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setAxisLineWidth(2f);
        xAxis.setLabelCount(11);
        xAxis.setLabelRotationAngle(45f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(values));

        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setAxisLineWidth(2f);
        yAxis.setAxisLineColor(Color.BLACK);
        yAxis.addLimitLine(zeroLine);
        yAxis.setDrawLimitLinesBehindData(true);

        List<Entry> entries = new ArrayList<>();
        float balance = 0;
        float maxBalance = 0;
        float minBalance = 0;
        for (int month = 0; month < 12; month++) {
            List<Transaction> transactions = map.getOrDefault(month, new ArrayList<>());
            for (Transaction transaction : transactions) {
                balance += transaction.getSignedValue();
                if (balance < minBalance) minBalance = balance;
                else if (balance > maxBalance) maxBalance = balance;
            }
            entries.add(new Entry(month, balance));
        }

        yAxis.setAxisMaximum(maxBalance + 10f);
        yAxis.setAxisMinimum(minBalance - 10f);

        LineDataSet dataset = new LineDataSet(entries, "Balance");
        dataset.setColor(Color.CYAN);
        dataset.setLineWidth(2f);

        LineData lineData = new LineData(dataset);

        lineChart.getAxisRight().setDrawGridLines(false);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getXAxis().setDrawGridLines(false);

        lineChart.setData(lineData);
        lineChart.animateXY(1000, 1000);
    }

    private void createPieChart() {
        ArrayList<PieEntry> categories = new ArrayList<>();

        Map<Category, Double> map = account.getCategories(yearToShow, showExpenses);
        ArrayList<Category> categoriesToShow = new ArrayList<>();
        for (Category category : map.keySet()) {
            categoriesToShow.add(category);
        }
        map.keySet().forEach(category -> categories.add(
                new PieEntry(Float.parseFloat(Objects.requireNonNull(map.get(category)).toString()),
                        category.toString())));

        totalBalance = 0;
        for (Category key : map.keySet()) {
            totalBalance += map.getOrDefault(key, 0.0);
        }

        int[] colors = {
                Color.parseColor("#488f31"),
//                Color.parseColor("#6a9832"),
                Color.parseColor("#88a037"),
//                Color.parseColor("#a5a73f"),
                Color.parseColor("#c0af4a"),
//                Color.parseColor("#dbb659"),
                Color.parseColor("#f4bd6a"),
//                Color.parseColor("#f2a95e"),
                Color.parseColor("#ef9556"),
//                Color.parseColor("#eb8050"),
                Color.parseColor("#e56b4e"),
//                Color.parseColor("#dd554f"),
                Color.parseColor("#de425b"),
        };

        PieDataSet pieDataSet = new PieDataSet(categories, "Categorias");
        pieDataSet.setColors(colors);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(15f);

        PieData pieData = new PieData(pieDataSet);

        Legend l = pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setForm(Legend.LegendForm.CIRCLE);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(5f);
        l.setYEntrySpace(5f);
        l.setYOffset(100f);
        l.setWordWrapEnabled(false);

        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                pieChart.setCenterText(round(e.getY(), 2) + " €" + "\n" + categoriesToShow.get(pieDataSet.getEntryIndex(e)));
            }

            @Override
            public void onNothingSelected() {
                pieChart.setCenterText(round(totalBalance, 2) + " €" + "\n" + "TOTAL");
            }
        });

        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText(round(totalBalance, 2) + " €" + "\n" + "TOTAL");
        pieChart.setCenterTextSize(15f);
        pieChart.setEntryLabelTextSize(0f);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setHoleRadius(40f);
        pieChart.setTransparentCircleRadius(45f);
        pieChart.animateXY(1000, 1000);
    }

    public void shareStatistics(){
        /*
        Bitmap img = getBitmapGraph();

        String filename = "${System.currentTimeMillis()}.jpg";

        try (FileOutputStream out = new FileOutputStream("filename")) {
            img.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (IOException e) {
            e.printStackTrace();
        }
        */

        /* es necesario hacer un intent con la constate ACTION_SEND */
        /*Llama a cualquier app que haga un envío*/
        Intent itSend = new Intent(Intent.ACTION_SEND);
        itSend.setType("text/plain");
        // itSend.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{para});
        itSend.putExtra(Intent.EXTRA_SUBJECT, "Registro de gastos e ingresos");
        StringBuilder stringBuilder = new StringBuilder("Historial de gastos / ingresos\n");
        for (Transaction transaction : account.getTransactionsList()) {
            if (transaction.isExpense()) {
                stringBuilder.append("-");
            } else {
                stringBuilder.append("+");
            }

            double value = transaction.getValue();
            value = round(value, 2);
            stringBuilder.append(value).append("€").append(" ").append("|").append(" ");

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy",
                    Locale.getDefault());
            String date = simpleDateFormat.format(transaction.getDate());
            stringBuilder.append(transaction.getName()).append(" ").append("|").append(" ")
                    .append(date).append("\n\r").append("\n\r");
        }
        stringBuilder.append("------------------------------------\n");
        stringBuilder.append("Balance Total: ").append(round(account.getBalance(), 2)).append("€");
        itSend.putExtra(Intent.EXTRA_TEXT, stringBuilder.toString());

        Intent shareIntent=Intent.createChooser(itSend, null);

        startActivity(shareIntent);
    }

    private Bitmap getBitmapGraph() {

        Bitmap bitmap = Bitmap.createBitmap(lineChart.getWidth(), lineChart.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        requireView().layout(lineChart.getLeft(), lineChart.getTop(), lineChart.getRight(),
                lineChart.getBottom());
        requireView().draw(canvas);
        return bitmap;
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_statistics, container, false);

        initializeVariables();

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
//        createLineChart(2023);
//        createPieChart(2023, false);
    }
}