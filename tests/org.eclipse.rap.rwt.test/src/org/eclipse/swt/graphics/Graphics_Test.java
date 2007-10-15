/*******************************************************************************
 * Copyright (c) 2002-2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.graphics;

import junit.framework.TestCase;

import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;


public class Graphics_Test extends TestCase {

  public void testTextExtentNull() throws Exception {
    Font font = Graphics.getFont( "Arial", 12, SWT.BOLD );
    assertNotNull( font );
    try {
      Graphics.textExtent( font , null, 0 );
      fail( "Null string should throw IAE" );
    } catch( final IllegalArgumentException e ) {
      // expected
    }
  }

  public void testStringExtentNull() throws Exception {
    Font font = Graphics.getFont( "Arial", 12, SWT.BOLD );
    assertNotNull( font );
    try {
      Graphics.stringExtent( font , null );
      fail( "Null string should throw IAE" );
    } catch( final IllegalArgumentException e ) {
      // expected
    }
  }
  
  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
