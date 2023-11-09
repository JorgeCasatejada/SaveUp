package com.example.saveup.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.saveup.R;
import com.example.saveup.model.Account;
import com.example.saveup.model.Category;
import com.example.saveup.model.Transaction;
import com.github.mikephil.charting.animation.Easing;
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
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StatisticsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatisticsFragment extends Fragment {

    private static final String ACCOUNT = "Account";
    private Account account;
    private View root;

    private FloatingActionButton shareFab;

    private LineChart lineChart;
    private PieChart pieChart;

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
        shareFab = root.findViewById(R.id.shareFab);
        shareFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareStatistics();
            }
        });

        createLineChart();
        createPieChart();
    }

    private void createLineChart() {
        String[] values = new String[] {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                "Julio", "Agosto", "Septiempre", "Octubre", "Noviembre", "Diciembre"};

        Map<Integer, List<Transaction>> map = getGroupedTransactions(2023 - 1900);

        lineChart = root.findViewById(R.id.lineChart);

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
                System.out.println(transaction.getSignedValue());
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

    private Map<Integer, List<Transaction>> getGroupedTransactions(int year) {
        account.getTransactionsList().forEach(System.out::println);
        return account.getTransactionsList().stream().filter(t -> t.getDate().getYear() == year).collect(Collectors.groupingBy(t -> t.getDate().getMonth()));
    }

    private void createPieChart() {
        pieChart = root.findViewById(R.id.pieChart);
        ArrayList<PieEntry> categories = new ArrayList<>();

        Map<Category, Double> map = getCategories();
        map.keySet().forEach(category -> categories.add(
                new PieEntry(Float.parseFloat(Objects.requireNonNull(map.get(category)).toString()),
                        category.toString())));

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
        pieDataSet.setValueTextSize(10f);

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

        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("Categorias");
        pieChart.setCenterTextSize(15f);
        pieChart.setEntryLabelTextSize(10f);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setHoleRadius(40f);
        pieChart.setTransparentCircleRadius(45f);
        pieChart.animateXY(1000, 1000);
    }

    private Map<Category, Double> getCategories() {
        return account.getTransactionsList().stream().collect(
                Collectors.groupingBy(Transaction::getCategory,
                    Collectors.mapping(Transaction::getValue, Collectors.summingDouble(Double::doubleValue))));
    }

    public void shareStatistics(){
        /* es necesario hacer un intent con la constate ACTION_SEND */
        /*Llama a cualquier app que haga un envío*/
        Intent itSend = new Intent(Intent.ACTION_SEND);
        /* vamos a enviar texto plano */
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
        System.out.println("///////////////////////////////////////////" +
                "///////////////////////////////////////////" +
                "///////////////////////////////////////////" +
                "///////////////////////////////////////////" +
                "///////////////////////////////////////////" +
                "///////////////////////////////////////////");
        createLineChart();
        createPieChart();
    }
}