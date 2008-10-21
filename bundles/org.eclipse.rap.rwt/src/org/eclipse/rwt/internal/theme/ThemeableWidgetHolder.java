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


public class ThemeableWidgetHolder {

  private final Map themeableWidgets;

  public ThemeableWidgetHolder() {
    themeableWidgets = new HashMap();
  }

  public void add( final ThemeableWidget widget ) {
    themeableWidgets.put( widget.widget, widget );
  }

  public ThemeableWidget get( final Class widget ) {
    return ( ThemeableWidget )themeableWidgets.get( widget );
  }

  public ThemeableWidget[] getAll() {
    Collection values = themeableWidgets.values();
    int size = values.size();
    ThemeableWidget[] result = new ThemeableWidget[ size ];
    values.toArray( result );
    return result;
  }
}
