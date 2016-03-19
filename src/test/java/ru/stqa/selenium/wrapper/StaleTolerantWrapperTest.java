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

package ru.stqa.selenium.wrapper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class StaleTolerantWrapperTest extends TestBase {

  private WebDriver original;
  private WebDriver driver;

  @Before
  public void setUp() {
    original = new HtmlUnitDriver(true);
    StaleTolerantWrapper wrapper = new StaleTolerantWrapper(original);
    driver = wrapper.getDriver();
  }

  @After
  public void tearDown() {
    driver.quit();
  }

  @Test
  public void testCanRediscoverAReplacedElement() {
    driver.get(testServer.page("/staleness.html"));
    WebElement button1 = driver.findElement(By.id("b1"));
    WebElement button2 = driver.findElement(By.id("b2"));

    button1.click();
    button2.click();

    assertThat(driver.findElement(By.id("text")).getText(), is("button2"));
  }

  @Test
  public void testCanRediscoverAReplacedChildElement() {
    driver.get(testServer.page("/staleness.html"));
    WebElement button1 = driver.findElement(By.id("b1"));
    WebElement button3 = driver.findElement(By.id("div3")).findElement(By.id("b3"));

    button1.click();
    button3.click();

    assertThat(driver.findElement(By.id("text")).getText(), is("button3"));
  }

  @Test
  public void testCanRediscoverAReplacedSubtree() {
    driver.get(testServer.page("/staleness.html"));
    WebElement button1 = driver.findElement(By.id("b1"));
    WebElement button4 = driver.findElement(By.id("div4")).findElement(By.id("b4"));

    button1.click();
    button4.click();

    assertThat(driver.findElement(By.id("text")).getText(), is("button4"));
  }

}
