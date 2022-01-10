/*
 * Copyright 2018-2022 52°North Spatial Information Research GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.wacodis.coreengine.evaluator.http.dataaccess;

import de.wacodis.core.models.AbstractResource;
import de.wacodis.core.models.DataAccessResourceSearchBody;
import de.wacodis.core.engine.utils.http.GenericPostRequest;
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
