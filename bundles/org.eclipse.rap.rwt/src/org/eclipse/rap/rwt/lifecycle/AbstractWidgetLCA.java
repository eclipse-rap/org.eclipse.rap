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

package org.eclipse.rap.rwt.lifecycle;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.rap.rwt.events.DisposeEvent;
import org.eclipse.rap.rwt.events.DisposeListener;
import org.eclipse.rap.rwt.graphics.Rectangle;
import org.eclipse.rap.rwt.internal.widgets.IWidgetAdapter;
import org.eclipse.rap.rwt.widgets.Widget;
import com.w4t.engine.service.ContextProvider;
import com.w4t.engine.service.IServiceStateInfo;


public abstract class AbstractWidgetLCA implements IWidgetLifeCycleAdapter {
  
  protected static final String DISPOSAL_LIST 
    = "org.eclipse.rap.rwt.lifecycle.AbstractWidgetLCA#disposalList";

  // TODO [rh] find a better place for this method
  public static Widget[] getDisposedWidgets() {
    Widget[] result;
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    List disposalList = ( List )stateInfo.getAttribute( DISPOSAL_LIST );
    if( disposalList == null ) {
      result = new Widget[ 0 ];
    } else {
      result = new Widget[ disposalList.size() ];
      disposalList.toArray( result );
    }
    return result;
  }
  
  public final void render( final Widget widget ) throws IOException {
    IWidgetAdapter widgetAdapter = WidgetUtil.getAdapter( widget );
    if( !widgetAdapter.isInitialized() ) {
      renderInitialization( widget );
      widget.addDisposeListener( new DisposeListener() {
        public void widgetDisposed( final DisposeEvent event ) {
          addToDisposeList( ( Widget )event.getSource() );
        }
      } );
    }
    renderChanges( widget );
    widgetAdapter.setInitialized( true );
  }
  
  public Rectangle adjustCoordinates( final Rectangle newBounds ) {
    // subclasses may override
    return newBounds;
  }
  
  public abstract void preserveValues( Widget widget );
  
  public abstract void renderInitialization( Widget widget ) throws IOException; 
  
  public abstract void renderChanges( Widget widget ) throws IOException;
  
  public abstract void renderDispose( Widget widget ) throws IOException;

  private static void addToDisposeList( final Widget widget ) {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    List disposalList = ( List )stateInfo.getAttribute( DISPOSAL_LIST );
    if( disposalList == null ) {
      disposalList = new LinkedList();
      stateInfo.setAttribute( DISPOSAL_LIST, disposalList );
    }
    disposalList.add( widget );
  }

}
