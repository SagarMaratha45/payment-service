package com.payment.paymentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WalletAdjustmentRequest {

    private double walletAdjustment;
}