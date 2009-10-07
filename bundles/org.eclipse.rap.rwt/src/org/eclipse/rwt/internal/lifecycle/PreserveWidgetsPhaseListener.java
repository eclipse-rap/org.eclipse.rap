/*******************************************************************************
 * Copyright (c) 2002, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.lifecycle;

import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.internal.widgets.*;
import org.eclipse.swt.internal.widgets.WidgetTreeVisitor.AllWidgetTreeVisitor;
import org.eclipse.swt.widgets.*;

public final class PreserveWidgetsPhaseListener implements PhaseListener {

  private static final long serialVersionUID = 1L;

  public void beforePhase( final PhaseEvent event ) {
  }

  public void afterPhase( final PhaseEvent event ) {
    if( PhaseId.READ_DATA.equals( event.getPhaseId() ) ) {
      Display display = RWTLifeCycle.getSessionDisplay();
      if( display != null ) {
        preserve( display );
      }
    } else if( PhaseId.RENDER.equals( event.getPhaseId() ) ) {
      Display display = RWTLifeCycle.getSessionDisplay();
      if( display != null ) {
        clearPreserved( display );
      }
    }
  }
  
  public PhaseId getPhaseId() {
    return PhaseId.ANY;
  }
  
  /////////////////////////////////////////////////////
  // Helping methods to preserve widget property values

  private static void preserve( final Display display ) {
    IDisplayLifeCycleAdapter displayLCA = DisplayUtil.getLCA( display );
    IWidgetAdapter adapter = DisplayUtil.getAdapter( display );
    if( adapter.isInitialized() ) {
      displayLCA.preserveValues( display );
      IDisplayAdapter displayAdapter
        = ( IDisplayAdapter )display.getAdapter( IDisplayAdapter.class );
      Composite[] shells = displayAdapter.getShells();
      for( int i = 0; i < shells.length; i++ ) {
        WidgetTreeVisitor.accept( shells[ i ], new AllWidgetTreeVisitor() {
          public boolean doVisit( final Widget widget ) {
            AbstractWidgetLCA widgetLCA = WidgetUtil.getLCA( widget );
            widgetLCA.preserveValues( widget );
            return true;
          }
        } );
      }
    }
  }
  
  static void clearPreserved( final Display display ) {
    WidgetAdapter widgetAdapter 
      = ( WidgetAdapter )DisplayUtil.getAdapter( display );
    widgetAdapter.clearPreserved();
    IDisplayAdapter displayAdapter
      = ( IDisplayAdapter )display.getAdapter( IDisplayAdapter.class );
    Composite[] shells = displayAdapter.getShells();
    for( int i = 0; i < shells.length; i++ ) {
      WidgetTreeVisitor.accept( shells[ i ], new AllWidgetTreeVisitor() {
        public boolean doVisit( final Widget widget ) {
          WidgetAdapter widgetAdapter 
            = ( WidgetAdapter )WidgetUtil.getAdapter( widget );
          widgetAdapter.clearPreserved();
          return true;
        }
      } );
    }
  }
}
