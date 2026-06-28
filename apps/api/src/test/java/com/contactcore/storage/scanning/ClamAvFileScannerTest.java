// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.storage.scanning;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

class ClamAvFileScannerTest {
    private static final byte[] EXPECTED_COMMAND = "zINSTREAM\0".getBytes(StandardCharsets.US_ASCII);

    @Test
    void returnsCleanWhenClamAvRespondsOk() throws Exception {
        byte[] content = "clean-content".getBytes(StandardCharsets.UTF_8);
        try (FakeClamAvServer server = FakeClamAvServer.start("stream: OK\0")) {
            ClamAvFileScanner scanner = new ClamAvFileScanner(properties(server.port(), 4));

            FileScanResult result = scanner.scan("document.txt", content);

            assertThat(result.clean()).isTrue();
            assertThat(result.scannerMessage()).isEqualTo("stream: OK");
            assertThat(server.awaitCommand()).containsExactly(EXPECTED_COMMAND);
            assertThat(server.awaitPayload()).containsExactly(content);
        }
    }

    @Test
    void returnsInfectedWhenClamAvRespondsFound() throws Exception {
        try (FakeClamAvServer server = FakeClamAvServer.start("stream: Eicar-Test-Signature FOUND\0")) {
            ClamAvFileScanner scanner = new ClamAvFileScanner(properties(server.port(), 8));

            FileScanResult result = scanner.scan("payload.txt", "x".getBytes(StandardCharsets.UTF_8));

            assertThat(result.clean()).isFalse();
            assertThat(result.status()).isEqualTo(FileScanStatus.INFECTED);
            assertThat(result.scannerMessage()).contains("FOUND");
        }
    }

    @Test
    void throwsUnavailableWhenClamAvReturnsUnexpectedResponse() throws Exception {
        try (FakeClamAvServer server = FakeClamAvServer.start("stream: UNKNOWN\0")) {
            ClamAvFileScanner scanner = new ClamAvFileScanner(properties(server.port(), 8));

            assertThatThrownBy(() -> scanner.scan("file.txt", "x".getBytes(StandardCharsets.UTF_8)))
                    .isInstanceOf(FileScanningUnavailableException.class)
                    .hasMessageContaining("Unexpected ClamAV response");
        }
    }

    @Test
    void throwsUnavailableWhenScannerCannotBeReached() throws IOException {
        int closedPort;
        try (ServerSocket server = new ServerSocket(0)) {
            closedPort = server.getLocalPort();
        }
        ClamAvFileScanner scanner = new ClamAvFileScanner(properties(closedPort, 8));

        assertThatThrownBy(() -> scanner.scan("file.txt", "x".getBytes(StandardCharsets.UTF_8)))
                .isInstanceOf(FileScanningUnavailableException.class)
                .hasMessageContaining("File scanner is unavailable");
    }

    private static FileScanningProperties properties(int port, int chunkSizeBytes) {
        return new FileScanningProperties(true, "127.0.0.1", port, 1_000, 1_000, chunkSizeBytes);
    }

    private static final class FakeClamAvServer implements AutoCloseable {
        private final ServerSocket serverSocket;
        private final ExecutorService executor;
        private final Future<Exchange> exchange;

        private FakeClamAvServer(ServerSocket serverSocket, ExecutorService executor, Future<Exchange> exchange) {
            this.serverSocket = serverSocket;
            this.executor = executor;
            this.exchange = exchange;
        }

        static FakeClamAvServer start(String response) throws IOException {
            ServerSocket serverSocket = new ServerSocket(0);
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<Exchange> exchange = executor.submit(() -> serveOnce(serverSocket, response));
            return new FakeClamAvServer(serverSocket, executor, exchange);
        }

        int port() {
            return serverSocket.getLocalPort();
        }

        byte[] awaitCommand() throws Exception {
            return exchange.get(2, TimeUnit.SECONDS).command();
        }

        byte[] awaitPayload() throws Exception {
            return exchange.get(2, TimeUnit.SECONDS).payload();
        }

        @Override
        public void close() throws Exception {
            serverSocket.close();
            executor.shutdownNow();
            executor.awaitTermination(2, TimeUnit.SECONDS);
        }

        private static Exchange serveOnce(ServerSocket serverSocket, String response) throws IOException {
            try (Socket socket = serverSocket.accept()) {
                byte[] command = socket.getInputStream().readNBytes(EXPECTED_COMMAND.length);
                ByteArrayOutputStream payload = new ByteArrayOutputStream();
                DataInputStream input = new DataInputStream(socket.getInputStream());
                while (true) {
                    int chunkSize = input.readInt();
                    if (chunkSize == 0) {
                        break;
                    }
                    payload.write(input.readNBytes(chunkSize));
                }
                socket.getOutputStream().write(response.getBytes(StandardCharsets.US_ASCII));
                socket.getOutputStream().flush();
                return new Exchange(Arrays.copyOf(command, command.length), payload.toByteArray());
            }
        }
    }

    private record Exchange(byte[] command, byte[] payload) {}
}
