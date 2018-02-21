package Controllers;

import Business.ChartCreation.ChartCreator;
import Business.Parsers.BnzCsvParser;
import Models.Transaction;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.joda.time.DateTime;

import java.io.File;
import java.math.BigDecimal;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class MainController implements Initializable {


    @FXML
    private LineChart<String, Number> moneyChart;
    @FXML
    private RadioButton outgoingRadio;
    @FXML
    private RadioButton incomingRadio;
    @FXML
    private RadioButton allRadio;
    @FXML
    private ComboBox fromComboBox;
    @FXML
    private ComboBox untilComboBox;

    private ArrayList<Transaction> transactions;
    private File selectedFile;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        moneyChart.setVisible(false);
        moneyChart.getYAxis().setLabel("Cash");
        moneyChart.getXAxis().setLabel("Time");
        outgoingRadio.setDisable(true);
        incomingRadio.setDisable(true);
        allRadio.setSelected(true);

        initComboBoxes();
    }

    public void initComboBoxes() {
        //This applies custom rendering changes
        fromComboBox.setButtonCell(new DateListCell());
        fromComboBox.setCellFactory((Callback<ListView<Date>, ListCell<Date>>) p -> new DateListCell());
        untilComboBox.setButtonCell(new DateListCell());
        untilComboBox.setCellFactory((Callback<ListView<Date>, ListCell<Date>>) p -> new DateListCell());
    }

    @FXML
    public void parseACsvFile() {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().addAll(
                new ExtensionFilter("CSV Files", "*.csv"));
        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile == null) return;
        this.selectedFile = selectedFile;
        transactions = new BnzCsvParser().parseFile(selectedFile);

        populateFromAndUntilCombos();
        setGraph();
        incomingRadio.setDisable(false);
        outgoingRadio.setDisable(false);
    }


    @FXML
    public void fromSelected() {
        Date from = (Date) fromComboBox.getValue();
        Date until = (Date) untilComboBox.getValue();
        if (until != null) checkSwap(from, until);
        setGraph();
    }

    @FXML
    public void untilSelected() {
        Date from = (Date) fromComboBox.getValue();
        Date until = (Date) untilComboBox.getValue();
        if (from != null) checkSwap(from, until);
        setGraph();

    }

    private void checkSwap(Date from, Date until) {
        //If from > until or vice versa, swap them.
        if (from != null && until != null && from.compareTo(until) > 0) {
            Platform.runLater(() -> {
                fromComboBox.setValue(until);
                untilComboBox.setValue(from);
            });
        }
    }

    @FXML
    public void outgoingRadioClicked() {
        incomingRadio.setSelected(false);
        allRadio.setSelected(false);
        setGraph();
    }

    @FXML
    public void incomingRadioClicked() {
        outgoingRadio.setSelected(false);
        allRadio.setSelected(false);
        setGraph();
    }

    @FXML
    public void allRadioClicked() {
        incomingRadio.setSelected(false);
        outgoingRadio.setSelected(false);
        setGraph();
    }



    private void populateFromAndUntilCombos() {
        transactions.sort(Comparator.comparing(Transaction::getDate));
        Date firstDate = transactions.get(0).getDate();
        Date lastDate = transactions.get(transactions.size() -1).getDate();
        long delta = lastDate.getTime() - firstDate.getTime();
        long daysDiff = TimeUnit.DAYS.convert(delta, TimeUnit.MILLISECONDS);

        ArrayList<Date> dates = new ArrayList<>(Arrays.asList(firstDate));
        Calendar cal = Calendar.getInstance();
        cal.setTime(firstDate);
        while (daysDiff > 7) {
            cal.add(Calendar.DAY_OF_YEAR, 7);
            dates.add(cal.getTime());
            daysDiff -= 7;
        }
        dates.add(lastDate);

        fromComboBox.setItems(FXCollections.observableArrayList(dates));
        untilComboBox.setItems(FXCollections.observableArrayList(dates));

        fromComboBox.setDisable(false);
        untilComboBox.setDisable(false);
    }

    private void setGraph() {
        moneyChart.getData().clear();
        moneyChart.setTitle(selectedFile.getName());

        ArrayList<Transaction> transactionsToDisplay = filterTransactions();

        XYChart.Series<String, Number> series = new ChartCreator(transactionsToDisplay).create();
        series.setName("Historical Data for " + selectedFile.getName());

        moneyChart.getData().add(series);
        moneyChart.setVisible(true);
    }

    private ArrayList<Transaction> filterTransactions() {
        Date from = (Date) fromComboBox.getValue();
        Date until = (Date) untilComboBox.getValue();

        ArrayList<Transaction> fromFilteredTransactions = new ArrayList<>();
        if (from != null) {
            transactions.forEach(transaction -> {
                if (from.compareTo(transaction.getDate()) < 0) {
                    fromFilteredTransactions.add(transaction);
                }
            });
        } else {
            fromFilteredTransactions.addAll(transactions);
        }

        ArrayList<Transaction> untilFilteredTransactions = new ArrayList<>();
        if (until != null) {
            fromFilteredTransactions.forEach(transaction -> {
                if (transaction.getDate().compareTo(until) < 0) {
                    untilFilteredTransactions.add(transaction);
                }
            });
        } else {
            untilFilteredTransactions.addAll(fromFilteredTransactions);
        }

        ArrayList<Transaction> radioFilteredTransactions = new ArrayList<>();
        if (outgoingRadio.isSelected()) {
            untilFilteredTransactions.forEach(transaction -> {
                if (transaction.getAmount().compareTo(new BigDecimal(0)) < 0) {
                    radioFilteredTransactions.add(transaction);
                }
            });
        } else if (incomingRadio.isSelected()) {
            untilFilteredTransactions.forEach(transaction -> {
                if (transaction.getAmount().compareTo(new BigDecimal(0)) > 0) {
                    radioFilteredTransactions.add(transaction);
                }
            });
        } else {
            radioFilteredTransactions.addAll(untilFilteredTransactions);
        }

        return radioFilteredTransactions;
    }


    //Custom renderer for Combo boxes for dates
    class DateListCell extends ListCell<Date> {
        @Override protected void updateItem(Date item, boolean empty) {
            SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy");
            super.updateItem(item, empty);
            if (!empty && item != null) {
                setText(df.format(item));
            } else {
                setText(null);
            }
        }
    }

}