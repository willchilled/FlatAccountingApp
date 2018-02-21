package Business.Parsers;

import Models.TransactionType;

public interface TransactionTypeMapper {

    TransactionType mapType(String transactionType);
}
