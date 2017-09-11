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
package ru.stqa.selenium.decorated.highlight;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import ru.stqa.selenium.decorated.Decorated;
import ru.stqa.selenium.decorated.DecoratedWebDriver;

import java.lang.reflect.Method;
import java.util.List;

public class HighlightingWebDriver extends DecoratedWebDriver {

  private String cssElementId = "highlighting-webdriver-id";
  private String actionStyle = "border: 2px solid red !important";
  private String actionClass = "highlighting-webdriver-action";
  private String foundStyle = "border: 2px solid blue !important";
  private String foundClass = "highlighting-webdriver-found";

  private long pause = 2000;

  public HighlightingWebDriver(final WebDriver driver) {
    super(driver);
  }

  public HighlightingWebDriver(final WebDriver driver, long pause) {
    super(driver);
    this.pause = pause;
  }

  public HighlightingWebDriver(final WebDriver driver, final String actionStyle, final String foundStyle) {
    super(driver);
    this.actionStyle = actionStyle;
    this.foundStyle = foundStyle;
  }

  public HighlightingWebDriver(final WebDriver driver, long pause, final String actionStyle, final String foundStyle) {
    super(driver);
    this.pause = pause;
    this.actionStyle = actionStyle;
    this.foundStyle = foundStyle;
  }

  @Override
  public void beforeMethodGlobal(Decorated<?> target, Method method, Object[] args) {
    if (target.getOriginal() instanceof WebElement) {
      final WebElement element = (WebElement) target.getOriginal();
      highlight(element, actionClass);
    }
    super.beforeMethodGlobal(target, method, args);
  }

  @Override
  public void afterMethodGlobal(Decorated<?> target, Method method, Object res, Object[] args) {
    if (res != null) {
      if (res instanceof WebElement) {
        WebElement element = (WebElement) res;
        highlight(element, foundClass);
      } else if (res instanceof List<?>) {

      }
    }
    super.afterMethodGlobal(target, method, res, args);
  }

  private void highlight(WebElement element, String cls) {
    addStyleToHeader();
    try {
      addClass(element, cls);
      pause();
    } finally {
      removeClass(element, cls);
    }
  }

  private void addStyleToHeader() {
    String script = "if (document.getElementById('" + cssElementId + "')) return; "+
        "var highlightingWebDriverStyleElement = document.createElement('style'); " +
        "highlightingWebDriverStyleElement.id = '"+ cssElementId +"'; " +
        "highlightingWebDriverStyleElement.type = 'text/css'; " +
        "var highlightingWebDriverStyle = '." + actionClass + " {" + actionStyle + "} ." + foundClass + " {" + foundStyle + "}'; " +
        "if (highlightingWebDriverStyleElement.styleSheet) { " +
        "highlightingWebDriverStyleElement.styleSheet.cssText = highlightingWebDriverStyle; " +
        "} else { highlightingWebDriverStyleElement.appendChild(document.createTextNode(highlightingWebDriverStyle)); } " +
        "var highlightingWebDriverHead = document.getElementsByTagName('head')[0]; " +
        "highlightingWebDriverHead.appendChild(highlightingWebDriverStyleElement);";
    ((JavascriptExecutor) getOriginal()).executeScript(script);
  }

  private void addClass(WebElement element, String cls) {
    ((JavascriptExecutor) getOriginal()).executeScript(
        "arguments[0].className += ' ' + arguments[1]", element, cls);
  }

  private void removeClass(WebElement element, String cls) {
    ((JavascriptExecutor) getOriginal()).executeScript(
        "arguments[0].className = arguments[0].className.replace(arguments[1], '')", element, cls);
  }

  private void pause() {
    try {
      Thread.sleep(pause);
    } catch (InterruptedException e) {
    }
  }

}
