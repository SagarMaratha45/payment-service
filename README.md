# Payment Service (Razorpay + Spring Boot + MongoDB)

A standalone **payment microservice** built with Spring Boot that exposes APIs for:

- Creating **incoming payments** via Razorpay (investor/funder paying into your platform)
- Recording **payouts** (admin sending money out; currently stubbed, no real Razorpay payout call)
- Storing all **transaction data** in MongoDB for audit and reporting
- Providing a clean **API layer** that other microservices can call (user service, investment service, etc.)

---

## High-Level Architecture

- **PaymentService**
  - `POST /api/payments/orders`  
    → Creates a Razorpay **Order** and a local `PaymentOrder` + `Transaction` record.
  - `POST /api/payments/webhook`  
    → Receives Razorpay webhooks (`payment.captured` / `payment.failed`) and updates local records.

- **PayoutService**
  - `POST /api/payouts`  
    → Creates an outgoing `Transaction` for payouts (currently stubbed; generates fake Razorpay payout id).

- **TransactionService**
  - `GET /api/transactions`  
  - `GET /api/transactions/{id}`  
    → Exposes transaction history for other services.

**User accounts, KYC, wallet balance checks, business rules, etc. are expected to be handled by other microservices.**  
They just call this service with:
- `externalUserId`
- `externalReferenceId` (e.g. INVESTMENT_ID, ORDER_ID)
- amount & currency.

---

## Configuration

Configuration is done via Spring properties (`application.properties` or environment variables).

### Mandatory properties

```properties
# Application
spring.application.name=paymentservice

# MongoDB
spring.data.mongodb.uri=mongodb://localhost:27017/payment_service

# Razorpay
razorpay.key-id=your_test_key_id_here
razorpay.key-secret=your_test_key_secret_here

# Optional – when you configure actual webhook in Razorpay dashboard
razorpay.webhook-secret=your_webhook_secret_here

Running Locally
# from project root
mvn clean install
mvn spring-boot:run


By default, the app will start on:

http://localhost:8080


(If you changed the port in application.properties, adjust accordingly.)

Integration Guide (for Other Services)
Concepts

externalUserId
The ID of the user in your system (from User Service).

externalReferenceId
Any business ID in your system, for example:

INVESTMENT_ID

ORDER_ID

TRANSACTION_ID in another service

We use these to correlate payment/payout records with your existing data.

API Reference
1. Create Payment Order (Incoming Payment)

Other microservices call this to initiate a Razorpay payment.

URL: POST /api/payments/orders

Content-Type: application/json

Request Body
{
  "externalUserId": "USER-123",
  "externalReferenceId": "INVESTMENT-987",
  "amount": 5000,
  "currency": "INR"
}


amount is in major units (e.g. ₹5000, not paise).
The service converts to paise for Razorpay.

externalReferenceId should be something you can use later to look up this payment in your own system.

Response
{
  "paymentOrderId": "6771e33f0e8d13532a9b8b52",
  "razorpayOrderId": "order_PcY8zVg7E0u123",
  "amount": 5000,
  "currency": "INR",
  "razorpayKeyId": "rzp_test_abc123xyz"
}


razorpayOrderId → used by frontend to open Razorpay Checkout

razorpayKeyId → public key for Checkout

paymentOrderId → local MongoDB ID; mostly internal to this service

What you need to do:

Call this API from your backend.

Pass razorpayOrderId + razorpayKeyId to your frontend.

Frontend integrates with Razorpay Checkout (see snippet below).

2. Razorpay Webhook Endpoint

Razorpay will call this when a payment succeeds or fails.

URL: POST /api/payments/webhook

Content-Type: application/json

Headers:

X-Razorpay-Signature: <signature> (optional in dev, required in prod)

We expect at least these fields:

event = "payment.captured" or "payment.failed"

payload.payment.entity.id = Razorpay payment id (pay_xxx)

payload.payment.entity.order_id = Razorpay order id (order_xxx)

Example payload for success
{
  "event": "payment.captured",
  "payload": {
    "payment": {
      "entity": {
        "id": "pay_PcY9ABCDEF1234",
        "order_id": "order_PcY8zVg7E0u123"
      }
    }
  }
}

Response

Status: 200 OK

Body: empty

What the service does

Looks up PaymentOrder by razorpayOrderId.

Updates:

PaymentOrder.status → SUCCESS

PaymentOrder.razorpayPaymentId → pay_xxx

Updates its associated Transaction → SUCCESS.

Your responsibility:
You can listen to status changes by:

Polling /api/transactions?externalReferenceId=...

Or (optionally) build your own listener that consumes DB changes / internal events (not included in this service).

3. Get Transaction by ID

URL: GET /api/transactions/{id}

Example
GET /api/transactions/6771e3ab0e8d13532a9b8b55

Example Response
{
  "id": "6771e3ab0e8d13532a9b8b55",
  "type": "PAYMENT_IN",
  "status": "SUCCESS",
  "externalUserId": "USER-123",
  "externalReferenceId": "INVESTMENT-987",
  "amount": 5000,
  "currency": "INR",
  "razorpayOrderId": "order_PcY8zVg7E0u123",
  "razorpayPaymentId": "pay_PcY9ABCDEF1234",
  "razorpayPayoutId": null,
  "direction": "IN",
  "createdAt": "2025-11-19T18:45:12.345Z"
}

4. Search Transactions
4.1 By externalReferenceId

Get all transactions for a specific business object (e.g. investment).

URL:
GET /api/transactions?externalReferenceId=INVESTMENT-987

4.2 By externalUserId

Get all transactions for a specific user.

URL:
GET /api/transactions?externalUserId=USER-123

Example Response
[
  {
    "id": "6771e3ab0e8d13532a9b8b55",
    "type": "PAYMENT_IN",
    "status": "SUCCESS",
    "externalUserId": "USER-123",
    "externalReferenceId": "INVESTMENT-987",
    "amount": 5000,
    "currency": "INR",
    "razorpayOrderId": "order_PcY8zVg7E0u123",
    "razorpayPaymentId": "pay_PcY9ABCDEF1234",
    "razorpayPayoutId": null,
    "direction": "IN",
    "createdAt": "2025-11-19T18:45:12.345Z"
  }
]


If no filters are provided (GET /api/transactions), the service returns an empty list.

5. Create Payout (Outgoing Payment) – Stubbed

This endpoint is meant for admin payouts (e.g. sending money to investor/funder).
Currently, it only:

Creates a Transaction of type PAYOUT_OUT

Marks it SUCCESS

Generates a fake razorpayPayoutId (pout_<UUID>)

Note: This does not call Razorpay Payouts API yet.
Integrators should treat this as a hook and plug in real payout logic later if needed.

URL: POST /api/payouts

Content-Type: application/json

Request Body
{
  "externalUserId": "USER-123",
  "externalReferenceId": "WITHDRAWAL-555",
  "amount": 2000,
  "currency": "INR",
  "mode": "UPI",
  "beneficiary": {
    "name": "Test User",
    "upiIdOrAccount": "testuser@upi"
  }
}


mode – for future use (UPI, BANK_TRANSFER, etc.)

beneficiary – details needed by your payout provider

Response
{
  "transactionId": "6771e4cd0e8d13532a9b8b60",
  "razorpayPayoutId": "pout_4d363c25-4c77-4fbb-98f4-02e5b69c10a9",
  "status": "SUCCESS"
}


You can then fetch the full transaction:

GET /api/transactions/6771e4cd0e8d13532a9b8b60

