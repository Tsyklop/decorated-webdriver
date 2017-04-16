/*
 * Copyright 2013 Alexei Barantsev
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
package ru.stqa.selenium.logging;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.internal.BuildInfo;
import org.openqa.selenium.logging.LogEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.stqa.selenium.decorated.Decorated;
import ru.stqa.selenium.decorated.DecoratedWebDriver;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class LoggingWebDriver extends DecoratedWebDriver {

  private static Logger DRIVER_LOG = LoggerFactory.getLogger("WebDriver");
  private static Logger BROWSER_LOG = LoggerFactory.getLogger("Browser");

  private boolean dumpBrowserLogs = false;

  public LoggingWebDriver(final WebDriver driver) {
    super(driver);
    DRIVER_LOG.info("Init tracer for {}, driver {}", new BuildInfo(), driver.getClass().getName());
  }

  public void setDumpBrowserLogs(boolean dumpBrowserLogs) {
    this.dumpBrowserLogs = dumpBrowserLogs;
  }

  @Override
  public void beforeMethodGlobal(Decorated<?> target, Method method, Object[] args) {
    StringBuilder message = new StringBuilder();
    message.append("-> ");
    appendMethod(message, method, args);
    message.append(" on ").append(target.getOriginal());
    DRIVER_LOG.info(message.toString());

    super.beforeMethodGlobal(target, method, args);
  }

  @Override
  public void afterMethodGlobal(Decorated<?> target, Method method, Object res, Object[] args) {
    StringBuilder message = new StringBuilder();
    message.append("<- ");
    appendMethod(message, method, args);
    message.append(" = ");
    if (res instanceof String) {
        message.append("\"").append(res).append("\"");
    } else {
      message.append(res);
    }
    message.append(" on ").append(target.getOriginal());
    DRIVER_LOG.info(message.toString());

    if (dumpBrowserLogs) {
      dumpBrowserLogs(getOriginal());
    }

    super.afterMethodGlobal(target, method, res, args);
  }

  @Override
  public Object onErrorGlobal(Decorated<?> target, Method method, InvocationTargetException e, Object[] args) throws Throwable {
    Throwable te = e.getTargetException();
    StringBuilder message = new StringBuilder();
    message.append(">< ");
    appendMethod(message, method, args);
    message.append(" on ").append(target.getOriginal());
    DRIVER_LOG.info(message.toString(), te);

    if (dumpBrowserLogs) {
      dumpBrowserLogs(getOriginal());
    }

    return super.onErrorGlobal(target, method, e, args);
  }

  private void appendMethod(StringBuilder message, Method method, Object[] args) {
    message.append(method.getName()).append("(");
    if (args != null && args.length > 0) {
      for (int i = 0; i < args.length; i++) {
        if (i > 0) {
          message.append(", ");
        }
        if (args[i] instanceof String) {
          message.append("\"").append(args[i]).append("\"");
        } else {
          message.append(args[i]);
        }
      }
    }
    message.append(")");
  }

  private void dumpBrowserLogs(WebDriver driver) {
    try {
      for (LogEntry logEntry : driver.manage().logs().get("browser").getAll()) {
        BROWSER_LOG.debug("" + logEntry);
      }
    } catch (Throwable e) {
    }
  }

}
