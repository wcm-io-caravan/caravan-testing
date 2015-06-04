/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2015 wcm.io
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

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.any;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import io.wcm.caravan.io.http.request.CaravanHttpRequest;
import io.wcm.caravan.io.http.request.CaravanHttpRequestBuilder;
import io.wcm.caravan.io.http.response.CaravanHttpResponse;

import java.io.IOException;

import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import rx.Observable;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

public class SimpleCaravanHttpClientTest {

  @Rule
  public WireMockRule wireMock = new WireMockRule(0);

  private SimpleCaravanHttpClient underTest;

  private CaravanHttpRequest request;

  @Before
  public void setUp() {
    underTest = new SimpleCaravanHttpClient();
    underTest.setHost("http://localhost:" + wireMock.port());
  }

  @Test
  public void testHasValidConfiguration() {
    assertTrue(underTest.hasValidConfiguration("service"));
  }


  @Test
  public void testExecuteOK() throws IOException {
    wireMock.stubFor(any(urlEqualTo("/service"))
        .willReturn(aResponse()
            .withStatus(HttpStatus.SC_OK)
            .withBody("test")
            ));

    request = new CaravanHttpRequestBuilder("service").append("/service").build();
    assertExecuteRequest(HttpStatus.SC_OK, "test");
    assertExecuteRequestFallback(HttpStatus.SC_OK, "test");
  }


  @Test
  public void testExecuteOKsubPath() throws IOException {
    wireMock.stubFor(any(urlEqualTo("/service/subpath"))
        .willReturn(aResponse()
            .withStatus(HttpStatus.SC_OK)
            .withBody("test")
            ));

    request = new CaravanHttpRequestBuilder("service").append("/service/subpath").build();
    assertExecuteRequest(HttpStatus.SC_OK, "test");
    assertExecuteRequestFallback(HttpStatus.SC_OK, "test");
  }


  @Test
  public void testExecuteOKquery() throws IOException {
    wireMock.stubFor(any(urlEqualTo("/service?queryParam=1"))
        .willReturn(aResponse()
            .withStatus(HttpStatus.SC_OK)
            .withBody("test")
            ));

    request = new CaravanHttpRequestBuilder("service").append("/service?queryParam=1").build();
    assertExecuteRequest(HttpStatus.SC_OK, "test");
    assertExecuteRequestFallback(HttpStatus.SC_OK, "test");
  }


  @Test(expected = RuntimeException.class)
  public void testExecuteNotFound() throws IOException {
    wireMock.stubFor(any(urlEqualTo("/service2"))
        .willReturn(aResponse()
            .withStatus(HttpStatus.SC_NOT_FOUND)
            .withBody("test")
            ));

    request = new CaravanHttpRequestBuilder("service").append("/service2").build();
    assertExecuteRequest(HttpStatus.SC_NOT_FOUND, "test");
  }


  @Test(expected = RuntimeException.class)
  public void testExecuteWithFallbackNotFound() throws IOException {
    wireMock.stubFor(any(urlEqualTo("/service"))
        .willReturn(aResponse()
            .withStatus(HttpStatus.SC_NOT_FOUND)
            .withBody("test")
            ));

    request = new CaravanHttpRequestBuilder("service").append("/service").build();
    assertExecuteRequestFallback(HttpStatus.SC_NOT_FOUND, "test");
  }

  private void assertExecuteRequest(int status, String payload) throws IOException {
    Observable<CaravanHttpResponse> responseObservable = underTest.execute(request);
    CaravanHttpResponse actualResponse = responseObservable.toBlocking().single();
    assertResponse(actualResponse, status, payload);
  }

  private void assertExecuteRequestFallback(int status, String payload) throws IOException {
    Observable<CaravanHttpResponse> responseObservable = underTest.execute(request, Observable.empty());
    CaravanHttpResponse actualResponse = responseObservable.toBlocking().single();
    assertResponse(actualResponse, status, payload);
  }

  private void assertResponse(CaravanHttpResponse actualResponse, int status, String payload) throws IOException {
    assertNotNull(actualResponse);
    assertEquals(status, actualResponse.status());
    assertEquals(payload, actualResponse.body().asString());
  }
}
