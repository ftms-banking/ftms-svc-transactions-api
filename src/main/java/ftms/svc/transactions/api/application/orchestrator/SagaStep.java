package ftms.svc.transactions.api.application.orchestrator;

public enum SagaStep {
    CREATED_TRANSACTION,
    BALANCE_RESERVED,
    SOURCE_DEBITED,
    DESTINATION_CREDITED,
    COMPLETED
}
