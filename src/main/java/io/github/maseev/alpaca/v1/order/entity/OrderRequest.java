package io.github.maseev.alpaca.v1.order.entity;

import static java.lang.String.format;
import static java.util.Arrays.asList;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.github.maseev.alpaca.v1.order.entity.Order.TimeInForce;
import io.github.maseev.alpaca.v1.order.entity.Order.Type;
import java.math.BigDecimal;
import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@JsonSerialize(as = ImmutableOrderRequest.class)
@JsonDeserialize(as = ImmutableOrderRequest.class)
public interface OrderRequest {

  String symbol();

  long qty();

  Order.Side side();

  Type type();

  @JsonProperty("time_in_force")
  TimeInForce timeInForce();

  @Nullable
  @JsonProperty("limit_price")
  BigDecimal limitPrice();

  @Nullable
  @JsonProperty("stop_price")
  BigDecimal stopPrice();

  @Nullable
  @JsonProperty("client_order_id")
  String clientOrderId();

  @Value.Check
  default void check() {
    if (qty() <= 0) {
      throw new IllegalStateException(format("'qty' must be positive; qty: %s", qty()));
    }

    if (timeInForce() == TimeInForce.IOC || timeInForce() == TimeInForce.FOK) {
      throw new IllegalStateException(
        format("'timeInForce' can only be %s; timeInForce: %s",
          asList(TimeInForce.DAY, TimeInForce.GTC, TimeInForce.OPG), timeInForce()));
    }

    if ((type() == Type.LIMIT || type() == Type.STOP_LIMIT)
      && limitPrice() == null) {
      throw new IllegalStateException(
        format("'limitPrice' can't be null when 'type' is %s or %s; limitPrice: %s",
          Type.LIMIT, Type.STOP, limitPrice()));
    }

    if ((type() == Type.STOP || type() == Type.STOP_LIMIT)
      && stopPrice() == null) {
      throw new IllegalStateException(
        format("'stopPrice' can't be null when 'type' is %s or %s; stopPrice: %s",
          Type.STOP, Type.STOP_LIMIT, stopPrice()));
    }

    if (limitPrice() != null && limitPrice().compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalStateException(
        format("'limitPrice' can't be negative; limitPrice: %s", limitPrice()));
    }

    if (stopPrice() != null && stopPrice().compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalStateException(
        format("'stopPrice' can't be negative; stopPrice: %s", stopPrice()));
    }

    final int MAX_CLIENT_ORDER_ID_LENGTH = 48;

    if (clientOrderId().length() > MAX_CLIENT_ORDER_ID_LENGTH) {
      throw new IllegalStateException(
        format("'clientOrderId' must be less than or equal to %s; clientOrderId: %s",
          MAX_CLIENT_ORDER_ID_LENGTH, clientOrderId()));
    }
  }
}
