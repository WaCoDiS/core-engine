/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.core.engine.utils.http;

import java.net.MalformedURLException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class HTTPRequestTest {

    /**
     * Test of stringToURL method, of class HTTPRequest.
     */
    @Test
    public void testStringToURL() throws Exception {
        String urlStr = "http://www.example.com";

        assertEquals(urlStr, HTTPRequest.stringToURL(urlStr).toString());
    }

    /**
     * Test of stringToURL method, of class HTTPRequest.
     */
    @Test
    @DisplayName("check malformed URL evokes Exception")
    public void testStringToURL_malformedURL() throws Exception {
        String malformedUrlStr = "www.example.com"; //no protocol (http://)

        assertThrows(MalformedURLException.class, () -> HTTPRequest.stringToURL(malformedUrlStr));
    }

}
