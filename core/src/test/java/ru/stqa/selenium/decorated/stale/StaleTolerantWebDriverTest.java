/*
 * Copyright 2016 Alexei Barantsev
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

package ru.stqa.selenium.decorated.stale;

import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.openqa.selenium.*;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class StaleTolerantWebDriverTest {

  private static class Fixture {
    WebDriver mockedDriver;
    WebDriver driver;

    public Fixture() {
      mockedDriver = mock(WebDriver.class);
      driver = new StaleTolerantWebDriver(mockedDriver).getActivated();
    }
  }

  @Test
  void shouldNotRediscoverNonStaleElement() {
    Fixture fixture = new Fixture();

    WebElement element1 = mock(WebElement.class);

    when(fixture.mockedDriver.findElement(By.id("test"))).thenReturn(element1);

    WebElement element = fixture.driver.findElement(By.id("test"));
    element.click();
    element.click();

    InOrder inOrder = inOrder(fixture.mockedDriver, element1);
    inOrder.verify(fixture.mockedDriver).findElement(By.id("test"));
    inOrder.verify(element1, times(2)).click();
    verifyNoMoreInteractions(fixture.mockedDriver);
    verifyNoMoreInteractions(element1);
  }

  @Test
  void shouldRediscoverAStaleElement() {
    Fixture fixture = new Fixture();

    WebElement element1 = mock(WebElement.class);
    WebElement element2 = mock(WebElement.class);

    when(fixture.mockedDriver.findElement(By.id("test")))
      .thenReturn(element1).thenReturn(element2);
    doNothing().doThrow(StaleElementReferenceException.class)
      .when(element1).click();

    WebElement element = fixture.driver.findElement(By.id("test"));
    element.click();
    element.click();

    InOrder inOrder = inOrder(fixture.mockedDriver, element1, element2);
    inOrder.verify(fixture.mockedDriver).findElement(By.id("test"));
    inOrder.verify(element1, times(2)).click();
    inOrder.verify(fixture.mockedDriver).findElement(By.id("test"));
    inOrder.verify(element2).click();
    verifyNoMoreInteractions(fixture.mockedDriver);
    verifyNoMoreInteractions(element1);
    verifyNoMoreInteractions(element2);
  }

  @Test
  void shouldRediscoverAStaleChild() {
    Fixture fixture = new Fixture();

    WebElement parent = mock(WebElement.class);
    WebElement child1 = mock(WebElement.class);
    WebElement child2 = mock(WebElement.class);

    when(fixture.mockedDriver.findElement(By.id("parent"))).thenReturn(parent);
    when(parent.findElement(By.id("child")))
      .thenReturn(child1).thenReturn(child2);
    doNothing().doThrow(StaleElementReferenceException.class)
      .when(child1).click();

    WebElement child = fixture.driver.findElement(By.id("parent")).findElement(By.id("child"));
    child.click();
    child.click();

    InOrder inOrder = inOrder(fixture.mockedDriver, parent, child1, child2);
    inOrder.verify(fixture.mockedDriver).findElement(By.id("parent"));
    inOrder.verify(parent).findElement(By.id("child"));
    inOrder.verify(child1, times(2)).click();
    inOrder.verify(parent).findElement(By.id("child"));
    inOrder.verify(child2).click();
    verifyNoMoreInteractions(fixture.mockedDriver);
    verifyNoMoreInteractions(parent);
    verifyNoMoreInteractions(child1);
    verifyNoMoreInteractions(child2);
  }

  @Test
  void shouldRediscoverASubtree() {
    Fixture fixture = new Fixture();

    WebElement parent1 = mock(WebElement.class);
    WebElement parent2 = mock(WebElement.class);
    WebElement child1 = mock(WebElement.class);
    WebElement child2 = mock(WebElement.class);

    when(fixture.mockedDriver.findElement(By.id("parent")))
      .thenReturn(parent1).thenReturn(parent2);
    when(parent1.findElement(By.id("child")))
      .thenReturn(child1).thenThrow(StaleElementReferenceException.class);
    when(parent2.findElement(By.id("child")))
      .thenReturn(child2);
    doNothing().doThrow(StaleElementReferenceException.class)
      .when(child1).click();

    WebElement child = fixture.driver.findElement(By.id("parent")).findElement(By.id("child"));
    child.click();
    child.click();

    InOrder inOrder = inOrder(fixture.mockedDriver, parent1, parent2, child1, child2);
    inOrder.verify(fixture.mockedDriver).findElement(By.id("parent"));
    inOrder.verify(parent1).findElement(By.id("child"));
    inOrder.verify(child1, times(2)).click();
    inOrder.verify(parent1).findElement(By.id("child"));
    inOrder.verify(fixture.mockedDriver).findElement(By.id("parent"));
    inOrder.verify(parent2).findElement(By.id("child"));
    inOrder.verify(child2).click();
    verifyNoMoreInteractions(fixture.mockedDriver);
    verifyNoMoreInteractions(parent1);
    verifyNoMoreInteractions(parent2);
    verifyNoMoreInteractions(child1);
    verifyNoMoreInteractions(child2);
  }

  @Test
  void shouldPropagateExceptions() {
    Fixture fixture = new Fixture();

    WebElement element1 = mock(WebElement.class);

    when(fixture.mockedDriver.findElement(By.id("test"))).thenReturn(element1);
    doThrow(WebDriverException.class).when(element1).click();

    WebElement element = fixture.driver.findElement(By.id("test"));
    assertThrows(WebDriverException.class, element::click);

    InOrder inOrder = inOrder(fixture.mockedDriver, element1);
    inOrder.verify(fixture.mockedDriver).findElement(By.id("test"));
    inOrder.verify(element1).click();
    verifyNoMoreInteractions(fixture.mockedDriver);
    verifyNoMoreInteractions(element1);
  }

  @Test
  void shouldRediscoverOnceAndThrowStale() {
    Fixture fixture = new Fixture();

    WebElement element1 = mock(WebElement.class);

    when(fixture.mockedDriver.findElement(By.id("test")))
      .thenReturn(element1).thenThrow(NoSuchElementException.class);
    doNothing().doThrow(StaleElementReferenceException.class)
      .when(element1).click();

    WebElement element = fixture.driver.findElement(By.id("test"));
    element.click();
    assertThrows(StaleElementReferenceException.class, element::click);

    InOrder inOrder = inOrder(fixture.mockedDriver, element1);
    inOrder.verify(fixture.mockedDriver).findElement(By.id("test"));
    inOrder.verify(element1, times(2)).click();
    inOrder.verify(fixture.mockedDriver).findElement(By.id("test"));
    verifyNoMoreInteractions(fixture.mockedDriver);
    verifyNoMoreInteractions(element1);
  }

  @Test
  void shouldPropagateExceptionsOnRediscovered() {
    Fixture fixture = new Fixture();

    WebElement element1 = mock(WebElement.class);
    WebElement element2 = mock(WebElement.class);

    when(fixture.mockedDriver.findElement(By.id("test")))
      .thenReturn(element1).thenReturn(element2);
    doNothing().doThrow(StaleElementReferenceException.class)
      .when(element1).click();
    doThrow(ElementNotVisibleException.class)
      .when(element2).click();

    WebElement element = fixture.driver.findElement(By.id("test"));
    element.click();
    assertThrows(ElementNotVisibleException.class, element::click);

    InOrder inOrder = inOrder(fixture.mockedDriver, element1, element2);
    inOrder.verify(fixture.mockedDriver).findElement(By.id("test"));
    inOrder.verify(element1, times(2)).click();
    inOrder.verify(fixture.mockedDriver).findElement(By.id("test"));
    inOrder.verify(element2).click();
    verifyNoMoreInteractions(fixture.mockedDriver);
    verifyNoMoreInteractions(element1);
    verifyNoMoreInteractions(element2);
  }

}
