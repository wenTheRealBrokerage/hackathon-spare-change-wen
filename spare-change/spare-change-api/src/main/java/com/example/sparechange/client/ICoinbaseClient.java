package com.example.sparechange.client;

import java.math.BigDecimal;
import java.util.List;

public interface ICoinbaseClient {
    String buyUsdToCrypto(BigDecimal usd, String productId);
    List<OrderDto> listOrders();
}