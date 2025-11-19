package com.payment.paymentservice.service;

import com.payment.paymentservice.dto.CreatePaymentOrderRequest;
import com.payment.paymentservice.dto.CreatePaymentOrderResponse;
import com.payment.paymentservice.model.PaymentOrder;
import com.payment.paymentservice.model.PaymentStatus;
import com.payment.paymentservice.model.Transaction;
import com.payment.paymentservice.model.TransactionStatus;
import com.payment.paymentservice.model.TransactionType;
import com.payment.paymentservice.repository.PaymentOrderRepository;
import com.payment.paymentservice.repository.TransactionRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentOrderService {

    private final RazorpayClient razorpayClient;
    private final PaymentOrderRepository paymentOrderRepository;
    private final TransactionRepository transactionRepository;

    @Value("${razorpay.key-id}")
    private String razorpayKeyId;

    public CreatePaymentOrderResponse createOrder(CreatePaymentOrderRequest req) throws RazorpayException {

        // 1) Create Razorpay Order (amount in paise)
        JSONObject options = new JSONObject();
        options.put("amount", req.getAmount().multiply(BigDecimal.valueOf(100)).longValue());
        options.put("currency", req.getCurrency());
        options.put("receipt", req.getExternalReferenceId());
        options.put("payment_capture", 1);

        log.info("Creating Razorpay order: userId={}, referenceId={}, amount={}, currency={}",
                req.getExternalUserId(),
                req.getExternalReferenceId(),
                req.getAmount(),
                req.getCurrency()
        );

        Order order = razorpayClient.orders.create(options);

        log.info("Razorpay order created: razorpayOrderId={}, amountPaise={}",
                order.get("id"), order.get("amount")
        );

        log.debug("Saving Transaction record. referenceId={}", req.getExternalReferenceId());

        log.info("Payment order created successfully. razorpayOrderId={}, userId={}, referenceId={}",
                order.get("id"),
                req.getExternalUserId(),
                req.getExternalReferenceId()
        );


        // 2) Save PaymentOrder
        PaymentOrder po = PaymentOrder.builder()
                .externalUserId(req.getExternalUserId())
                .externalReferenceId(req.getExternalReferenceId())
                .amount(req.getAmount())
                .currency(req.getCurrency())
                .razorpayOrderId(order.get("id"))
                .status(PaymentStatus.CREATED)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        paymentOrderRepository.save(po);

        // 3) Create Transaction record
        Transaction txn = Transaction.builder()
                .type(TransactionType.PAYMENT_IN)
                .status(TransactionStatus.CREATED)
                .externalUserId(req.getExternalUserId())
                .externalReferenceId(req.getExternalReferenceId())
                .amount(req.getAmount())
                .currency(req.getCurrency())
                .razorpayOrderId(order.get("id"))
                .direction("IN")
                .createdAt(Instant.now())
                .build();

        transactionRepository.save(txn);

        // 4) Response
        return new CreatePaymentOrderResponse(
                po.getId(),
                order.get("id"),
                req.getAmount(),
                req.getCurrency(),
                razorpayKeyId
        );
    }
}

