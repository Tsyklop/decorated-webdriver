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

package ru.stqa.selenium.decorated;

import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.logging.Logs;

import java.util.Set;

public class DecoratedOptions extends DecoratedChild<WebDriver.Options, DecoratedWebDriver> implements WebDriver.Options {

  public DecoratedOptions(final WebDriver.Options options, final DecoratedWebDriver driverWrapper) {
    super(options, driverWrapper);
  }

  @Override
  public void addCookie(Cookie cookie) {
    getOriginal().addCookie(cookie);
  }

  @Override
  public void deleteCookieNamed(String name) {
    getOriginal().deleteCookieNamed(name);
  }

  @Override
  public void deleteCookie(Cookie cookie) {
    getOriginal().deleteCookie(cookie);
  }

  @Override
  public void deleteAllCookies() {
    getOriginal().deleteAllCookies();
  }

  @Override
  public Set<Cookie> getCookies() {
    return getOriginal().getCookies();
  }

  @Override
  public Cookie getCookieNamed(String name) {
    return getOriginal().getCookieNamed(name);
  }

  @Override
  public WebDriver.Timeouts timeouts() {
    return new Activator<WebDriver.Timeouts>().activate(getTopmostDecorated().createDecorated(getOriginal().timeouts()));
  }

  @Override
  public WebDriver.ImeHandler ime() {
    return getOriginal().ime();
  }

  @Override
  public WebDriver.Window window() {
    return new Activator<WebDriver.Window>().activate(getTopmostDecorated().createDecorated(getOriginal().window()));
  }

  @Override
  public Logs logs() {
    return getOriginal().logs();
  }
}
