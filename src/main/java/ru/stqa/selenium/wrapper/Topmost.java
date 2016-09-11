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

package ru.stqa.selenium.wrapper;

import com.google.common.base.Throwables;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public interface Topmost {
  default void beforeMethodGlobal(Decorated<?> target, Method method, Object[] args) {};
  default Object callMethodGlobal(Decorated<?> target, Method method, Object[] args) throws Throwable {
    return method.invoke(target, args);
  };
  default void afterMethodGlobal(Decorated<?> target, Method method, Object res, Object[] args) {};
  default Object onErrorGlobal(Decorated<?> target, Method method, InvocationTargetException e, Object[] args) throws Throwable {
    throw Throwables.propagate(e.getTargetException());
  };
}
