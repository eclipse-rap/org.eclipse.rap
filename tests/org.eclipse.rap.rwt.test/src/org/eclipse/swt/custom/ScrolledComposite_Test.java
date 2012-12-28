/*******************************************************************************
 * Copyright (c) 2009, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.custom;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ScrolledComposite_Test {

  private Display display;
  private Shell shell;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display , SWT.NONE );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testCreation() {
    ScrolledComposite sc = new ScrolledComposite( shell, SWT.NONE );
    assertNull( sc.getHorizontalBar() );
    assertNull( sc.getVerticalBar() );
    sc = new ScrolledComposite( shell, SWT.V_SCROLL );
    assertNull( sc.getHorizontalBar() );
    assertNotNull( sc.getVerticalBar() );
    sc = new ScrolledComposite( shell, SWT.H_SCROLL );
    assertNotNull( sc.getHorizontalBar() );
    assertNull( sc.getVerticalBar() );
    sc = new ScrolledComposite( shell, SWT.V_SCROLL | SWT.H_SCROLL );
    assertNotNull( sc.getHorizontalBar() );
    assertNotNull( sc.getVerticalBar() );
  }

  @Test
  public void testDispose() {
    ScrolledComposite sc = new ScrolledComposite( shell, SWT.V_SCROLL | SWT.H_SCROLL );
    Composite content = new Composite( sc, SWT.NONE );
    sc.setContent( content );

    ScrollBar horizontalBar = sc.getHorizontalBar();
    ScrollBar verticalBar = sc.getVerticalBar();
    sc.dispose();
    assertTrue( sc.isDisposed() );
    assertTrue( content.isDisposed() );
    assertTrue( horizontalBar.isDisposed() );
    assertTrue( verticalBar.isDisposed() );
  }

  @Test
  public void testContent() {
    ScrolledComposite sc = new ScrolledComposite( shell, SWT.V_SCROLL | SWT.H_SCROLL );
    assertNull( sc.getContent() );
    Composite content = new Composite( sc, SWT.NONE );
    sc.setContent( content );
    assertSame( content, sc.getContent() );
    assertTrue( content.isListening( SWT.Resize ) );
  }

  @Test
  public void testOrigin() {
    ScrolledComposite sc = new ScrolledComposite( shell, SWT.V_SCROLL | SWT.H_SCROLL );
    assertEquals( new Point( 0, 0 ), sc.getOrigin() );
    // test without content
    sc.setOrigin( 10, 10 );
    assertEquals( new Point( 0, 0 ), sc.getOrigin() );
    assertEquals( 0, sc.getHorizontalBar().getSelection() );
    assertEquals( 0, sc.getVerticalBar().getSelection() );
    // test with content
    Composite content = new Composite( sc, SWT.NONE );
    sc.setContent( content );
    sc.setOrigin( 10, 10 );
    assertEquals( new Point( 10, 10 ), sc.getOrigin() );
    assertEquals( 10, sc.getHorizontalBar().getSelection() );
    assertEquals( 10, sc.getVerticalBar().getSelection() );
    assertEquals( new Point( -10, -10 ), content.getLocation() );
    // test negative
    sc.setOrigin( -5, -5 );
    assertEquals( new Point( 10, 10 ), sc.getOrigin() );
    assertEquals( 10, sc.getHorizontalBar().getSelection() );
    assertEquals( 10, sc.getVerticalBar().getSelection() );
    assertEquals( new Point( -10, -10 ), content.getLocation() );
  }

  @Test
  public void testAlwaysShowScrollBars() {
    ScrolledComposite sc = new ScrolledComposite( shell, SWT.V_SCROLL | SWT.H_SCROLL );
    sc.setSize( 100, 100 );
    Composite content = new Composite( sc, SWT.NONE );
    content.setSize( 50, 50 );
    sc.setContent( content );
    assertFalse( sc.getAlwaysShowScrollBars() );
    assertFalse( sc.getVerticalBar().getVisible() );
    assertFalse( sc.getHorizontalBar().getVisible() );
    sc.setAlwaysShowScrollBars( true );
    assertTrue( sc.getAlwaysShowScrollBars() );
    assertTrue( sc.getVerticalBar().getVisible() );
    assertTrue( sc.getHorizontalBar().getVisible() );
  }

  @Test
  public void testLayout() {
    ScrolledComposite sc = new ScrolledComposite( shell, SWT.V_SCROLL | SWT.H_SCROLL );
    // test ignore layout
    sc.setLayout( new GridLayout() );
    assertFalse( sc.getLayout() instanceof GridLayout );
  }

  @Test
  public void testMinSize() {
    ScrolledComposite sc = new ScrolledComposite( shell, SWT.V_SCROLL | SWT.H_SCROLL );
    assertEquals( 0, sc.getMinWidth() );
    assertEquals( 0, sc.getMinHeight() );
    sc.setMinHeight( 10 );
    assertEquals( 0, sc.getMinWidth() );
    assertEquals( 10, sc.getMinHeight() );
    sc.setMinWidth( 10 );
    assertEquals( 10, sc.getMinWidth() );
    assertEquals( 10, sc.getMinHeight() );
    sc.setMinSize( 20, 20 );
    assertEquals( 20, sc.getMinWidth() );
    assertEquals( 20, sc.getMinHeight() );
    sc.setMinSize( new Point( 30, 30 ) );
    assertEquals( 30, sc.getMinWidth() );
    assertEquals( 30, sc.getMinHeight() );
    sc.setMinSize( null );
    assertEquals( 0, sc.getMinWidth() );
    assertEquals( 0, sc.getMinHeight() );
    sc.setMinSize( -20, -20 );
    assertEquals( 0, sc.getMinWidth() );
    assertEquals( 0, sc.getMinHeight() );
  }

  @Test
  public void testExpand() {
    ScrolledComposite sc = new ScrolledComposite( shell, SWT.V_SCROLL | SWT.H_SCROLL );
    assertFalse( sc.getExpandHorizontal() );
    assertFalse( sc.getExpandVertical() );
    sc.setExpandHorizontal( true );
    sc.setExpandVertical( true );
    assertTrue( sc.getExpandHorizontal() );
    assertTrue( sc.getExpandVertical() );
  }

  @Test
  public void testClientArea() {
    ScrolledComposite sc = new ScrolledComposite( shell, SWT.V_SCROLL | SWT.H_SCROLL );
    sc.setSize( 100, 100 );
    assertEquals( new Rectangle( 0, 0, 100, 100), sc.getClientArea() );
    sc.setAlwaysShowScrollBars( true );
    assertEquals( new Rectangle( 0, 0, 90, 90 ), sc.getClientArea() );
  }

  @Test
  public void testNeedHScroll() {
    ScrolledComposite sc = new ScrolledComposite( shell, SWT.V_SCROLL | SWT.H_SCROLL );
    sc.setSize( 100, 100 );
    assertFalse( sc.needHScroll( new Rectangle( 0, 0, 95, 95 ), false ) );
    assertTrue( sc.needHScroll( new Rectangle( 0, 0, 95, 95 ), true ) );
    sc.setExpandHorizontal( true );
    sc.setMinWidth( 95 );
    assertFalse( sc.needHScroll( new Rectangle( 0, 0, 50, 50 ), false ) );
    assertTrue( sc.needHScroll( new Rectangle( 0, 0, 50, 50 ), true ) );
    sc.setMinWidth( 50 );
    assertFalse( sc.needHScroll( new Rectangle( 0, 0, 50, 50 ), false ) );
    assertFalse( sc.needHScroll( new Rectangle( 0, 0, 50, 50 ), true ) );
    sc.setMinWidth( 150 );
    assertTrue( sc.needHScroll( new Rectangle( 0, 0, 50, 50 ), false ) );
    assertTrue( sc.needHScroll( new Rectangle( 0, 0, 50, 50 ), true ) );
  }

  @Test
  public void testNeedVScroll() {
    ScrolledComposite sc = new ScrolledComposite( shell, SWT.V_SCROLL | SWT.H_SCROLL );
    sc.setSize( 100, 100 );
    assertFalse( sc.needVScroll( new Rectangle( 0, 0, 95, 95 ), false ) );
    assertTrue( sc.needVScroll( new Rectangle( 0, 0, 95, 95 ), true ) );
    sc.setExpandVertical( true );
    sc.setMinHeight( 95 );
    assertFalse( sc.needVScroll( new Rectangle( 0, 0, 50, 50 ), false ) );
    assertTrue( sc.needVScroll( new Rectangle( 0, 0, 50, 50 ), true ) );
    sc.setMinHeight( 50 );
    assertFalse( sc.needVScroll( new Rectangle( 0, 0, 50, 50 ), false ) );
    assertFalse( sc.needVScroll( new Rectangle( 0, 0, 50, 50 ), true ) );
    sc.setMinHeight( 150 );
    assertTrue( sc.needVScroll( new Rectangle( 0, 0, 50, 50 ), false ) );
    assertTrue( sc.needVScroll( new Rectangle( 0, 0, 50, 50 ), true ) );
  }

  @Test
  public void testShowFocusedControl() {
    ScrolledComposite sc = new ScrolledComposite( shell, SWT.V_SCROLL | SWT.H_SCROLL );
    assertFalse( sc.getShowFocusedControl() );
    sc.setShowFocusedControl( true );
    assertTrue( sc.getShowFocusedControl() );
  }

  @Test
  public void testShowControl() {
    ScrolledComposite sc = new ScrolledComposite( shell, SWT.V_SCROLL | SWT.H_SCROLL );
    Composite content = new Composite( sc, SWT.NONE );
    Button button = new Button( shell, SWT.PUSH );
    sc.setContent( content );
    try {
      sc.showControl( null );
      fail( "Null value is not allowed" );
    } catch( IllegalArgumentException expected ) {
    }
    Button disposedControl = new Button( shell, SWT.PUSH );
    disposedControl.dispose();
    try {
      sc.showControl( disposedControl );
      fail( "Disposed control is not allowed" );
    } catch( IllegalArgumentException expected ) {
    }
    try {
      sc.showControl( button );
      fail( "Control that is not a child of the composite is not allowed" );
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testIsSerializable() throws Exception {
    ScrolledComposite sc = new ScrolledComposite( shell, SWT.V_SCROLL | SWT.H_SCROLL );
    sc.setContent( new Label( sc, SWT.NONE ) );

    ScrolledComposite deserializedSC = Fixture.serializeAndDeserialize( sc );

    assertTrue( deserializedSC.getContent() instanceof Label );
    assertTrue( deserializedSC.getLayout() instanceof ScrolledCompositeLayout );
  }

}
