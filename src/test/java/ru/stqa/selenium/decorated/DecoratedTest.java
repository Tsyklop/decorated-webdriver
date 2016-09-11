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
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.openqa.selenium.WebDriverException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;

public class DecoratedTest {

  static class Fixture<T> {

    AbstractDecorated<T> deco;
    Topmost topmost;

    public Fixture(T original) {
      topmost = mock(Topmost.class);
      deco = new AbstractDecorated<T>(original) {
        @Override
        public Topmost getTopmostDecorated() {
          return topmost;
        }
      };
    }
  }

  @Test
  public void testConstructor() {
    Object obj = new Object();
    Fixture<Object> fixture = new Fixture<Object>(obj);
    assertThat(fixture.deco.getOriginal(), sameInstance(obj));
  }

  @Test
  public void testSetOriginal() {
    Fixture<String> fixture = new Fixture<String>("test");
    fixture.deco.setOriginal("diff");
    assertThat(fixture.deco.getOriginal(), equalTo("diff"));
  }

  @Test
  public void testToString() {
    Fixture<String> fixture = new Fixture<String>("test");
    assertThat(fixture.deco.toString(), equalTo("Decorated {test}"));
  }

  @Test
  public void testEquals() {
    Fixture<String> fixture1 = new Fixture<String>("test");
    Fixture<String> fixture2 = new Fixture<String>("test");
    assertThat(fixture1.deco, equalTo(fixture2.deco));
  }

  @Test
  public void testEqualToOriginal() {
    Fixture<String> fixture = new Fixture<String>("test");
    assertEquals(fixture.deco, "test");
  }

  @Test
  public void testHashCode() {
    Fixture<String> fixture = new Fixture<String>("test");
    assertEquals(fixture.deco.hashCode(), "test".hashCode());
  }

  interface Target {
    String hello(String who);
  }

  static class DecoratedTarget extends AbstractDecorated<Target> implements Target {

    private final Topmost topmost;

    public DecoratedTarget(Target original, Topmost topmost) {
      super(original);
      this.topmost = topmost;
    }

    @Override
    public Topmost getTopmostDecorated() {
      return topmost;
    }

    @Override
    public String hello(String who) {
      return getOriginal().hello(who);
    }
  }

  static class TargetFixture {

    DecoratedTarget deco;
    Topmost topmost;

    public TargetFixture(Target target) {
      topmost = mock(Topmost.class);
      deco = new DecoratedTarget(target, topmost);
    }
  }

  @Test
  public void testDelegatesToTopmost() throws Throwable {
    Target target = mock(Target.class);
    TargetFixture fixture = new TargetFixture(target);
    when(target.hello("world")).thenReturn("test");
    when(fixture.topmost.callMethodGlobal(any(Decorated.class), any(Method.class), any(Object[].class))).thenCallRealMethod();
    Target decorated = new Decorator<Target>().activate(fixture.deco);

    ArgumentCaptor<Decorated> tBefore = ArgumentCaptor.forClass(Decorated.class);
    ArgumentCaptor<Decorated> tCall = ArgumentCaptor.forClass(Decorated.class);
    ArgumentCaptor<Decorated> tAfter = ArgumentCaptor.forClass(Decorated.class);
    ArgumentCaptor<Method> mBefore = ArgumentCaptor.forClass(Method.class);
    ArgumentCaptor<Method> mCall = ArgumentCaptor.forClass(Method.class);
    ArgumentCaptor<Method> mAfter = ArgumentCaptor.forClass(Method.class);
    ArgumentCaptor<Object[]> argsBefore = ArgumentCaptor.forClass(Object[].class);
    ArgumentCaptor<Object[]> argsCall = ArgumentCaptor.forClass(Object[].class);
    ArgumentCaptor<Object[]> argsAfter = ArgumentCaptor.forClass(Object[].class);
    ArgumentCaptor<Object> resAfter = ArgumentCaptor.forClass(Object.class);

    assertThat(decorated.hello("world"), equalTo("test"));

    InOrder inOrder = inOrder(fixture.topmost);
    inOrder.verify(fixture.topmost, times(1)).beforeMethodGlobal(tBefore.capture(), mBefore.capture(), argsBefore.capture());
    inOrder.verify(fixture.topmost, times(1)).callMethodGlobal(tCall.capture(), mCall.capture(), argsCall.capture());
    inOrder.verify(fixture.topmost, times(1)).afterMethodGlobal(tAfter.capture(), mAfter.capture(), resAfter.capture(), argsAfter.capture());
    verify(fixture.topmost, times(0)).onErrorGlobal(any(Decorated.class), any(Method.class), any(InvocationTargetException.class), any(Object[].class));

    assertThat(tBefore.getValue(), sameInstance(fixture.deco));
    assertThat(tCall.getValue(), sameInstance(fixture.deco));
    assertThat(tAfter.getValue(), sameInstance(fixture.deco));
    assertThat(mBefore.getValue().getName(), equalTo("hello"));
    assertThat(mCall.getValue().getName(), equalTo("hello"));
    assertThat(mAfter.getValue().getName(), equalTo("hello"));
    assertThat(argsBefore.getValue(), equalTo(new Object[]{"world"}));
    assertThat(argsCall.getValue(), equalTo(new Object[]{"world"}));
    assertThat(argsAfter.getValue(), equalTo(new Object[]{"world"}));
    assertThat(resAfter.getValue(), equalTo("test"));
  }

