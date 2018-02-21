package Business.Parsers;

import java.math.BigDecimal;
import java.text.*;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;

import Models.Transaction;
import Models.TransactionType;
import org.apache.commons.lang3.StringUtils;

public class BnzCsvParser extends AbstractParser {

    private static final int EXPECTED_BNZ_FIELD_LENGTH = 11;

    public BnzCsvParser() {}

    protected void skippIrrelevantLines(Scanner scanner) {
        scanner.nextLine();
    }

    public boolean validateLineData(String[] fields) {
        if (fields.length != EXPECTED_BNZ_FIELD_LENGTH) return false;

        String txDate = fields[0];
        String processDate = fields[1];
        String type = fields[2];
        String details = fields[3];
        String particulars = fields[4];
        String code = fields[5];
        String ref = fields[6];
        String amount = fields[7];
        String toFrmAccount = fields[8];
        String conversionCharge = fields[9];
        String foreignCurrenctyAmt = fields[10];

        //Hack to fix this fucky double eftpos business
        if (type.equals("Eft-Pos")) type = "EFTPOS";

        DateFormat df = new SimpleDateFormat("dd MMM yyyy");
        try {
            df.parse(txDate);
        } catch (ParseException e) {
            System.err.println("Could not parse transaction date");
            return false;
        }

        if (!processDate.isEmpty()) {
            try {
                df.parse(txDate);
            } catch (ParseException e) {
                System.err.println("Could not parse processed date");
                return false;
            }
        }

        try {
            parseAmount(amount);
        } catch (ParseException e) {
            System.err.println("Could not parse amount");
            return false;
        }

        return StringUtils.isNumericSpace(toFrmAccount.replaceAll("-", " "));


    }

    public BigDecimal parseAmount(String amount) throws ParseException {
        final NumberFormat format = NumberFormat.getNumberInstance(Locale.US);
        if (format instanceof DecimalFormat) {
            ((DecimalFormat) format).setParseBigDecimal(true);
        }
        return (BigDecimal) format.parse(amount.replaceAll("[^\\d.,-]",""));
    }

    public Transaction makeTransaction(String[] fields){
        DateFormat df = new SimpleDateFormat("dd MMM yyyy");
        try {
            Date txDate = df.parse(fields[0]);
            TransactionType type = new BnzTransactionTypeMapper().mapType(fields[2]);
            String details = fields[3];
            String particulars = fields[4];
            String code = fields[5];
            String ref = fields[6];
            BigDecimal amount = parseAmount(fields[7]);
            String toFrmAccount = fields[8];
            String conversionCharge = fields[9];
            String foreignCurrenctyAmt = fields[10];

            return new Transaction(txDate, amount, particulars, code, ref);
        } catch (ParseException e) {
            System.err.println("Unknown error in parsing");
        }

        return null;

    }

}
