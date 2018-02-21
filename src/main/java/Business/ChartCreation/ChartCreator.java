package Business.ChartCreation;

import Models.Transaction;
import javafx.scene.chart.XYChart;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChartCreator {

    private ArrayList<Transaction> transactions;
    private static final DateFormat df = new SimpleDateFormat("dd MMM yyyy");

    public ChartCreator(ArrayList<Transaction> transactions) {
        this.transactions = transactions;
    }

    public XYChart.Series<String, Number> create() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (Transaction transaction : transactions) {
            series.getData().add(new XYChart.Data<>(df.format(transaction.getDate()), transaction.getAmount()));
        }

        return series;

    }
}
