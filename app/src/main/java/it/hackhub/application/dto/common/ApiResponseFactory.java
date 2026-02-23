package it.hackhub.application.dto.common;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public final class ApiResponseFactory {

  private ApiResponseFactory() {}

  public static ResponseEntity<StandardResponse<Void>> error(HttpStatus status, String message) {
    return ResponseEntity.status(status).body(StandardResponse.error(message));
  }

  public static ResponseEntity<StandardResponse<Void>> validationError(
    String message,
    Map<String, String> fieldErrors
  ) {
    return ResponseEntity
      .status(HttpStatus.BAD_REQUEST)
      .body(StandardResponse.error(message, fieldErrors));
  }
}
