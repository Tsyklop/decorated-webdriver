/*
 * Copyright 2013 Alexei Barantsev
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
package ru.stqa.selenium.wait;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Coordinates;
import org.openqa.selenium.interactions.Locatable;
import ru.stqa.selenium.decorated.*;
import ru.stqa.trier.Clock;
import ru.stqa.trier.LimitExceededException;
import ru.stqa.trier.Sleeper;
import ru.stqa.trier.TimeBasedTrier;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class ImplicitlyWaitingWebDriver extends DecoratedWebDriver {

  @Override
  protected Decorated<WebElement> createDecorated(WebElement original) {
    return new ImplicitlyWaitingWebElement(original, this);
  }

  @Override
  protected Decorated<TargetLocator> createDecorated(final TargetLocator original) {
    return new ImplicitlyWaitingTargetLocator(original, this);
  }

  private static final long DEFAULT_TIMEOUT = 10;
  private static final long DEFAULT_SLEEP_TIMEOUT = 500;

  private long timeout = DEFAULT_TIMEOUT;
  private long interval = DEFAULT_SLEEP_TIMEOUT;
  private Clock clock;
  private Sleeper sleeper;

  public ImplicitlyWaitingWebDriver(final WebDriver driver) {
    this(driver, DEFAULT_TIMEOUT);
  }

  public ImplicitlyWaitingWebDriver(final WebDriver driver, long timeoutInSeconds) {
    this(driver, timeoutInSeconds, DEFAULT_SLEEP_TIMEOUT);
  }

  public ImplicitlyWaitingWebDriver(final WebDriver driver, long timeoutInSeconds, long sleepTimeOut) {
    this(driver, new Clock(){}, new Sleeper(){}, timeoutInSeconds, sleepTimeOut);
  }

  protected ImplicitlyWaitingWebDriver(final WebDriver driver, Clock clock, Sleeper sleeper, long timeoutInSeconds, long sleepTimeOut) {
    super(driver);
    this.timeout = timeoutInSeconds;
    this.interval = sleepTimeOut;
    this.clock = clock;
    this.sleeper = sleeper;
    driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
  }

  private TimeBasedTrier<Object> trier() {
    return new TimeBasedTrier<>(timeout*1000, clock, sleeper, interval);
  }

  private RuntimeException propagate(Throwable throwable) {
    if (throwable instanceof RuntimeException) {
      throw (RuntimeException) throwable;
    }
    if (throwable instanceof Error) {
      throw (Error) throwable;
    }
    throw new RuntimeException(throwable);
  }

  @Override
  public WebElement findElement(By locator) {
    try {
      return new ImplicitlyWaitingWebElement(trier().tryTo(getOriginal()::findElement, locator), this).getActivated();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    } catch (LimitExceededException e) {
      throw propagate(e.getCause());
    }
  }

  @Override
  public List<WebElement> findElements(By locator) {
    try {
      return wrapElements(trier().tryTo(getOriginal()::findElements, locator));
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    } catch (LimitExceededException e) {
      return new ArrayList<>();
    }
  }

  public class ImplicitlyWaitingWebElement extends DecoratedWebElement {

    public ImplicitlyWaitingWebElement(WebElement element, DecoratedWebDriver driver) {
      super(element, driver);
    }

    @Override
    public void click() {
      try {
        trier().tryTo(getOriginal()::click);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      } catch (LimitExceededException e) {
        throw propagate(e.getCause());
      }
    }

    @Override
    public void submit() {
      try {
        trier().tryTo(getOriginal()::submit);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      } catch (LimitExceededException e) {
        throw propagate(e.getCause());
      }
    }

    @Override
    public void sendKeys(CharSequence... keysToSend) {
      try {
        trier().tryTo(() -> getOriginal().sendKeys(keysToSend));
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      } catch (LimitExceededException e) {
        throw propagate(e.getCause());
      }
    }

    @Override
    public void clear() {
      try {
        trier().tryTo(getOriginal()::clear);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      } catch (LimitExceededException e) {
        throw propagate(e.getCause());
      }
    }

    @Override
    public boolean isSelected() {
      try {
        return trier().ignoring((res) -> false).tryTo(getOriginal()::isSelected);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      } catch (LimitExceededException e) {
        throw propagate(e.getCause());
      }
    }

    @Override
    public boolean isEnabled() {
      try {
        return trier().ignoring((res) -> false).tryTo(getOriginal()::isEnabled);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      } catch (LimitExceededException e) {
        throw propagate(e.getCause());
      }
    }

    @Override
    public WebElement findElement(By locator) {
      try {
        WebElement found = trier().tryTo(getOriginal()::findElement, locator);
        return new ImplicitlyWaitingWebElement(found, getTopmostDecorated()).getActivated();
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      } catch (LimitExceededException e) {
        throw propagate(e.getCause());
      }
    }

    @Override
    public List<WebElement> findElements(By locator) {
      try {
        return wrapElements(trier().tryTo(getOriginal()::findElements, locator));
      } catch (InterruptedException | LimitExceededException e) {
        return new ArrayList<>();
      }
    }

    @Override
    public Coordinates getCoordinates() {
      try {
        return trier().tryTo(((Locatable) getOriginal())::getCoordinates);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      } catch (LimitExceededException e) {
        throw propagate(e.getCause());
      }
    }
  }

  public class ImplicitlyWaitingTargetLocator extends DecoratedTargetLocator {

    public ImplicitlyWaitingTargetLocator(TargetLocator targetLocator, DecoratedWebDriver driver) {
      super(targetLocator, driver);
    }

    @Override
    public Alert alert() {
      try {
        return new DecoratedAlert(trier().tryTo(getOriginal()::alert), getTopmostDecorated()).getActivated();
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      } catch (LimitExceededException e) {
        throw propagate(e.getCause());
      }
    }

    @Override
    public WebDriver frame(final int index) {
      try {
        trier().tryTo((Consumer<Integer>) getOriginal()::frame, index);
        return getTopmostDecorated().getActivated();
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      } catch (LimitExceededException e) {
        throw propagate(e.getCause());
      }
    }

    @Override
    public WebDriver frame(final String idOrName) {
      try {
        trier().tryTo((Consumer<String>) getOriginal()::frame, idOrName);
        return getTopmostDecorated().getActivated();
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      } catch (LimitExceededException e) {
        throw propagate(e.getCause());
      }
    }
  }

}
