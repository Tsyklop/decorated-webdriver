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
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

/**
 * This class allows to extend WebDriver by adding new functionality to a wrapper.
 * Example of use:
 * <code>WebDriver driver = DecoratedWebDriver.decorate(originalDriver, MyWebDriverWrapper.class);</code>
 * or
 * <code>MyWebDriverWrapper wrapper = new MyWebDriverWrapper(originalDriver, otherParameter);<br>
 * WebDriver driver = new MyWebDriverWrapper(originalDriver, otherParameter).getDriver();</code>
 */
public class DecoratedWebDriver extends DecoratedByReflection<WebDriver>
    implements Topmost, WebDriver, WrapsDriver, JavascriptExecutor, HasInputDevices, HasTouchScreen {

  public DecoratedWebDriver(WebDriver driver) {
    super(null, driver);
  }

  protected FactoryOfDecorated<WebElement, DecoratedWebDriver> getDecoratedWebElementFactory() {
    return new FactoryOfDecorated<WebElement, DecoratedWebDriver>() {
      @Override
      public DecoratedWebElement create(WebElement original, DecoratedWebDriver topmost) {
        return new DecoratedWebElement(topmost, original);
      }
    };
  }

  protected WebElement wrapElement(final WebElement element) {
    return new Decorator<WebElement>().activate(getDecoratedWebElementFactory().create(element, this));
  }

  protected List<WebElement> wrapElements(final List<WebElement> elements) {
    for (ListIterator<WebElement> iterator = elements.listIterator(); iterator.hasNext(); ) {
      iterator.set(wrapElement(iterator.next()));
    }
    return elements;
  }

  protected FactoryOfDecorated<TargetLocator, DecoratedWebDriver> getDecoratedTargetLocatorFactory() {
    return new FactoryOfDecorated<TargetLocator, DecoratedWebDriver>() {
      @Override
      public DecoratedTargetLocator create(TargetLocator original, DecoratedWebDriver topmost) {
        return new DecoratedTargetLocator(topmost, original);
      }
    };
  }

  protected TargetLocator wrapTargetLocator(final TargetLocator targetLocator) {
    return new Decorator<TargetLocator>().activate(getDecoratedTargetLocatorFactory().create(targetLocator, this));
  }

  protected FactoryOfDecorated<Alert, DecoratedWebDriver> getDecoratedAlertFactory() {
    return new FactoryOfDecorated<Alert, DecoratedWebDriver>() {
      @Override
      public DecoratedAlert create(Alert original, DecoratedWebDriver topmost) {
        return new DecoratedAlert(topmost, original);
      }
    };
  }

  protected Alert wrapAlert(final Alert alert) {
    return new Decorator<Alert>().activate(getDecoratedAlertFactory().create(alert, this));
  }

  protected FactoryOfDecorated<Navigation, DecoratedWebDriver> getDecoratedNavigationFactory() {
    return new FactoryOfDecorated<Navigation, DecoratedWebDriver>() {
      @Override
      public DecoratedNavigation create(Navigation original, DecoratedWebDriver topmost) {
        return new DecoratedNavigation(topmost, original);
      }
    };
  }

  protected Navigation wrapNavigation(final Navigation navigator) {
    return new Decorator<Navigation>().activate(getDecoratedNavigationFactory().create(navigator, this));
  }

  protected FactoryOfDecorated<Options, DecoratedWebDriver> getDecoratedOptionsFactory() {
    return new FactoryOfDecorated<Options, DecoratedWebDriver>() {
      @Override
      public DecoratedOptions create(Options original, DecoratedWebDriver topmost) {
        return new DecoratedOptions(topmost, original);
      }
    };
  }

  protected Options wrapOptions(final Options options) {
    return new Decorator<Options>().activate(getDecoratedOptionsFactory().create(options, this));
  }

  protected FactoryOfDecorated<Timeouts, DecoratedWebDriver> getDecoratedTimeoutsFactory() {
    return new FactoryOfDecorated<Timeouts, DecoratedWebDriver>() {
      @Override
      public DecoratedTimeouts create(Timeouts original, DecoratedWebDriver topmost) {
        return new DecoratedTimeouts(topmost, original);
      }
    };
  }

  protected Timeouts wrapTimeouts(final Timeouts timeouts) {
    return new Decorator<Timeouts>().activate(getDecoratedTimeoutsFactory().create(timeouts, this));
  }

  protected FactoryOfDecorated<Window, DecoratedWebDriver> getDecoratedWindowFactory() {
    return new FactoryOfDecorated<Window, DecoratedWebDriver>() {
      @Override
      public DecoratedWindow create(Window original, DecoratedWebDriver topmost) {
        return new DecoratedWindow(topmost, original);
      }
    };
  }

  protected Window wrapWindow(final Window window) {
    return new Decorator<Window>().activate(getDecoratedWindowFactory().create(window, this));
  }

  protected FactoryOfDecorated<Coordinates, DecoratedWebDriver> getDecoratedCoordinatesFactory() {
    return new FactoryOfDecorated<Coordinates, DecoratedWebDriver>() {
      @Override
      public DecoratedCoordinates create(Coordinates original, DecoratedWebDriver topmost) {
        return new DecoratedCoordinates(topmost, original);
      }
    };
  }

  protected Coordinates wrapCoordinates(final Coordinates coordinates) {
    return new Decorator<Coordinates>().activate(getDecoratedCoordinatesFactory().create(coordinates, this));
  }

  protected FactoryOfDecorated<Keyboard, DecoratedWebDriver> getDecoratedKeyboardFactory() {
    return new FactoryOfDecorated<Keyboard, DecoratedWebDriver>() {
      @Override
      public DecoratedKeyboard create(Keyboard original, DecoratedWebDriver topmost) {
        return new DecoratedKeyboard(topmost, original);
      }
    };
  }

  protected Keyboard wrapKeyboard(final Keyboard keyboard) {
    return new Decorator<Keyboard>().activate(getDecoratedKeyboardFactory().create(keyboard, this));
  }

  protected FactoryOfDecorated<Mouse, DecoratedWebDriver> getDecoratedMouseFactory() {
    return new FactoryOfDecorated<Mouse, DecoratedWebDriver>() {
      @Override
      public DecoratedMouse create(Mouse original, DecoratedWebDriver topmost) {
        return new DecoratedMouse(topmost, original);
      }
    };
  }

  protected Mouse wrapMouse(final Mouse mouse) {
    return new Decorator<Mouse>().activate(getDecoratedMouseFactory().create(mouse, this));
  }

  protected FactoryOfDecorated<TouchScreen, DecoratedWebDriver> getDecoratedTouchScreenFactory() {
    return new FactoryOfDecorated<TouchScreen, DecoratedWebDriver>() {
      @Override
      public DecoratedTouchScreen create(TouchScreen original, DecoratedWebDriver topmost) {
        return new DecoratedTouchScreen(topmost, original);
      }
    };
  }

  protected TouchScreen wrapTouchScreen(final TouchScreen touchScreen) {
    return new Decorator<TouchScreen>().activate(getDecoratedTouchScreenFactory().create(touchScreen, this));
  }

  // TODO: implement proper wrapping for arbitrary objects
  Object wrapObject(final Object object) {
    if (object instanceof WebElement) {
      return wrapElement((WebElement) object);
    } else {
      return object;
    }
  }

  public void beforeMethodGlobal(Decorated<?> target, Method method, Object[] args) {
  }

  public Object callMethodGlobal(Decorated<?> target, Method method, Object[] args) throws Throwable {
    return method.invoke(target, args);
  }

  public void afterMethodGlobal(Decorated<?> target, Method method, Object res, Object[] args) {
  }

  public Object onErrorGlobal(Decorated<?> target, Method method, InvocationTargetException e, Object[] args) throws Throwable {
    throw Throwables.propagate(e.getTargetException());
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
    return wrapElement(getOriginal().findElement(by));
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
    return wrapTargetLocator(getOriginal().switchTo());
  }

  @Override
  public Navigation navigate() {
    return wrapNavigation(getOriginal().navigate());
  }

  @Override
  public Options manage() {
    return wrapOptions(getOriginal().manage());
  }

  @Override
  public Object executeScript(String script, Object... args) {
    WebDriver driver = getOriginal();
    if (driver instanceof JavascriptExecutor) {
      return wrapObject(((JavascriptExecutor) driver).executeScript(script, args));
    } else {
      throw new WebDriverException("Wrapped webdriver does not support JavascriptExecutor: " + driver);
    }
  }

  @Override
  public Object executeAsyncScript(String script, Object... args) {
    WebDriver driver = getOriginal();
    if (driver instanceof JavascriptExecutor) {
      return wrapObject(((JavascriptExecutor) driver).executeAsyncScript(script, args));
    } else {
      throw new WebDriverException("Wrapped webdriver does not support JavascriptExecutor: " + driver);
    }
  }

  @Override
  public Keyboard getKeyboard() {
    return wrapKeyboard(((HasInputDevices) getOriginal()).getKeyboard());
  }

  @Override
  public Mouse getMouse() {
    return wrapMouse(((HasInputDevices) getOriginal()).getMouse());
  }

  @Override
  public TouchScreen getTouch() {
    return wrapTouchScreen(((HasTouchScreen) getOriginal()).getTouch());
  }

}