  @Test
  public void testCanPropagateExceptions() throws Throwable {
    Target target = mock(Target.class);
    TargetFixture fixture = new TargetFixture(target);
    when(target.hello("world")).thenThrow(WebDriverException.class);
    when(fixture.topmost.callMethodGlobal(any(Decorated.class), any(Method.class), any(Object[].class))).thenCallRealMethod();
    when(fixture.topmost.onErrorGlobal(any(Decorated.class), any(Method.class), any(InvocationTargetException.class), any(Object[].class))).thenCallRealMethod();
    Target decorated = new Decorator<Target>().activate(fixture.deco);

    ArgumentCaptor<Decorated> tBefore = ArgumentCaptor.forClass(Decorated.class);
    ArgumentCaptor<Decorated> tCall = ArgumentCaptor.forClass(Decorated.class);
    ArgumentCaptor<Decorated> tAfter = ArgumentCaptor.forClass(Decorated.class);
    ArgumentCaptor<Method> mBefore = ArgumentCaptor.forClass(Method.class);
    ArgumentCaptor<Method> mCall = ArgumentCaptor.forClass(Method.class);
    ArgumentCaptor<Method> mAfter = ArgumentCaptor.forClass(Method.class);
    ArgumentCaptor<Object[]> argsBefore = ArgumentCaptor.forClass(Object[].class);
    ArgumentCaptor<Object[]> argsCall = ArgumentCaptor.forClass(Object[].class);
    ArgumentCaptor<Object[]> argsAfter = ArgumentCaptor.forClass(Object[].class);
    ArgumentCaptor<InvocationTargetException> exAfter = ArgumentCaptor.forClass(InvocationTargetException.class);

    boolean thrown = false;
    try {
      decorated.hello("world");
    } catch (Throwable expected) {
      thrown = true;
      assertThat(expected, instanceOf(WebDriverException.class));
    }
    assertTrue(thrown);

    InOrder inOrder = inOrder(fixture.topmost);
    inOrder.verify(fixture.topmost, times(1)).beforeMethodGlobal(tBefore.capture(), mBefore.capture(), argsBefore.capture());
    inOrder.verify(fixture.topmost, times(1)).callMethodGlobal(tCall.capture(), mCall.capture(), argsCall.capture());
    inOrder.verify(fixture.topmost, times(1)).onErrorGlobal(tAfter.capture(), mAfter.capture(), exAfter.capture(), argsAfter.capture());
    verify(fixture.topmost, times(0)).afterMethodGlobal(any(Decorated.class), any(Method.class), any(Object.class), any(Object[].class));


    assertThat(tBefore.getValue(), sameInstance(fixture.deco));
    assertThat(tCall.getValue(), sameInstance(fixture.deco));
    assertThat(tAfter.getValue(), sameInstance(fixture.deco));
    assertThat(mBefore.getValue().getName(), equalTo("hello"));
    assertThat(mCall.getValue().getName(), equalTo("hello"));
    assertThat(mAfter.getValue().getName(), equalTo("hello"));
    assertThat(argsBefore.getValue(), equalTo(new Object[]{"world"}));
    assertThat(argsCall.getValue(), equalTo(new Object[]{"world"}));
    assertThat(argsAfter.getValue(), equalTo(new Object[]{"world"}));
    assertThat(exAfter.getValue().getTargetException(), instanceOf(WebDriverException.class));
  }

}
