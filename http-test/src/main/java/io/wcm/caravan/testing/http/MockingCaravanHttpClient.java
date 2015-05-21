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

import io.wcm.caravan.io.http.CaravanHttpClient;
import io.wcm.caravan.io.http.request.CaravanHttpRequest;
import io.wcm.caravan.io.http.response.CaravanHttpResponse;
import io.wcm.caravan.io.http.response.CaravanHttpResponseBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.Observable;

import com.google.common.collect.Maps;

/**
 * Mocking implementation of {@link CaravanHttpClient} for tests. Use mockRequest methods to register a response.
 * Returns a 404 NOT FOUND response if there is
 * no response registered for the request.
 */
public class MockingCaravanHttpClient implements CaravanHttpClient {

  private static final Logger LOGGER = LoggerFactory.getLogger(MockingCaravanHttpClient.class);

  private static final CaravanHttpResponse NOT_FOUND = new CaravanHttpResponseBuilder()
  .status(HttpStatus.SC_NOT_FOUND)
  .reason("Not Found")
  .build();

  private final List<RequestMatcher> requestMatchers = new ArrayList<>();

  private Boolean hasValidConfigurationAll;
  private Map<String, Boolean> hasValidConfiguration = Maps.newConcurrentMap();

  @Override
  public Observable<CaravanHttpResponse> execute(final CaravanHttpRequest request) {
    return execute(request, Observable.just(NOT_FOUND));
  }

  @Override
  public Observable<CaravanHttpResponse> execute(final CaravanHttpRequest request, final Observable<CaravanHttpResponse> fallback) {
    String serviceName = request.getServiceName();
    String url = request.getUrl();

    for (RequestMatcher matcher : requestMatchers) {
      if (matcher.matches(serviceName, url)) {
        return Observable.just(matcher.getResponse());
      }
    }

    LOGGER.warn("No response registered for url: " + url);
    return fallback;
  }

  @Override
  public boolean hasValidConfiguration(String serviceName) {
    Boolean validConfig = hasValidConfiguration.get(serviceName);
    if (validConfig == null) {
      if (hasValidConfigurationAll != null) {
        return hasValidConfigurationAll.booleanValue();
      }
      else {
        return true;
      }
    }
    else {
      return validConfig.booleanValue();
    }
  }

  /**
   * Set valid configuration for a given service (if not set defaults to true)
   * @param serviceName Service name
   * @param valid Configuration valid status
   */
  public void setValidConfiguration(String serviceName, boolean valid) {
    hasValidConfiguration.put(serviceName, valid);
  }

  /**
   * Set valid configuration for any service.
   * @param valid Configuration valid status
   */
  public void setValidConfigurationAnyService(boolean valid) {
    hasValidConfigurationAll = valid;
  }

  /**
   * Define which request should be mocked with which response.
   * @return Request matcher
   */
  public RequestMatcher mockRequest() {
    RequestMatcher matcher = new RequestMatcher();
    requestMatchers.add(matcher);
    return matcher;
  }

}
