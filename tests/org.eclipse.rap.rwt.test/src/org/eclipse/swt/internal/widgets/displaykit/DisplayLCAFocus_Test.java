/*******************************************************************************
 * Copyright (c) 2007, 2012 Innoopract Informationssysteme GmbH and others.
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

import static org.eclipse.rap.rwt.internal.lifecycle.DisplayUtil.getId;
import static org.eclipse.rap.rwt.lifecycle.WidgetUtil.getId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.eclipse.rap.rwt.internal.lifecycle.DisplayUtil;
import org.eclipse.rap.rwt.internal.protocol.ClientMessageConst;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


/*
 * Put in separate class because this test does not share the same setUp/tearDown
 * as the tests in DisplayLCA_Test.
 */
public class DisplayLCAFocus_Test {

  private Display display;
  private Shell shell;
  private Button button;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display );
    button = new Button( shell, SWT.PUSH );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testUnchangedFocus() {
    shell.setSize( 400, 400 );
    shell.setLayout( new FillLayout() );
    shell.layout();
    shell.open();

    // Simulate initial request that constructs UI
    Fixture.fakeNewRequest();
    Fixture.executeLifeCycleFromServerThread();

    fakeSetFocusControl();
    Fixture.executeLifeCycleFromServerThread();

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( DisplayUtil.getId( display ), "focusControl" ) );
  }

  /* Test case for https://bugs.eclipse.org/bugs/show_bug.cgi?id=196911 */
  @Test
  public void testSetFocusToClientSideFocusedControl() {
    final Shell[] childShell = { null };
    shell.setSize( 400, 400 );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent e ) {
        childShell[ 0 ] = new Shell( shell, SWT.NONE );
        childShell[ 0 ].setBounds( 0, 0, 100, 100 );
        childShell[ 0 ].open();
        button.setFocus();
      }
    } );
    shell.setLayout( new FillLayout() );
    shell.layout();
    shell.open();

    // Simulate initial request that constructs UI
    Fixture.fakeNewRequest();
    Fixture.executeLifeCycleFromServerThread( );

    // Simulate request that is sent when button was pressed
    fakeSetFocusControl();
    Fixture.executeLifeCycleFromServerThread( );

    // ensure that widgetSelected was called
    assertNotNull( childShell[ 0 ] );
    Message message = Fixture.getProtocolMessage();
    String displayId = DisplayUtil.getId( display );
    assertEquals( getId( button ), message.findSetProperty( displayId, "focusControl" ) );
  }

  private void fakeSetFocusControl() {
    Fixture.fakeNewRequest();
    Fixture.fakeNotifyOperation( getId( button ), ClientMessageConst.EVENT_SELECTION, null );
    Fixture.fakeSetParameter( getId( display ), "focusControl", getId( button ) );
  }

}
