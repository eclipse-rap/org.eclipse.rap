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

import org.eclipse.rwt.internal.lifecycle.IDisplayLifeCycleAdapter;
import org.eclipse.swt.widgets.Display;


public class DisplayLCAFacadeImpl extends DisplayLCAFacade {

  IDisplayLifeCycleAdapter getDisplayLCAInternal() {
    return new DisplayLCA();
  }

  void writeAppScriptInternal( final String id ) throws IOException {
    DisplayLCA.writeAppScript( id );
  }

  void writeLibrariesInternal() throws IOException {
    DisplayLCA.writeLibraries();
  }

  void readBounds( Display display ) {
    DisplayLCA.readBounds( display );
  }

  void readFocusControl( Display display ) {
    DisplayLCA.readFocusControl( display );
  }
}
