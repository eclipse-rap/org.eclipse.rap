/*******************************************************************************
 * Copyright (c) 2008, 2015 Innoopract Informationssysteme GmbH and others.
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.eclipse.rap.rwt.internal.lifecycle.WidgetLCA;
import org.eclipse.rap.rwt.testfixture.TestContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.widgets.scalekit.ScaleLCA;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


public class Scale_Test {

  @Rule
  public TestContext context = new TestContext();

  private Shell shell;
  private Scale scale;

  @Before
  public void setUp() {
    Display display = new Display();
    shell = new Shell( display );
    scale = new Scale( shell, SWT.NONE );
  }

  @Test
  public void testInitialValues() {
    assertEquals( 0, scale.getMinimum() );
    assertEquals( 100, scale.getMaximum() );
    assertEquals( 0, scale.getSelection() );
    assertEquals( 1, scale.getIncrement() );
    assertEquals( 10, scale.getPageIncrement() );
  }

  @Test
  public void testValues() {
    scale.setSelection( 34 );
    assertEquals( 34, scale.getSelection() );
    scale.setMinimum( 10 );
    assertEquals( 10, scale.getMinimum() );
    scale.setMaximum( 56 );
    assertEquals( 56, scale.getMaximum() );
    scale.setIncrement( 5 );
    assertEquals( 5, scale.getIncrement() );
    scale.setPageIncrement( 15 );
    assertEquals( 15, scale.getPageIncrement() );

    scale.setMinimum( 40 );
    assertEquals( 40, scale.getMinimum() );
    assertEquals( 40, scale.getSelection() );

    scale.setSelection( 52 );
    scale.setMaximum( 50 );
    assertEquals( 50, scale.getMaximum() );
    assertEquals( 50, scale.getSelection() );

    scale.setMaximum( 30 );
    assertEquals( 50, scale.getMaximum() );

    scale.setSelection( 52 );
    assertEquals( 50, scale.getSelection() );

    scale.setSelection( 10 );
    assertEquals( 50, scale.getSelection() );

    scale.setSelection( -10 );
    assertEquals( 50, scale.getSelection() );

    scale.setPageIncrement( -15 );
    assertEquals( 15, scale.getPageIncrement() );

    scale.setIncrement( -5 );
    assertEquals( 5, scale.getIncrement() );
  }

  @Test
  public void testStyle() {
    // Test SWT.NONE
    assertTrue( ( scale.getStyle() & SWT.HORIZONTAL ) != 0 );
    // Test SWT.BORDER
    scale = new Scale( shell, SWT.BORDER );
    assertTrue( ( scale.getStyle() & SWT.HORIZONTAL ) != 0 );
    assertTrue( ( scale.getStyle() & SWT.BORDER ) != 0 );
    // Test SWT.VERTICAL
    scale = new Scale( shell, SWT.VERTICAL );
    assertTrue( ( scale.getStyle() & SWT.VERTICAL ) != 0 );
    // Test combination of SWT.HORIZONTAL | SWT.VERTICAL
    scale = new Scale( shell, SWT.HORIZONTAL | SWT.VERTICAL );
    assertTrue( ( scale.getStyle() & SWT.HORIZONTAL ) != 0 );
    assertTrue( ( scale.getStyle() & SWT.VERTICAL ) == 0 );
  }

  @Test
  public void testDispose() {
    scale.dispose();
    assertTrue( scale.isDisposed() );
  }

  @Test
  public void testComputeSize() {
    Scale scale = new Scale( shell, SWT.HORIZONTAL );
    Point expected = new Point( 160, 41 );
    assertEquals( expected, scale.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    scale = new Scale( shell, SWT.HORIZONTAL | SWT.BORDER );
    expected = new Point( 162, 43 );
    assertEquals( expected, scale.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    scale = new Scale( shell, SWT.VERTICAL );
    expected = new Point( 41, 160 );
    assertEquals( expected, scale.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    scale = new Scale( shell, SWT.VERTICAL | SWT.BORDER );
    expected = new Point( 43, 162 );
    assertEquals( expected, scale.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    expected = new Point( 102, 102 );
    assertEquals( expected, scale.computeSize( 100, 100 ) );
  }

  @Test
  public void testIsSerializable() throws Exception {
    scale.setSelection( 12 );

    Scale deserializedScale = serializeAndDeserialize( scale );

    assertEquals( scale.getSelection(), deserializedScale.getSelection() );
  }

  @Test
  public void testAddSelectionListener() {
    scale.addSelectionListener( mock( SelectionListener.class ) );

    assertTrue( scale.isListening( SWT.Selection ) );
    assertTrue( scale.isListening( SWT.DefaultSelection ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testAddSelectionListener_withNullArgument() {
    scale.addSelectionListener( null );
  }

  @Test
  public void testRemoveSelectionListener() {
    SelectionListener listener = mock( SelectionListener.class );
    scale.addSelectionListener( listener );

    scale.removeSelectionListener( listener );

    assertFalse( scale.isListening( SWT.Selection ) );
    assertFalse( scale.isListening( SWT.DefaultSelection ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testRemoveSelectionListener_withNullArgument() {
    scale.removeSelectionListener( null );
  }

  @Test
  public void testGetAdapter_LCA() {
    assertTrue( scale.getAdapter( WidgetLCA.class ) instanceof ScaleLCA );
    assertSame( scale.getAdapter( WidgetLCA.class ), scale.getAdapter( WidgetLCA.class ) );
  }

}
