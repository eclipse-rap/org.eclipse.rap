/*******************************************************************************
 * Copyright (c) 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.graphics;

import junit.framework.TestCase;

import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;


public class Cursor_Test extends TestCase {

  public void testCreate() {
    Cursor arrow = Graphics.getCursor( SWT.CURSOR_ARROW );
    Cursor cross = Graphics.getCursor( SWT.CURSOR_CROSS );
    assertNotNull( arrow );
    assertNotNull( cross );
    assertNotSame( arrow, cross );
    assertFalse( arrow.equals( cross ) );
  }

  public void testSame() {
    Cursor arrow1 = Graphics.getCursor( SWT.CURSOR_ARROW );
    Cursor arrow2 = Graphics.getCursor( SWT.CURSOR_ARROW );
    assertSame( arrow1, arrow2 );
  }

  public void testSameSystem() {
    Display display = new Display();
    Cursor arrow1 = display.getSystemCursor( SWT.CURSOR_ARROW );
    Cursor arrow2 = Graphics.getCursor( SWT.CURSOR_ARROW );
    assertSame( arrow1, arrow2 );
  }
  
  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
