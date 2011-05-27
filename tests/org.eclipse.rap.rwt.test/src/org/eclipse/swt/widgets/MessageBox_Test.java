/*******************************************************************************
 * Copyright (c) 2008, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.widgets;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.swt.SWT;

public class MessageBox_Test extends TestCase {

  private Display display;
  private Shell shell;
  
  protected void setUp() throws Exception {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display, SWT.NONE );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testMessage() {
    String msg = "Lorem ipsum dolor sit amet consectetuer adipiscing elit.";
    MessageBox messageBox = new MessageBox( shell, SWT.NONE );
    messageBox.setMessage( msg );
    assertEquals( msg, messageBox.getMessage() );
  }

  public void testText() {
    String title = "MessageBox Title";
    MessageBox messageBox = new MessageBox( shell, SWT.NONE );
    messageBox.setText( title );
    assertEquals( title, messageBox.getText() );
  }

  public void testStyle() {
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
