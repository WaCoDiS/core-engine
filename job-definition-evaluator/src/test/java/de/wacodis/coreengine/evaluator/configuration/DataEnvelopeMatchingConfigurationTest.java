/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.evaluator.configuration;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class DataEnvelopeMatchingConfigurationTest {

    public DataEnvelopeMatchingConfigurationTest() {
    }

    @Test
    @DisplayName("throws exception if value exceeds 100.0")
    public void testSetMinimumOverlapPercentage_Exceed() {
        DataEnvelopeMatchingConfiguration config = new DataEnvelopeMatchingConfiguration();
        assertThrows(IllegalArgumentException.class, () -> config.setMinimumOverlapPercentage(110.5f));
    }
    
    @Test
    @DisplayName("setMinimumOverlapPercentage valid value")
    public void testSetMinimumOverlapPercentage(){
        DataEnvelopeMatchingConfiguration config = new DataEnvelopeMatchingConfiguration();
        config.setMinimumOverlapPercentage(50.0f);

        assertEquals(50.0f, config.getMinimumOverlapPercentage());
    }

}
