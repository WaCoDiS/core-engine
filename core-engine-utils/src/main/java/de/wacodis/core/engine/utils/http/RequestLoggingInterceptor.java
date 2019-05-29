/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.core.engine.utils.http;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

/**
 * Interceptor for logging HTTP requests
 *
 * @author <a href="mailto:s.drost@52north.org">Sebastian Drost</a>
 */
public class RequestLoggingInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestLoggingInterceptor.class);

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution exec) throws IOException {
        logRequest(request, body);
        ClientHttpResponse response = exec.execute(request, body);
        return response;
    }

    private void logRequest(HttpRequest request, byte[] body) throws IOException {
        LOGGER.info("executing {} to {}", request.getMethod(), request.getURI());
        LOGGER.trace("Headers     : {}", request.getHeaders());
        LOGGER.trace("Request body: {}", new String(body, "UTF-8"));
    }
}
