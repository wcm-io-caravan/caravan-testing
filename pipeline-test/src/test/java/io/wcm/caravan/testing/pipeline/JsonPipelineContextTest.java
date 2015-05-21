/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2014 wcm.io
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package io.wcm.caravan.testing.pipeline;

import static org.junit.Assert.assertEquals;
import io.wcm.caravan.io.http.request.CaravanHttpRequest;
import io.wcm.caravan.io.http.request.CaravanHttpRequestBuilder;
import io.wcm.caravan.pipeline.JsonPipeline;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.testing.mock.osgi.junit.OsgiContext;
import org.junit.Rule;
import org.junit.Test;

public class JsonPipelineContextTest {

  private static final String PAYLOAD = "{\"data\":\"test\"}";

  @Rule
  public OsgiContext context = new OsgiContext();
  @Rule
  public JsonPipelineContext pipelineContext = new JsonPipelineContext(context);

  @Test
  public void testPipeline() {
    pipelineContext.getCaravanHttpClient().mockRequest().response(PAYLOAD);

    CaravanHttpRequest request = new CaravanHttpRequestBuilder("service1").append("/url1").build();
    JsonPipeline pipeline = pipelineContext.getJsonPipelineFactory().create(request);
    StringUtils.equals(PAYLOAD, pipeline.getStringOutput().toBlocking().single());
  }

  @Test
  public void testLoadJson() {
    assertEquals(PAYLOAD, pipelineContext.loadJson("/test.json").toString());
  }

}
