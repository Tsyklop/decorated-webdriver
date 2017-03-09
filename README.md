webdriver-wrapper
====================

[![Run Status](https://api.shippable.com/projects/58be80a3e4b9a205009c616a/badge?branch=master)](https://app.shippable.com/projects/58be80a3e4b9a205009c616a)

The base set of classes that helps to implement WebDriver extensions using Decorator design pattern.
 
It also contains several ready-to-use extensions:
 * a wrapper that highlights elements before an action,
 * a wrapper that handles unhandled alerts,
 * a wrapper that handles "stale" elements and attempts to find them again to perform the action on the "restored" element
