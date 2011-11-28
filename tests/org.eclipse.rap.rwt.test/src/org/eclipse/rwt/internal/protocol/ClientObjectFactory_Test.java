/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.protocol;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rwt.lifecycle.UICallBack;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


public class ClientObjectFactory_Test extends TestCase {

  private Display display;
  private Shell shell;

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display );
    Fixture.fakeResponseWriter();
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testCreate() {
    IClientObject clientObject = ClientObjectFactory.getForWidget( shell );

    assertNotNull( clientObject );
  }

  public void testSameInstance() {
    IClientObject clientObject = ClientObjectFactory.getForWidget( shell );

    assertSame( clientObject, ClientObjectFactory.getForWidget( shell ) );
  }

  public void testCreateDisposed() {
    shell.dispose();

    IClientObject clientObject = ClientObjectFactory.getForWidget( shell );

    assertNotNull( clientObject );
  }

  public void testCreateDisplayDisposed() {
    display.dispose();

    IClientObject clientObject = ClientObjectFactory.getForWidget( shell );

    assertNotNull( clientObject );
  }
  
  public void testGetClientObjectForDisplay() {
    IClientObject clientObject = ClientObjectFactory.getForDisplay( display );
    
    assertNotNull( clientObject );
  }  

  public void testDisplaySameInstance() {
    IClientObject clientObject = ClientObjectFactory.getForDisplay( display );

    assertSame( clientObject, ClientObjectFactory.getForDisplay( display ) );
  }

  public void testDiaplayCreateDisposed() {
    display.dispose();

    IClientObject clientObject = ClientObjectFactory.getForDisplay( display );

    assertNotNull( clientObject );
  }

  public void testGetClientObjectForGC() {
    IClientObject clientObject = ClientObjectFactory.getForGC( shell );
    
    assertNotNull( clientObject );
  }  

  public void testGetClientObjectForGCSameInstance() {
    IClientObject clientObject = ClientObjectFactory.getForGC( shell );
    
    assertNotNull( clientObject );
  }  
  
  public void testGetClientObjectForGCDisposed() {
    shell.dispose();
    IClientObject clientObject = ClientObjectFactory.getForGC( shell );
    
    assertSame( clientObject, ClientObjectFactory.getForGC( shell ) );
  }  

  public void testCreateFromNonUIThreadFails() throws InterruptedException {
    final List<Exception> log = new ArrayList<Exception>();
    Thread backgroundThread = new Thread() {

      @Override
      public void run() {
        UICallBack.runNonUIThreadWithFakeContext( display, new Runnable() {

          public void run() {
            try {
              ClientObjectFactory.getForWidget( shell );
            } catch( Exception exception ) {
              log.add( exception );
            }
          }
        } );
      }
    };
    backgroundThread.start();
    backgroundThread.join();
    assertEquals( 1, log.size() );
    assertEquals( IllegalStateException.class, log.get( 0 ).getClass() );
  }

}
