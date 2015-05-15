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
package io.wcm.caravan.testing.io;

import io.wcm.caravan.io.http.CaravanHttpClient;
import io.wcm.caravan.io.http.request.CaravanHttpRequest;
import io.wcm.caravan.io.http.response.CaravanHttpResponse;
import io.wcm.caravan.io.http.response.CaravanHttpResponseBuilder;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.Observable;

import com.google.common.base.Charsets;
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

  private final Map<String, CaravanHttpResponse> responsePerServiceUrl = Maps.newConcurrentMap();
  private final Map<String, CaravanHttpResponse> responsePerUrl = Maps.newConcurrentMap();
  private final Map<String, CaravanHttpResponse> responsePerService = Maps.newConcurrentMap();
  private CaravanHttpResponse responseMatchesAll;

  private Boolean hasValidConfigurationAll;
  private Map<String, Boolean> hasValidConfiguration = Maps.newConcurrentMap();

  @Override
  public Observable<CaravanHttpResponse> execute(final CaravanHttpRequest request) {
    return execute(request, Observable.just(NOT_FOUND));
  }

  @Override
  public Observable<CaravanHttpResponse> execute(final CaravanHttpRequest request, final Observable<CaravanHttpResponse> fallback) {
    String url = request.getUrl();

    // search for matching service and url (starting with)
    String serviceUrlAsKey = buildServiceUrlKey(request.getServiceName(), url);
    for (Entry<String, CaravanHttpResponse> entry : responsePerServiceUrl.entrySet()) {
      if (StringUtils.startsWith(serviceUrlAsKey, entry.getKey())) {
        return Observable.just(entry.getValue());
      }
    }
    // search for matching url (starting with)
    for (Entry<String, CaravanHttpResponse> entry : responsePerUrl.entrySet()) {
      if (StringUtils.startsWith(url, entry.getKey())) {
        return Observable.just(entry.getValue());
      }
    }
    // search for service
    if (responsePerService.containsKey(request.getServiceName())) {
      return Observable.just(responsePerService.get(request.getServiceName()));
    }
    else if (responseMatchesAll != null) {
      return Observable.just(responseMatchesAll);
    }
    else {
      LOGGER.warn("No response registered for url: " + url);
      return fallback;
    }
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
   * Registers a response for the given service and URL.
   * @param url The URL
   * @param response The response to register
   */
  public void mockServiceRequest(final String serviceName, final String url, final CaravanHttpResponse response) {
    responsePerServiceUrl.put(buildServiceUrlKey(serviceName, url), response);
  }

  /**
   * Registers a response for the given service and URL.
   * @param url The URL
   * @param payload Payload to return with an HTTP 200 answer
   */
  public void mockServiceRequest(final String serviceName, final String url, final String payload) {
    mockServiceRequest(serviceName, url, toResponse(payload));
  }

  /**
   * Registers the response for the given service name.
   * @param serviceName The service name
   * @param response The response to return
   */
  public void mockServiceAnyRequest(final String serviceName, final CaravanHttpResponse response) {
    responsePerService.put(serviceName, response);
  }

  /**
   * Registers the response for the given service name.
   * @param serviceName The service name
   * @param payload Payload to return with an HTTP 200 answer
   */
  public void mockServiceAnyRequest(final String serviceName, final String payload) {
    mockServiceAnyRequest(serviceName, toResponse(payload));
  }

  /**
   * Registers a response for the given service and URL.
   * @param url The URL
   * @param response The response to register
   */
  public void mockRequest(final String url, final CaravanHttpResponse response) {
    responsePerUrl.put(url, response);
  }

  /**
   * Registers a response for the given service and URL.
   * @param url The URL
   * @param payload Payload to return with an HTTP 200 answer
   */
  public void mockRequest(final String url, final String payload) {
    mockRequest(url, toResponse(payload));
  }

  /**
   * Returns the given response for any request.
   * @param response Response to return
   */
  public void mockAnyRequest(final CaravanHttpResponse response) {
    responseMatchesAll = response;
  }

  /**
   * Returns the given response for any request.
   * @param payload Payload to return with an HTTP 200 answer
   */
  public void mockAnyRequest(final String payload) {
    mockAnyRequest(toResponse(payload));
  }

  /**
   * Registers the response for the given service name.
   * @param serviceName The service name
   * @param response The response to return
   * @deprecated Please use {@link #mockServiceAnyRequest(String, CaravanHttpResponse)} instead.
   */
  @Deprecated
  public void mockRequestByService(final String serviceName, final CaravanHttpResponse response) {
    mockServiceAnyRequest(serviceName, response);
  }

  
  private static CaravanHttpResponse toResponse(String payload) {
    return new CaravanHttpResponseBuilder()
    .status(200)
    .reason("OK")
    .body(payload.toString(), Charsets.UTF_8)
    .build();
  }

  private static String buildServiceUrlKey(String serviceName, String url) {
    return serviceName + "#" + url;
  }

}
