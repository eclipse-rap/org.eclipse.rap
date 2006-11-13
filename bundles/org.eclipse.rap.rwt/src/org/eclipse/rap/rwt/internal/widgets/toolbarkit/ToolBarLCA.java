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
package org.eclipse.rap.rwt.internal.widgets.toolbarkit;

import java.io.IOException;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.events.SelectionEvent;
import org.eclipse.rap.rwt.internal.widgets.*;
import org.eclipse.rap.rwt.lifecycle.*;
import org.eclipse.rap.rwt.widgets.ToolBar;
import org.eclipse.rap.rwt.widgets.Widget;


public class ToolBarLCA extends AbstractWidgetLCA {

  public void preserveValues( final  Widget widget ) {
    ToolBar toolBar = ( ToolBar )widget;
    ControlLCAUtil.preserveValues( toolBar );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    boolean hasListener = SelectionEvent.hasListener( toolBar );
    adapter.preserve( Props.SELECTION_LISTENERS,
                      Boolean.valueOf( hasListener ) );
  }

  public void readData( final  Widget widget ) {
  }

  public void processAction( final  Widget widget ) {
  }
  
  public void renderChanges( final Widget widget ) throws IOException {
    ToolBar toolBar = ( ToolBar )widget;
    ControlLCAUtil.writeBounds( toolBar );
    ControlLCAUtil.writeToolTip( toolBar );
  }

  public void renderDispose( final  Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.dispose();
  }

  public void renderInitialization( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.newWidget( "qx.ui.toolbar.ToolBar" );
    if( ( widget.getStyle() & RWT.VERTICAL ) != 0 ){
      writer.set( "orientation", "qx.constant.Layout.ORIENTATION_VERTICAL" );
    }
  }
}
