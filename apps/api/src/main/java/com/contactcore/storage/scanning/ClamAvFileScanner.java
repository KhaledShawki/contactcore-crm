// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.storage.scanning;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ClamAvFileScanner implements FileScanner {
    private static final byte[] INSTREAM_COMMAND = "zINSTREAM\0".getBytes(StandardCharsets.US_ASCII);
    private static final int RESPONSE_LIMIT_BYTES = 4096;

    private final FileScanningProperties properties;

    public ClamAvFileScanner(FileScanningProperties properties) {
        this.properties = properties;
    }

    @Override
    public FileScanResult scan(String filename, byte[] content) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(properties.host(), properties.port()), properties.connectTimeoutMs());
            socket.setSoTimeout(properties.readTimeoutMs());

            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            output.write(INSTREAM_COMMAND);
            writeChunks(output, content);
            output.writeInt(0);
            output.flush();

            String response = readResponse(socket.getInputStream());
            if (response.contains("FOUND")) {
                return FileScanResult.infected(response);
            }
            if (response.contains("OK")) {
                return FileScanResult.clean(response);
            }
            throw new FileScanningUnavailableException("Unexpected ClamAV response while scanning " + filename + ": " + response, null);
        } catch (IOException ex) {
            throw new FileScanningUnavailableException("File scanner is unavailable. Upload rejected before storage.", ex);
        }
    }

    private void writeChunks(DataOutputStream output, byte[] content) throws IOException {
        int offset = 0;
        while (offset < content.length) {
            int length = Math.min(properties.chunkSizeBytes(), content.length - offset);
            output.writeInt(length);
            output.write(content, offset, length);
            offset += length;
        }
    }

    private String readResponse(InputStream input) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int next;
        while ((next = input.read()) != -1 && next != 0 && buffer.size() < RESPONSE_LIMIT_BYTES) {
            buffer.write(next);
        }
        return buffer.toString(StandardCharsets.UTF_8).trim();
    }
}
