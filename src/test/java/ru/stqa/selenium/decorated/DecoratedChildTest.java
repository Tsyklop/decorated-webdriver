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
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;

public class DecoratedChildTest {

  interface Target {
    String hello(String who);
  }

  static class DecoratedTarget extends DecoratedChild<Target, DecoratedTopmost<?>> implements Target {

    public DecoratedTarget(Target original, DecoratedTopmost<?> topmost) {
      super(original, topmost);
    }

    @Override
    public String hello(String who) {
      return getOriginal().hello(who);
    }
  }

  static class TargetFixture {

    DecoratedTarget deco;
    DecoratedTopmost<Object> topmost;

    public TargetFixture(Target target) {
      topmost = spy(new DecoratedTopmost<Object>(mock(Object.class)){});
      deco = new DecoratedTarget(target, topmost);
    }
  }

  @Test
  public void testDelegatesToTopmost() throws Throwable {
    Target target = mock(Target.class);
    TargetFixture fixture = new TargetFixture(target);
    when(target.hello("world")).thenReturn("test");
    Target decorated = new Activator<Target>().activate(fixture.deco);

    assertThat(decorated.hello("world"), equalTo("test"));

    InOrder inOrder = inOrder(fixture.topmost);
    inOrder.verify(fixture.topmost, times(1)).beforeMethodGlobal(same(fixture.deco),
      argThat(m -> m.getName().equals("hello")), eq(new Object[]{"world"}));
    inOrder.verify(fixture.topmost, times(1)).callMethodGlobal(same(fixture.deco),
      argThat(m -> m.getName().equals("hello")), eq(new Object[]{"world"}));
    inOrder.verify(fixture.topmost, times(1)).afterMethodGlobal(same(fixture.deco),
      argThat(m -> m.getName().equals("hello")), eq("test"), eq(new Object[]{"world"}));
    verifyNoMoreInteractions(fixture.topmost);
  }

  @Test
  public void testCanPropagateExceptions() throws Throwable {
    Target target = mock(Target.class);
    TargetFixture fixture = new TargetFixture(target);
    when(target.hello("world")).thenThrow(WebDriverException.class);
    Target decorated = new Activator<Target>().activate(fixture.deco);

    boolean thrown = false;
    try {
      decorated.hello("world");
    } catch (Throwable expected) {
      thrown = true;
      assertThat(expected, instanceOf(WebDriverException.class));
    }
    assertTrue(thrown);

    InOrder inOrder = inOrder(fixture.topmost);
    inOrder.verify(fixture.topmost, times(1)).beforeMethodGlobal(same(fixture.deco),
      argThat(m -> m.getName().equals("hello")), eq(new Object[]{"world"}));
    inOrder.verify(fixture.topmost, times(1)).callMethodGlobal(same(fixture.deco),
      argThat(m -> m.getName().equals("hello")), eq(new Object[]{"world"}));
    inOrder.verify(fixture.topmost, times(1)).onErrorGlobal(same(fixture.deco),
      argThat(m -> m.getName().equals("hello")),
      argThat((InvocationTargetException ex) -> ex.getTargetException() instanceof WebDriverException),
      eq(new Object[]{"world"}));
    verifyNoMoreInteractions(fixture.topmost);
  }

}
