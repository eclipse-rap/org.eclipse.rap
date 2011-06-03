/*******************************************************************************
 * Copyright (c) 2002, 2010 Innoopract Informationssysteme GmbH.
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
import org.eclipse.rwt.lifecycle.PhaseId;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;

public class Scrollable_Test extends TestCase {

  protected void setUp() throws Exception {
    Fixture.setUp();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testComputeTrim() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    Composite composite = new Composite( shell, SWT.BORDER );
    assertEquals( 1, composite.getBorderWidth() );
    Rectangle trim = composite.computeTrim( 20, 30, 200, 300 );
    assertEquals( 19, trim.x );
    assertEquals( 29, trim.y );
    assertEquals( 202, trim.width );
    assertEquals( 302, trim.height );

    composite = new Composite( shell, SWT.BORDER ) {
      private static final long serialVersionUID = 1L;
      int getVScrollBarWidth() {
        return 20;
      }
      int getHScrollBarHeight() {
        return 20;
      }
      Rectangle getPadding() {
        return new Rectangle( 10, 10, 10, 10 );
      }
    };
    assertEquals( 1, composite.getBorderWidth() );
    trim = composite.computeTrim( 20, 30, 200, 300 );
    assertEquals( 9, trim.x );
    assertEquals( 19, trim.y );
    assertEquals( 232, trim.width );
    assertEquals( 332, trim.height );
  }

  public void testGetClientArea() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    Composite composite = new Composite( shell, SWT.BORDER );
    composite.setSize( 100, 100 );
    assertEquals( 1, composite.getBorderWidth() );
    Rectangle expected = new Rectangle( 0, 0, 98, 98 );
    assertEquals( expected, composite.getClientArea() );

    composite = new Composite( shell, SWT.BORDER ) {
      private static final long serialVersionUID = 1L;
      int getVScrollBarWidth() {
        return 20;
      }
      int getHScrollBarHeight() {
        return 20;
      }
      Rectangle getPadding() {
        return new Rectangle( 10, 10, 10, 10 );
      }
    };
    composite.setSize( 100, 100 );
    assertEquals( 1, composite.getBorderWidth() );
    expected = new Rectangle( 10, 10, 68, 68 );
    assertEquals( expected, composite.getClientArea() );
  }

  public void testClientAreaIsZero() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    Composite composite = new Composite( shell, SWT.BORDER );
    composite.setSize( 0, 0 );
    assertEquals( 1, composite.getBorderWidth() );
    Rectangle expected = new Rectangle( 0, 0, 0, 0 );
    assertEquals( expected, composite.getClientArea() );
    composite = new Composite( shell, SWT.BORDER ) {
      private static final long serialVersionUID = 1L;
      int getVScrollBarWidth() {
        return 20;
      }
      int getHScrollBarHeight() {
        return 20;
      }
      Rectangle getPadding() {
        return new Rectangle( 10, 10, 10, 10 );
      }
    };
    composite.setSize( 25, 25 );
    assertEquals( 1, composite.getBorderWidth() );
    expected = new Rectangle( 10, 10, 0, 0 );
    assertEquals( expected, composite.getClientArea() );
  }

}
