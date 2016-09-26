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

package ru.stqa.selenium.decorated.staletolerant;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;
import ru.stqa.selenium.decorated.DecoratedWebElement;

import java.lang.reflect.Method;

public class StaleTolerantWebElement extends DecoratedWebElement implements Rediscoverable {

  private SearchContext searchContext;
  private By locator;

  public StaleTolerantWebElement(WebElement element, StaleTolerantWebDriver driver) {
    super(element, driver);
  }

  @Override
  public void afterMethod(Method method, Object res, Object[] args) {
    getTopmostDecorated().afterMethodGlobal(this, method, res, args);
  }

  @Override
  public void setSearchContext(SearchContext searchContext) {
    this.searchContext = searchContext;
  }

  @Override
  public SearchContext getSearchContext() {
    return searchContext;
  }

  @Override
  public void setLocator(By locator) {
    this.locator = locator;
  }

  @Override
  public By getLocator() {
    return locator;
  }
}
