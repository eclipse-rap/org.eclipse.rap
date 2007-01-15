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

package org.eclipse.rap.rwt.internal.widgets.toolitemkit;

import java.io.IOException;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.internal.widgets.IWidgetAdapter;
import org.eclipse.rap.rwt.lifecycle.*;
import org.eclipse.rap.rwt.widgets.ToolItem;

final class SeparatorToolItemDelegateLCA extends ToolItemDelegateLCA {

  // tool item functions as defined in org.eclipse.rap.rwt.ToolItemUtil
  private static final String CREATE_SEPERATOR = 
    "org.eclipse.rap.rwt.ToolItemUtil.createToolItemSeparator";
  private static final String SET_CONTROL_FOR_SEPERATOR = 
    "org.eclipse.rap.rwt.ToolItemUtil.setControlForSeparator";
  

  void readData( final ToolItem toolItem ) {
    // do nothing
  }
  
  void renderInitialization( final ToolItem toolItem ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( toolItem );
    Object[] args = new Object[]{
      WidgetUtil.getId( toolItem ),
      toolItem.getParent()
    };
    writer.callStatic( CREATE_SEPERATOR, args );
    if ((toolItem.getParent().getStyle() & RWT.FLAT) == 0) {
      writer.set( "visibility", false );
    }
  }

  void renderChanges( final ToolItem toolItem ) throws IOException {
    final JSWriter writer = JSWriter.getWriterFor( toolItem );
    final Object[] args = new Object[]{
      WidgetUtil.getId( toolItem ),
      toolItem.getParent(),
      new Integer( toolItem.getWidth() ),
      toolItem.getControl()
    };
    if( toolItem.getControl() != null ) {
      IWidgetAdapter adapter = WidgetUtil.getAdapter( toolItem.getControl() );
      adapter.setRenderRunnable( new IRenderRunnable() {
        public void afterRender() throws IOException {
          writer.callStatic( SET_CONTROL_FOR_SEPERATOR, args );
        }
      } );
    }
  }
}
