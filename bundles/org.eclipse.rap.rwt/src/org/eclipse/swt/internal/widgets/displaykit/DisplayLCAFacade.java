/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.displaykit;

import java.io.IOException;

import org.eclipse.rwt.internal.FacadesInitializer;
import org.eclipse.rwt.internal.lifecycle.IDisplayLifeCycleAdapter;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.events.TypedEvent;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.WidgetTreeVisitor;
import org.eclipse.swt.internal.widgets.WidgetTreeVisitor.AllWidgetTreeVisitor;
import org.eclipse.swt.widgets.*;


public abstract class DisplayLCAFacade {
  private final static DisplayLCAFacade FACADE_IMPL
    = ( DisplayLCAFacade )FacadesInitializer.load( DisplayLCAFacade.class );

  public static void registerResources() {
    FACADE_IMPL.registerResourcesInternal();
  }

  public static IDisplayLifeCycleAdapter getDisplayLCA() {
    return FACADE_IMPL.getDisplayLCAInternal();
  }

  public static void writeTestWidgetId( final Widget widget, final String id )
    throws IOException
  {
    FACADE_IMPL.writeTestWidgetIdInternal( widget, id );
  }

  abstract void registerResourcesInternal();

  abstract IDisplayLifeCycleAdapter getDisplayLCAInternal();
  
  abstract void writeTestWidgetIdInternal( Widget widget, String id ) throws IOException;

  abstract void readBounds( Display display );
  
  abstract void readFocusControl( Display display );

  static void doReadData( final Display display ) {
    Rectangle oldBounds = display.getBounds();
    FACADE_IMPL.readBounds( display );
    FACADE_IMPL.readFocusControl( display );
    WidgetTreeVisitor visitor = new AllWidgetTreeVisitor() {
      public boolean doVisit( final Widget widget ) {
        IWidgetLifeCycleAdapter adapter = WidgetUtil.getLCA( widget );
        adapter.readData( widget );
        return true;
      }
    };
    Shell[] shells = display.getShells();
    for( int i = 0; i < shells.length; i++ ) {
      Composite shell = shells[ i ];
      WidgetTreeVisitor.accept( shell, visitor );
    }

    // TODO: [fappel] since there is no possibility yet to determine whether
    //                a shell is maximized, we use this hack to adjust
    //                the bounds of a maximized shell in case of a document
    //                resize event
    for( int i = 0; i < shells.length; i++ ) {
      if( shells[ i ].getBounds().equals( oldBounds ) ) {
        shells[ i ].setBounds( display.getBounds() );
      }
    }
  }

  static void doProcessAction( final Device display ) {
    ProcessActionRunner.execute();
    TypedEvent.processScheduledEvents();
  }
}
