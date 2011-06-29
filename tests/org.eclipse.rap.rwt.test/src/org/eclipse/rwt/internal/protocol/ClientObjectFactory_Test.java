/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.protocol;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.lifecycle.UICallBack;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


public class ClientObjectFactory_Test extends TestCase {

  protected void setUp() throws Exception {
    Fixture.setUp();
    Fixture.fakeResponseWriter();
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testCreate() {
    Display display = new Display();
    Shell shell = new Shell( display );
    IClientObject clientObject = ClientObjectFactory.getForWidget( shell );
    assertNotNull( clientObject );
  }

  public void testSameInstance() {
    Display display = new Display();
    Shell shell = new Shell( display );
    IClientObject clientObject = ClientObjectFactory.getForWidget( shell );
    assertSame( clientObject, ClientObjectFactory.getForWidget( shell ) );
  }

  public void testCreateFromNonUIThreadFails() throws InterruptedException {
    final Display display = new Display();
    final Shell shell = new Shell( display );
    final List<Exception> log = new ArrayList<Exception>();
    Thread backgroundThread = new Thread() {

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
