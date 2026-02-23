package it.hackhub.application.handlers.external.pagamenti.impl;

import it.hackhub.application.handlers.external.pagamenti.PaymentService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class PayPalPaymentService implements PaymentService {

  private static final Logger log = LoggerFactory.getLogger(
    PayPalPaymentService.class
  );

  @Value("${paypal.client-id}")
  private String clientId;

  @Value("${paypal.client-secret}")
  private String clientSecret;

  @Value("${paypal.mode}")
  private String mode;

  private final RestTemplate restTemplate = new RestTemplate();

  @Override
  public PaymentResponse processPayment(
    String email,
    Double amount,
    String currency,
    String referenceId
  ) {
    String baseUrl = getBaseUrl();
    log.info(
      "PayPal processPayment: baseUrl={}, email={}, amount={} {}, referenceId={}",
      baseUrl,
      email,
      amount,
      currency,
      referenceId
    );
    try {
      log.debug(
        "PayPal: richiesta access token OAuth a {}/v1/oauth2/token",
        baseUrl
      );
      String accessToken = getAccessToken();
      if (accessToken == null || accessToken.isEmpty()) {
        log.error("PayPal: access token vuoto dopo getAccessToken()");
        return new PaymentResponse(
          false,
          null,
          "Access token non ottenuto",
          null,
          null,
          null
        );
      }

      String url = baseUrl + "/v1/payments/payouts";
      log.info("PayPal: invio richiesta payout a {}", url);

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.setBearerAuth(accessToken);

      Map<String, Object> requestBody = new HashMap<>();

      Map<String, Object> senderBatchHeader = new HashMap<>();
      senderBatchHeader.put(
        "sender_batch_id",
        "Payouts_" + referenceId + "_" + System.currentTimeMillis()
      );
      senderBatchHeader.put("email_subject", "You have a payout!");
      requestBody.put("sender_batch_header", senderBatchHeader);

      List<Map<String, Object>> items = new ArrayList<>();
      Map<String, Object> item = new HashMap<>();
      item.put("recipient_type", "EMAIL");

      Map<String, Object> amountMap = new HashMap<>();
      amountMap.put(
        "value",
        String.format("%.2f", amount).replace(",", ".")
      );
      amountMap.put("currency", currency);

      item.put("amount", amountMap);
      item.put("receiver", email);
      item.put("note", "Grazie per la partecipazione!");
      item.put("sender_item_id", referenceId);

      items.add(item);
      requestBody.put("items", items);

      HttpEntity<Map<String, Object>> request = new HttpEntity<>(
        requestBody,
        headers
      );

      ResponseEntity<Map> response = restTemplate.postForEntity(
        url,
        request,
        Map.class
      );

      log.info(
        "PayPal payout response: status={}, body present={}",
        response.getStatusCode(),
        response.getBody() != null
      );

      if (response.getStatusCode() != HttpStatus.CREATED) {
        String errMsg = "PayPal API returned: " + response.getStatusCode();
        if (response.getBody() != null) {
          errMsg += " " + response.getBody();
        }
        log.error("PayPal payout failed: {}", errMsg);
        return new PaymentResponse(
          false,
          null,
          errMsg,
          null,
          null,
          null
        );
      }
      Map body = response.getBody();
      if (body != null && body.containsKey("batch_header")) {
        Map batchHeader = (Map) body.get("batch_header");
        String payoutBatchId = (String) batchHeader.get("payout_batch_id");
        String batchStatus = (String) batchHeader.get("batch_status");
        String senderBatchId = null;
        if (batchHeader.containsKey("sender_batch_header")) {
          Map responseSenderBatch = (Map) batchHeader.get(
            "sender_batch_header"
          );
          if (responseSenderBatch != null) {
            senderBatchId =
              (String) responseSenderBatch.get("sender_batch_id");
          }
        }
        String payoutDetailsUrl = null;
        if (body.containsKey("links") && body.get("links") instanceof List) {
          List<?> links = (List<?>) body.get("links");
          if (
            !links.isEmpty() && links.get(0) instanceof Map
          ) {
            Object href = ((Map<?, ?>) links.get(0)).get("href");
            if (href != null) {
              payoutDetailsUrl = href.toString();
            }
          }
        }
        return new PaymentResponse(
          true,
          payoutBatchId,
          null,
          batchStatus,
          senderBatchId,
          payoutDetailsUrl
        );
      }
      return new PaymentResponse(true, null, null, null, null, null);
    } catch (Exception e) {
      log.error(
        "PayPal processPayment exception: {}",
        e.getMessage(),
        e
      );
      return new PaymentResponse(
        false,
        null,
        e.getMessage(),
        null,
        null,
        null
      );
    }
  }

  private String getAccessToken() {
    String url = getBaseUrl() + "/v1/oauth2/token";

    HttpHeaders headers = new HttpHeaders();
    headers.setBasicAuth(clientId, clientSecret);
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
    body.add("grant_type", "client_credentials");

    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(
      body,
      headers
    );

    ResponseEntity<Map> response = restTemplate.postForEntity(
      url,
      request,
      Map.class
    );

    if (response.getStatusCode() != HttpStatus.OK) {
      log.error(
        "PayPal OAuth failed: status={}, body={}",
        response.getStatusCode(),
        response.getBody()
      );
      throw new RuntimeException(
        "PayPal OAuth failed: " +
        response.getStatusCode() +
        " " +
        response.getBody()
      );
    }
    Map responseBody = response.getBody();
    if (responseBody == null) {
      log.error("PayPal OAuth: response body null");
      throw new RuntimeException("PayPal OAuth: response body null");
    }
    Object token = responseBody.get("access_token");
    if (token == null) {
      log.error(
        "PayPal OAuth: access_token mancante nel body. Body: {}",
        responseBody
      );
      throw new RuntimeException(
        "PayPal OAuth: access_token mancante. Verifica client-id e client-secret su https://developer.paypal.com/dashboard/applications/sandbox"
      );
    }
    return (String) token;
  }

  private String getBaseUrl() {
    return "sandbox".equalsIgnoreCase(mode)
      ? "https://api-m.sandbox.paypal.com"
      : "https://api-m.paypal.com";
  }
}
