package com.payment.paymentservice.service;

import com.payment.paymentservice.model.PaymentStatus;
import com.payment.paymentservice.model.TransactionStatus;
import com.payment.paymentservice.repository.PaymentOrderRepository;
import com.payment.paymentservice.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class WebhookService {

    @Value("${razorpay.webhook-secret:}")
    private String webhookSecret;

    private final PaymentOrderRepository paymentOrderRepository;
    private final TransactionRepository transactionRepository;

    public void handleWebhook(String payload, String receivedSignature) {

        // If no secret configured, accept all webhooks (DEV ONLY).
        if (webhookSecret != null && !webhookSecret.isBlank()) {
            if (!isValidSignature(payload, receivedSignature)) {
                // Invalid signature: ignore
                return;
            }
        }

        JSONObject json = new JSONObject(payload);
        String event = json.optString("event");

        if ("payment.captured".equalsIgnoreCase(event)) {
            handlePaymentCaptured(json);
        } else if ("payment.failed".equalsIgnoreCase(event)) {
            handlePaymentFailed(json);
        }
    }

    private boolean isValidSignature(String payload, String signature) {
        if (signature == null) return false;
        try {
            Mac sha256Hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(
                    webhookSecret.getBytes(StandardCharsets.UTF_8),
                    "HmacSHA256"
            );
            sha256Hmac.init(secretKey);
            byte[] hash = sha256Hmac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            String computed = bytesToHex(hash);
            return computed.equals(signature);
        } catch (Exception e) {
            return false;
        }
    }

    private String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    private void handlePaymentCaptured(JSONObject json) {
        JSONObject payload = json.getJSONObject("payload");
        JSONObject payment = payload.getJSONObject("payment").getJSONObject("entity");

        String razorpayOrderId = payment.optString("order_id");
        String razorpayPaymentId = payment.optString("id");

        paymentOrderRepository.findByRazorpayOrderId(razorpayOrderId)
                .ifPresent(order -> {
                    order.setStatus(PaymentStatus.SUCCESS);
                    order.setRazorpayPaymentId(razorpayPaymentId);
                    order.setUpdatedAt(Instant.now());
                    paymentOrderRepository.save(order);

                    transactionRepository.findByExternalReferenceId(order.getExternalReferenceId())
                            .forEach(txn -> {
                                if (razorpayOrderId.equals(txn.getRazorpayOrderId())) {
                                    txn.setStatus(TransactionStatus.SUCCESS);
                                    txn.setRazorpayPaymentId(razorpayPaymentId);
                                    transactionRepository.save(txn);
                                }
                            });
                });
    }

    private void handlePaymentFailed(JSONObject json) {
        JSONObject payload = json.getJSONObject("payload");
        JSONObject payment = payload.getJSONObject("payment").getJSONObject("entity");

        String razorpayOrderId = payment.optString("order_id");
        String razorpayPaymentId = payment.optString("id");

        paymentOrderRepository.findByRazorpayOrderId(razorpayOrderId)
                .ifPresent(order -> {
                    order.setStatus(PaymentStatus.FAILED);
                    order.setRazorpayPaymentId(razorpayPaymentId);
                    order.setUpdatedAt(Instant.now());
                    paymentOrderRepository.save(order);

                    transactionRepository.findByExternalReferenceId(order.getExternalReferenceId())
                            .forEach(txn -> {
                                if (razorpayOrderId.equals(txn.getRazorpayOrderId())) {
                                    txn.setStatus(TransactionStatus.FAILED);
                                    txn.setRazorpayPaymentId(razorpayPaymentId);
                                    transactionRepository.save(txn);
                                }
                            });
                });
    }
}

