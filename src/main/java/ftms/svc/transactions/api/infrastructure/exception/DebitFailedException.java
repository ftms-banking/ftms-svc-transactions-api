package ftms.svc.transactions.api.infrastructure.exception;

public class DebitFailedException extends ServiceOperationException {
    public DebitFailedException() {
        super("Debit operation failed");
    }

    public DebitFailedException(String message) {
        super(message);
    }
}
