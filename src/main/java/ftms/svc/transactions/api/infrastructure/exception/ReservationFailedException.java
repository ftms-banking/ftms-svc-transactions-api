package ftms.svc.transactions.api.infrastructure.exception;

public class ReservationFailedException extends ServiceOperationException {
    public ReservationFailedException() {
        super("Balance reservation failed");
    }

    public ReservationFailedException(String message) {
        super(message);
    }
}
