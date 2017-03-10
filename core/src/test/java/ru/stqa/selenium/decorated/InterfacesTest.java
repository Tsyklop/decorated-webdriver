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
import org.openqa.selenium.WebDriver;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

public class InterfacesTest {

  private interface SomeOtherInterface {}

  private interface ExtendedDriver extends WebDriver, SomeOtherInterface {}

  private static class SimpleDecoratedWebDriver extends DecoratedWebDriver {
    public SimpleDecoratedWebDriver(final WebDriver driver) {
      super(driver);
    }
  }

  @Test
  public void shouldNotAddInterfacesNotAvailableInTheOriginalDriver() {
    final WebDriver driver = mock(WebDriver.class);
    assertThat(driver, not(instanceOf(SomeOtherInterface.class)));

    final WebDriver decorated = new Activator<WebDriver>().activate(new SimpleDecoratedWebDriver(driver));
    assertThat(decorated, not(instanceOf(SomeOtherInterface.class)));
  }

  @Test
  public void shouldRespectInterfacesAvailableInTheOriginalDriver() {
    final WebDriver driver = mock(ExtendedDriver.class);
    assertThat(driver, instanceOf(SomeOtherInterface.class));

    final WebDriver decorated = new Activator<WebDriver>().activate(new SimpleDecoratedWebDriver(driver));
    assertThat(decorated, instanceOf(SomeOtherInterface.class));
  }

}
