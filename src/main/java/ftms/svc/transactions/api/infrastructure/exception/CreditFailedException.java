package ftms.svc.transactions.api.infrastructure.exception;

public class CreditFailedException extends ServiceOperationException {
    public CreditFailedException() {
        super("Credit operation failed");
    }

    public CreditFailedException(String message) {
        super(message);
    }
}
