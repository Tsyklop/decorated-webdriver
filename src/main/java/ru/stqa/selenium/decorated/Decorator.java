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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Set;

public class Decorator<T> {

  public final T activate(final Decorated<T> decorated) {
    final Set<Class<?>> decoratedInterfaces = extractInterfaces(decorated);

    final InvocationHandler handler = new InvocationHandler() {
      public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
          if (! decoratedInterfaces.contains(method.getDeclaringClass())) {
            return method.invoke(decorated.getOriginal(), args);
          }
          boolean isUnwrap = method.getName().equals("getOriginal");
          if (! isUnwrap) {
            decorated.beforeMethod(method, args);
          }
          Object result = decorated.callMethod(method, args);
          if (! isUnwrap) {
            decorated.afterMethod(method, result, args);
          }
          return result;

        } catch (InvocationTargetException e) {
          return decorated.onError(method, e, args);
        }
      }
    };

    Set<Class<?>> allInterfaces = extractInterfaces(decorated.getOriginal());
    allInterfaces.addAll(decoratedInterfaces);
    Class<?>[] allInterfacesArray = allInterfaces.toArray(new Class<?>[allInterfaces.size()]);

    return (T) Proxy.newProxyInstance(
      this.getClass().getClassLoader(),
      allInterfaces.toArray(allInterfacesArray),
      handler);
  }

  private static Set<Class<?>> extractInterfaces(final Object object) {
    return extractInterfaces(object.getClass());
  }

  private static Set<Class<?>> extractInterfaces(final Class<?> clazz) {
    Set<Class<?>> allInterfaces = new HashSet<Class<?>>();
    extractInterfaces(allInterfaces, clazz);

    return allInterfaces;
  }

  private static void extractInterfaces(final Set<Class<?>> collector, final Class<?> clazz) {
    if (clazz == null || Object.class.equals(clazz)) {
      return;
    }

    final Class<?>[] classes = clazz.getInterfaces();
    for (Class<?> interfaceClass : classes) {
      collector.add(interfaceClass);
      for (Class<?> superInterface : interfaceClass.getInterfaces()) {
        collector.add(superInterface);
        extractInterfaces(collector, superInterface);
      }
    }
    extractInterfaces(collector, clazz.getSuperclass());
  }

}
