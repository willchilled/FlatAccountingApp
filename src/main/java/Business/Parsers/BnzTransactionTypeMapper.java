package Business.Parsers;

import Models.TransactionType;

public class BnzTransactionTypeMapper implements TransactionTypeMapper{


    public BnzTransactionTypeMapper() {}

    @Override
    public TransactionType mapType(String transactionType) {
        switch (transactionType) {
            case "Automatic Payment" : return TransactionType.AUPTO_PAYMENT;
            case "Direct Credit" : return TransactionType.DIRECT_CREDIT;
            case "Transfer" : return TransactionType.TRANSFER;
            case "EFTPOS" : return TransactionType.EFTPOS;
            case "Eft-Pos" : return  TransactionType.EFTPOS;
            default : return TransactionType.UNKNOWN;
        }
    }
}
