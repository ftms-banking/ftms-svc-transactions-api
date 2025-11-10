package ftms.svc.transactions.api.infrastructure.exception;

public class TransferFailedException extends ServiceOperationException {
    public TransferFailedException() {
        super("Transfer failed during saga execution");
    }

    public TransferFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
