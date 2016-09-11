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

package ru.stqa.selenium.decorated;

import org.junit.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class DecoratedTargetLocatorTest {

  private static class Fixture {
    WebDriver mockedDriver;
    DecoratedWebDriver decoratedDriver;
    WebDriver.TargetLocator mockedTarget;
    DecoratedTargetLocator decoratedTarget;

    public Fixture() {
      mockedDriver = mock(WebDriver.class);
      decoratedDriver = new DecoratedWebDriver(mockedDriver);
      mockedTarget = mock(WebDriver.TargetLocator.class);
      decoratedTarget = new DecoratedTargetLocator(mockedTarget, decoratedDriver);
    }
  }

  @Test
  public void testConstructor() {
    Fixture fixture = new Fixture();

    assertThat(fixture.mockedTarget, sameInstance(fixture.decoratedTarget.getOriginal()));
    assertThat(fixture.decoratedDriver, sameInstance(fixture.decoratedTarget.getTopmostDecorated()));
  }

  @Test
  public void testWindow() {
    Fixture fixture = new Fixture();
    when(fixture.mockedTarget.window("test")).thenReturn(fixture.mockedDriver);

    WebDriver proxy = fixture.decoratedTarget.window("test");
    assertThat(fixture.mockedDriver, not(sameInstance(proxy)));
    verify(fixture.mockedTarget, times(1)).window("test");

    proxy.quit();
    verify(fixture.mockedDriver, times(1)).quit();
  }

  @Test
  public void testFrameByIndex() {
    Fixture fixture = new Fixture();
    when(fixture.mockedTarget.frame(3)).thenReturn(fixture.mockedDriver);

    WebDriver proxy = fixture.decoratedTarget.frame(3);
    assertThat(fixture.mockedDriver, not(sameInstance(proxy)));
    verify(fixture.mockedTarget, times(1)).frame(3);

    proxy.quit();
    verify(fixture.mockedDriver, times(1)).quit();
  }

  @Test
  public void testFrameByString() {
    Fixture fixture = new Fixture();
    when(fixture.mockedTarget.frame("test")).thenReturn(fixture.mockedDriver);

    WebDriver proxy = fixture.decoratedTarget.frame("test");
    assertThat(fixture.mockedDriver, not(sameInstance(proxy)));
    verify(fixture.mockedTarget, times(1)).frame("test");

    proxy.quit();
    verify(fixture.mockedDriver, times(1)).quit();
  }

  @Test
  public void testFrameByReference() {
    Fixture fixture = new Fixture();
    WebElement frame = mock(WebElement.class);
    when(fixture.mockedTarget.frame(frame)).thenReturn(fixture.mockedDriver);

    WebDriver proxy = fixture.decoratedTarget.frame(frame);
    assertThat(fixture.mockedDriver, not(sameInstance(proxy)));
    verify(fixture.mockedTarget, times(1)).frame(frame);

    proxy.quit();
    verify(fixture.mockedDriver, times(1)).quit();
  }

  @Test
  public void testParentFrame() {
    Fixture fixture = new Fixture();

    WebDriver proxy = fixture.decoratedTarget.parentFrame();
    assertThat(fixture.mockedDriver, not(sameInstance(proxy)));
    verify(fixture.mockedTarget, times(1)).parentFrame();

    proxy.quit();
    verify(fixture.mockedDriver, times(1)).quit();
  }

  @Test
  public void testDefaultContent() {
    Fixture fixture = new Fixture();

    WebDriver proxy = fixture.decoratedTarget.defaultContent();
    assertThat(fixture.mockedDriver, not(sameInstance(proxy)));
    verify(fixture.mockedTarget, times(1)).defaultContent();

    proxy.quit();
    verify(fixture.mockedDriver, times(1)).quit();
  }

  @Test
  public void testActiveElement() {
    Fixture fixture = new Fixture();
    WebElement active = mock(WebElement.class);
    when(fixture.mockedTarget.activeElement()).thenReturn(active);

    WebElement proxy = fixture.decoratedTarget.activeElement();
    assertThat(active, not(sameInstance(proxy)));
    verify(fixture.mockedTarget, times(1)).activeElement();

    proxy.isDisplayed();
    verify(active, times(1)).isDisplayed();
  }

  @Test
  public void testAlert() {
    Fixture fixture = new Fixture();
    Alert alert = mock(Alert.class);
    when(fixture.mockedTarget.alert()).thenReturn(alert);

    Alert proxy = fixture.decoratedTarget.alert();
    assertThat(alert, not(sameInstance(proxy)));
    verify(fixture.mockedTarget, times(1)).alert();

    proxy.dismiss();
    verify(alert, times(1)).dismiss();
  }

}
