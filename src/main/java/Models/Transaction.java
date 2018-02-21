package Models;

import java.math.BigDecimal;
import java.util.Date;

public class Transaction {

    private Date date;
    private BigDecimal amount;
    private String particulars;
    private String code;
    private String reference;

    public Transaction(Date date, BigDecimal amount, String particulars, String code, String reference) {

        this.date = date;
        this.amount = amount;
        this.particulars = particulars;
        this.code = code;
        this.reference = reference;
    }

    public Date getDate() {
        return date;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getParticulars() {
        return particulars;
    }

    public String getCode() {
        return code;
    }

    public String getReference() {
        return reference;
    }
}
