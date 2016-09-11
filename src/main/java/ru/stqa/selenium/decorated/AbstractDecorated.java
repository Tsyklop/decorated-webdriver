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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractDecorated<T> implements Decorated<T> {

  private T original;

  public AbstractDecorated(final T original) {
    this.original = original;
  }

  public final T getOriginal() {
    return original;
  }

  protected void setOriginal(final T original) {
    this.original = original;
  }

  protected <X> X activate(Decorated<X> decorated) {
    return new Decorator<X>().activate(decorated);
  }

  protected Object unwrap(Object result) {
    if (result instanceof Decorated) {
      return ((Decorated) result).getOriginal();
    }
    if (result instanceof List) {
      // TODO: Create typed list
      List<Object> newList = new ArrayList<Object>();
      for (Object o : (List) result) {
        if (o instanceof Decorated) {
          newList.add(((Decorated) o).getOriginal());
        } else {
          newList.add(o);
        }
      }
      return newList;
    }
    return result;
  }

  @Override
  public String toString() {
    return String.format("Decorated {%s}", original);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;

    if (o instanceof AbstractDecorated) {
      AbstractDecorated that = (AbstractDecorated) o;
      return original.equals(that.original);

    } else {
      return this.original.equals(o);
    }
  }

  @Override
  public int hashCode() {
    return original.hashCode();
  }
}
