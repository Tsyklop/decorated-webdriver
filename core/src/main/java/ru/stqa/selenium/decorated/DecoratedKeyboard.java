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

import org.openqa.selenium.interactions.Keyboard;

public class DecoratedKeyboard extends DecoratedChild<Keyboard, DecoratedWebDriver> implements Keyboard {

  public DecoratedKeyboard(final Keyboard keyboard, final DecoratedWebDriver driverWrapper) {
    super(keyboard, driverWrapper);
  }

  @Override
  public void sendKeys(CharSequence... charSequences) {
    getOriginal().sendKeys(charSequences);
  }

  @Override
  public void pressKey(CharSequence charSequence) {
    getOriginal().pressKey(charSequence);
  }

  @Override
  public void releaseKey(CharSequence charSequence) {
    getOriginal().releaseKey(charSequence);
  }
}
