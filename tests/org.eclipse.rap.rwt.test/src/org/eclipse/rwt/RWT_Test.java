/*******************************************************************************
 * Copyright (c) 2010 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt;

import junit.framework.TestCase;

import org.eclipse.rwt.internal.engine.RWTContext;
import org.eclipse.rwt.internal.lifecycle.DisplayUtil;
import org.eclipse.rwt.internal.service.ApplicationStoreImpl;
import org.eclipse.rwt.internal.service.RequestParams;
import org.eclipse.rwt.service.IApplicationStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.Display;


public class RWT_Test extends TestCase {
  
  public void testRequestThreadExecFromBackgroundThread() throws Exception {
    final Throwable[] exception = { null };
    Thread thread = new Thread( new Runnable() {
      public void run() {
        try {
          RWT.requestThreadExec( new Runnable() {
            public void run() {
            }
          } );
        } catch( Exception e ) {
          exception[ 0 ] = e;
        }
      }
    }, "testRequestThreadExecFromBackgroundThread" );
    thread.start();
    thread.join();
    assertNotNull( exception[ 0 ] );
    assertTrue( exception[ 0 ] instanceof SWTException );
    SWTException swtException = ( SWTException )exception[ 0 ];
    assertEquals( SWT.ERROR_THREAD_INVALID_ACCESS, swtException.code );
  }
  
  public void testThreadRequestExec() {
    final Thread[] requestThread = { null };
    Display display = new Display();
    // use asyncExec to run code during executeLifeCycleFromServerThread
    display.asyncExec( new Runnable() { 
      public void run() {
        RWT.requestThreadExec( new Runnable() {
          public void run() {
            requestThread[ 0 ] = Thread.currentThread();
          }
        } );
      }
    } );
    Fixture.fakeNewRequest();
    String displayId = DisplayUtil.getId( display );
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.executeLifeCycleFromServerThread();
    assertNotNull( requestThread[ 0 ] );
  }

  public void testGetApplicationStore() {
    IApplicationStore applicationStore = RWT.getApplicationStore();

    assertSame( applicationStore, RWTContext.getSingleton( ApplicationStoreImpl.class ) );
  }
  
  protected void setUp() throws Exception {
    Fixture.setUp();
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
}
