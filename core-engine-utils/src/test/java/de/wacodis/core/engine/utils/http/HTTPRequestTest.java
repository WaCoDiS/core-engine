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
