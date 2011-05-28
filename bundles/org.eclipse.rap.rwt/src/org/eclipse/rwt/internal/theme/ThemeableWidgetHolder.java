/*******************************************************************************
 * Copyright (c) 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.theme;

import java.util.*;


class ThemeableWidgetHolder {
  private final Map themeableWidgets;

  ThemeableWidgetHolder() {
    themeableWidgets = new LinkedHashMap();
  }

  void add( final ThemeableWidget widget ) {
    themeableWidgets.put( widget.widget, widget );
  }

  ThemeableWidget get( final Class widget ) {
    return ( ThemeableWidget )themeableWidgets.get( widget );
  }

  ThemeableWidget[] getAll() {
    Collection values = themeableWidgets.values();
    ThemeableWidget[] result = new ThemeableWidget[ values.size() ];
    values.toArray( result );
    return result;
  }
  
  void reset() {
    themeableWidgets.clear();
  }
}