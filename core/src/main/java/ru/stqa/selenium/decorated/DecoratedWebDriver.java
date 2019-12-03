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
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Coordinates;
import org.openqa.selenium.interactions.HasInputDevices;
import org.openqa.selenium.interactions.HasTouchScreen;
import org.openqa.selenium.interactions.Interactive;
import org.openqa.selenium.interactions.Keyboard;
import org.openqa.selenium.interactions.Mouse;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.interactions.TouchScreen;
import org.openqa.selenium.internal.WrapsDriver;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

/**
 * This class allows to extend WebDriver by adding new functionality to a decorated.
 * Example of use:
 * <code>WebDriver driver = DecoratedWebDriver.decorate(originalDriver, MyWebDriverWrapper.class);</code>
 * or
 * <code>MyWebDriverWrapper decorated = new MyWebDriverWrapper(originalDriver, otherParameter);<br>
 * WebDriver driver = new MyWebDriverWrapper(originalDriver, otherParameter).getDriver();</code>
 */
public class DecoratedWebDriver extends DecoratedTopmost<WebDriver>
  implements WebDriver, WrapsDriver, JavascriptExecutor, HasInputDevices, HasTouchScreen, Interactive {

  public DecoratedWebDriver(WebDriver driver) {
    super(driver);
  }

  protected Decorated<WebElement> createDecorated(WebElement original) {
    return new DecoratedWebElement(original, this);
  }

  protected List<WebElement> wrapElements(final List<WebElement> elements) {
    for (ListIterator<WebElement> iterator = elements.listIterator(); iterator.hasNext(); ) {
      // TODO: WTF?
      iterator.set(createDecorated(iterator.next()).getActivated());
    }
    return elements;
  }

  protected Decorated<TargetLocator> createDecorated(final TargetLocator original) {
    return new DecoratedTargetLocator(original, this);
  }

  protected Decorated<Alert> createDecorated(final Alert original) {
    return new DecoratedAlert(original, this);
  }

  protected Decorated<Navigation> createDecorated(final Navigation original) {
    return new DecoratedNavigation(original, this);
  }

  protected Decorated<Options> createDecorated(final Options original) {
    return new DecoratedOptions(original, this);
  }

  protected Decorated<Timeouts> createDecorated(final Timeouts original) {
    return new DecoratedTimeouts(original, this);
  }

  protected Decorated<Window> createDecorated(final Window original) {
    return new DecoratedWindow(original, this);
  }

  protected Decorated<Coordinates> createDecorated(final Coordinates original) {
    return new DecoratedCoordinates(original, this);
  }

  protected Decorated<Keyboard> createDecorated(final Keyboard original) {
    return new DecoratedKeyboard(original, this);
  }

  protected Decorated<Mouse> createDecorated(final Mouse original) {
    return new DecoratedMouse(original, this);
  }

  protected Decorated<TouchScreen> createDecorated(final TouchScreen original) {
    return new DecoratedTouchScreen(original, this);
  }

  // TODO: implement proper wrapping for arbitrary objects
  Object wrapObject(final Object object) {
    if (object instanceof WebElement) {
      return createDecorated((WebElement) object).getActivated();
    } else {
      return object;
    }
  }

  @Override
  public final WebDriver getWrappedDriver() {
    return getOriginal();
  }

  @Override
  public void get(String url) {
    getOriginal().get(url);
  }

  @Override
  public String getCurrentUrl() {
    return getOriginal().getCurrentUrl();
  }

  @Override
  public String getTitle() {
    return getOriginal().getTitle();
  }

  @Override
  public WebElement findElement(final By by) {
    return createDecorated(getOriginal().findElement(by)).getActivated();
  }

  @Override
  public List<WebElement> findElements(final By by) {
    return wrapElements(getOriginal().findElements(by));
  }

  @Override
  public String getPageSource() {
    return getOriginal().getPageSource();
  }

  @Override
  public void close() {
    getOriginal().close();
  }

  @Override
  public void quit() {
    getOriginal().quit();
  }

  @Override
  public Set<String> getWindowHandles() {
    return getOriginal().getWindowHandles();
  }

  @Override
  public String getWindowHandle() {
    return getOriginal().getWindowHandle();
  }

  @Override
  public TargetLocator switchTo() {
    return createDecorated(getOriginal().switchTo()).getActivated();
  }

  @Override
  public Navigation navigate() {
    return createDecorated(getOriginal().navigate()).getActivated();
  }

  @Override
  public Options manage() {
    return createDecorated(getOriginal().manage()).getActivated();
  }

  @Override
  public Object executeScript(String script, Object... args) {
    WebDriver driver = getOriginal();
    if (driver instanceof JavascriptExecutor) {
      return wrapObject(((JavascriptExecutor) driver).executeScript(script, args));
    } else {
      throw new WebDriverException("Wrapped webdriver does not implement JavascriptExecutor: " + driver);
    }
  }

  @Override
  public Object executeAsyncScript(String script, Object... args) {
    WebDriver driver = getOriginal();
    if (driver instanceof JavascriptExecutor) {
      return wrapObject(((JavascriptExecutor) driver).executeAsyncScript(script, args));
    } else {
      throw new WebDriverException("Wrapped webdriver does not implement JavascriptExecutor: " + driver);
    }
  }

  @Override
  public Keyboard getKeyboard() {
    return createDecorated(((HasInputDevices) getOriginal()).getKeyboard()).getActivated();
  }

  @Override
  public Mouse getMouse() {
    return createDecorated(((HasInputDevices) getOriginal()).getMouse()).getActivated();
  }

  @Override
  public TouchScreen getTouch() {
    return createDecorated(((HasTouchScreen) getOriginal()).getTouch()).getActivated();
  }

  @Override
  public void perform(Collection<Sequence> actions) {
    WebDriver driver = getOriginal();
    if (driver instanceof Interactive) {
      ((Interactive) driver).perform(actions);
    } else {
      throw new WebDriverException("Wrapped webdriver does not implement Interactive: " + driver);
    }
  }

  @Override
  public void resetInputState() {
    WebDriver driver = getOriginal();
    if (driver instanceof Interactive) {
      ((Interactive) driver).resetInputState();
    } else {
      throw new WebDriverException("Wrapped webdriver does not implement Interactive: " + driver);
    }
  }
}
