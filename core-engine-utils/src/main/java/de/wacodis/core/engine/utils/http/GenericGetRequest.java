/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.core.engine.utils.http;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

/**
 * execute HTTP-GET requests, response can be POJO or Generic, e.g collections
 *
 * @author <a href="mailto:s.drost@52north.org">Sebastian Drost</a>
 * @param <R> response type
 */
public class GenericGetRequest<R> implements HTTPRequest<R> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GenericGetRequest.class);

    private URL url;
    private HttpHeaders headers;
    private Class<R> responseType;
    private ParameterizedTypeReference<R> responseTypeReference;
    private List<ClientHttpRequestInterceptor> interceptors;

    /**
     * use if response type is a generic, e.g. collections
     *
     * @param url
     * @param responseTypeReference ParameterizedTypeReference<R> object
     * expected
     */
    public GenericGetRequest(URL url, ParameterizedTypeReference<R> responseTypeReference) {
        this(responseTypeReference);
        this.url = url;
    }

    /**
     * use if response type is a generic, e.g. collections
     *
     * @param url
     * @param responseTypeReference ParameterizedTypeReference<R> object
     * expected
     * @param headers
     */
    public GenericGetRequest(URL url, ParameterizedTypeReference<R> responseTypeReference, HttpHeaders headers) {
        this.url = url;
        this.headers = headers;
        this.responseTypeReference = responseTypeReference;
        this.interceptors = Arrays.asList(new RequestLoggingInterceptor());
    }

    /**
     * use if response type is a generic, e.g. collections
     *
     * @param responseTypeReference ParameterizedTypeReference<R> object
     * expected
     */
    public GenericGetRequest(ParameterizedTypeReference<R> responseTypeReference) {
        this.headers = new HttpHeaders();
        this.responseTypeReference = responseTypeReference;
        this.interceptors = Arrays.asList(new RequestLoggingInterceptor());
    }

    /**
     * use if response type is a POJO
     *
     * @param url
     * @param responseType class object for response type R expected
     */
    public GenericGetRequest(URL url, Class<R> responseType) {
        this(responseType);
        this.url = url;
    }

    /**
     * use if response type is a POJO
     *
     * @param url
     * @param responseType class object for response type R expected
     * @param headers
     */
    public GenericGetRequest(URL url, Class<R> responseType, HttpHeaders headers) {
        this.url = url;
        this.headers = headers;
        this.responseType = responseType;
        this.interceptors = Arrays.asList(new RequestLoggingInterceptor());
    }

    /**
     * use if response type is a POJO
     *
     * @param responseType class object for response type R expected
     */
    public GenericGetRequest(Class<R> responseType) {
        this.headers = new HttpHeaders();
        this.responseType = responseType;
        this.interceptors = Arrays.asList(new RequestLoggingInterceptor());
    }

    @Override
    public ResponseEntity<R> execute() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setInterceptors(interceptors);
        HttpEntity<?> request = new HttpEntity(headers);
        ResponseEntity<R> response = null;

        if (this.responseType != null) {
            response = restTemplate.exchange(this.url.toString(), HttpMethod.GET, request, responseType);
        } else if (this.responseTypeReference != null) {
            response = restTemplate.exchange(this.url.toString(), HttpMethod.POST, request, responseTypeReference);
        } else {
            LOGGER.error("responseType and responseTypeReference are both null, cannot convert response of HTTP-GET to " + this.url.toString());
            throw new IllegalArgumentException("Cannot execute HTTP-GET to " + this.url.toString() + " , neither responseType (Class<R>) nor responseTypeReference (ParameterizedTypeReference<R>) provided");
        }

        LOGGER.info("executed HTTP-GET to " + this.url.toString() + " with status code: " + response.getStatusCodeValue());

        return response;
    }

    @Override
    public void setUrl(URL url) {
        this.url = url;
    }

    @Override
    public void setHeaders(HttpHeaders headers) {
        this.headers = headers;
    }

    public URL getUrl() {
        return url;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

}
