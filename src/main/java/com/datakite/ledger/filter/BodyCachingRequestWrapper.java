package com.datakite.ledger.filter;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * A mutable {@link HttpServletRequestWrapper} that eagerly reads the request
 * body into a {@code byte[]} so it can be:
 * <ul>
 *   <li>read multiple times (e.g. by an interceptor AND then by the controller), and</li>
 *   <li>replaced entirely via {@link #setBody(byte[])} before the controller deserializes it.</li>
 * </ul>
 *
 * <p>Semantically equivalent to Spring's {@code ContentCachingRequestWrapper}, but adds
 * the mutation capability required for fraud-prevention body rewriting.
 */
public class BodyCachingRequestWrapper extends HttpServletRequestWrapper {

    private byte[] cachedBody;

    public BodyCachingRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        this.cachedBody = request.getInputStream().readAllBytes();
    }

    /** Returns the currently cached body bytes (possibly modified by an interceptor). */
    public byte[] getBody() {
        return cachedBody;
    }

    /**
     * Replaces the cached body so downstream readers see the new content.
     *
     * @throws IllegalArgumentException if {@code body} is {@code null} — a null array
     *         would cause a {@link NullPointerException} inside {@link #getInputStream()}.
     */
    public void setBody(byte[] body) {
        if (body == null) {
            throw new IllegalArgumentException("body must not be null");
        }
        this.cachedBody = body;
    }

    @Override
    public ServletInputStream getInputStream() {
        ByteArrayInputStream bais = new ByteArrayInputStream(cachedBody);
        return new ServletInputStream() {
            @Override public boolean isFinished()  { return bais.available() == 0; }
            @Override public boolean isReady()     { return true; }
            @Override public void setReadListener(ReadListener listener) {
                throw new UnsupportedOperationException("setReadListener is not supported");
            }
            @Override public int read() { return bais.read(); }
        };
    }

    @Override
    public BufferedReader getReader() {
        String encoding = getCharacterEncoding();
        Charset charset = (encoding != null)
                ? Charset.forName(encoding)
                : StandardCharsets.UTF_8;
        return new BufferedReader(new InputStreamReader(getInputStream(), charset));
    }
}
