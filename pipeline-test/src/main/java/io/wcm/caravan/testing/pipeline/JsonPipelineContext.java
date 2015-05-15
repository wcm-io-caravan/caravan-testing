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

import io.wcm.caravan.io.http.CaravanHttpClient;
import io.wcm.caravan.pipeline.JsonPipelineFactory;
import io.wcm.caravan.pipeline.cache.spi.CacheAdapter;
import io.wcm.caravan.pipeline.impl.JsonPipelineFactoryImpl;
import io.wcm.caravan.testing.io.MockingCaravanHttpClient;
import io.wcm.caravan.testing.json.JsonFixture;
import io.wcm.caravan.testing.json.TestConfiguration;
import io.wcm.caravan.testing.pipeline.cache.InMemoryCacheAdapter;

import java.io.InputStream;

import org.apache.sling.testing.mock.osgi.context.OsgiContextImpl;
import org.junit.rules.ExternalResource;

import com.codahale.metrics.MetricRegistry;

/**
 * JUnit rule for setting up a OSGi-based context with {@link JsonPipelineFactory} support.
 */
public final class JsonPipelineContext extends ExternalResource {

  private final OsgiContextImpl context;

  /**
   * @param osgiContext OSGi context
   */
  public JsonPipelineContext(OsgiContextImpl osgiContext) {
    this.context = osgiContext;
  }

  private InMemoryCacheAdapter cacheAdapter;
  private MockingCaravanHttpClient caravanHttpClient;
  private MetricRegistry metricRegistry;
  private JsonPipelineFactory jsonPipelineFactory;

  @Override
  protected void before() {
    // configure JSON infrastructure
    TestConfiguration.init();

    // prepare in-memory cache adapter
    cacheAdapter = new InMemoryCacheAdapter();
    context.registerService(CacheAdapter.class, cacheAdapter);

    // prepare mocking caravan http client
    caravanHttpClient = new MockingCaravanHttpClient();
    context.registerService(CaravanHttpClient.class, caravanHttpClient);

    // setup json pipeline
    metricRegistry = context.registerService(MetricRegistry.class, new MetricRegistry());
    jsonPipelineFactory = context.registerInjectActivateService(new JsonPipelineFactoryImpl());
  }

  public MockingCaravanHttpClient getCaravanHttpClient() {
    return this.caravanHttpClient;
  }

  public MetricRegistry getMetricRegistry() {
    return this.metricRegistry;
  }

  public JsonPipelineFactory getJsonPipelineFactory() {
    return this.jsonPipelineFactory;
  }

  /**
   * Load a JSON fixture from classpath.
   * @param classpathResource Resource path in classpath
   * @return {@link JsonFixture} item
   */
  public JsonFixture loadJson(final String classpathResource) {
    InputStream is = getClass().getResourceAsStream(classpathResource);
    if (is == null) {
      throw new IllegalArgumentException("Classpath resource not found: " + classpathResource);
    }
    return new JsonFixture(is);
  }

}
