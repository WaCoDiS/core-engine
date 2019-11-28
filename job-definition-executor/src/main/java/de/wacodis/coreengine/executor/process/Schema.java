/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
    private String schemalLocation;

    public Schema() {
    }

    public Schema(String name) {
        this.name = name;
    }

    public Schema(String name, String schemalLocation) {
        this.name = name;
        this.schemalLocation = schemalLocation;
    }

    
    
    public String getSchemalLocation() {
        return schemalLocation;
    }

    public void setSchemalLocation(String schemalLocation) {
        this.schemalLocation = schemalLocation;
    } 

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    

    @Override
    public String toString() {
        return "Schema{" + "name=" + name + ", schemalLocation=" + schemalLocation + '}';
    }
}
