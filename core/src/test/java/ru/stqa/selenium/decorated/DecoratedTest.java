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

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;

class DecoratedTest {

  static class Fixture<T> {

    AbstractDecorated<T> deco;

    public Fixture(T original) {
      deco = new DecoratedTopmost<T>(original) { };
    }
  }

  @Test
  void testConstructor() {
    Object obj = new Object();
    Fixture<Object> fixture = new Fixture<>(obj);
    assertThat(fixture.deco.getOriginal(), sameInstance(obj));
  }

  @Test
  void testSetOriginal() {
    Fixture<String> fixture = new Fixture<>("test");
    fixture.deco.setOriginal("diff");
    assertThat(fixture.deco.getOriginal(), equalTo("diff"));
  }

  @Test
  void testToString() {
    Fixture<String> fixture = new Fixture<>("test");
    assertThat(fixture.deco.toString(), equalTo("Decorated {test}"));
  }

  @Test
  void testEquals() {
    Fixture<String> fixture1 = new Fixture<>("test");
    Fixture<String> fixture2 = new Fixture<>("test");
    assertThat(fixture1.deco, equalTo(fixture2.deco));
  }

  @Test
  void testEqualToOriginal() {
    Fixture<String> fixture = new Fixture<>("test");
    assertThat(fixture.deco, equalTo("test"));
  }

  @Test
  void testHashCode() {
    Fixture<String> fixture = new Fixture<>("test");
    assertThat(fixture.deco.hashCode(), equalTo("test".hashCode()));
  }

  @Test
  void testUnwrapUnwrapped() {
    Fixture<String> fixture = new Fixture<>("");
    Object obj = new Object();
    assertThat(fixture.deco.unwrap(obj), sameInstance(obj));

  }

  static class DecoratedString extends DecoratedTopmost<String> {
    DecoratedString(String original) {
      super(original);
    }
  }

  @Test
  void testUnwrapWrapped() {
    Fixture<String> fixture = new Fixture<>("");
    String test = "test";
    Decorated<String> decorated = new DecoratedString(test);
    assertThat(fixture.deco.unwrap(decorated), sameInstance(test));
  }

  @Test
  void testUnwrapListOfUnwrapped() {
    Fixture<String> fixture = new Fixture<>("");
    String test = "test";
    List<Object> list = new ArrayList<>();
    list.add(test);
    List<Object> unwrapped = (List<Object>) fixture.deco.unwrap(list);
    assertThat(unwrapped.size(), equalTo(1));
    assertThat(unwrapped.get(0), sameInstance(test));
  }

  @Test
  void testUnwrapListOfWrapped() {
    Fixture<String> fixture = new Fixture<>("");
    String test = "test";
    Decorated<String> decorated = new DecoratedString(test);
    List<Object> list = new ArrayList<>();
    list.add(decorated);
    List<Object> unwrapped = (List<Object>) fixture.deco.unwrap(list);
    assertThat(unwrapped.size(), equalTo(1));
    assertThat(unwrapped.get(0), sameInstance(test));
  }

}
