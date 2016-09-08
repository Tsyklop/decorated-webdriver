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

import java.lang.reflect.*;
import java.util.HashSet;
import java.util.Set;

public class Decorator<T> {

  /**
   * Builds a {@link java.lang.reflect.Proxy} implementing all interfaces of original object. It will delegate calls to
   * wrapper when wrapper implements the requested method otherwise to original object.
   *
   * @param driverWrapper        the underlying driver's wrapper
   * @param original             the underlying original object
   * @param wrapperClass         the class of a wrapper
   * @return                     a proxy that wraps the original object
   */
  public T decorate(final DecoratedWebDriver driverWrapper, final T original, final Class<? extends Decorated<T>> wrapperClass) {
    Decorated<T> wrapper;
    Constructor<? extends Decorated<T>> constructor = null;
    if (driverWrapper == null) { // top level DecoratedWebDriver
      constructor = findMatchingConstructor(wrapperClass, original.getClass());
      if (constructor == null) {
        throw new Error("Wrapper class " + wrapperClass + " does not provide an appropriate constructor");
      }
      try {
        wrapper = constructor.newInstance(original);
      } catch (Exception e) {
        throw new Error("Can't create a new wrapper object", e);
      }

    } else { // enclosed wrapper
      if (wrapperClass.getEnclosingClass() != null) {
        try {
          constructor = findMatchingConstructor(wrapperClass, wrapperClass.getEnclosingClass(), original.getClass());
        } catch (Exception e) {
          throw new Error("Can't create a new wrapper object", e);
        }
      }
      if (constructor == null) {
        try {
          constructor = findMatchingConstructor(wrapperClass, DecoratedWebDriver.class, original.getClass());
        } catch (Exception e) {
          throw new Error("Can't create a new wrapper object", e);
        }
      }
      if (constructor == null) {
        throw new Error("Wrapper class " + wrapperClass + " does not provide an appropriate constructor");
      }
      try {
        wrapper = constructor.newInstance(driverWrapper, original);
      } catch (Exception e) {
        throw new Error("Can't create a new wrapper object", e);
      }
    }
    return activate(wrapper);
  }

  public final T decorate(final T original, final Class<? extends Decorated<T>> classOfDecorated) {
    Constructor<? extends Decorated<T>> constructor = findMatchingConstructor(classOfDecorated, original.getClass());
    if (constructor == null) {
      throw new Error(String.format("Class %s does not provide an appropriate constructor to decorate %s",
        classOfDecorated, original.getClass()));
    }
    final Decorated<T> decorated;
    try {
      decorated = constructor.newInstance(original);
    } catch (Exception e) {
      throw new Error("Can't create a new wrapper object", e);
    }

    return activate(decorated);
  }

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

  private static <T> Constructor<? extends Decorated<T>> findMatchingConstructor(
    Class<? extends Decorated<T>> wrapperClass, Class<?>... classes)
  {
    for (Constructor<?> ctor : wrapperClass.getConstructors()) {
      if (isMatchingConstructor(ctor, classes)) {
        return (Constructor<? extends Decorated<T>>) ctor;
      }
    }
    return null;
  }

  private static boolean isMatchingConstructor(Constructor<?> ctor, Class<?>... classes) {
    Class<?>[] parameterTypes = ctor.getParameterTypes();
    if (parameterTypes.length != classes.length) {
      return false;
    }
    for (int i = 0; i < parameterTypes.length; i++) {
      if (! parameterTypes[i].isAssignableFrom(classes[i])) {
        return false;
      }
    }
    return true;
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
