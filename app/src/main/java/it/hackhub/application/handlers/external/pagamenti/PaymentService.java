package it.hackhub.application.handlers.external.pagamenti;

public interface PaymentService {

  PaymentResponse processPayment(
    String email,
    Double amount,
    String currency,
    String referenceId
  );

  record PaymentResponse(
    boolean success,
    String transactionId,
    String errorMessage,
    String batchStatus,
    String senderBatchId,
    String payoutDetailsUrl
  ) {}
}
