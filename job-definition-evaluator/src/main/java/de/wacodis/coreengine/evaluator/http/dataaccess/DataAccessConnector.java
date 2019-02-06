/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.evaluator.http.dataaccess;

import de.wacodis.core.models.AbstractResource;
import de.wacodis.core.models.DataAccessResourceSearchBody;
import de.wacodis.core.engine.utils.http.GenericPostRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.net.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

/**
 * search data-access-api for available resources
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class DataAccessConnector implements DataAccessResourceProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataAccessConnector.class);
    
    private final GenericPostRequest<DataAccessResourceSearchBody, Map<String, List<AbstractResource>>> dataAccessRequest;

    public DataAccessConnector() {
        this.dataAccessRequest = new DataAccessResourceSearchRequest();
    }

    public DataAccessConnector(GenericPostRequest<DataAccessResourceSearchBody, Map<String, List<AbstractResource>>> dataAccessRequest) {
        this.dataAccessRequest = dataAccessRequest;
    }

    public DataAccessConnector(URL url) {
        this();
        this.dataAccessRequest.setUrl(url);
    }

    public DataAccessConnector(URL url, GenericPostRequest<DataAccessResourceSearchBody, Map<String, List<AbstractResource>>> dataAccessRequest) {
        this(dataAccessRequest);
        this.dataAccessRequest.setUrl(url);
    }

    public URL getUrl() {
        return this.dataAccessRequest.getUrl();
    }

    public void setUrl(URL url) {
        this.dataAccessRequest.setUrl(url);
    }

    /**
     * execute HTTP-Post to data-access-api, instance variable Url must be set
     * @param searchBody
     * @return response body
     * @throws java.io.IOException if request is not successful or has no response body
     */
    @Override
    public Map<String, List<AbstractResource>> searchResources(DataAccessResourceSearchBody searchBody) throws IOException {
        this.dataAccessRequest.setPayload(searchBody);
        ResponseEntity<Map<String, List<AbstractResource>>> response = this.dataAccessRequest.execute();
        
        if(response.getStatusCode().is2xxSuccessful() && response.hasBody()){
            LOGGER.info("Retrieving data from data-acces-api succeeded, Url: " + this.dataAccessRequest.getUrl().toString() +
                         ", StatusCode: " + response.getStatusCodeValue() + ", RequestBody: " + searchBody.toString() 
                         + ", ResponseBody: " + response.getBody().toString());
            
            return response.getBody();
        }else{
            LOGGER.error("Retrieving data from data-acces-api failed, Url: " + this.dataAccessRequest.getUrl().toString() +
                         ", StatusCode: " + response.getStatusCodeValue() + ", RequestBody: " + searchBody.toString() 
                         + ", hasResponseBody: " + response.hasBody());
            throw new java.io.IOException("Could not retrieve resources from " + this.dataAccessRequest.getUrl().toString()+ ", StatusCode: " + response.getStatusCodeValue() + ", hasBody: " + response.hasBody());
        }
    }

}
