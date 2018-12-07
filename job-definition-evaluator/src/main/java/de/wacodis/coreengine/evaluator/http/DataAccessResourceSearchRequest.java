/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.evaluator.http;

import de.wacodis.core.models.AbstractResource;
import de.wacodis.core.models.DataAccessResourceSearchBody;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class DataAccessResourceSearchRequest extends GenericPostRequest<DataAccessResourceSearchBody, Map<String, List<AbstractResource>>> {

    public DataAccessResourceSearchRequest() {
        //return type
        super(new ParameterizedTypeReference<Map<String, List<AbstractResource>>>() {
        });
        //headers
        HttpHeaders headers = new HttpHeaders();
        List<MediaType> acceptableMediaTypes = new ArrayList<>(); //accept
        acceptableMediaTypes.add(MediaType.APPLICATION_JSON);       
        headers.setAccept(acceptableMediaTypes); //content
        headers.setContentType(MediaType.APPLICATION_JSON);
        super.setHeaders(headers);
    }

}
