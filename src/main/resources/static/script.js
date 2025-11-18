document.getElementById("payButton").addEventListener("click", async () => {
  const amountInput = document.getElementById("amount").value;
  const userId = document.getElementById("userId").value || "demo-user-1";

  if (!amountInput || amountInput <= 0) {
    alert("Please enter a valid amount");
    return;
  }

  const amountPaise = parseInt(amountInput);

  try {
    const createResp = await fetch("/api/v1/payments/createPayment", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        amount: amountPaise,
        currency: "INR",
        userId: userId,
        receipt: "rcpt_" + Date.now()
      })
    });

    const paymentData = await createResp.json();

    console.log("Payment created:", paymentData);

    if (!paymentData.razorpayOrderId) {
      throw new Error("Failed to create order");
    }

    const options = {
      key: "rzp_test_RZDFjFGdBzZRbz", // ✅ your test key
      amount: paymentData.amount,
      currency: paymentData.currency,
      name: "Payment Demo",
      description: "Test Transaction",
      order_id: paymentData.razorpayOrderId,
      handler: async function (response) {
        console.log("Payment success response:", response);

        const confirmResp = await fetch(`/api/v1/payments/${response.razorpay_order_id}/confirm`, {
          method: "POST",
          headers: { "Content-Type": "application/x-www-form-urlencoded" },
          body: new URLSearchParams({
            paymentId: response.razorpay_payment_id,
            signature: response.razorpay_signature
          })
        });

        const confirmData = await confirmResp.json();
        console.log("Confirm API response:", confirmData);

        document.getElementById("response").innerHTML = `
          <div class="success">
            ✅ Payment Successful! <br>
            Order ID: ${response.razorpay_order_id} <br>
            Payment ID: ${response.razorpay_payment_id}
          </div>`;
      },
      prefill: {
        name: "Demo User",
        email: "demo@example.com",
        contact: "9999999999"
      },
      theme: { color: "#528FF0" }
    };

    const rzp = new Razorpay(options);
    rzp.open();
  } catch (err) {
    console.error("Error:", err);
    document.getElementById("response").innerHTML = `<div class="error">❌ ${err.message}</div>`;
  }
});
