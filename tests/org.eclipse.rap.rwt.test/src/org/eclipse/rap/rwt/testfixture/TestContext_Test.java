/*******************************************************************************
 * Copyright (c) 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.testfixture;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.testfixture.TestContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


public class TestContext_Test {

  @Rule
  public TestContext context = new TestContext();

  @Before
  public void setUp() {
    assertNotNull( context.getApplicationContext() );
    assertNotNull( context.getUISession() );
    context.getApplicationContext().setAttribute( "foo", "bar" );
    context.getUISession().setAttribute( "foo", "bar" );
  }

  @After
  public void tearDown() {
    assertNotNull( context.getApplicationContext() );
    assertNotNull( context.getUISession() );
  }

  @Test()
  public void testStoresAreAvailable() {
    assertSame( RWT.getApplicationContext(), context.getApplicationContext() );
    assertSame( RWT.getUISession(), context.getUISession() );
  }

  @Test()
  public void testStoresAreAccessible() {
    assertEquals( "bar", context.getApplicationContext().getAttribute( "foo" ) );
    assertEquals( "bar", context.getUISession().getAttribute( "foo" ) );
  }

  @Test
  public void testDisplayAndWidgetsCanBeCreated() {
    Display display = new Display();
    Shell shell = new Shell( display );

    shell.open();

    assertTrue( shell.isVisible() );
  }

  @Test()
  public void testListenersAreNotified() {
    Listener listener = mock( Listener.class );
    Event event = new Event();
    Display display = new Display();
    Shell shell = new Shell( display );
    shell.addListener( SWT.Resize, listener );

    shell.notifyListeners( SWT.Resize, event );

    verify( listener ).handleEvent( event );
  }

  @Test()
  public void testLayoutCanBePerformed() {
    Display display = new Display();
    Shell shell = new Shell( display );
    shell.setLayout( new GridLayout() );
    Button button = new Button(shell, SWT.PUSH );
    button.setText( "foo" );

    shell.layout();

    assertTrue( button.getSize().x > 0 );
  }

}
