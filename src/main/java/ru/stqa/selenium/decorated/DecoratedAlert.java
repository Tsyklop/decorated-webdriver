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

import org.openqa.selenium.Alert;
import org.openqa.selenium.Beta;
import org.openqa.selenium.security.Credentials;

public class DecoratedAlert extends AbstractDecoratedChild<Alert,DecoratedWebDriver> implements Alert {

  public DecoratedAlert(final Alert alert, final DecoratedWebDriver driverWrapper) {
    super(alert, driverWrapper);
  }

  @Override
  public void accept() {
    getOriginal().accept();
  }

  @Override
  @Beta
  public void authenticateUsing(Credentials creds) {
    getOriginal().authenticateUsing(creds);
  }

  @Override
  public void dismiss() {
    getOriginal().dismiss();
  }

  @Override
  public String getText() {
    return getOriginal().getText();
  }

  @Override
  public void sendKeys(String text) {
    getOriginal().sendKeys(text);
  }

  @Override
  public void setCredentials(Credentials credentials) {
    getOriginal().setCredentials(credentials);
  }
}
