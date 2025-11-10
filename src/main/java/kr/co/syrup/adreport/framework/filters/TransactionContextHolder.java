package kr.co.syrup.adreport.framework.filters;
import org.springframework.util.Assert;

public class TransactionContextHolder {

    private static final ThreadLocal<String> transactionContextHolder = new ThreadLocal<String>();

    public static void setTransactionId(String transactionId) {
        Assert.notNull(transactionId, "TransactionId cannot be null");
        transactionContextHolder.set(transactionId);
    }

    public static String getTransactionId() {
        return transactionContextHolder.get();
    }

    public static void clearTransactionId() {
        transactionContextHolder.remove();
    }
}
