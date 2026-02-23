package it.hackhub.application.dto.common;

public class StandardResponse<T> {

  private boolean success;
  private String message;
  private T data;
  private Object error;

  public StandardResponse() {}

  public StandardResponse(boolean success, String message, T data) {
    this.success = success;
    this.message = message;
    this.data = data;
  }

  public static <T> StandardResponse<T> success(T data) {
    return new StandardResponse<>(true, "Operazione completata con successo", data);
  }

  public static <T> StandardResponse<T> success(String message) {
    return new StandardResponse<>(true, message, null);
  }

  public static StandardResponse<Void> error(String message) {
    return new StandardResponse<>(false, message, null);
  }

  public static StandardResponse<Void> error(String message, Object errorDetails) {
    StandardResponse<Void> r = new StandardResponse<>(false, message, null);
    r.setError(errorDetails);
    return r;
  }

  public boolean isSuccess() { return success; }
  public void setSuccess(boolean success) { this.success = success; }
  public String getMessage() { return message; }
  public void setMessage(String message) { this.message = message; }
  public T getData() { return data; }
  public void setData(T data) { this.data = data; }
  public Object getError() { return error; }
  public void setError(Object error) { this.error = error; }
}
