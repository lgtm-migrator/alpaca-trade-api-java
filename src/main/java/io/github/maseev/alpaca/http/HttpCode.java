package io.github.maseev.alpaca.http;

import io.github.maseev.alpaca.http.exception.APIException;
import io.github.maseev.alpaca.http.exception.AuthenticationException;
import io.github.maseev.alpaca.http.exception.RateLimitException;
import java.util.function.Function;
import org.asynchttpclient.Response;

public enum HttpCode {

  OK(200),
  UNAUTHORIZED(401, AuthenticationException::new),
  TOO_MANY_REQUESTS(429, RateLimitException::new);

  private final int code;
  private final Function<Response, APIException> exceptionSupplier;

  HttpCode(int code) {
    this.code = code;
    exceptionSupplier = null;
  }

  HttpCode(int code, Function<Response, APIException> exceptionSupplier) {
    this.code = code;
    this.exceptionSupplier = exceptionSupplier;
  }

  public void doThrow(Response response) throws APIException {
    if (exceptionSupplier != null) {
      throw exceptionSupplier.apply(response);
    }
  }

  public static HttpCode valueOf(int statusCode) {
    for (HttpCode httpCode : HttpCode.values()) {
      if (httpCode.code == statusCode) {
        return httpCode;
      }
    }

    throw new IllegalArgumentException(
      String.format("Unrecognizable status code; code: %s", statusCode));
  }
}