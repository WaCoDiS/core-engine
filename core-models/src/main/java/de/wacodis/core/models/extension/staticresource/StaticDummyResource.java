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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.core.models.extension.staticresource;

import de.wacodis.core.models.AbstractResource;
import de.wacodis.core.models.StaticSubsetDefinition;

/**
 * used to handle static inputs of wacodis job in the same way as other inputs internally
 * inherited attributes (url, method) can be null in case not needed
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class StaticDummyResource extends AbstractResource {
    
    private String value;
    private StaticSubsetDefinition.DataTypeEnum dataType;

    /**
     * get string encoded static value of the resource
     * @return 
     */
    public String getValue() {
        return value;
    }

    /**
     * set string encoded static value of the resource
     * @param value 
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * determines how the value attribute should be interpreted
     * @return 
     */
    public StaticSubsetDefinition.DataTypeEnum getDataType() {
        return dataType;
    }

   /**
    * determines how the value attribute should be interpreted
    * @param dataType 
    */
    public void setDataType(StaticSubsetDefinition.DataTypeEnum dataType) {
        this.dataType = dataType;
    }
   
}
