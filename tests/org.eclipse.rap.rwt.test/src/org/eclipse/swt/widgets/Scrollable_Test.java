/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
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

import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class Scrollable_Test {

  private Display display;
  private Shell shell;

  @Before
  public void setUp() {
    Fixture.setUp();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    display = new Display();
    shell = new Shell( display, SWT.NONE );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testComputeTrim() {
    Composite scrollable = new Composite( shell, SWT.BORDER );
    Rectangle trim = scrollable.computeTrim( 20, 30, 200, 300 );
    int borderWidth = scrollable.getBorderWidth();
    assertEquals( 20 - borderWidth, trim.x );
    assertEquals( 30 - borderWidth, trim.y );
    assertEquals( 200 + ( 2 * borderWidth ), trim.width );
    assertEquals( 300 + ( 2 * borderWidth ), trim.height );
  }

  @Test
  public void testComputeTrimWithPadding() {
    final Rectangle padding = new Rectangle( 1, 2, 3, 4 );
    Composite scrollable = new Composite( shell, SWT.BORDER ) {
      @Override
      Rectangle getPadding() {
        return padding;
      }
    };
    int borderWidth = scrollable.getBorderWidth();
    Rectangle trim = scrollable.computeTrim( 20, 30, 200, 300 );
    assertEquals( 20 - padding.x - borderWidth, trim.x );
    assertEquals( 30 - padding.y - borderWidth, trim.y );
    assertEquals( 205, trim.width );
    assertEquals( 306, trim.height );
  }

  @Test
  public void testComputeTrimWithScrollbars() {
    Composite scrollable = new Composite( shell, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL );
    int borderWidth = scrollable.getBorderWidth();
    Rectangle trim = scrollable.computeTrim( 20, 30, 200, 300 );
    assertEquals( 20 - borderWidth, trim.x );
    assertEquals( 30 - borderWidth, trim.y );
    assertEquals( 212, trim.width );
    assertEquals( 312, trim.height );
  }

  @Test
  public void testGetClientArea() {
    Composite scrollable = new Composite( shell, SWT.BORDER );
    scrollable.setSize( 100, 100 );
    Rectangle expected = new Rectangle( 0, 0, 98, 98 );
    assertEquals( expected, scrollable.getClientArea() );
  }

  @Test
  public void testGetClientArea_WithScrollbars() {
    Composite scrollable = new Composite( shell, SWT.V_SCROLL | SWT.H_SCROLL );
    scrollable.getHorizontalBar().setVisible( true );
    scrollable.getVerticalBar().setVisible( true );
    scrollable.setSize( 100, 100 );
    Rectangle expected = new Rectangle( 0, 0, 90, 90 );
    assertEquals( expected, scrollable.getClientArea() );
  }

  @Test
  public void testClientAreaWithPadding() {
    Composite scrollable = new Composite( shell, SWT.BORDER ) {
      @Override
      int getVScrollBarWidth() {
        return 20;
      }
      @Override
      int getHScrollBarHeight() {
        return 20;
      }
      @Override
      Rectangle getPadding() {
        return new Rectangle( 10, 10, 10, 10 );
      }
    };
    scrollable.setSize( 100, 100 );
    assertEquals( 1, scrollable.getBorderWidth() );
    Rectangle expected = new Rectangle( 10, 10, 68, 68 );
    assertEquals( expected, scrollable.getClientArea() );
  }

  @Test
  public void testClientAreaIsZero() {
    Composite scrollable = new Composite( shell, SWT.BORDER );
    scrollable.setSize( 0, 0 );
    Rectangle expected = new Rectangle( 0, 0, 0, 0 );
    assertEquals( expected, scrollable.getClientArea() );
  }

  @Test
  public void testClientAreaIsZeroWithPadding() {
    Composite scrollable = new Composite( shell, SWT.BORDER ) {
      @Override
      int getVScrollBarWidth() {
        return 20;
      }
      @Override
      int getHScrollBarHeight() {
        return 20;
      }
      @Override
      Rectangle getPadding() {
        return new Rectangle( 10, 10, 10, 10 );
      }
    };
    scrollable.setSize( 25, 25 );
    Rectangle expected = new Rectangle( 10, 10, 0, 0 );
    assertEquals( expected, scrollable.getClientArea() );
  }

  @Test
  public void testScrollBarsAreDisposed() {
    Composite scrollable = new Composite( shell, SWT.V_SCROLL | SWT.H_SCROLL );
    ScrollBar verticalBar = scrollable.getVerticalBar();
    ScrollBar horizontalBar = scrollable.getHorizontalBar();

    scrollable.dispose();

    assertTrue( verticalBar.isDisposed() );
    assertTrue( horizontalBar.isDisposed() );
  }

  @Test
  public void testDisposeWithoutScrollBars() {
    Composite scrollable = new Composite( shell, SWT.NONE );

    scrollable.dispose();

    assertTrue( scrollable.isDisposed() );
  }

}
