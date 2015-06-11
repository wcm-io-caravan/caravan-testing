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

import io.wcm.caravan.io.http.response.CaravanHttpResponse;
import io.wcm.caravan.io.http.response.CaravanHttpResponseBuilder;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.StringStartsWith;
import org.osgi.annotation.versioning.ProviderType;

import com.google.common.base.Charsets;

/**
 * Defines which requests should match.
 * Only one service and one url matcher can be defined.
 */
@ProviderType
public final class RequestMatcher {

  private String serviceName;
  private Matcher<String> urlMatcher;
  private CaravanHttpResponse response;

  /**
   * @param value Service name
   * @return this
   */
  public RequestMatcher serviceName(String value) {
    this.serviceName = value;
    return this;
  }

  /**
   * @param value Exact URL
   * @return this
   */
  public RequestMatcher url(String value) {
    this.urlMatcher = IsEqual.<String>equalTo(value);
    return this;
  }

  /**
   * @param value Starting part of URL
   * @return this
   */
  public RequestMatcher urlStartsWith(String value) {
    this.urlMatcher = StringStartsWith.startsWith(value);
    return this;
  }

  /**
   * @param value Matcher for URL
   * @return this
   */
  public RequestMatcher urlMatches(Matcher<String> value) {
    this.urlMatcher = value;
    return this;
  }

  /**
   * @param pattern Regex pattern to mathc URL
   * @return this
   */
  public RequestMatcher urlMatches(final Pattern pattern) {
    this.urlMatcher = new BaseMatcher<String>() {
      @Override
      public boolean matches(Object item) {
        return pattern.matcher(item.toString()).matches();
      }
      @Override
      public void describeTo(Description description) {
        description.appendText("Pattern " + pattern.toString());
      }
    };
    return this;
  }

  /**
   * Set the response that this request matcher should return.
   * @param payload Payload
   */
  public void response(String payload) {
    response(toResponse(payload));
  }

  /**
   * Set the response that this request matcher should return.
   * @param payload Payload
   */
  public void response(CaravanHttpResponse payload) {
    this.response = payload;
  }

  boolean matches(String expectedServiceName, String expectedUrl) {
    return (this.serviceName == null || StringUtils.equals(expectedServiceName, this.serviceName))
        && (this.urlMatcher == null || urlMatcher.matches(expectedUrl))
        && (response != null);
  }

  CaravanHttpResponse getResponse() {
    return response;
  }

  private static CaravanHttpResponse toResponse(String payload) {
    return new CaravanHttpResponseBuilder()
    .status(200)
    .reason("OK")
    .body(payload.toString(), Charsets.UTF_8)
    .build();
  }

}
