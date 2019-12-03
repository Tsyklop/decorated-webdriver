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

package ru.stqa.selenium.decorated.stale;

import com.google.common.base.Throwables;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import ru.stqa.selenium.decorated.Decorated;
import ru.stqa.selenium.decorated.DecoratedWebDriver;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class StaleTolerantWebDriver extends DecoratedWebDriver {

  public StaleTolerantWebDriver(final WebDriver driver) {
    super(driver);
  }

  protected Decorated<WebElement> createDecorated(WebElement original) {
    return new StaleTolerantWebElement(original, this);
  }

  @Override
  public void afterMethod(Method method, Object res, Object[] args) {
    afterMethodGlobal(this, method, res, args);
  }

  @Override
  public void afterMethodGlobal(Decorated<?> target, Method method, Object res, Object[] args) {
    super.afterMethodGlobal(target, method, unwrap(res), args);
    if (method.getName().equals("findElement")) {
      Rediscoverable elementWrapper = (Rediscoverable) res;
      elementWrapper.setSearchContext((SearchContext) target.getActivated());
      elementWrapper.setLocator((By) args[0]);
    }
  }

  @Override
  public Object onErrorGlobal(Decorated<?> target, Method method, InvocationTargetException e, Object[] args) throws Throwable {
    Throwable te = e.getTargetException();
    if (te instanceof StaleElementReferenceException) {
      StaleTolerantWebElement elementWrapper = (StaleTolerantWebElement) target;
      try {
        WebElement newElement = elementWrapper.getSearchContext().findElement(elementWrapper.getLocator());
        elementWrapper.setOriginal(newElement);
        try {
          return callMethodGlobal(target, method, args);
        } catch (InvocationTargetException e1) {
          throw Throwables.propagate(e1.getTargetException());
        }
      } catch (NoSuchElementException ex) {
        throw Throwables.propagate(te);
      }
    }
    throw Throwables.propagate(te);
  }

}
