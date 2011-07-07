/*******************************************************************************
 * Copyright (c) 2002, 2010 Innoopract Informationssysteme GmbH.
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

import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

final class SeparatorToolItemLCA extends ToolItemDelegateLCA {

  private static final String QX_TYPE = "org.eclipse.rwt.widgets.ToolSeparator";

  private static final String PARAM_LINE_VISIBLE = "lineVisible";

  void preserveValues( final ToolItem toolItem ) {
    IWidgetAdapter adapter = WidgetUtil.getAdapter( toolItem );
    adapter.preserve( PARAM_LINE_VISIBLE,
                      Boolean.valueOf( isLineVisible( toolItem ) ) );
    ToolItemLCAUtil.preserveValues( toolItem );
  }

  void readData( final ToolItem toolItem ) {
    // do nothing
  }

  void renderInitialization( final ToolItem toolItem ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( toolItem );
    ToolBar toolBar = toolItem.getParent();
    Boolean flat = Boolean.valueOf( ( toolBar.getStyle() & SWT.FLAT  ) != 0 );
    Boolean vertical
      = Boolean.valueOf( ( toolBar.getStyle() & SWT.VERTICAL  ) != 0 );
    writer.newWidget( QX_TYPE, new Object[]{ flat, vertical } );
    writer.call( toolBar, "add", new Object[]{ toolItem } );
  }

  void renderChanges( final ToolItem toolItem ) throws IOException {
    WidgetLCAUtil.writeEnabled( toolItem, toolItem.getEnabled() );
    ToolItemLCAUtil.writeVisible( toolItem );
    ToolItemLCAUtil.writeBounds( toolItem );
    WidgetLCAUtil.writeCustomVariant( toolItem );
    writeLineVisible( toolItem );
  }

  static void writeLineVisible( final ToolItem toolItem ) throws IOException {
    boolean isLineVisible = isLineVisible( toolItem );
    Boolean defValue = Boolean.TRUE;
    Boolean actValue = Boolean.valueOf( isLineVisible );
    String prop = PARAM_LINE_VISIBLE;
    if( WidgetLCAUtil.hasChanged( toolItem, prop, actValue, defValue ) ) {
      JSWriter writer = JSWriter.getWriterFor( toolItem );
      writer.set( "lineVisible", actValue );
    }
  }

  static boolean isLineVisible( final ToolItem toolItem ) {
    return toolItem.getControl() == null;
  }
}
