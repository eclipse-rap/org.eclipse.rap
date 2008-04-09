/*******************************************************************************
 * Copyright (c) 2002-2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.internal.widgets.displaykit;


import java.io.IOException;

import org.eclipse.rwt.internal.lifecycle.HtmlResponseWriter;
import org.eclipse.rwt.internal.lifecycle.IDisplayLifeCycleAdapter;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.service.IServiceStateInfo;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.widgets.Display;


public class DisplayLCAFacadeImpl extends DisplayLCAFacade {

  IDisplayLifeCycleAdapter getDisplayLCAInternal() {
    return new IDisplayLifeCycleAdapter() {
      public void preserveValues( final Display display ) {
      }
      public void processAction( final Device display ) {
        doProcessAction( display );
      }
      public void readData( final Display display ) {
        doReadData( display );
      }

      public void render( final Display display ) throws IOException {
        IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
        HtmlResponseWriter out = stateInfo.getResponseWriter();
        out.writeText( "Render Fake", null );
      }
    };
  }

  void writeAppScriptInternal( final String id ) throws IOException {
  }

  void writeLibrariesInternal() throws IOException {
  }

  void readBounds( final Display display ) {
  }

  void readFocusControl( final Display display ) {
  }
}
