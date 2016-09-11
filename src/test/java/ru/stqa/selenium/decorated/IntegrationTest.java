/*
 * Copyright 2013 Alexei Barantsev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ru.stqa.selenium.decorated;

import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class IntegrationTest {

  @Ignore
  @Test
  public void canWrapASingleMethod() {
    final WebDriver mockedDriver = mock(WebDriver.class);
    final WebElement mockedElement = mock(WebElement.class, "element1");
    final WebElement mockedElement2 = mock(WebElement.class, "element2");

    when(mockedDriver.findElement(By.name("foo"))).thenReturn(mockedElement);
    when(mockedElement.findElement(By.tagName("div"))).thenReturn(mockedElement2);
    when(mockedElement2.isDisplayed()).thenReturn(true);

    final AtomicInteger counter = new AtomicInteger();
    final WebDriver driver = new Decorator<WebDriver>().activate(new ClickCountingDriver(mockedDriver, counter));

    // check for element wrapped by the driver
    final WebElement firstElement = driver.findElement(By.name("foo"));
    firstElement.click();
    assertThat(counter.get(), is(1));

    // check that elements returned by WebElement.findElement are wrapped too
    final WebElement nestedElement = firstElement.findElement(By.tagName("div"));
    nestedElement.click();
    assertThat(counter.get(), is(2));

    // check that methods that are not overwritten works normally
    assertThat(nestedElement.isDisplayed(), is(true));

    verify(mockedDriver, times(1)).findElement(By.name("foo"));
    verify(mockedElement, times(1)).click();
    verify(mockedElement, times(1)).findElement(By.tagName("div"));
    verify(mockedElement2, times(1)).click();
    verify(mockedElement2, times(1)).isDisplayed();
  }

  private static class ClickCountingDriver extends DecoratedWebDriver {
    private final AtomicInteger counter;

    private ClickCountingDriver(final WebDriver driver, final AtomicInteger clickCounter) {
      super(driver);
      counter = clickCounter;
    }

    protected DecoratedWebElement createDecorated(WebElement element) {
      return new DecoratedWebElement(element, this) {
        @Override
        public void click() {
          super.click();
          counter.incrementAndGet();
        }
      };
    }
  }

  @Ignore
  @Test(expected = NoSuchElementException.class)
  public void canPropagateExceptions() {
    final WebDriver mockedDriver = mock(WebDriver.class);

    when(mockedDriver.findElement(By.name("foo"))).thenThrow(NoSuchElementException.class);

    final WebDriver driver = new Decorator<WebDriver>().activate(new DecoratedWebDriver(mockedDriver));

    driver.findElement(By.name("foo"));
  }

  @Ignore
  @Test
  public void canPreventExceptions() {
    final WebDriver mockedDriver = mock(WebDriver.class);

    when(mockedDriver.findElement(By.name("foo"))).thenThrow(NoSuchElementException.class);

    final WebDriver driver = new Decorator<WebDriver>().activate(new DecoratedWebDriver(mockedDriver) {
      @Override
      public Object onError(Method method, InvocationTargetException e, Object[] args) {
        return null;
      }
    });

    assertThat(driver.findElement(By.name("foo")), is(nullValue()));
  }

}
