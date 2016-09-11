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
 */

package ru.stqa.selenium.decorated;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public abstract class AbstractDecoratedTopmost<T> extends AbstractDecorated<T> implements DecoratedTopmost {

  public AbstractDecoratedTopmost(T original) {
    super(original);
  }

  @Override
  public void beforeMethod(Method method, Object[] args) {
    beforeMethodGlobal(this, method, args);
  }

  @Override
  public Object callMethod(Method method, Object[] args) throws Throwable {
    return callMethodGlobal(this, method, args);
  }

  @Override
  public void afterMethod(Method method, Object res, Object[] args) {
    afterMethodGlobal(this, method, unwrap(res), args);
  }

  @Override
  public Object onError(Method method, InvocationTargetException e, Object[] args) throws Throwable {
    return onErrorGlobal(this, method, e, args);
  }

}
