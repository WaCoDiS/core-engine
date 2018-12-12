/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.evaluator.http.dataaccess;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class DataAccessResourceSearchRequestTest {

    public DataAccessResourceSearchRequestTest() {
    }

    @Test
    public void testAcceptableMediaTypes() {
        DataAccessResourceSearchRequest request = new DataAccessResourceSearchRequest();
        assertTrue(request.getHeaders().get("accept").contains("application/json"));
    }

    @Test
    public void testContentType() {
        DataAccessResourceSearchRequest request = new DataAccessResourceSearchRequest();
        assertTrue(request.getHeaders().get("Content-Type").contains("application/json"));
    }

}
