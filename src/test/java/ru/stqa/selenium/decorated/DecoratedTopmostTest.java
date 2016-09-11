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

package ru.stqa.selenium.decorated;

import org.junit.Test;
import org.mockito.InOrder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class DecoratedTopmostTest {

  interface Target {
    String hello(String who);
  }

  static class DecoratedTarget extends AbstractDecoratedTopmost<Target> implements Target {

    public DecoratedTarget(Target original) {
      super(original);
    }

    @Override
    public String hello(String who) {
      return getOriginal().hello(who);
    }
  }

  static class Fixture {

    DecoratedTarget deco;

    public Fixture(Target original) {
      deco = new DecoratedTarget(original) { };
    }
  }

  @Test
  public void testDelegatesToGlobal() throws Throwable {
    Target target = mock(Target.class);
    Fixture fixture = new Fixture(target);
    when(target.hello("world")).thenReturn("test");
    AbstractDecoratedTopmost<Target> spy = spy(fixture.deco);
    Target decorated = new Decorator<Target>().activate(spy);

    assertThat(decorated.hello("world"), equalTo("test"));

    InOrder inOrder = inOrder(spy);
    inOrder.verify(spy).beforeMethod(any(Method.class), any(Object[].class));
    inOrder.verify(spy).beforeMethodGlobal(any(Decorated.class), any(Method.class), any(Object[].class));
    inOrder.verify(spy).callMethod(any(Method.class), any(Object[].class));
    inOrder.verify(spy).callMethodGlobal(any(Decorated.class), any(Method.class), any(Object[].class));
    inOrder.verify(spy).afterMethod(any(Method.class), any(), any(Object[].class));
    inOrder.verify(spy).afterMethodGlobal(any(Decorated.class), any(Method.class), any(), any(Object[].class));
  }

  @Test
  public void testDelegatesExceptionToGlobal() throws Throwable {
    Target target = mock(Target.class);
    Fixture fixture = new Fixture(target);
    when(target.hello("world")).thenThrow(RuntimeException.class);
    AbstractDecoratedTopmost<Target> spy = spy(fixture.deco);
    Target decorated = new Decorator<Target>().activate(spy);

    boolean thrown = false;
    try {
      decorated.hello("world");
    } catch (RuntimeException expected) {
      thrown = true;
    }
    assertTrue(thrown);

    InOrder inOrder = inOrder(spy);
    inOrder.verify(spy).beforeMethod(any(Method.class), any(Object[].class));
    inOrder.verify(spy).beforeMethodGlobal(any(Decorated.class), any(Method.class), any(Object[].class));
    inOrder.verify(spy).callMethod(any(Method.class), any(Object[].class));
    inOrder.verify(spy).callMethodGlobal(any(Decorated.class), any(Method.class), any(Object[].class));
    inOrder.verify(spy).onError(any(Method.class), any(InvocationTargetException.class), any(Object[].class));
    inOrder.verify(spy).onErrorGlobal(any(Decorated.class), any(Method.class), any(InvocationTargetException.class), any(Object[].class));
  }

}
