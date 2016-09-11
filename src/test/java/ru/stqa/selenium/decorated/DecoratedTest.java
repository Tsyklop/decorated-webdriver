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

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class DecoratedTest {

  static class Fixture<T> {

    AbstractDecorated<T> deco;

    public Fixture(T original) {
      deco = new AbstractDecoratedTopmost<T>(original) { };
    }
  }

  @Test
  public void testConstructor() {
    Object obj = new Object();
    Fixture<Object> fixture = new Fixture<>(obj);
    assertThat(fixture.deco.getOriginal(), sameInstance(obj));
  }

  @Test
  public void testSetOriginal() {
    Fixture<String> fixture = new Fixture<>("test");
    fixture.deco.setOriginal("diff");
    assertThat(fixture.deco.getOriginal(), equalTo("diff"));
  }

  @Test
  public void testToString() {
    Fixture<String> fixture = new Fixture<>("test");
    assertThat(fixture.deco.toString(), equalTo("Decorated {test}"));
  }

  @Test
  public void testEquals() {
    Fixture<String> fixture1 = new Fixture<>("test");
    Fixture<String> fixture2 = new Fixture<>("test");
    assertThat(fixture1.deco, equalTo(fixture2.deco));
  }

  @Test
  public void testEqualToOriginal() {
    Fixture<String> fixture = new Fixture<>("test");
    assertEquals(fixture.deco, "test");
  }

  @Test
  public void testHashCode() {
    Fixture<String> fixture = new Fixture<>("test");
    assertEquals(fixture.deco.hashCode(), "test".hashCode());
  }
}
