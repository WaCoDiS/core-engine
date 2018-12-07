/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.evaluator.http;

import java.net.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * execute HTTP-POST requests, response can be POJO or Generic, e.g collections
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 * @param <P> payload type
 * @param <R> response type
 */
public class GenericPostRequest<P, R> implements HTTPRequest<R> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GenericPostRequest.class);

    private URL url;
    private P payload;
    private HttpHeaders headers;
    private Class<R> responseType;
    private ParameterizedTypeReference<R> responseTypeReference;

    /**
     * use if response type is a generic, e.g. collections
     * @param url
     * @param responseTypeReference ParameterizedTypeReference<R> object expected
     * @param payload POST body, object of type P
     */
    public GenericPostRequest(URL url, ParameterizedTypeReference<R> responseTypeReference, P payload) {
        this(responseTypeReference);
        this.url = url;
        this.payload = payload;
    }

    /**
     * use if response type is a generic, e.g. collections
     * @param url
     * @param payload POST body, object of type P
     * @param responseTypeReference ParameterizedTypeReference<R> object expected
     * @param headers 
     */
    public GenericPostRequest(URL url, P payload, ParameterizedTypeReference<R> responseTypeReference, HttpHeaders headers) {
        this.url = url;
        this.payload = payload;
        this.headers = headers;
        this.responseTypeReference = responseTypeReference;
    }

    /**
     * use if response type is a generic, e.g. collections
     * @param responseTypeReference ParameterizedTypeReference<R> object expected
     */
    public GenericPostRequest(ParameterizedTypeReference<R> responseTypeReference) {
        this.headers = new HttpHeaders();
        this.responseTypeReference = responseTypeReference;
    }

    /**
     * use if response type is a POJO
     * @param url
     * @param payload POST body, object of type P
     * @param responseType class object for response type R expected
     */
    public GenericPostRequest(URL url, Class<R> responseType, P payload) {
        this(responseType);
        this.url = url;
        this.payload = payload;
    }

    /**
     * use if response type is a POJO
     * @param url
     * @param payload POST body, object of type P
     * @param responseType class object for response type R expected
     * @param headers 
     */
    public GenericPostRequest(URL url, P payload, Class<R> responseType, HttpHeaders headers) {
        this.url = url;
        this.payload = payload;
        this.headers = headers;
        this.responseType = responseType;
    }

    /**
     * use if response type is a POJO
     * @param responseType class object for response type R expected
     */
    public GenericPostRequest(Class<R> responseType) {
        this.headers = new HttpHeaders();
        this.responseType = responseType;
    }

    public URL getUrl() {
        return url;
    }

    @Override
    public void setUrl(URL url) {
        this.url = url;
    }

    public P getPayload() {
        return this.payload;
    }

    /**
     * 
     * @param payload POST body, object of type P
     */
    public void setPayload(P payload) {
        this.payload = payload;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    @Override
    public void setHeaders(HttpHeaders headers) {
        this.headers = headers;
    }

    /**
     * execute HTTP-POST request
     * @return 
     */
    @Override
    public ResponseEntity<R> execute() {
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<P> request = new HttpEntity<>(payload, headers);
        ResponseEntity<R> response = null;

        LOGGER.info("executing HTTP-Post to " + this.url.toString());

        if (this.responseType != null) {
            response = restTemplate.exchange(this.url.toString(), HttpMethod.POST, request, responseType);
        } else if (this.responseTypeReference != null) {
            response = restTemplate.exchange(this.url.toString(), HttpMethod.POST, request, responseTypeReference);
        } else {
            LOGGER.error("responseType and responseTypeReference are both null, cannot convert response of HTTP-POST to " + this.url.toString());
            throw new IllegalArgumentException("Cannot execute HTTP-POST to " + this.url.toString() + " , neither responseType (Class<R>) nor responseTypeReference (ParameterizedTypeReference<R>) provided");
        }

        LOGGER.info("executed HTTP-Post to " + this.url.toString() + " with status code: " + response.getStatusCodeValue());

        return response;
    }
}
