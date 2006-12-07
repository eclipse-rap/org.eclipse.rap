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

package org.eclipse.rap.rwt.internal.custom.ctabitemkit;

import java.io.IOException;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.custom.CTabItem;
import org.eclipse.rap.rwt.internal.widgets.*;
import org.eclipse.rap.rwt.lifecycle.*;
import org.eclipse.rap.rwt.widgets.Control;
import org.eclipse.rap.rwt.widgets.Widget;


public class CTabItemLCA extends AbstractWidgetLCA {
  
  public void preserveValues( final Widget widget ) {
    IWidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    CTabItem tabItem = ( CTabItem )widget;
    adapter.preserve( Props.TEXT, tabItem.getText() );
    adapter.preserve( Props.IMAGE, tabItem.getImage() );
    adapter.preserve( Props.TOOL_TIP_TEXT, tabItem.getToolTipText() );
  }
  
  public void readData( final Widget widget ) {
  }
  
  public void processAction( final Widget widget ) {
  }

  public void renderInitialization( final Widget widget ) throws IOException {
    CTabItem item = ( CTabItem )widget;
    JSWriter writer = JSWriter.getWriterFor( item );
    boolean canClose = ( item.getStyle() & RWT.CLOSE ) != 0;
    Object[] args = new Object[] { 
      WidgetUtil.getId( item ), 
      Boolean.valueOf( canClose )
    };
    writer.call( item.getParent(), "createTab", args );
    setJSParent( item );
  }
  
  public void renderChanges( final Widget widget ) throws IOException {
    CTabItem item = ( CTabItem )widget;
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.set( Props.TEXT, JSConst.QX_FIELD_LABEL, item.getText() );
    ControlLCAUtil.writeToolTip( widget, item.getToolTipText() );
    ControlLCAUtil.writeImage( item, item.getImage() );    
  }

  public void renderDispose( final Widget widget ) throws IOException {
  }

  private static void setJSParent( final CTabItem tabItem ) {
    Control control = tabItem.getControl();
    if( control != null ) {
      IWidgetAdapter itemAdapter = WidgetUtil.getAdapter( tabItem );
      StringBuffer replacementId = new StringBuffer();
      replacementId.append( itemAdapter.getId() );
      replacementId.append( "pg" );
      IWidgetAdapter controlAdapter = WidgetUtil.getAdapter( control );
      controlAdapter.setJSParent( replacementId.toString() );
    }
  }
}
