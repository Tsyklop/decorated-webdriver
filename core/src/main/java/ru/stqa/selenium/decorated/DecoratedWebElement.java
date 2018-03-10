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

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.internal.Coordinates;
import org.openqa.selenium.interactions.internal.Locatable;
import org.openqa.selenium.internal.WrapsElement;

import java.util.List;

/**
 * Simple {@link DecoratedWebElement} delegating all calls to the wrapped {@link WebElement}.
 */
public class DecoratedWebElement extends DecoratedChild<WebElement,DecoratedWebDriver>
  implements WebElement, WrapsElement, Locatable {

  public DecoratedWebElement(final WebElement element, final DecoratedWebDriver driverWrapper) {
    super(element, driverWrapper);
  }

  @Override
  public final WebElement getWrappedElement() {
    return getOriginal();
  }

  @Override
  public void click() {
    getOriginal().click();
  }

  @Override
  public void submit() {
    getOriginal().submit();
  }

  @Override
  public void sendKeys(final CharSequence... keysToSend) {
    getOriginal().sendKeys(keysToSend);
  }

  @Override
  public void clear() {
    getOriginal().clear();
  }

  @Override
  public String getTagName() {
    return getOriginal().getTagName();
  }

  @Override
  public String getAttribute(final String name) {
    return getOriginal().getAttribute(name);
  }

  @Override
  public boolean isSelected() {
    return getOriginal().isSelected();
  }

  @Override
  public boolean isEnabled() {
    return getOriginal().isEnabled();
  }

  @Override
  public String getText() {
    return getOriginal().getText();
  }

  @Override
  public List<WebElement> findElements(final By by) {
    return getTopmostDecorated().wrapElements(getOriginal().findElements(by));
  }

  @Override
  public WebElement findElement(final By by) {
    return getTopmostDecorated().createDecorated(getOriginal().findElement(by)).getActivated();
  }

  @Override
  public boolean isDisplayed() {
    return getOriginal().isDisplayed();
  }

  @Override
  public Point getLocation() {
    return getOriginal().getLocation();
  }

  @Override
  public Dimension getSize() {
    return getOriginal().getSize();
  }

  @Override
  public Rectangle getRect() {
    return getOriginal().getRect();
  }

  @Override
  public String getCssValue(final String propertyName) {
    return getOriginal().getCssValue(propertyName);
  }

  public Coordinates getCoordinates() {
    Locatable locatable = (Locatable) getOriginal();
    return new Activator<Coordinates>().activate(getTopmostDecorated().createDecorated(locatable.getCoordinates()));
  }

  @Override
  public <X> X getScreenshotAs(OutputType<X> outputType) throws WebDriverException {
    return getOriginal().getScreenshotAs(outputType);
  }
}
