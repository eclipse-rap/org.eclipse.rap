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

package org.eclipse.rap.rwt.internal.widgets.sashkit;

import java.io.IOException;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.events.SelectionEvent;
import org.eclipse.rap.rwt.graphics.Rectangle;
import org.eclipse.rap.rwt.internal.widgets.*;
import org.eclipse.rap.rwt.lifecycle.*;
import org.eclipse.rap.rwt.widgets.*;


public class SashLCA extends AbstractWidgetLCA {
  
  public void preserveValues( final Widget widget ) {
    ControlLCAUtil.preserveValues( ( Control )widget );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    adapter.preserve( Props.SELECTION_LISTENERS, 
                      SelectionEvent.getListeners( widget ) );
  }
  
  public void readData( final Widget widget ) {
  }
  
  public void processAction( final Widget widget ) {
    ControlLCAUtil.processSelection( ( Sash )widget, null );
  }
  
  public void renderInitialization( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.newWidget( "org.eclipse.rap.rwt.Sash" );
    Sash sash = ( Sash )widget;
    if( ( sash.getStyle() & RWT.HORIZONTAL ) != 0 ) {
      writer.set( JSConst.QX_FIELD_ORIENTATION, 
                  JSConst.QX_CONST_VERTICAL_ORIENTATION );
    }
  }

  public void renderChanges( final Widget widget ) throws IOException {
    Sash sash = ( Sash )widget;
    ControlLCAUtil.writeChanges( sash );
    JSWriter writer = JSWriter.getWriterFor( widget );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    Rectangle oldBounds = ( Rectangle )adapter.getPreserved( Props.BOUNDS );
    Rectangle newBounds = sash.getBounds();
    if(    !adapter.isInitialized() 
        || oldBounds == null 
        || oldBounds.width != newBounds.width ) 
    {      
      if( ( sash.getStyle() & RWT.HORIZONTAL ) != 0 ) {
        writer.set( "splitterSize", newBounds.height );
      } else {
        writer.set( "splitterSize", newBounds.width );
      }
    }
  }

  public void renderDispose( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.dispose();
  }
}
