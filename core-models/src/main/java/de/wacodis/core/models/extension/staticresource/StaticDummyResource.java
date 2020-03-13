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

    @Override
    public String toString() {
        return "StaticDummyResource{" +
                "value='" + value + '\'' +
                ", dataType=" + dataType +
                '}';
    }
}
