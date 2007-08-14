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

package org.eclipse.rwt.lifecycle;

import java.io.IOException;

import org.eclipse.rwt.internal.lifecycle.DisposedWidgets;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.WidgetAdapter;
import org.eclipse.swt.widgets.Widget;



public abstract class AbstractWidgetLCA implements IWidgetLifeCycleAdapter {
  
  public final void render( final Widget widget ) throws IOException {
    WidgetAdapter adapter = ( WidgetAdapter )WidgetUtil.getAdapter( widget );
    if( !adapter.isInitialized() ) {
      renderInitialization( widget );
      widget.addDisposeListener( new DisposeListener() {
        public void widgetDisposed( final DisposeEvent event ) {
          DisposedWidgets.add( ( Widget )event.getSource() );
          widget.removeDisposeListener( this );
        }
      } );
    }
    renderChanges( widget );
    adapter.setInitialized( true );
  }
  
  public Rectangle adjustCoordinates( final Rectangle bounds ) {
    // subclasses may override
    return bounds;
  }
  
  public abstract void preserveValues( Widget widget );
  
  public abstract void renderInitialization( Widget widget ) throws IOException; 
  
  public abstract void renderChanges( Widget widget ) throws IOException;
  
  public abstract void renderDispose( Widget widget ) throws IOException;
  
  public abstract void createResetHandlerCalls( String typePoolId )
    throws IOException;
  
  public abstract String getTypePoolId( Widget widget ) throws IOException;
}
