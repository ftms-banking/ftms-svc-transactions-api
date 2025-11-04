package ftms.svc.transactions.api.domain.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public class Transaction {

    private UUID uuid;
    private UUID sourceAccountId;
    private UUID destinationAccountId;
    private BigDecimal amount;
    private String currency;
    private String description;
    private String idempotencyKey;
    private TransactionStatus status;
    private TransactionType type;
    private OffsetDateTime createdAt;
    private OffsetDateTime completedAt;

    public Transaction(UUID uuid,
                       UUID sourceAccountId,
                       UUID destinationAccountId,
                       BigDecimal amount,
                       String currency,
                       String description,
                       TransactionStatus status,
                       TransactionType type,
                       OffsetDateTime createdAt,
                       OffsetDateTime completedAt,
                       String idempotencyKey) {
        this.uuid = uuid;
        this.sourceAccountId = sourceAccountId;
        this.destinationAccountId = destinationAccountId;
        this.amount = amount;
        this.currency = currency;
        this.description = description;
        this.status = status;
        this.type = type;
        this.createdAt = createdAt;
        this.completedAt = completedAt;
        this.idempotencyKey = idempotencyKey;
    }

    public String getIdempotencyKey() { return idempotencyKey; }

    public UUID getUuid() { return uuid; }
    public UUID getSourceAccountId() { return sourceAccountId; }
    public UUID getDestinationAccountId() { return destinationAccountId; }
    public BigDecimal getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public String getDescription() { return description; }
    public TransactionStatus getStatus() { return status; }
    public TransactionType getType() { return type; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getCompletedAt() { return completedAt; }
}
