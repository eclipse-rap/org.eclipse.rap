/*******************************************************************************
 * Copyright (c) 2002, 2015 Innoopract Informationssysteme GmbH and others.
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

import static org.eclipse.rap.rwt.testfixture.internal.SerializationTestUtil.serializeAndDeserialize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eclipse.rap.rwt.internal.lifecycle.WidgetLCA;
import org.eclipse.rap.rwt.testfixture.TestContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.groupkit.GroupLCA;
import org.eclipse.swt.layout.FillLayout;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


public class Group_Test {

  @Rule
  public TestContext context = new TestContext();

  private Shell shell;
  private Group group;

  @Before
  public void setUp() {
    Display display = new Display();
    shell = new Shell( display , SWT.NONE );
    group = new Group( shell, SWT.NONE );
  }

  @Test
  public void testText() {
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

  @Test
  public void testComputeSize() {
    group.setLayout( new FillLayout( SWT.VERTICAL ) );
    new Button( group, SWT.RADIO ).setText( "Radio 1" );
    new Button( group, SWT.RADIO ).setText( "Radio 2" );
    Point expected = new Point( 115, 103 );
    assertEquals( expected, group.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    group.setText( "This is a very long group title." );
    expected = new Point( 204, 103 );
    assertEquals( expected, group.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    group = new Group( shell, SWT.BORDER );
    group.setLayout( new FillLayout( SWT.VERTICAL ) );
    new Button( group, SWT.RADIO ).setText( "Radio 1" );
    new Button( group, SWT.RADIO ).setText( "Radio 2" );
    expected = new Point( 119, 107 );
    assertEquals( expected, group.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    // hint + trimmings + border
    expected = new Point( 142, 159 );
    assertEquals( expected, group.computeSize( 100, 100 ) );
  }

  @Test
  public void testComputeTrim() {
    // trimmings = 3, 17, 6, 20
    Rectangle expected = new Rectangle( -19, -36, 38, 55 );
    assertEquals( expected, group.computeTrim( 0, 0, 0, 0 ) );

    expected = new Rectangle( 1, -16, 138, 155 );
    assertEquals( expected, group.computeTrim( 20, 20, 100, 100 ) );
  }

  @Test
  public void testClientArea() {
    group.setText( "This is a very long group title." );
    group.setSize( 100, 100 );
    group.setLayout( new FillLayout( SWT.VERTICAL ) );
    new Button( group, SWT.RADIO ).setText( "Radio 1" );
    new Button( group, SWT.RADIO ).setText( "Radio 2" );

    // trimmings = 3, 17, 6, 20
    Rectangle expected = new Rectangle( 19, 36, 62, 45 );
    assertEquals( expected, group.getClientArea() );
  }

  @Test
  public void testIsSerializable() throws Exception {
    String groupText = "text";
    group.setText( groupText );

    Group deserializedGroup = serializeAndDeserialize( group );

    assertEquals( groupText, deserializedGroup.getText() );
  }

  @Test
  public void testGetAdapter_LCA() {
    assertTrue( group.getAdapter( WidgetLCA.class ) instanceof GroupLCA );
    assertSame( group.getAdapter( WidgetLCA.class ), group.getAdapter( WidgetLCA.class ) );
  }

}
