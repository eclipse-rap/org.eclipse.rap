/*******************************************************************************
 * Copyright (c) 2002, 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.events;

import junit.framework.TestCase;

import org.eclipse.rwt.lifecycle.PhaseId;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;


public class DisposeEvent_Test extends TestCase {
  
  private static final String WIDGET_DISPOSED = "widgetDiposed|";
  
  private String log = "";
  
  protected void setUp() throws Exception {
    RWTFixture.setUp();
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
  }
  
  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
  
  public void testAddRemoveListener() {
    DisposeListener listener = new DisposeListener() {
      public void widgetDisposed( final DisposeEvent event ) {
        log += WIDGET_DISPOSED;
      }
    };
    Display display = new Display();
    Composite shell = new Shell( display , SWT.NONE );
    shell.addDisposeListener( listener );
    
    DisposeEvent event = new DisposeEvent( shell ); 
    event.processEvent();
    assertEquals( WIDGET_DISPOSED, log );
    
    log = "";
    shell.removeDisposeListener( listener );
    event = new DisposeEvent( shell ); 
    event.processEvent();
    assertEquals( "", log );
  }
}
