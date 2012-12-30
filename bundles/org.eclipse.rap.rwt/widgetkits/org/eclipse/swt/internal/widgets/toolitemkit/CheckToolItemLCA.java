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
package org.eclipse.swt.internal.widgets.toolitemkit;

import java.io.IOException;

import org.eclipse.rap.rwt.internal.protocol.ClientMessageConst;
import org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil;
import org.eclipse.swt.widgets.ToolItem;


final class CheckToolItemLCA extends ToolItemDelegateLCA {

  void preserveValues( ToolItem toolItem ) {
    ToolItemLCAUtil.preserveValues( toolItem );
  }

  void readData( ToolItem toolItem ) {
    if( WidgetLCAUtil.wasEventSent( toolItem, ClientMessageConst.EVENT_SELECTION ) ) {
      String value = WidgetLCAUtil.readPropertyValue( toolItem, "selection" );
      toolItem.setSelection( Boolean.valueOf( value ).booleanValue() );
      ToolItemLCAUtil.processSelection( toolItem );
    }
  }

  void renderInitialization( ToolItem toolItem ) throws IOException {
    ToolItemLCAUtil.renderInitialization( toolItem );
  }

  void renderChanges( ToolItem toolItem ) throws IOException {
    ToolItemLCAUtil.renderChanges( toolItem );
  }
}
