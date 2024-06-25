package com.example;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.json.JSONObject;

@RestController
public class WebhookController {

    @PostMapping("/midtrans/webhook")
    public void handleMidtransWebhook(@RequestBody String payload) {
        try {
            JSONObject json = new JSONObject(payload);
            String orderId = json.getString("order_id");
            String transactionStatus = json.getString("transaction_status");
            String fraudStatus = json.getString("fraud_status");

            // Proses notifikasi dari Midtrans sesuai dengan status transaksi
            if ("capture".equals(transactionStatus) && "accept".equals(fraudStatus)) {
                // Transaksi sukses
                System.out.println("Transaction " + orderId + " has been paid.");
            } else if ("settlement".equals(transactionStatus)) {
                // Transaksi selesai
                System.out.println("Transaction " + orderId + " is settled.");
            } else if ("deny".equals(transactionStatus) || "cancel".equals(transactionStatus) || "expire".equals(transactionStatus)) {
                // Transaksi gagal
                System.out.println("Transaction " + orderId + " is failed.");
            } else if ("pending".equals(transactionStatus)) {
                // Transaksi pending
                System.out.println("Transaction " + orderId + " is pending.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
