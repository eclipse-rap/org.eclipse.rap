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
import java.io.Writer;

import org.eclipse.rwt.internal.lifecycle.IDisplayLifeCycleAdapter;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.service.IServiceStateInfo;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Widget;


public class DisplayLCAFacadeImpl extends DisplayLCAFacade {

  IDisplayLifeCycleAdapter getDisplayLCAInternal() {
    return new IDisplayLifeCycleAdapter() {
      public void preserveValues( Display display ) {
      }
      public void processAction( Device display ) {
        doProcessAction( display );
      }
      public void readData( Display display ) {
        doReadData( display );
      }

      public void render( Display display ) throws IOException {
        IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
        Writer responseWriter = stateInfo.getResponseWriter();
        responseWriter.write( "Render Fake" );
      }
    };
  }

  void registerResourcesInternal() {
  }

  void readBounds( Display display ) {
  }

  void readFocusControl( Display display ) {
  }

  void writeTestWidgetIdInternal( Widget widget, String id ) {
  }
}
