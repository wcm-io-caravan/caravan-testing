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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import io.wcm.caravan.io.http.CaravanHttpClient;
import io.wcm.caravan.io.http.request.CaravanHttpRequest;
import io.wcm.caravan.io.http.request.CaravanHttpRequestBuilder;
import io.wcm.caravan.io.http.response.CaravanHttpResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import rx.Observable;

@RunWith(MockitoJUnitRunner.class)
public class HostSplittingCaravanHttpClientTest {

  @Mock
  private CaravanHttpClient delegateForHost;

  @Mock
  private CaravanHttpClient delegateOthers;

  @Mock
  private Observable<CaravanHttpResponse> expectedObservable;

  private CaravanHttpRequest request;

  private HostSplittingCaravanHttpClient underTest;



  @Before
  public void setUp() {
    underTest = new HostSplittingCaravanHttpClient(delegateForHost, delegateOthers, "localhost");
  }

  @Test
  public void testHasValidConfiguration() {
    assertTrue(underTest.hasValidConfiguration("service"));
  }

  @Test
  public void testExecuteForHost() {
    when(delegateForHost.execute(Matchers.any())).thenReturn(expectedObservable);
    request = new CaravanHttpRequestBuilder("service").append("http://localhost:8080/service").build();
    Observable<CaravanHttpResponse> actualObservable = underTest.execute(request);
    assertEquals(expectedObservable, actualObservable);
  }

  @Test
  public void testExecuteForHostWithFallback() {
    when(delegateForHost.execute(Matchers.any(), Matchers.any())).thenReturn(expectedObservable);
    request = new CaravanHttpRequestBuilder("service").append("http://localhost:8080/service").build();
    Observable<CaravanHttpResponse> actualObservable = underTest.execute(request, Observable.empty());
    assertEquals(expectedObservable, actualObservable);
  }

  @Test
  public void testExecuteOthers() {
    when(delegateOthers.execute(Matchers.any())).thenReturn(expectedObservable);
    request = new CaravanHttpRequestBuilder("service").append("http://customhost:8080/service").build();
    Observable<CaravanHttpResponse> actualObservable = underTest.execute(request);
    assertEquals(expectedObservable, actualObservable);
  }

  @Test
  public void testExecuteOthersWithFallback() {
    when(delegateOthers.execute(Matchers.any(), Matchers.any())).thenReturn(expectedObservable);
    request = new CaravanHttpRequestBuilder("service").append("http://customhost:8080/service").build();
    Observable<CaravanHttpResponse> actualObservable = underTest.execute(request, Observable.empty());
    assertEquals(expectedObservable, actualObservable);
  }

  @Test
  public void testExecuteMalformedURLException() {
    when(delegateOthers.execute(Matchers.any())).thenReturn(expectedObservable);
    request = new CaravanHttpRequestBuilder("service").append("httphttp://localhost:8080/service").build();
    Observable<CaravanHttpResponse> actualObservable = underTest.execute(request);
    assertEquals(expectedObservable, actualObservable);
  }

  @Test
  public void testExecuteWithFallbackMalformedURLException() {
    when(delegateOthers.execute(Matchers.any(), Matchers.any())).thenReturn(expectedObservable);
    request = new CaravanHttpRequestBuilder("service").append("httphttp://localhost:8080/service").build();
    Observable<CaravanHttpResponse> actualObservable = underTest.execute(request, Observable.empty());
    assertEquals(expectedObservable, actualObservable);
  }
}
