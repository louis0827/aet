/**
 * AET
 *
 * Copyright (C) 2013 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.cognifide.aet.cleaner.processors;

import com.cognifide.aet.cleaner.processors.exchange.ReferencedArtifactsMessageBody;
import com.cognifide.aet.vs.ArtifactsDAO;
import com.cognifide.aet.vs.mongodb.MongoDBClient;
import java.util.Set;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(service = RemoveOrphanArtifactsProcessor.class)
public class RemoveOrphanArtifactsProcessor extends RemoveArtifactsProcessor {

  @Reference
  private ArtifactsDAO artifactsDAO;

  @Reference
  private MongoDBClient client;

  @Override
  Set<String> getArtifactsIdsToRemove(ReferencedArtifactsMessageBody messageBody) {
    Set<String> artifactsToRemove = artifactsDAO.getArtifactsIds(messageBody.getDbKey());
    artifactsToRemove.removeAll(messageBody.getArtifactsToKeep());
    return artifactsToRemove;
  }

  @Override
  ArtifactsDAO getArtifactsDAO() {
    return artifactsDAO;
  }
}
