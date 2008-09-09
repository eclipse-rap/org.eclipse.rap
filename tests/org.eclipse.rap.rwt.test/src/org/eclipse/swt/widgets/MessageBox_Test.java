/*******************************************************************************
 * Copyright (c) 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.widgets;

import junit.framework.TestCase;

import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;

public class MessageBox_Test extends TestCase {

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }

  public void testMessage() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    String mesg = "Lorem ipsum dolor sit amet consectetuer adipiscing elit.";
    MessageBox mb = new MessageBox( shell, SWT.NONE );
    mb.setMessage( mesg );
    assertEquals( mesg, mb.getMessage() );
  }

  public void testText() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    String title = "MessageBox Title";
    MessageBox mb = new MessageBox( shell, SWT.NONE );
    mb.setText( title );
    assertEquals( title, mb.getText() );
  }

  public void testStyle() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    // Test SWT.NONE
    MessageBox mb = new MessageBox( shell, SWT.NONE );
    assertTrue( ( mb.getStyle() & SWT.OK ) != 0 );

    // Test SWT.OK | SWT.CANCEL
    mb = new MessageBox( shell, SWT.OK | SWT.CANCEL );
    assertTrue( ( mb.getStyle() & SWT.OK ) != 0 );
    assertTrue( ( mb.getStyle() & SWT.CANCEL ) != 0 );
    mb = new MessageBox( shell, SWT.OK | SWT.CANCEL | SWT.YES );
    assertTrue( ( mb.getStyle() & SWT.OK ) != 0 );
    assertTrue( ( mb.getStyle() & SWT.CANCEL ) == 0 );
    assertTrue( ( mb.getStyle() & SWT.YES ) == 0 );

    // Test SWT.YES | SWT.NO | SWT.CANCEL
    mb = new MessageBox( shell, SWT.YES );
    assertTrue( ( mb.getStyle() & SWT.OK ) != 0 );
    assertTrue( ( mb.getStyle() & SWT.YES ) == 0 );
    mb = new MessageBox( shell, SWT.NO );
    assertTrue( ( mb.getStyle() & SWT.OK ) != 0 );
    assertTrue( ( mb.getStyle() & SWT.NO ) == 0 );
    mb = new MessageBox( shell, SWT.YES | SWT.NO );
    assertTrue( ( mb.getStyle() & SWT.YES ) != 0 );
    assertTrue( ( mb.getStyle() & SWT.NO ) != 0 );
    mb = new MessageBox( shell, SWT.YES | SWT.NO | SWT.CANCEL );
    assertTrue( ( mb.getStyle() & SWT.YES ) != 0 );
    assertTrue( ( mb.getStyle() & SWT.NO ) != 0 );
    assertTrue( ( mb.getStyle() & SWT.CANCEL ) != 0 );
    mb = new MessageBox( shell, SWT.YES | SWT.CANCEL );
    assertTrue( ( mb.getStyle() & SWT.OK ) != 0 );
    assertTrue( ( mb.getStyle() & SWT.YES ) == 0 );
    assertTrue( ( mb.getStyle() & SWT.NO ) == 0 );
    assertTrue( ( mb.getStyle() & SWT.CANCEL ) == 0 );
    mb = new MessageBox( shell, SWT.NO | SWT.CANCEL );
    assertTrue( ( mb.getStyle() & SWT.OK ) != 0 );
    assertTrue( ( mb.getStyle() & SWT.YES ) == 0 );
    assertTrue( ( mb.getStyle() & SWT.NO ) == 0 );
    assertTrue( ( mb.getStyle() & SWT.CANCEL ) == 0 );

    // Test SWT.ABORT | SWT.RETRY | SWT.IGNORE
    mb = new MessageBox( shell, SWT.ABORT | SWT.RETRY | SWT.IGNORE );
    assertTrue( ( mb.getStyle() & SWT.ABORT ) != 0 );
    assertTrue( ( mb.getStyle() & SWT.RETRY ) != 0 );
    assertTrue( ( mb.getStyle() & SWT.IGNORE ) != 0 );
    mb = new MessageBox( shell, SWT.CANCEL | SWT.RETRY );
    assertTrue( ( mb.getStyle() & SWT.CANCEL ) != 0 );
    assertTrue( ( mb.getStyle() & SWT.RETRY ) != 0 );
    mb = new MessageBox( shell, SWT.YES | SWT.RETRY | SWT.IGNORE );
    assertTrue( ( mb.getStyle() & SWT.OK ) != 0 );
    assertTrue( ( mb.getStyle() & SWT.YES ) == 0 );
    assertTrue( ( mb.getStyle() & SWT.RETRY ) == 0 );
    assertTrue( ( mb.getStyle() & SWT.IGNORE ) == 0 );
  }
}
