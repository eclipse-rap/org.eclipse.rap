/*******************************************************************************
 * Copyright (c) 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.scripting;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;

import java.util.List;

import org.eclipse.rap.rwt.scripting.ClientListener;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ClientListenerUtil_Test {

  private Widget widget;

  @Before
  public void setUp() {
    Fixture.setUp();
    new Display();
    Shell shell = new Shell();
    widget = new Widget( shell, SWT.NONE ) {};
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testClientListenerAdded() {
    ClientListener listener = mock( ClientListener.class );

    ClientListenerUtil.clientListenerAdded( widget, SWT.Move, listener );

    List<ClientListenerBinding> added = ClientListenerUtil.getAddedClientListeners( widget );
    assertEquals( 1, added.size() );
    assertEquals( SWT.Move, added.get( 0 ).getEventType() );
    assertEquals( listener, added.get( 0 ).getListener() );
  }

  @Test
  public void testClearAddedClientListener() {
    ClientListenerUtil.clientListenerAdded( widget, SWT.Move, mock( ClientListener.class ) );

    ClientListenerUtil.clearAddedClientListeners( widget );

    assertNull( ClientListenerUtil.getAddedClientListeners( widget ) );
  }

  @Test
  public void testClientListenerRemoved() {
    ClientListener listener = mock( ClientListener.class );

    ClientListenerUtil.clientListenerRemoved( widget, SWT.Move, listener );

    List<ClientListenerBinding> removed = ClientListenerUtil.getRemovedClientListeners( widget );
    assertEquals( 1, removed.size() );
    assertEquals( SWT.Move, removed.get( 0 ).getEventType() );
    assertEquals( listener, removed.get( 0 ).getListener() );
  }

  @Test
  public void testClearRemovedClientListener() {
    ClientListenerUtil.clientListenerRemoved( widget, SWT.Move, mock( ClientListener.class ) );

    ClientListenerUtil.clearRemovedClientListeners( widget );

    assertNull( ClientListenerUtil.getRemovedClientListeners( widget ) );
  }

}
