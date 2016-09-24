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

package ru.stqa.selenium.decorated.eventfiring;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Keyboard;
import org.openqa.selenium.interactions.Mouse;
import org.openqa.selenium.interactions.TouchScreen;
import org.openqa.selenium.interactions.internal.Coordinates;

import java.net.URL;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public interface WebDriverListener {

  default void beforeGet(WebDriver driver, String url) {};
  default void afterGet(WebDriver driver, String url) {};

  default void beforeGetCurrentUrl(WebDriver driver) {};
  default void afterGetCurrentUrl(String result, WebDriver driver) {};

  default void beforeGetTitle(WebDriver driver) {};
  default void afterGetTitle(String result, WebDriver driver) {};

  default void beforeFindElement(WebDriver driver, By locator) {};
  default void afterFindElement(WebElement result, WebDriver driver, By locator) {};

  default void beforeFindElements(WebDriver driver, By locator) {};
  default void afterFindElements(List<WebElement> result, WebDriver driver, By locator) {};

  default void beforeGetPageSource(WebDriver driver) {};
  default void afterGetPageSource(String result, WebDriver driver) {};

  default void beforeClose(WebDriver driver) {};
  default void afterClose(WebDriver driver) {};

  default void beforeQuit(WebDriver driver) {};
  default void afterQuit(WebDriver driver) {};

  default void beforeGetWindowHandles(WebDriver driver) {};
  default void afterGetWindowHandles(Set<String> result, WebDriver driver) {};

  default void beforeGetWindowHandle(WebDriver driver) {};
  default void afterGetWindowHandle(String result, WebDriver driver) {};

  default void beforeExecuteScript(WebDriver driver, String script, Object... args) {};
  default void afterExecuteScript(Object result, WebDriver driver, String script, Object... args) {};

  default void beforeExecuteAsyncScript(WebDriver driver, String script, Object... args) {};
  default void afterExecuteAsyncScript(Object result, WebDriver driver, String script, Object... args) {};

  default void beforeClick(WebElement element) {};
  default void afterClick(WebElement element) {};

  default void beforeSubmit(WebElement element) {};
  default void afterSubmit(WebElement element) {};

  default void beforeSendKeys(WebElement element, CharSequence... keysToSend) {};
  default void afterSendKeys(WebElement element, CharSequence... keysToSend) {};

  default void beforeClear(WebElement element) {};
  default void afterClear(WebElement element) {};

  default void beforeGetTagName(WebElement element) {};
  default void afterGetTagName(String result, WebElement element) {};

  default void beforeGetAttribute(WebElement element, String name) {};
  default void afterGetAttribute(String result, WebElement element, String name) {};

  default void beforeIsSelected(WebElement element) {};
  default void afterIsSelected(boolean result, WebElement element) {};

  default void beforeIsEnabled(WebElement element) {};
  default void afterIsEnabled(boolean result, WebElement element) {};

  default void beforeGetText(WebElement element) {};
  default void afterGetText(String result, WebElement element) {};

  default void beforeFindElement(WebElement element, By locator) {};
  default void afterFindElement(WebElement result, WebElement element, By locator) {};

  default void beforeFindElements(WebElement element, By locator) {};
  default void afterFindElements(List<WebElement> result, WebElement element, By locator) {};

  default void beforeIsDisplayed(WebElement element) {};
  default void afterIsDisplayed(boolean result, WebElement element) {};

  default void beforeGetLocation(WebElement element) {};
  default void afterGetLocation(Point result, WebElement element) {};

  default void beforeGetSize(WebElement element) {};
  default void afterGetSize(Dimension result, WebElement element) {};

  default void beforeGetCssValue(WebElement element) {};
  default void afterGetCssValue(String result, WebElement element) {};

  default void beforeTo(WebDriver.Navigation navigation, String url) {};
  default void afterTo(WebDriver.Navigation navigation, String url) {};

  default void beforeTo(WebDriver.Navigation navigation, URL url) {};
  default void afterTo(WebDriver.Navigation navigation, URL url) {};

  default void beforeBack(WebDriver.Navigation navigation) {};
  default void afterBack(WebDriver.Navigation navigation) {};

  default void beforeForward(WebDriver.Navigation navigation) {};
  default void afterForward(WebDriver.Navigation navigation) {};

  default void beforeRefresh(WebDriver.Navigation navigation) {};
  default void afterRefresh(WebDriver.Navigation navigation) {};

  default void beforeAccept(Alert alert) {};
  default void afterAccept(Alert alert) {};

  default void beforeDismiss(Alert alert) {};
  default void afterDismiss(Alert alert) {};

  default void beforeGetText(Alert alert) {};
  default void afterGetText(String result, Alert alert) {};

  default void beforeSendKeys(Alert alert, String text) {};
  default void afterSendKeys(Alert alert, String text) {};

  default void beforeAddCookie(WebDriver.Options options, Cookie cookie) {};
  default void afterAddCookie(WebDriver.Options options, Cookie cookie) {};

  default void beforeDeleteCookieNamed(WebDriver.Options options, String name) {};
  default void afterDeleteCookieNamed(WebDriver.Options options, String name) {};

  default void beforeDeleteCookie(WebDriver.Options options, Cookie cookie) {};
  default void afterDeleteCookie(WebDriver.Options options, Cookie cookie) {};

  default void beforeDeleteAllCookies(WebDriver.Options options) {};
  default void afterDeleteAllCookies(WebDriver.Options options) {};

  default void beforeGetCookies(WebDriver.Options options) {};
  default void afterGetCookies(Set<Cookie> result, WebDriver.Options options) {};

  default void beforeGetCookieNamed(WebDriver.Options options, String name) {};
  default void afterGetCookieNamed(Cookie result, WebDriver.Options options, String name) {};

  default void beforeImplicitlyWait(WebDriver.Timeouts timeouts, long timeout, TimeUnit timeUnit) {};
  default void afterImplicitlyWait(WebDriver.Timeouts timeouts, long timeout, TimeUnit timeUnit) {};

  default void beforeSetScriptTimeout(WebDriver.Timeouts timeouts, long timeout, TimeUnit timeUnit) {};
  default void afterSetScriptTimeout(WebDriver.Timeouts timeouts, long timeout, TimeUnit timeUnit) {};

  default void beforePageLoadTimeout(WebDriver.Timeouts timeouts, long timeout, TimeUnit timeUnit) {};
  default void afterPageLoadTimeout(WebDriver.Timeouts timeouts, long timeout, TimeUnit timeUnit) {};

  default void beforeGetSize(WebDriver.Window window) {};
  default void afterGetSize(Dimension result, WebDriver.Window window) {};

  default void beforeSetSize(WebDriver.Window window, Dimension size) {};
  default void afterSetSize(WebDriver.Window window, Dimension size) {};

  default void beforeGetPosition(WebDriver.Window window) {};
  default void afterGetPosition(Point result, WebDriver.Window window) {};

  default void beforeSetPosition(WebDriver.Window window, Point position) {};
  default void afterSetPosition(WebDriver.Window window, Point position) {};

  default void beforeMaximize(WebDriver.Window window) {};
  default void afterMaximize(WebDriver.Window window) {};

  default void beforeSendKeys(Keyboard keyboard, CharSequence... keysToSend) {};
  default void afterSendKeys(Keyboard keyboard, CharSequence... keysToSend) {};

  default void beforePressKey(Keyboard keyboard, CharSequence keysToSend) {};
  default void afterPressKey(Keyboard keyboard, CharSequence keysToSend) {};

  default void beforeReleaseKey(Keyboard keyboard, CharSequence keysToSend) {};
  default void afterReleaseKey(Keyboard keyboard, CharSequence keysToSend) {};

  default void beforeClick(Mouse mouse, Coordinates coordinates) {};
  default void afterClick(Mouse mouse, Coordinates coordinates) {};

  default void beforeDoubleClick(Mouse mouse, Coordinates coordinates) {};
  default void afterDoubleClick(Mouse mouse, Coordinates coordinates) {};

  default void beforeContextClick(Mouse mouse, Coordinates coordinates) {};
  default void afterContextClick(Mouse mouse, Coordinates coordinates) {};

  default void beforeMouseDown(Mouse mouse, Coordinates coordinates) {};
  default void afterMouseDown(Mouse mouse, Coordinates coordinates) {};

  default void beforeMouseUp(Mouse mouse, Coordinates coordinates) {};
  default void afterMouseUp(Mouse mouse, Coordinates coordinates) {};

  default void beforeMouseMove(Mouse mouse, Coordinates coordinates) {};
  default void afterMouseMove(Mouse mouse, Coordinates coordinates) {};

  default void beforeMouseMove(Mouse mouse, Coordinates coordinates, long x, long y) {};
  default void afterMouseMove(Mouse mouse, Coordinates coordinates, long x, long y) {};

  default void beforeSingleTap(TouchScreen touchScreen, Coordinates coordinates) {};
  default void afterSingleTap(TouchScreen touchScreen, Coordinates coordinates) {};

  default void beforeDoubleTap(TouchScreen touchScreen, Coordinates coordinates) {};
  default void afterDoubleTap(TouchScreen touchScreen, Coordinates coordinates) {};

  default void beforeLongPress(TouchScreen touchScreen, Coordinates coordinates) {};
  default void afterLongPress(TouchScreen touchScreen, Coordinates coordinates) {};

  default void beforeDown(TouchScreen touchScreen, long x, long y) {};
  default void afterDown(TouchScreen touchScreen, long x, long y) {};

  default void beforeUp(TouchScreen touchScreen, long x, long y) {};
  default void afterUp(TouchScreen touchScreen, long x, long y) {};

  default void beforeMove(TouchScreen touchScreen, long x, long y) {};
  default void afterMove(TouchScreen touchScreen, long x, long y) {};

  default void beforeScroll(TouchScreen touchScreen, long x, long y) {};
  default void afterScroll(TouchScreen touchScreen, long x, long y) {};

  default void beforeScroll(TouchScreen touchScreen, Coordinates coordinates, long x, long y) {};
  default void afterScroll(TouchScreen touchScreen, Coordinates coordinates, long x, long y) {};

  default void beforeFlick(TouchScreen touchScreen, long x, long y) {};
  default void afterFlick(TouchScreen touchScreen, long x, long y) {};

  default void beforeFlick(TouchScreen touchScreen, Coordinates coordinates, long x, long y) {};
  default void afterFlick(TouchScreen touchScreen, Coordinates coordinates, long x, long y) {};
}
