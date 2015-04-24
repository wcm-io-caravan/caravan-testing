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

import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.IOUtils;

import rx.Observable;

/**
 * Very simple HTTP client only performing GET requests.
 */
public class SimpleCaravanHttpClient implements CaravanHttpClient {

  private String host = "http://localhost:8080";


  @Override
  public Observable<CaravanHttpResponse> execute(CaravanHttpRequest request) {
    String url = host + request.getUrl();
    try {
      byte[] data = IOUtils.toByteArray(new URL(url));
      CaravanHttpResponse response = new CaravanHttpResponseBuilder()
      .status(200)
      .reason("OK")
      .body(data)
      .build();
      return Observable.just(response);
    }
    catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  @Override
  public Observable<CaravanHttpResponse> execute(CaravanHttpRequest request, Observable<CaravanHttpResponse> fallback) {
    return execute(request);
  }

  @Override
  public boolean hasValidConfiguration(String serviceName) {
    return true;
  }

  /**
   * @return the host
   */
  public String getHost() {
    return this.host;
  }

  /**
   * @param host the host to set
   */
  public void setHost(String host) {
    this.host = host;
  }

}
