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
package org.eclipse.swt.internal.widgets.toolbarkit;

import java.io.IOException;

import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.Widget;


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

  public void renderInitialization( final Widget widget ) throws IOException {
    ToolBar toolBar = ( ToolBar )widget;
    JSWriter writer = JSWriter.getWriterFor( toolBar );
    writer.newWidget( "qx.ui.toolbar.ToolBar" );
    if( ( toolBar.getStyle() & SWT.VERTICAL ) != 0 ){
      writer.set( JSConst.QX_FIELD_ORIENTATION, 
                  JSConst.QX_CONST_VERTICAL_ORIENTATION );
    }
    ControlLCAUtil.writeStyleFlags( toolBar );
  }
  
  public void renderChanges( final Widget widget ) throws IOException {
    ToolBar toolBar = ( ToolBar )widget;
    ControlLCAUtil.writeChanges( toolBar );
  }

  public void renderDispose( final  Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.dispose();
  }
  
  public void createResetHandlerCalls( final String typePoolId ) 
    throws IOException 
  {
  }
  
  public String getTypePoolId( final Widget widget ) {
    return null;
  }
}
