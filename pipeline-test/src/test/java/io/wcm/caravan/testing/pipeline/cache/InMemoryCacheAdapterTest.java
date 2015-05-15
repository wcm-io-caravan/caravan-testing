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
package io.wcm.caravan.testing.pipeline.cache;

import static org.junit.Assert.assertEquals;
import io.wcm.caravan.pipeline.cache.spi.CacheAdapter;

import org.junit.Before;
import org.junit.Test;

public class InMemoryCacheAdapterTest {

  private static final String PAYLOAD = "{\"data\":\"test\"}";

  private CacheAdapter underTest;

  @Before
  public void setUp() {
    underTest = new InMemoryCacheAdapter();
  }

  @Test
  public void testSetGet() {
    underTest.put("key1", PAYLOAD, null);
    assertEquals(PAYLOAD, underTest.get("key1", null).toBlocking().single());
  }

}
