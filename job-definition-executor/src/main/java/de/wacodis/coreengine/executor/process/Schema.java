/*
 * Copyright 2018-2021 52°North Spatial Information Research GmbH
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
package de.wacodis.coreengine.executor.process;

/**
 *
 * @author Arne
 */
public class Schema {
    
    //well-known schemas
    public final static Schema GML1 = new Schema("GML1", "http://schemas.opengis.net/gml/1.0.0/gmlfeature.dtd");
    public final static Schema GML2 = new Schema("GML2", "http://schemas.opengis.net/gml/2.1.2/feature.xsd");
    public final static Schema GML3 = new Schema("GML3", "http://schemas.opengis.net/gml/3.1.1/base/feature.xsd");
      
    private String name;
    private String schemaLocation;

    public Schema() {
    }

    public Schema(String name) {
        this.name = name;
    }

    public Schema(String name, String schemalLocation) {
        this.name = name;
        this.schemaLocation = schemalLocation;
    }

    
    
    public String getSchemaLocation() {
        return schemaLocation;
    }

    public void setSchemaLocation(String schemaLocation) {
        this.schemaLocation = schemaLocation;
    } 

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    

    @Override
    public String toString() {
        return "Schema{" + "name=" + name + ", schemalLocation=" + schemaLocation + '}';
    }
}
