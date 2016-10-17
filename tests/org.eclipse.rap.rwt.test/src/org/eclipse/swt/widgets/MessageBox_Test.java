/*******************************************************************************
 * Copyright (c) 2008, 2016 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.widgets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eclipse.rap.rwt.testfixture.TestContext;
import org.eclipse.swt.SWT;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


public class MessageBox_Test {

  @Rule
  public TestContext context = new TestContext();

  private Shell shell;
  private MessageBox messageBox;

  @Before
  public void setUp() {
    Display display = new Display();
    shell = new Shell( display, SWT.NONE );
    messageBox = new MessageBox( shell, SWT.NONE );
  }

  @Test
  public void testSetMessage() {
    String msg = "Lorem ipsum dolor sit amet consectetuer adipiscing elit.";

    messageBox.setMessage( msg );

    assertEquals( msg, messageBox.getMessage() );
  }

  @Test
  public void testSetText() {
    String title = "MessageBox Title";

    messageBox.setText( title );

    assertEquals( title, messageBox.getText() );
  }

  @Test
  public void testStyle() {
    // Test SWT.NONE
    assertTrue( ( messageBox.getStyle() & SWT.OK ) != 0 );

    // Test SWT.OK | SWT.CANCEL
    messageBox = new MessageBox( shell, SWT.OK | SWT.CANCEL );
    assertTrue( ( messageBox.getStyle() & SWT.OK ) != 0 );
    assertTrue( ( messageBox.getStyle() & SWT.CANCEL ) != 0 );
    messageBox = new MessageBox( shell, SWT.OK | SWT.CANCEL | SWT.YES );
    assertTrue( ( messageBox.getStyle() & SWT.OK ) != 0 );
    assertTrue( ( messageBox.getStyle() & SWT.CANCEL ) == 0 );
    assertTrue( ( messageBox.getStyle() & SWT.YES ) == 0 );

    // Test SWT.YES | SWT.NO | SWT.CANCEL
    messageBox = new MessageBox( shell, SWT.YES );
    assertTrue( ( messageBox.getStyle() & SWT.OK ) != 0 );
    assertTrue( ( messageBox.getStyle() & SWT.YES ) == 0 );
    messageBox = new MessageBox( shell, SWT.NO );
    assertTrue( ( messageBox.getStyle() & SWT.OK ) != 0 );
    assertTrue( ( messageBox.getStyle() & SWT.NO ) == 0 );
    messageBox = new MessageBox( shell, SWT.YES | SWT.NO );
    assertTrue( ( messageBox.getStyle() & SWT.YES ) != 0 );
    assertTrue( ( messageBox.getStyle() & SWT.NO ) != 0 );
    messageBox = new MessageBox( shell, SWT.YES | SWT.NO | SWT.CANCEL );
    assertTrue( ( messageBox.getStyle() & SWT.YES ) != 0 );
    assertTrue( ( messageBox.getStyle() & SWT.NO ) != 0 );
    assertTrue( ( messageBox.getStyle() & SWT.CANCEL ) != 0 );
    messageBox = new MessageBox( shell, SWT.YES | SWT.CANCEL );
    assertTrue( ( messageBox.getStyle() & SWT.OK ) != 0 );
    assertTrue( ( messageBox.getStyle() & SWT.YES ) == 0 );
    assertTrue( ( messageBox.getStyle() & SWT.NO ) == 0 );
    assertTrue( ( messageBox.getStyle() & SWT.CANCEL ) == 0 );
    messageBox = new MessageBox( shell, SWT.NO | SWT.CANCEL );
    assertTrue( ( messageBox.getStyle() & SWT.OK ) != 0 );
    assertTrue( ( messageBox.getStyle() & SWT.YES ) == 0 );
    assertTrue( ( messageBox.getStyle() & SWT.NO ) == 0 );
    assertTrue( ( messageBox.getStyle() & SWT.CANCEL ) == 0 );

    // Test SWT.ABORT | SWT.RETRY | SWT.IGNORE
    messageBox = new MessageBox( shell, SWT.ABORT | SWT.RETRY | SWT.IGNORE );
    assertTrue( ( messageBox.getStyle() & SWT.ABORT ) != 0 );
    assertTrue( ( messageBox.getStyle() & SWT.RETRY ) != 0 );
    assertTrue( ( messageBox.getStyle() & SWT.IGNORE ) != 0 );
    messageBox = new MessageBox( shell, SWT.CANCEL | SWT.RETRY );
    assertTrue( ( messageBox.getStyle() & SWT.CANCEL ) != 0 );
    assertTrue( ( messageBox.getStyle() & SWT.RETRY ) != 0 );
    messageBox = new MessageBox( shell, SWT.YES | SWT.RETRY | SWT.IGNORE );
    assertTrue( ( messageBox.getStyle() & SWT.OK ) != 0 );
    assertTrue( ( messageBox.getStyle() & SWT.YES ) == 0 );
    assertTrue( ( messageBox.getStyle() & SWT.RETRY ) == 0 );
    assertTrue( ( messageBox.getStyle() & SWT.IGNORE ) == 0 );
  }

}
