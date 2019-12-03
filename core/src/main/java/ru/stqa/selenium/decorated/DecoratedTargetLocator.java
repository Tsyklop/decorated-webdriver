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

public class DecoratedTargetLocator extends DecoratedChild<WebDriver.TargetLocator, DecoratedWebDriver> implements WebDriver.TargetLocator {

  public DecoratedTargetLocator(final WebDriver.TargetLocator targetLocator, final DecoratedWebDriver driverWrapper) {
    super(targetLocator, driverWrapper);
  }

  @Override
  public WebDriver frame(int frameIndex) {
    getOriginal().frame(frameIndex);
    return getTopmostDecorated().getActivated();
  }

  @Override
  public WebDriver frame(String frameName) {
    getOriginal().frame(frameName);
    return getTopmostDecorated().getActivated();
  }

  @Override
  public WebDriver frame(WebElement frameElement) {
    getOriginal().frame(frameElement);
    return getTopmostDecorated().getActivated();
  }

  @Override
  public WebDriver parentFrame() {
    getOriginal().parentFrame();
    return getTopmostDecorated().getActivated();
  }

  @Override
  public WebDriver window(String windowName) {
    getOriginal().window(windowName);
    return getTopmostDecorated().getActivated();
  }

  @Override
  public WebDriver defaultContent() {
    getOriginal().defaultContent();
    return getTopmostDecorated().getActivated();
  }

  @Override
  public WebElement activeElement() {
    return getTopmostDecorated().createDecorated(getOriginal().activeElement()).getActivated();
  }

  @Override
  public Alert alert() {
    return getTopmostDecorated().createDecorated(getOriginal().alert()).getActivated();
  }
}
