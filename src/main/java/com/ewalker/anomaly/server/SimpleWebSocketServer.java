package com.ewalker.anomaly.server;

import com.ewalker.anomaly.detector.AnomalyDetector;
import com.ewalker.anomaly.detector.DetectionResult;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Scanner;

public final class SimpleWebSocketServer {
    private final int port;
    private final AnomalyDetector detector;
    private boolean running;

    public SimpleWebSocketServer(int port) {
        this.port = port;
        this.detector = new AnomalyDetector();
        this.running = false;
    }

    public void start() throws Exception {
        running = true;
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("WebSocket server listening on port " + port);

        new Thread(() -> {
            try {
                while (running) {
                    Socket socket = serverSocket.accept();
                    new Thread(() -> handleClient(socket)).start();
                }
            } catch (Exception e) {
                if (running) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void stop() {
        running = false;
    }

    private void handleClient(Socket socket) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedOutputStream writer = new BufferedOutputStream(socket.getOutputStream());

            String line = reader.readLine();
            if (line == null || !line.startsWith("GET")) {
                socket.close();
                return;
            }

            String key = null;
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                if (line.startsWith("Sec-WebSocket-Key:")) {
                    key = line.substring(19).trim();
                }
            }

            if (key == null) {
                socket.close();
                return;
            }

            String acceptKey = generateAcceptKey(key);
            String response = "HTTP/1.1 101 Switching Protocols\r\n" +
                    "Upgrade: websocket\r\n" +
                    "Connection: Upgrade\r\n" +
                    "Sec-WebSocket-Accept: " + acceptKey + "\r\n" +
                    "\r\n";

            writer.write(response.getBytes(StandardCharsets.UTF_8));
            writer.flush();

            handleWebSocketConnection(socket, reader, writer);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleWebSocketConnection(Socket socket, BufferedReader reader, BufferedOutputStream writer) throws Exception {
        InputStream rawInput = socket.getInputStream();
        byte[] buffer = new byte[1024];

        while (running) {
            int bytesRead = rawInput.read(buffer, 0, 2);
            if (bytesRead < 2) {
                break;
            }

            byte opcode = (byte) (buffer[0] & 0x0F);
            byte masked = buffer[1];
            boolean isMasked = (masked & 0x80) != 0;
            int payloadLength = masked & 0x7F;

            if (payloadLength == 126) {
                rawInput.read(buffer, 0, 2);
                payloadLength = ((buffer[0] & 0xFF) << 8) | (buffer[1] & 0xFF);
            } else if (payloadLength == 127) {
                rawInput.read(buffer, 0, 8);
                payloadLength = (int) (((long) buffer[4] << 24) | ((long) buffer[5] << 16) | ((long) buffer[6] << 8) | (buffer[7] & 0xFF));
            }

            byte[] maskKey = null;
            if (isMasked) {
                maskKey = new byte[4];
                rawInput.read(maskKey, 0, 4);
            }

            byte[] payload = new byte[payloadLength];
            rawInput.read(payload, 0, payloadLength);

            if (isMasked) {
                for (int i = 0; i < payloadLength; i++) {
                    payload[i] = (byte) (payload[i] ^ maskKey[i % 4]);
                }
            }

            if (opcode == 1) {
                String message = new String(payload, StandardCharsets.UTF_8);
                DetectionResult result = detector.analyze(message);
                String responseText = "Anomaly: " + result.anomaly() + " | Reason: " + result.reason() + " | Entropy: " + String.format("%.2f", result.entropy());
                sendWebSocketMessage(writer, responseText);
            } else if (opcode == 8) {
                break;
            }
        }
    }

    private void sendWebSocketMessage(BufferedOutputStream writer, String message) throws Exception {
        byte[] payload = message.getBytes(StandardCharsets.UTF_8);
        byte[] frame = new byte[payload.length + 2];
        frame[0] = (byte) 0x81;
        frame[1] = (byte) payload.length;
        System.arraycopy(payload, 0, frame, 2, payload.length);

        writer.write(frame);
        writer.flush();
    }

    private String generateAcceptKey(String key) throws Exception {
        String concatenated = key + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] hash = md.digest(concatenated.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hash);
    }
}
