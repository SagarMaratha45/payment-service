package com.payment.paymentservice.service;

import com.payment.paymentservice.dto.CreatePaymentRequest;
import com.payment.paymentservice.dto.CreatePaymentResponse;
import com.payment.paymentservice.model.Payment;
import com.payment.paymentservice.model.PaymentStatus;
import com.payment.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

        private final PaymentRepository paymentRepository;
        private final UserServiceClient userServiceClient;

        @Value("${payment.admin.user-id}")
        private String adminUserId;

        public CreatePaymentResponse createPayment(CreatePaymentRequest req) {

                log.info("Starting payment. userId={}, amount={}", req.getExternalUserId(), req.getAmount());

                // 1) Create payment entry with INITIATED
                Payment payment = Payment.builder()
                                .externalUserId(req.getExternalUserId())
                                .amount(req.getAmount())
                                .status(PaymentStatus.INITIATED)
                                .createdAt(Instant.now())
                                .updatedAt(Instant.now())
                                .build();

                payment = paymentRepository.save(payment);
                log.info("Payment record created with id={} and status={}", payment.getId(), payment.getStatus());

                // 2) (Real world) call Razorpay etc.
                // For demo: simulate success
                log.info("Simulating Razorpay payment success in test/demo mode.");

                // 3) Mark as SUCCESS
                payment.setStatus(PaymentStatus.SUCCESS);
                payment.setUpdatedAt(Instant.now());
                payment = paymentRepository.save(payment);
                log.info("Payment id={} marked as SUCCESS", payment.getId());

                // 4) Call user-service to adjust wallet for the user
                userServiceClient.adjustUserWallet(req.getExternalUserId(), req.getAmount());

                // 5) Also add the same amount to admin, if you want that view
                userServiceClient.adjustUserWallet(adminUserId, req.getAmount());

                // 6) Return response
                return new CreatePaymentResponse(
                                payment.getId(),
                                payment.getExternalUserId(),
                                payment.getAmount(),
                                payment.getStatus().name(),
                                "Payment processed successfully (demo mode with Razorpay test).");
        }
}