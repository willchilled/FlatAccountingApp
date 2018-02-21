package Business.Parsers;
import Models.Transaction;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.text.*;
import java.util.ArrayList;
import java.util.Scanner;

public abstract class AbstractParser {


    public ArrayList<Transaction> parseFile(File file) {

        ArrayList<Transaction> transactions = new ArrayList<>();

        try {
            Scanner scanner = new Scanner(file);
            if (!scanner.hasNextLine()) return transactions;

            //Skip pre lines if there are headers etc.
            skippIrrelevantLines(scanner);

            while (scanner.hasNextLine()) {
                String[] fields = scanner.nextLine().split(",", -1);
                if (validateLineData(fields)) {
                    Transaction transaction = makeTransaction(fields);
                    if (transaction != null) transactions.add(transaction);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return transactions;
    }

    //Skip however many lines as are necessary to get to the actual data in the scanner
    protected abstract void skippIrrelevantLines(Scanner scanner);

    //When the data is comma delimited split, Validate its fields returning true if it is all good
    protected abstract boolean validateLineData(String[] fields);

    //Turn the amount value into a BigDecimal
    protected abstract BigDecimal parseAmount(String amount) throws ParseException;

    //Create a transaction Object
    protected abstract Transaction makeTransaction(String[] fields);

}
