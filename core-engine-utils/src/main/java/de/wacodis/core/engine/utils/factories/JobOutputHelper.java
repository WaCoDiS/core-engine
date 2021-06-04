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
package de.wacodis.core.engine.utils.factories;

import de.wacodis.core.models.JobOutputDescriptor;
import de.wacodis.core.models.WacodisJobDefinition;
import java.util.List;

/**
 * Helper to get expected outputs for a Wacodis Job, needed since expected outputs are optional in job definition
 * @author Arne
 */
public interface JobOutputHelper {
    
    /**
     * @param jobDef
     * @return list of expected outputs
     */
    List<JobOutputDescriptor> getExepectedOutputsForJob(WacodisJobDefinition jobDef);
    
}
