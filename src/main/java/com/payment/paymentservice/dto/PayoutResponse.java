package com.payment.paymentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PayoutResponse {

    private String transactionId;
    private String razorpayPayoutId;
    private String status;
}
