/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.executor.process;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class ExpectedProcessOutputTest {
    
    @Test
    @DisplayName("if null set mime type to empty string")
    public void testGetMimeType_Null() {
        ExpectedProcessOutput epo = new ExpectedProcessOutput("testID", null);
        
        assertEquals("", epo.getMimeType());
    }

}
