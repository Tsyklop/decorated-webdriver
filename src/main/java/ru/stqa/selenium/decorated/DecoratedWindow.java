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

import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;

public class DecoratedWindow extends DecoratedWebDriverChild<WebDriver.Window> implements WebDriver.Window {

  public DecoratedWindow(final DecoratedWebDriver driverWrapper, final WebDriver.Window window) {
    super(driverWrapper, window);
  }

  @Override
  public void setSize(Dimension size) {
    getOriginal().setSize(size);
  }

  @Override
  public void setPosition(Point position) {
    getOriginal().setPosition(position);
  }

  @Override
  public Dimension getSize() {
    return getOriginal().getSize();
  }

  @Override
  public Point getPosition() {
    return getOriginal().getPosition();
  }

  @Override
  public void maximize() {
    getOriginal().maximize();
  }

  @Override
  public void fullscreen() {
    getOriginal().fullscreen();
  }
}
