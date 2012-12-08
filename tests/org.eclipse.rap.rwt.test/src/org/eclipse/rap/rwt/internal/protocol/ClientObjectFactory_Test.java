/*******************************************************************************
 * Copyright (c) 2011, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.protocol;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.Adaptable;
import org.eclipse.rap.rwt.testfixture.Fixture;
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
    IClientObject clientObject = ClientObjectFactory.getClientObject( shell );

    assertNotNull( clientObject );
  }

  public void testCreateWithNullParameter() {
    try {
      ClientObjectFactory.getClientObject( null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  public void testCreateWithInvalidAdaptable() {
    Adaptable illegalAdaptable = new Adaptable() {

      public <T> T getAdapter( Class<T> adapter ) {
        return null;
      }
    };
    try {
      ClientObjectFactory.getClientObject( illegalAdaptable );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }

  public void testSameInstance() {
    IClientObject clientObject = ClientObjectFactory.getClientObject( shell );

    assertSame( clientObject, ClientObjectFactory.getClientObject( shell ) );
  }

  public void testCreateDisposed() {
    shell.dispose();

    IClientObject clientObject = ClientObjectFactory.getClientObject( shell );

    assertNotNull( clientObject );
  }

  public void testCreateDisplayDisposed() {
    display.dispose();

    IClientObject clientObject = ClientObjectFactory.getClientObject( shell );

    assertNotNull( clientObject );
  }

  public void testGetClientObjectForDisplay() {
    IClientObject clientObject = ClientObjectFactory.getClientObject( display );

    assertNotNull( clientObject );
  }

  public void testDisplaySameInstance() {
    IClientObject clientObject = ClientObjectFactory.getClientObject( display );

    assertSame( clientObject, ClientObjectFactory.getClientObject( display ) );
  }

  public void testDisplayCreateDisposed() {
    display.dispose();

    IClientObject clientObject = ClientObjectFactory.getClientObject( display );

    assertNotNull( clientObject );
  }

}
