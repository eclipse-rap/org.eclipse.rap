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
import org.eclipse.rap.rwt.lifecycle.*;
import org.eclipse.rap.rwt.widgets.ToolItem;

final class SeparatorToolItemDelegateLCA extends ToolItemDelegateLCA {

  private static final String PROP_CONTROL = "control";
  
  // tool item functions as defined in org.eclipse.rap.rwt.ToolItemUtil
  private static final String CREATE_SEPARATOR = 
    "org.eclipse.rap.rwt.ToolItemUtil.createSeparator";
  private static final String SET_CONTROL_FOR_SEPARATOR = 
    "org.eclipse.rap.rwt.ToolItemUtil.setControlForSeparator";

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
      Boolean.valueOf( ( toolItem.getParent().getStyle() & RWT.FLAT  ) != 0 )
    };
    writer.callStatic( CREATE_SEPARATOR, args );
  }

  void renderChanges( final ToolItem toolItem ) throws IOException {
    final JSWriter writer = JSWriter.getWriterFor( toolItem );
    // TODO [rst] If control is set to null, this change must also be rendered!
    if( toolItem.getControl() != null ) {
      IWidgetAdapter adapter = WidgetUtil.getAdapter( toolItem.getControl() );
      // TODO [rst] Control is now preserved in ToolItemLCA - change this?
      if( WidgetLCAUtil.hasChanged( toolItem,
                                    PROP_CONTROL,
                                    toolItem.getControl(),
                                    null ) )
      {
        final Object[] args = new Object[]{
          WidgetUtil.getId( toolItem ),
          toolItem.getParent(),
          new Integer( toolItem.getWidth() ),
          toolItem.getControl()
        };
        adapter.setRenderRunnable( new IRenderRunnable() {
          public void afterRender() throws IOException {
            writer.callStatic( SET_CONTROL_FOR_SEPARATOR, args );
          }
        } );
      }
    }
  }
}
