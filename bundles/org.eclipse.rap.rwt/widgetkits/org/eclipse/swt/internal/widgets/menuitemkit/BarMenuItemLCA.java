/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.menuitemkit;

import java.io.IOException;

import org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil;
import org.eclipse.swt.widgets.MenuItem;


final class BarMenuItemLCA extends MenuItemDelegateLCA {

  void preserveValues( MenuItem item ) {
    MenuItemLCAUtil.preserveValues( item );
  }

  void readData( MenuItem item ) {
    WidgetLCAUtil.processHelp( item );
  }

  void renderInitialization( MenuItem item ) throws IOException {
    MenuItemLCAUtil.renderInitialization( item );
  }

  void renderChanges( MenuItem item ) throws IOException {
    MenuItemLCAUtil.renderChanges( item );
  }
}
