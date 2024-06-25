package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import org.json.JSONObject;

@SpringBootApplication
public class MidtransPayment {
    private static final String MIDTRANS_SERVER_KEY = "SB-Mid-server-Ku_AZUvHKuoPPO8881DxCoNW";
    private static final String MIDTRANS_API_BASE_URL = "https://api.sandbox.midtrans.com/v2";

    public static void main(String[] args) {
        try {

            SpringApplication.run(MidtransPayment.class, args);
            Scanner scanner = new Scanner(System.in);
            long orderId = System.currentTimeMillis();

            int choice;
            String paymentType;
            do {
                System.out.println("Pilih metode pembayaran:");
                System.out.println("1. QRIS");
                System.out.println("2. Bank Transfer");
                System.out.print("Masukkan pilihan Anda: ");

                choice = scanner.nextInt();
                paymentType = choosePaymentMethod(choice);

                if (paymentType == null) {
                    System.out.println("Metode pembayaran tidak valid. Silakan pilih ulang.");
                }

                
            } while (paymentType == null);


            System.out.println("Masukkan nominal transaksi:");
            int amount = scanner.nextInt();

            if (amount > 0) {
                JSONObject transactionParams = new JSONObject()
                        .put("payment_type", paymentType)
                        .put("transaction_details", new JSONObject()
                                .put("order_id", orderId)
                                .put("gross_amount", amount));

                String response = sendPostRequest("/charge", transactionParams.toString());
                System.out.println("Response from Midtrans: " + response);
            } else {
                System.out.println("Nominal transaksi harus lebih dari 0.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String choosePaymentMethod(int choice) {
        switch (choice) {
            case 1:
                return "qris";
            case 2:
                return "bank_transfer";
            default:
                return null;
        }
    }

    private static String sendPostRequest(String endpoint, String payload) throws IOException {
        URL url = new URL(MIDTRANS_API_BASE_URL + endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Setup koneksi HTTP POST
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "Basic " + encodeBase64(MIDTRANS_SERVER_KEY + ":"));

        connection.setDoOutput(true);
        try (DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())) {
            outputStream.write(payload.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        }

        // Baca respons dari server
        StringBuilder response = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
        }

        return response.toString();
    }

    private static String encodeBase64(String value) {
        return java.util.Base64.getEncoder().encodeToString(value.getBytes());
    }

}