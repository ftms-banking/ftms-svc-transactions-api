package ftms.svc.transactions.api.infrastructure.exception;

public class ServiceOperationException extends RuntimeException {
    public ServiceOperationException(String message) {
        super(message);
    }

    public ServiceOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceOperationException() {
        super("Service operation failed");
    }
}
