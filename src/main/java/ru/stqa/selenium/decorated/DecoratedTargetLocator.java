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

import org.openqa.selenium.Alert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class DecoratedTargetLocator extends DecoratedWebDriverChild<WebDriver.TargetLocator> implements WebDriver.TargetLocator {

  public DecoratedTargetLocator(final DecoratedWebDriver driverWrapper, final WebDriver.TargetLocator targetLocator) {
    super(driverWrapper, targetLocator);
  }

  @Override
  public WebDriver frame(int frameIndex) {
    getOriginal().frame(frameIndex);
    return activate(getDriverWrapper());
  }

  @Override
  public WebDriver frame(String frameName) {
    getOriginal().frame(frameName);
    return activate(getDriverWrapper());
  }

  @Override
  public WebDriver frame(WebElement frameElement) {
    getOriginal().frame(frameElement);
    return activate(getDriverWrapper());
  }

  @Override
  public WebDriver parentFrame() {
    getOriginal().parentFrame();
    return activate(getDriverWrapper());
  }

  @Override
  public WebDriver window(String windowName) {
    getOriginal().window(windowName);
    return activate(getDriverWrapper());
  }

  @Override
  public WebDriver defaultContent() {
    getOriginal().defaultContent();
    return activate(getDriverWrapper());
  }

  @Override
  public WebElement activeElement() {
    return getDriverWrapper().activate(getDriverWrapper().createDecorated(getOriginal().activeElement()));
  }

  @Override
  public Alert alert() {
    return activate(getDriverWrapper().createDecorated(getOriginal().alert()));
  }
}
