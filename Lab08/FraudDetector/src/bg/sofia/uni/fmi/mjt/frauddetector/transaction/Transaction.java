package bg.sofia.uni.fmi.mjt.frauddetector.transaction;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record Transaction(String transactionID, String accountID, double transactionAmount,
                          LocalDateTime transactionDate, String location, Channel channel) {

    private static final int TRANSACTION_ID_INDEX = 0;
    private static final int ACCOUNT_ID_INDEX = 1;
    private static final int TRANSACTION_AMOUNT_INDEX = 2;
    private static final int TRANSACTION_DATE_INDEX = 3;
    private static final int LOCATION_INDEX = 4;
    private static final int CHANNEL_INDEX = 5;
    private static final int EXPECTED_FIELDS_COUNT = 6;
    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final String LINE_DELIMITER = ",";

    public static Transaction of(String line) {
        validateString(line);

        String[] transactionInfo = line.split(LINE_DELIMITER);
        if (transactionInfo.length != EXPECTED_FIELDS_COUNT) {
            throw new IllegalArgumentException("The given line does not give enough info");
        }

        String transactionId = transactionInfo[TRANSACTION_ID_INDEX];
        validateString(transactionId);

        String accountId = transactionInfo[ACCOUNT_ID_INDEX];
        validateString(accountId);

        double amount = Double.parseDouble(transactionInfo[TRANSACTION_AMOUNT_INDEX]);
        if (amount < 0) {
            throw new IllegalArgumentException("Transaction amount cannot be negative");
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
        LocalDateTime transactionDate = LocalDateTime.parse(transactionInfo[TRANSACTION_DATE_INDEX], formatter);

        String location = transactionInfo[LOCATION_INDEX];
        validateString(location);

        Channel channel = getChannelFromString(transactionInfo[CHANNEL_INDEX]);

        return new Transaction(transactionId, accountId, amount, transactionDate, location, channel);
    }

    private static void validateString(String str) {
        if (str == null || str.isBlank()) {
            throw new IllegalArgumentException(str + " cannot be null or blank");
        }
    }

    private static Channel getChannelFromString(String channel) {
        String upperChannel = channel.toUpperCase();

        for (Channel ch : Channel.values()) {
            if (ch.name().equals(upperChannel)) {
                return ch;
            }
        }

        throw new IllegalArgumentException("Invalid channel");
    }
}
