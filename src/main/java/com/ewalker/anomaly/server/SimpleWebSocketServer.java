package com.ewalker.anomaly.server;

import com.ewalker.anomaly.detector.AnomalyDetector;
import com.ewalker.anomaly.detector.DetectionResult;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

public final class SimpleWebSocketServer {
    private final int port;
    private final AnomalyDetector detector;
    private final SimpleLogger logger;
    private boolean running;

    public SimpleWebSocketServer(int port) {
        this.port = port;
        this.detector = new AnomalyDetector();
        this.logger = new SimpleLogger("logs/anomaly-detector.log");
        this.running = false;
    }

    public void start() throws Exception {
        running = true;
        ServerSocket serverSocket = new ServerSocket(port);
        logger.log("WebSocket server listening on port " + port);

        new Thread(() -> {
            try {
                while (running) {
                    Socket socket = serverSocket.accept();
                    logger.log("Client connected from " + socket.getInetAddress().getHostAddress());
                    new Thread(() -> handleClient(socket)).start();
                }
            } catch (Exception e) {
                if (running) {
                    logger.error("Server error", e);
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
            OutputStream output = socket.getOutputStream();

            // Read HTTP upgrade request
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

            // Send WebSocket upgrade response
            String acceptKey = generateAcceptKey(key);
            String response = "HTTP/1.1 101 Switching Protocols\r\n" +
                    "Upgrade: websocket\r\n" +
                    "Connection: Upgrade\r\n" +
                    "Sec-WebSocket-Accept: " + acceptKey + "\r\n" +
                    "\r\n";
            output.write(response.getBytes(StandardCharsets.UTF_8));
            output.flush();

            logger.log("WebSocket handshake completed");
            handleWebSocketMessages(socket, reader, output);
        } catch (Exception e) {
            logger.error("Client handler error", e);
        } finally {
            try {
                socket.close();
                logger.log("Client disconnected");
            } catch (IOException e) {
                logger.error("Error closing socket", e);
            }
        }
    }

    private void handleWebSocketMessages(Socket socket, BufferedReader reader, OutputStream output) throws Exception {
        InputStream rawInput = socket.getInputStream();
        byte[] buffer = new byte[4096];

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

            if (isMasked && maskKey != null) {
                for (int i = 0; i < payloadLength; i++) {
                    payload[i] = (byte) (payload[i] ^ maskKey[i % 4]);
                }
            }

            if (opcode == 1) {
                // Text message
                String inputText = new String(payload, StandardCharsets.UTF_8);
                logger.log("Received: " + inputText);
                
                DetectionResult result = detector.analyze(inputText);
                String outputText = result.anomaly() + " | " + result.reason() + " | Entropy: " + String.format("%.2f", result.entropy());
                logger.log("Result: " + outputText);
                
                sendText(output, outputText);
            } else if (opcode == 8) {
                // Close frame
                logger.log("Close frame received");
                break;
            }
        }
    }

    private void sendText(OutputStream output, String text) throws Exception {
        byte[] payload = text.getBytes(StandardCharsets.UTF_8);
        byte[] frame = new byte[payload.length + 2];
        frame[0] = (byte) 0x81; // Text frame, final
        frame[1] = (byte) payload.length;
        System.arraycopy(payload, 0, frame, 2, payload.length);

        output.write(frame);
        output.flush();
    }

    private String generateAcceptKey(String key) throws Exception {
        String concatenated = key + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] hash = md.digest(concatenated.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hash);
    }
}
