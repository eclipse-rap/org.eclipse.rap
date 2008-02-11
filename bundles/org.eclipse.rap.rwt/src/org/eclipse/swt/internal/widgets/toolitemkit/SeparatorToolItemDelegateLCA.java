/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.internal.widgets.toolitemkit;

import java.io.IOException;

import org.eclipse.rwt.internal.lifecycle.IRenderRunnable;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.widgets.WidgetAdapter;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ToolItem;

final class SeparatorToolItemDelegateLCA extends ToolItemDelegateLCA {

  private static final String PROP_CONTROL = "control";
  
  // tool item functions as defined in org.eclipse.swt.ToolItemUtil
  private static final String CREATE_SEPARATOR = 
    "org.eclipse.swt.ToolItemUtil.createSeparator";
  private static final String SET_CONTROL = 
    "org.eclipse.swt.ToolItemUtil.setControl";

  void preserveValues( final ToolItem toolItem ) {
    ToolItemLCAUtil.preserveValues( toolItem );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( toolItem );
    adapter.preserve( PROP_CONTROL, toolItem.getControl() );
  }
  
  void readData( final ToolItem toolItem ) {
    // do nothing
  }
  
  void renderInitialization( final ToolItem toolItem ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( toolItem );
    Object[] args = new Object[]{
      WidgetUtil.getId( toolItem ),
      toolItem.getParent(),
      Boolean.valueOf( ( toolItem.getParent().getStyle() & SWT.FLAT  ) != 0 )
    };
    writer.callStatic( CREATE_SEPARATOR, args );
    WidgetLCAUtil.writeCustomAppearance( toolItem );
  }

  void renderChanges( final ToolItem toolItem ) throws IOException {
    WidgetLCAUtil.writeEnabled( toolItem, toolItem.getEnabled() );
    ToolItemLCAUtil.writeVisible( toolItem );
    writeControl( toolItem );
  }

  private void writeControl( final ToolItem toolItem ) throws IOException {
    final JSWriter writer = JSWriter.getWriterFor( toolItem );
    Control control = toolItem.getControl();
    if( WidgetLCAUtil.hasChanged( toolItem, PROP_CONTROL, control, null ) ) {
      final Object[] args = new Object[] { toolItem, control };
      if( control != null ) {
        // defer call since controls are rendered after items
        WidgetAdapter adapter 
          = ( WidgetAdapter )WidgetUtil.getAdapter( control );
        adapter.setRenderRunnable( new IRenderRunnable() {
          public void afterRender() throws IOException {
            writer.callStatic( SET_CONTROL, args );
          }
        } );
      } else {
        writer.callStatic( SET_CONTROL, args );        
      }
    }
  }
}
