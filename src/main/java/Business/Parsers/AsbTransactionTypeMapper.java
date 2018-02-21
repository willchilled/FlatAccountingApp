package Business.Parsers;

import Models.TransactionType;

public class AsbTransactionTypeMapper implements TransactionTypeMapper {

    public AsbTransactionTypeMapper() {}

    @Override
    public TransactionType mapType(String transactionType) {
        switch (transactionType) {
            case "A/P" : return TransactionType.AUPTO_PAYMENT;
            case "D/C" : return TransactionType.DIRECT_CREDIT;
            case "TFR OUT" : return TransactionType.TRANSFER;
            case "TFR IN" : return TransactionType.TRANSFER;
            case "EFTPOS" : return  TransactionType.EFTPOS;
            default : return TransactionType.UNKNOWN;
        }
    }
}
