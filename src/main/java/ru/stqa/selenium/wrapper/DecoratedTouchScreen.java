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

package ru.stqa.selenium.wrapper;

import org.openqa.selenium.interactions.TouchScreen;
import org.openqa.selenium.interactions.internal.Coordinates;

public class DecoratedTouchScreen extends DecoratedByReflection<TouchScreen> implements TouchScreen {

  public DecoratedTouchScreen(final DecoratedWebDriver driverWrapper, final TouchScreen touchScreen) {
    super(driverWrapper, touchScreen);
  }

  @Override
  public void singleTap(Coordinates coordinates) {
    getOriginal().singleTap(coordinates);
  }

  @Override
  public void down(int x, int y) {
    getOriginal().down(x, y);
  }

  @Override
  public void up(int x, int y) {
    getOriginal().up(x, y);
  }

  @Override
  public void move(int x, int y) {
    getOriginal().move(x, y);
  }

  @Override
  public void scroll(Coordinates coordinates, int x, int y) {
    getOriginal().scroll(coordinates, x, y);
  }

  @Override
  public void doubleTap(Coordinates coordinates) {
    getOriginal().doubleTap(coordinates);
  }

  @Override
  public void longPress(Coordinates coordinates) {
    getOriginal().longPress(coordinates);
  }

  @Override
  public void scroll(int x, int y) {
    getOriginal().scroll(x, y);
  }

  @Override
  public void flick(int xSpeed, int ySpeed) {
    getOriginal().flick(xSpeed, ySpeed);
  }

  @Override
  public void flick(Coordinates coordinates, int x, int y, int speed) {
    getOriginal().flick(coordinates, x, y, speed);
  }
}
