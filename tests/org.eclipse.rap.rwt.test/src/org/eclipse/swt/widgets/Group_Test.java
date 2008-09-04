/*******************************************************************************
 * Copyright (c) 2002, 2008 Innoopract Informationssysteme GmbH.
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

import org.eclipse.rwt.lifecycle.PhaseId;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;


public class Group_Test extends TestCase {

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }

  public void testText() {
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    Group group = new Group( shell, SWT.NONE );
    assertEquals( "", group.getText() );
    group.setText( "xyz" );
    assertEquals( "xyz", group.getText() );
    try {
      group.setText( null );
      fail( "Must not allow to set null-text." );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  public void testComputeSize() throws Exception {
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    Group group = new Group( shell, SWT.NONE );
    group.setLayout( new FillLayout( SWT.VERTICAL ) );
    new Button( group, SWT.RADIO ).setText( "Radio 1" );
    new Button( group, SWT.RADIO ).setText( "Radio 2" );
    Point expected = new Point( 70, 64 );
    assertEquals( expected, group.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    group.setText( "This is a very long group title." );
    expected = new Point( 193, 64 );
    assertEquals( expected, group.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    group = new Group( shell, SWT.BORDER );
    group.setLayout( new FillLayout( SWT.VERTICAL ) );
    new Button( group, SWT.RADIO ).setText( "Radio 1" );
    new Button( group, SWT.RADIO ).setText( "Radio 2" );
    expected = new Point( 78, 72 );
    assertEquals( expected, group.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    // hint + trimmings + border
    expected = new Point( 114, 128 );
    assertEquals( expected, group.computeSize( 100, 100 ) );
  }

  public void testComputeTrim() throws Exception {
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    Group group = new Group( shell, SWT.NONE );
    // trimmings = 3, 17, 6, 20
    Rectangle expected = new Rectangle( -3, -17, 6, 20 );
    assertEquals( expected, group.computeTrim( 0, 0, 0, 0 ) );

    expected = new Rectangle( 17, 3, 106, 120 );
    assertEquals( expected, group.computeTrim( 20, 20, 100, 100 ) );
  }

  public void testClientArea() throws Exception {
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    Group group = new Group( shell, SWT.NONE );
    group.setText( "This is a very long group title." );
    group.setSize( 100, 100 );
    group.setLayout( new FillLayout( SWT.VERTICAL ) );
    new Button( group, SWT.RADIO ).setText( "Radio 1" );
    new Button( group, SWT.RADIO ).setText( "Radio 2" );

    // trimmings = 3, 17, 6, 20
    Rectangle expected = new Rectangle( 3, 17, 94, 80 );
    assertEquals( expected, group.getClientArea() );
  }
}
