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

import org.openqa.selenium.WebDriver;

import java.net.URL;

public class DecoratedNavigation extends DecoratedChild<WebDriver.Navigation, DecoratedWebDriver> implements WebDriver.Navigation {

  public DecoratedNavigation(final WebDriver.Navigation navigator, final DecoratedWebDriver driverWrapper) {
    super(navigator, driverWrapper);
  }

  @Override
  public void to(String url) {
    getOriginal().to(url);
  }

  @Override
  public void to(URL url) {
    getOriginal().to(url);
  }

  @Override
  public void back() {
    getOriginal().back();
  }

  @Override
  public void forward() {
    getOriginal().forward();
  }

  @Override
  public void refresh() {
    getOriginal().refresh();
  }
}
