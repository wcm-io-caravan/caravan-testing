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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import io.wcm.caravan.pipeline.JsonPipelineExceptionHandler;
import io.wcm.caravan.pipeline.JsonPipelineOutput;

import org.osgi.annotation.versioning.ProviderType;

import rx.Observable;

/**
 * Checks the HTTP status code of the error response.
 */
@ProviderType
public final class AssertStatusCodeExceptionHandler implements JsonPipelineExceptionHandler {

  private final int statusCode;

  /**
   * @param statusCode Asserted HTTP status code
   */
  public AssertStatusCodeExceptionHandler(int statusCode) {
    this.statusCode = statusCode;
  }

  @Override
  public Observable<JsonPipelineOutput> call(JsonPipelineOutput defaultFallbackContent, RuntimeException caughtException) {
    assertThat("Asserted HTTP status code " + statusCode + " but got " + defaultFallbackContent.getStatusCode(), defaultFallbackContent.getStatusCode(),
        equalTo(statusCode));
    throw caughtException;
  }

}
