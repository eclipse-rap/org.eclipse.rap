/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.menukit;

import java.io.IOException;

import org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil;
import org.eclipse.swt.widgets.Menu;


final class DropDownMenuLCA extends MenuDelegateLCA {

  void preserveValues( Menu menu ) {
    MenuLCAUtil.preserveValues( menu );
  }

  void readData( Menu menu ) {
    MenuLCAUtil.readMenuEvent( menu );
    WidgetLCAUtil.processHelp( menu );
  }

  void renderInitialization( Menu menu ) throws IOException {
    MenuLCAUtil.renderInitialization( menu );
  }

  void renderChanges( Menu menu ) throws IOException {
    MenuLCAUtil.renderChanges( menu );
    MenuLCAUtil.renderUnhideItems( menu );
  }
}
