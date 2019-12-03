/*
 * Copyright 2016 Alexei Barantsev
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package ru.stqa.selenium.decorated;

import org.openqa.selenium.WebDriver;

import java.util.concurrent.TimeUnit;

public class DecoratedTimeouts extends DecoratedChild<WebDriver.Timeouts, DecoratedWebDriver> implements WebDriver.Timeouts {

  public DecoratedTimeouts(final WebDriver.Timeouts timeouts, final DecoratedWebDriver driverWrapper) {
    super(timeouts, driverWrapper);
  }

  @Override
  public WebDriver.Timeouts implicitlyWait(long timeout, TimeUnit timeUnit) {
    getOriginal().implicitlyWait(timeout, timeUnit);
    return this;
  }

  @Override
  public WebDriver.Timeouts setScriptTimeout(long timeout, TimeUnit timeUnit) {
    getOriginal().setScriptTimeout(timeout, timeUnit);
    return this;
  }

  @Override
  public WebDriver.Timeouts pageLoadTimeout(long timeout, TimeUnit timeUnit) {
    getOriginal().pageLoadTimeout(timeout, timeUnit);
    return this;
  }
}
