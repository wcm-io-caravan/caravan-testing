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
package io.wcm.caravan.testing.http;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import io.wcm.caravan.io.http.request.CaravanHttpRequest;
import io.wcm.caravan.io.http.request.CaravanHttpRequestBuilder;
import io.wcm.caravan.io.http.response.CaravanHttpResponse;

import java.io.IOException;
import java.util.regex.Pattern;

import org.apache.http.HttpStatus;
import org.hamcrest.core.StringEndsWith;
import org.junit.Before;
import org.junit.Test;

public class MockingCaravanHttpClientTest {

  private static final String PAYLOAD = "{'data':'test'}";

  private MockingCaravanHttpClient underTest;

  @Before
  public void setUp() {
    underTest = new MockingCaravanHttpClient();
  }

  @Test
  public void testHasValidConfiguration_NothingSet() {
    assertTrue(underTest.hasValidConfiguration("anyService"));
  }

  @Test
  public void testHasValidConfiguration_AnySet() {
    underTest.setValidConfigurationAnyService(false);
    assertFalse(underTest.hasValidConfiguration("anyService"));
  }

  @Test
  public void testHasValidConfiguration_ServiceSet() {
    underTest.setValidConfiguration("service1", true);
    underTest.setValidConfiguration("service2", false);
    assertTrue(underTest.hasValidConfiguration("service1"));
    assertFalse(underTest.hasValidConfiguration("service2"));
    assertTrue(underTest.hasValidConfiguration("anyService"));
  }

  @Test
  public void testHasValidConfiguration_ServiceSetAndAnySet() {
    underTest.setValidConfigurationAnyService(false);
    underTest.setValidConfiguration("service1", true);
    underTest.setValidConfiguration("service2", false);
    assertTrue(underTest.hasValidConfiguration("service1"));
    assertFalse(underTest.hasValidConfiguration("service2"));
    assertFalse(underTest.hasValidConfiguration("anyService"));
  }

  @Test
  public void testMockServiceRequest() {
    underTest.mockRequest().serviceName("service1").urlStartsWith("/url1").response(PAYLOAD);
    assertResponse("service1", "/url1", PAYLOAD);
    assertResponse("service1", "/url1/sub1", PAYLOAD);
    assertResponse("service1", "/url2", null);
    assertResponse("service2", "/url1", null);
  }

  @Test
  public void testMockServiceAnyRequest() {
    underTest.mockRequest().serviceName("service1").response(PAYLOAD);
    assertResponse("service1", "/url1", PAYLOAD);
    assertResponse("service1", "/url1/sub1", PAYLOAD);
    assertResponse("service1", "/url2", PAYLOAD);
    assertResponse("service2", "/url1", null);
  }

  @Test
  public void testMockAnyRequest() {
    underTest.mockRequest().response(PAYLOAD);
    assertResponse("service1", "/url1", PAYLOAD);
    assertResponse("service1", "/url1/sub1", PAYLOAD);
    assertResponse("service1", "/url2", PAYLOAD);
    assertResponse("service2", "/url1", PAYLOAD);
  }

  @Test
  public void testMockRequest_StartsWith() {
    underTest.mockRequest().urlStartsWith("/url1").response(PAYLOAD);
    assertResponse("service1", "/url1", PAYLOAD);
    assertResponse("service1", "/url1/sub1", PAYLOAD);
    assertResponse("service1", "/url2", null);
    assertResponse("service2", "/url1", PAYLOAD);
  }

  @Test
  public void testMockRequest_Exact() {
    underTest.mockRequest().url("/url1").response(PAYLOAD);
    assertResponse("service1", "/url1", PAYLOAD);
    assertResponse("service1", "/url1/sub1", null);
    assertResponse("service1", "/url2", null);
    assertResponse("service2", "/url1", PAYLOAD);
  }

  @Test
  public void testMockRequest_Matches() {
    underTest.mockRequest().urlMatches(Pattern.compile("^/.*/.*$")).response(PAYLOAD);
    assertResponse("service1", "/url1", null);
    assertResponse("service1", "/url1/sub1", PAYLOAD);
    assertResponse("service1", "/url2", null);
    assertResponse("service2", "/url1", null);
  }

  @Test
  public void testMockRequest_CustomMatcher() {
    underTest.mockRequest().urlMatches(StringEndsWith.endsWith("/sub1")).response(PAYLOAD);
    assertResponse("service1", "/url1", null);
    assertResponse("service1", "/url1/sub1", PAYLOAD);
    assertResponse("service1", "/url2", null);
    assertResponse("service2", "/url1", null);
  }

  private void assertResponse(String serviceName, String url, String payload) {
    CaravanHttpRequest request = new CaravanHttpRequestBuilder(serviceName).append(url).build();
    CaravanHttpResponse response = underTest.execute(request).toBlocking().single();
    if (payload == null) {
      assertEquals(HttpStatus.SC_NOT_FOUND, response.status());
    }
    else {
      assertEquals(HttpStatus.SC_OK, response.status());
      try {
        assertEquals(payload, response.body().asString());
      }
      catch (IOException ex) {
        throw new RuntimeException(ex);
      }
    }
  }

}
