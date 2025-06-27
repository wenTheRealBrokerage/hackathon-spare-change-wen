package com.example.sparechange.client;

import java.math.BigDecimal;
import java.util.List;

public interface ICoinbaseClient {
    String buyUsdcToBtc(BigDecimal usd);
    List<OrderDto> listOrders();
}