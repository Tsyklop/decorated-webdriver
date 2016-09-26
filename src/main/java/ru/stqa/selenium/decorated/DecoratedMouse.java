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

import org.openqa.selenium.interactions.Mouse;
import org.openqa.selenium.interactions.internal.Coordinates;

public class DecoratedMouse extends DecoratedChild<Mouse,DecoratedWebDriver> implements Mouse {

  public DecoratedMouse(final Mouse mouse, final DecoratedWebDriver driverWrapper) {
    super(mouse, driverWrapper);
  }

  @Override
  public void click(Coordinates coordinates) {
    getOriginal().click(coordinates);
  }

  @Override
  public void doubleClick(Coordinates coordinates) {
    getOriginal().doubleClick(coordinates);
  }

  @Override
  public void mouseDown(Coordinates coordinates) {
    getOriginal().mouseDown(coordinates);
  }

  @Override
  public void mouseUp(Coordinates coordinates) {
    getOriginal().mouseUp(coordinates);
  }

  @Override
  public void mouseMove(Coordinates coordinates) {
    getOriginal().mouseMove(coordinates);
  }

  @Override
  public void mouseMove(Coordinates coordinates, long x, long y) {
    getOriginal().mouseMove(coordinates, x, y);
  }

  @Override
  public void contextClick(Coordinates coordinates) {
    getOriginal().contextClick(coordinates);
  }
}
