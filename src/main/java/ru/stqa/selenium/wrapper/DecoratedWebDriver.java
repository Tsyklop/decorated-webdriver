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

package ru.stqa.selenium.wrapper;

import com.google.common.base.Throwables;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.*;
import org.openqa.selenium.interactions.internal.Coordinates;
import org.openqa.selenium.internal.WrapsDriver;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

/**
 * This class allows to extend WebDriver by adding new functionality to a wrapper.
 * Example of use:
 * <code>WebDriver driver = DecoratedWebDriver.wrapDriver(originalDriver, MyWebDriverWrapper.class);</code>
 * or
 * <code>MyWebDriverWrapper wrapper = new MyWebDriverWrapper(originalDriver, otherParameter);<br>
 * WebDriver driver = new MyWebDriverWrapper(originalDriver, otherParameter).getDriver();</code>
 */
public class DecoratedWebDriver extends DecoratedByReflection<WebDriver>
    implements WebDriver, WrapsDriver, JavascriptExecutor, HasInputDevices, HasTouchScreen {

  private WebDriver enhancedDriver = null;

  public DecoratedWebDriver(WebDriver driver) {
    super(null, driver);
  }

  protected Class<? extends DecoratedWebElement> getElementWrapperClass() {
    return DecoratedWebElement.class;
  }

  protected WebElement wrapElement(final WebElement element) {
    return DecoratedWebElement.wrapOriginal(this, element, getElementWrapperClass());
  }

  protected List<WebElement> wrapElements(final List<WebElement> elements) {
    for (ListIterator<WebElement> iterator = elements.listIterator(); iterator.hasNext(); ) {
      iterator.set(wrapElement(iterator.next()));
    }
    return elements;
  }

  protected Class<? extends DecoratedTargetLocator> getTargetLocatorWrapperClass() {
    return DecoratedTargetLocator.class;
  }

  protected TargetLocator wrapTargetLocator(final TargetLocator targetLocator) {
    return DecoratedTargetLocator.wrapOriginal(this, targetLocator, getTargetLocatorWrapperClass());
  }

  protected Class<? extends DecoratedAlert> getAlertWrapperClass() {
    return DecoratedAlert.class;
  }

  protected Alert wrapAlert(final Alert alert) {
    return DecoratedAlert.wrapOriginal(this, alert, getAlertWrapperClass());
  }

  protected Class<? extends DecoratedNavigation> getNavigationWrapperClass() {
    return DecoratedNavigation.class;
  }

  protected Navigation wrapNavigation(final Navigation navigator) {
    return DecoratedNavigation.wrapOriginal(this, navigator, getNavigationWrapperClass());
  }

  protected Class<? extends DecoratedOptions> getOptionsWrapperClass() {
    return DecoratedOptions.class;
  }

  protected Options wrapOptions(final Options options) {
    return DecoratedOptions.wrapOriginal(this, options, getOptionsWrapperClass());
  }

  protected Class<? extends DecoratedTimeouts> getTimeoutsWrapperClass() {
    return DecoratedTimeouts.class;
  }

  protected Timeouts wrapTimeouts(final Timeouts timeouts) {
    return DecoratedTimeouts.wrapOriginal(this, timeouts, getTimeoutsWrapperClass());
  }

  protected Class<? extends DecoratedWindow> getWindowWrapperClass() {
    return DecoratedWindow.class;
  }

  protected Window wrapWindow(final Window window) {
    return DecoratedWindow.wrapOriginal(this, window, getWindowWrapperClass());
  }

  protected Class<? extends DecoratedCoordinates> getCoordinatesWrapperClass() {
    return DecoratedCoordinates.class;
  }

  protected Coordinates wrapCoordinates(final Coordinates coordinates) {
    return DecoratedCoordinates.wrapOriginal(this, coordinates, getCoordinatesWrapperClass());
  }

  protected Class<? extends DecoratedKeyboard> getKeyboardWrapperClass() {
    return DecoratedKeyboard.class;
  }

  protected Keyboard wrapKeyboard(final Keyboard keyboard) {
    return DecoratedKeyboard.wrapOriginal(this, keyboard, getKeyboardWrapperClass());
  }

  protected Class<? extends DecoratedMouse> getMouseWrapperClass() {
    return DecoratedMouse.class;
  }

  protected Mouse wrapMouse(final Mouse mouse) {
    return DecoratedMouse.wrapOriginal(this, mouse, getMouseWrapperClass());
  }

  protected Class<? extends DecoratedTouchScreen> getTouchScreenWrapperClass() {
    return DecoratedTouchScreen.class;
  }

  protected TouchScreen wrapTouchScreen(final TouchScreen touchScreen) {
    return DecoratedTouchScreen.wrapOriginal(this, touchScreen, getTouchScreenWrapperClass());
  }

  // TODO: implement proper wrapping for arbitrary objects
  Object wrapObject(final Object object) {
    if (object instanceof WebElement) {
      return wrapElement((WebElement) object);
    } else {
      return object;
    }
  }

  protected void beforeMethodGlobal(DecoratedByReflection target, Method method, Object[] args) {
  }

  protected Object callMethodGlobal(DecoratedByReflection target, Method method, Object[] args) throws Throwable {
    return method.invoke(target, args);
  }

  protected void afterMethodGlobal(DecoratedByReflection target, Method method, Object res, Object[] args) {
  }

  protected Object onErrorGlobal(DecoratedByReflection target, Method method, InvocationTargetException e, Object[] args) throws Throwable {
    throw Throwables.propagate(e.getTargetException());
  }

  @Override
  public final WebDriver getWrappedDriver() {
    return getOriginal();
  }

  @Override
  public void get(String url) {
    getWrappedDriver().get(url);
  }

  @Override
  public String getCurrentUrl() {
    return getWrappedDriver().getCurrentUrl();
  }

  @Override
  public String getTitle() {
    return getWrappedDriver().getTitle();
  }

  @Override
  public WebElement findElement(final By by) {
    return wrapElement(getWrappedDriver().findElement(by));
  }

  @Override
  public List<WebElement> findElements(final By by) {
    return wrapElements(getWrappedDriver().findElements(by));
  }

  @Override
  public String getPageSource() {
    return getWrappedDriver().getPageSource();
  }

  @Override
  public void close() {
    getWrappedDriver().close();
  }

  @Override
  public void quit() {
    getWrappedDriver().quit();
  }

  @Override
  public Set<String> getWindowHandles() {
    return getWrappedDriver().getWindowHandles();
  }

  @Override
  public String getWindowHandle() {
    return getWrappedDriver().getWindowHandle();
  }

  @Override
  public TargetLocator switchTo() {
    return wrapTargetLocator(getWrappedDriver().switchTo());
  }

  @Override
  public Navigation navigate() {
    return wrapNavigation(getWrappedDriver().navigate());
  }

  @Override
  public Options manage() {
    return wrapOptions(getWrappedDriver().manage());
  }

  @Override
  public Object executeScript(String script, Object... args) {
    WebDriver driver = getWrappedDriver();
    if (driver instanceof JavascriptExecutor) {
      return wrapObject(((JavascriptExecutor) driver).executeScript(script, args));
    } else {
      throw new WebDriverException("Wrapped webdriver does not support JavascriptExecutor: " + driver);
    }
  }

  @Override
  public Object executeAsyncScript(String script, Object... args) {
    WebDriver driver = getWrappedDriver();
    if (driver instanceof JavascriptExecutor) {
      return wrapObject(((JavascriptExecutor) driver).executeAsyncScript(script, args));
    } else {
      throw new WebDriverException("Wrapped webdriver does not support JavascriptExecutor: " + driver);
    }
  }

  @Override
  public Keyboard getKeyboard() {
    return wrapKeyboard(((HasInputDevices) getWrappedDriver()).getKeyboard());
  }

  @Override
  public Mouse getMouse() {
    return wrapMouse(((HasInputDevices) getWrappedDriver()).getMouse());
  }

  @Override
  public TouchScreen getTouch() {
    return wrapTouchScreen(((HasTouchScreen) getWrappedDriver()).getTouch());
  }

  /**
   * Builds a {@link Proxy} implementing all interfaces of original driver. It will delegate calls to
   * wrapper when wrapper implements the requested method otherwise to original driver.
   *
   * @param driver               the underlying driver
   * @param wrapperClass         the class of a wrapper
   * @return                     a proxy that wraps the original driver
   */
  public static WebDriver wrapDriver(final WebDriver driver, final Class<? extends DecoratedWebDriver> wrapperClass) {
    return wrapOriginal(null, driver, wrapperClass);
  }

  /**
   * Builds a {@link Proxy} implementing all interfaces of original driver. It will delegate calls to
   * wrapper when wrapper implements the requested method otherwise to original driver.
   */
  public WebDriver getDriver() {
    if (enhancedDriver == null) {
      enhancedDriver = getDecorated();
    }
    return enhancedDriver;
  }

}
