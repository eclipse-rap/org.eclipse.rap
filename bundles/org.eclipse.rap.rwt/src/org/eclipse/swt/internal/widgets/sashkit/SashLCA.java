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

package org.eclipse.swt.internal.widgets.sashkit;

import java.io.IOException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.lifecycle.*;
import org.eclipse.swt.widgets.*;


public final class SashLCA extends AbstractWidgetLCA {
  
  public void preserveValues( final Widget widget ) {
    ControlLCAUtil.preserveValues( ( Control )widget );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    adapter.preserve( Props.SELECTION_LISTENERS, 
                      SelectionEvent.getListeners( widget ) );
  }
  
  public void readData( final Widget widget ) {
    // TODO [rh] clarify whether bounds should be sent (last parameter)
    ControlLCAUtil.processSelection( widget, null, true );
  }
  
  public void renderInitialization( final Widget widget ) throws IOException {
    Sash sash = ( Sash )widget;
    JSWriter writer = JSWriter.getWriterFor( widget );
    // Due to a current qooxdoo bug, the orientation must be set in the 
    // constructor. Doesn't hurt us anyway.
    JSVar orientation
      = ( sash.getStyle() & SWT.HORIZONTAL ) != 0
      ? JSConst.QX_CONST_VERTICAL_ORIENTATION
      : JSConst.QX_CONST_HORIZONTAL_ORIENTATION;
    writer.newWidget( "org.eclipse.swt.Sash", new Object[] { orientation } );
    ControlLCAUtil.writeStyleFlags( widget );
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
      if( ( sash.getStyle() & SWT.HORIZONTAL ) != 0 ) {
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
