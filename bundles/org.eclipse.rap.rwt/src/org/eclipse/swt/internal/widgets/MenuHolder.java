/*******************************************************************************
 * Copyright (c) 2002, 2015 Innoopract Informationssysteme GmbH and others.
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
package org.eclipse.swt.internal.widgets;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.internal.SerializableCompatibility;
import org.eclipse.swt.widgets.Menu;


public final class MenuHolder implements Iterable<Menu>, SerializableCompatibility {

  private final List<Menu> menus;

  public MenuHolder() {
    menus = new ArrayList<>();
  }

  public void addMenu( Menu menu ) {
    menus.add( menu );
  }

  public void removeMenu( Menu menu ) {
    menus.remove( menu );
  }

  public Menu[] getMenus() {
    return menus.toArray( new Menu[ 0 ] );
  }

  public int size() {
    return menus.size();
  }

  @Override
  public Iterator<Menu> iterator() {
    return menus.iterator();
  }

}
