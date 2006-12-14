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
import org.eclipse.rap.rwt.internal.widgets.IWidgetAdapter;
import org.eclipse.rap.rwt.lifecycle.*;
import org.eclipse.rap.rwt.widgets.ToolItem;
import org.eclipse.rap.rwt.widgets.Widget;

public class SeparatorToolItemDelegateLCA extends ToolItemDelegateLCA {

  public static final String SET_CONTROL_FOR_SEPARATOR_RUNNABLE
    = "setControlForSeparatorRunnable";

  // tool item functions as defined in org.eclipse.rap.rwt.ToolItemUtil
  private static final String CREATE_SEPERATOR = 
    "org.eclipse.rap.rwt.ToolItemUtil.createToolItemSeparatorUtil";
  private static final String SET_CONTROL_FOR_SEPERATOR = 
    "org.eclipse.rap.rwt.ToolItemUtil.setControlForSeparator";
  

  public void delegateProcessAction( final Widget widget ) {
  }

  public void delegateRenderInitialization( final Widget widget )
    throws IOException
  {
    JSWriter writer = JSWriter.getWriterFor( widget );
    ToolItem push = ( ToolItem )widget;
    Object[] args = new Object[]{
      WidgetUtil.getId( push ),
      push.getParent()};
    writer.callStatic( CREATE_SEPERATOR, args );
  }

  public void delegateRenderChanges( final Widget widget ) throws IOException {
    final JSWriter writer = JSWriter.getWriterFor( widget );
    ToolItem push = ( ToolItem )widget;
    final Object[] args = new Object[] {
      WidgetUtil.getId( push ),
      push.getParent(),
      new Integer( push.getWidth() ),
      push.getControl()
    };
    if( push.getControl()!= null ) {
      IWidgetAdapter adapter = WidgetUtil.getAdapter( push.getControl() );
      adapter.setRenderRunnable( new IRenderRunnable() {
        public void afterRender() throws IOException {
          writer.callStatic( SET_CONTROL_FOR_SEPERATOR, args );
        }
    } );
    }
  }
}
