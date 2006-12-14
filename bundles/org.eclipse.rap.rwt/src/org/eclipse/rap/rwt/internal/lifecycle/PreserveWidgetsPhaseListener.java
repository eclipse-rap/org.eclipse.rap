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

package org.eclipse.rap.rwt.internal.lifecycle;

import org.eclipse.rap.rwt.internal.widgets.IWidgetAdapter;
import org.eclipse.rap.rwt.internal.widgets.WidgetTreeVisitor;
import org.eclipse.rap.rwt.internal.widgets.WidgetTreeVisitor.AllWidgetTreeVisitor;
import org.eclipse.rap.rwt.lifecycle.*;
import org.eclipse.rap.rwt.widgets.*;
import com.w4t.engine.lifecycle.*;

// TODO: [fappel] preserving the values should take place after the
//                read data phase, since reseting a value programmatically
//                in ProcessAction to the value it had before ReadData would 
//                prevent that the UI will be updated with the correct value.
public final class PreserveWidgetsPhaseListener implements PhaseListener {

  private static final long serialVersionUID = 1L;

  public void beforePhase( final PhaseEvent event ) {
    if( PhaseId.READ_DATA.equals( event.getPhaseId() ) ) {
      Display display = Display.getCurrent();
      if( display != null ) {
        preserve( display );
      }
    }
  }

  public void afterPhase( final PhaseEvent event ) {
    if( PhaseId.RENDER.equals( event.getPhaseId() ) ) {
      Display display = Display.getCurrent();
      if( display != null ) {
        clearPreservd( display );
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
    displayLCA.preserveValues( display );
    Composite[] shells = display.getShells();
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
  
  private static void clearPreservd( final Display display ) {
    IWidgetAdapter displayAdapter = DisplayUtil.getAdapter( display );
    displayAdapter.clearPreserved();
    Composite[] shells = display.getShells();
    for( int i = 0; i < shells.length; i++ ) {
      WidgetTreeVisitor.accept( shells[ i ], new AllWidgetTreeVisitor() {
        public boolean doVisit( final Widget widget ) {
          IWidgetAdapter widgetAdapter = WidgetUtil.getAdapter( widget );
          widgetAdapter.clearPreserved();
          return true;
        }
      } );
    }
  }
}
