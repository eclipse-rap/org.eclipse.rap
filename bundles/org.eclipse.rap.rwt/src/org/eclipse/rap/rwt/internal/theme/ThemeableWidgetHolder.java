/*******************************************************************************
 * Copyright (c) 2008, 2015 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.theme;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;


class ThemeableWidgetHolder {

  private final Map<String, ThemeableWidget> themeableWidgets;

  ThemeableWidgetHolder() {
    themeableWidgets = new LinkedHashMap<>();
  }

  void add( ThemeableWidget widget ) {
    themeableWidgets.put( widget.className, widget );
  }

  ThemeableWidget get( String className ) {
    return themeableWidgets.get( className );
  }

  ThemeableWidget[] getAll() {
    Collection<ThemeableWidget> values = themeableWidgets.values();
    return values.toArray( new ThemeableWidget[ values.size() ] );
  }

  void reset() {
    themeableWidgets.clear();
  }

}
