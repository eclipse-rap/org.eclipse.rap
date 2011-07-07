/*******************************************************************************
 * Copyright (c) 2002, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.toolitemkit;

import java.io.IOException;

import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.lifecycle.WidgetLCAUtil;
import org.eclipse.swt.widgets.ToolItem;


final class CheckToolItemLCA extends ToolItemDelegateLCA {

  private static final String PARAM_CHECK = "check";

  void preserveValues( final ToolItem toolItem ) {
    ToolItemLCAUtil.preserveValues( toolItem );
    ToolItemLCAUtil.preserveImages( toolItem );
    ToolItemLCAUtil.preserveSelection( toolItem );
  }

  void readData( final ToolItem toolItem ) {
    if( WidgetLCAUtil.wasEventSent( toolItem, JSConst.EVENT_WIDGET_SELECTED ) ) {
      String value = WidgetLCAUtil.readPropertyValue( toolItem, "selection" );
      toolItem.setSelection( Boolean.valueOf( value ).booleanValue() );
      ToolItemLCAUtil.processSelection( toolItem );
    }
  }

  void renderInitialization( final ToolItem toolItem ) throws IOException {
    ToolItemLCAUtil.renderInitialization( toolItem, PARAM_CHECK );  }

  void renderChanges( final ToolItem toolItem ) throws IOException {
    ToolItemLCAUtil.renderChanges( toolItem );
    ToolItemLCAUtil.writeSelection( toolItem );
  }
}
