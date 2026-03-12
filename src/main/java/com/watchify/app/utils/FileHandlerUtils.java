package com.watchify.app.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

public class FileHandlerUtils {

    private FileHandlerUtils() {

    }

    public static String extractFileExtension(String originalFilename) {
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));

        }
        return fileExtension;
    }

    public static Path findFileByUuid(Path directory, String uuid) throws Exception {
        return Files.list(directory)
                .filter(path -> path.getFileName().toString().startsWith(uuid))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("File not found for UUID: " + uuid));
    }

    public static String detectVideoContentType(String fileName) {
        if (fileName == null) {
            return "video/mp4";
        }

        String lower = fileName.toLowerCase();

        if (lower.endsWith(".mp4")) {
            return "video/mp4";
        }
        if (lower.endsWith(".webm")) {
            return "video/webm";
        }
        if (lower.endsWith(".mkv")) {
            return "video/x-matroska";
        }
        if (lower.endsWith(".ogg")) {
            return "video/ogg";
        }
        if (lower.endsWith(".avi")) {
            return "video/x-msvideo";
        }
        if (lower.endsWith(".mov")) {
            return "video/quicktime";
        }
        if (lower.endsWith(".flv")) {
            return "video/x-flv";
        }
        if (lower.endsWith(".wmv")) {
            return "video/x-ms-wmv";
        }
        if (lower.endsWith(".m4v")) {
            return "video/x-m4v";
        }
        if (lower.endsWith(".3gp")) {
            return "video/3gpp";
        }
        if (lower.endsWith(".mpg") || lower.endsWith(".mpeg")) {
            return "video/mpeg";
        }

        return "video/mp3";
    }

    public static String detectImageContentType(String fileName) {
        if (fileName.endsWith(".png")) {
            return "image/png";
        }
        if (fileName.endsWith(".gif")) {
            return "image/gif";
        }
        if (fileName.endsWith(".webp")) {
            return "image/webp";
        }

        return "image/jpeg";

    }

    public static long[] parseRangeHeader(String rangeHeader, long fileLength) {
        String[] ranges = rangeHeader.replace("bytes=", "").split("-");
        long rangeStart = Long.parseLong(ranges[0]);
        long rangeEnd = ranges.length > 1
                && !ranges[1].isEmpty() ? Long.parseLong(ranges[1]) : fileLength - 1;
        return new long[]{rangeStart, rangeEnd};
    }

    public static Resource createRangeResource(Path filePath, long rangeStart, long rangeLength) throws IOException {
        RandomAccessFile fileReader = new RandomAccessFile(filePath.toFile(), "r");
        fileReader.seek(rangeStart);

        InputStream partialContentStream = new InputStream() {
            private long totalBytesRead = 0;

            @Override
            public int read() throws java.io.IOException {
                if (totalBytesRead > rangeLength) {
                    fileReader.close();
                    return -1;
                }
                totalBytesRead++;
                return fileReader.read();
            }

            @Override
            public int read(byte[] buffer, int offset, int length) throws IOException {
                if (totalBytesRead >= rangeLength) {
                    fileReader.close();
                    return -1;
                }
                long remainingBytes = rangeLength - totalBytesRead;
                int bytesToRead = (int) Math.min(length, remainingBytes);
                int bytesActuallyRead = fileReader.read(buffer, offset, bytesToRead);
                if (bytesActuallyRead > 0) {
                    totalBytesRead += bytesActuallyRead;
                }
                if (totalBytesRead >= rangeLength) {
                    fileReader.close();
                }
                return bytesActuallyRead;
            }

            @Override
            public void close() throws IOException {
                fileReader.close();
            }

        };

        return new InputStreamResource(partialContentStream) {
            @Override
            public long contentLength() {
                return rangeLength;
            }
        };

    }

    public static Resource createFullResource(Path filePath) throws IOException {
        Resource resource = new UrlResource(filePath.toUri());
        if (!resource.exists() || !resource.isReadable()) {
            throw new IOException("File not found or reaable" + filePath);

        }
        return resource;
    }
}
