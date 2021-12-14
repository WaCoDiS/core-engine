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
package de.wacodis.coreengine.evaluator.wacodisjobevaluation;

import de.wacodis.core.models.AbstractDataEnvelope;
import de.wacodis.core.models.AbstractSubsetDefinition;
import de.wacodis.core.models.WacodisJobDefinition;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.joda.time.DateTime;
import org.junit.Assert;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class SourceTypeMatcherTest {

    @org.junit.Test
    public void testMatch() {
        SourceTypeDataEnvelopeMatcher matcher = new SourceTypeDataEnvelopeMatcher();

        AbstractSubsetDefinition input = new AbstractSubsetDefinition();
        List<AbstractSubsetDefinition> inputs = new ArrayList<>();
        inputs.add(input);
        AbstractDataEnvelope envelope = new AbstractDataEnvelope();
        WacodisJobDefinition jobDef = new WacodisJobDefinition();
        jobDef.setInputs(inputs);
        WacodisJobWrapper job = new WacodisJobWrapper(new WacodisJobExecutionContext(UUID.randomUUID(), DateTime.now(), 0), jobDef);

        input.setSourceType(AbstractSubsetDefinition.SourceTypeEnum.DWDSUBSETDEFINITION);
        envelope.setSourceType(AbstractDataEnvelope.SourceTypeEnum.DWDDATAENVELOPE);
        Assert.assertTrue(matcher.match(envelope, job, input));

        input.setSourceType(AbstractSubsetDefinition.SourceTypeEnum.CATALOGUESUBSETDEFINITION);
        envelope.setSourceType(AbstractDataEnvelope.SourceTypeEnum.GDIDEDATAENVELOPE);
        Assert.assertTrue(matcher.match(envelope, job, input));

        input.setSourceType(AbstractSubsetDefinition.SourceTypeEnum.COPERNICUSSUBSETDEFINITION);
        envelope.setSourceType(AbstractDataEnvelope.SourceTypeEnum.COPERNICUSDATAENVELOPE);
        Assert.assertTrue(matcher.match(envelope, job, input));

        input.setSourceType(AbstractSubsetDefinition.SourceTypeEnum.SENSORWEBSUBSETDEFINITION);
        envelope.setSourceType(AbstractDataEnvelope.SourceTypeEnum.SENSORWEBDATAENVELOPE);
        Assert.assertTrue(matcher.match(envelope, job, input));

        input.setSourceType(AbstractSubsetDefinition.SourceTypeEnum.WACODISPRODUCTSUBSETDEFINITION);
        envelope.setSourceType(AbstractDataEnvelope.SourceTypeEnum.WACODISPRODUCTDATAENVELOPE);
        Assert.assertTrue(matcher.match(envelope, job, input));

        input.setSourceType(AbstractSubsetDefinition.SourceTypeEnum.STATICSUBSETDEFINITION);
        envelope.setSourceType(AbstractDataEnvelope.SourceTypeEnum.WACODISPRODUCTDATAENVELOPE);
        Assert.assertFalse(matcher.match(envelope, job, input));
    }

}
