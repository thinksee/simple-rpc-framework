package com.thinksee.compress;

import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

@Slf4j
public class GzipCompress implements Compress{

    private static final int BUFFER_SIZE = 1024 * 4;

    @Override
    public byte[] compress(byte[] bytes) {
        if(bytes == null) {
            throw new NullPointerException("bytes is null");
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (GZIPOutputStream gzip = new GZIPOutputStream(byteArrayOutputStream)){
            gzip.write(bytes);
            gzip.flush();
            gzip.finish();
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException("gzip compress error", e);
        }
    }

    @Override
    public byte[] decompress(byte[] bytes) {
        if(bytes == null) {
            throw new NullPointerException("bytes is null");
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try(GZIPInputStream gunzip = new GZIPInputStream(new ByteArrayInputStream(bytes))) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int n;
            while ((n = gunzip.read(buffer)) > -1) {
                byteArrayOutputStream.write(buffer, 0, n);
            }
            return byteArrayOutputStream.toByteArray();
        }catch (IOException e) {
            throw new RuntimeException("gzip decompress error", e);
        }
    }
}
