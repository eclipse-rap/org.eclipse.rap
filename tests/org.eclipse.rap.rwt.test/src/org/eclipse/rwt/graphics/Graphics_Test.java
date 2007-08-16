/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rwt.graphics;

import junit.framework.TestCase;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;


public class Graphics_Test extends TestCase {

  public void testGetColor() throws Exception {
    Color black = Graphics.getColor( 0, 0, 0 );
    assertEquals( 0, black.getRed() );
    assertEquals( 0, black.getGreen() );
    assertEquals( 0, black.getBlue() );
    Color color = Graphics.getColor( 15, 127, 255 );
    assertEquals( 15, color.getRed() );
    assertEquals( 127, color.getGreen() );
    assertEquals( 255, color.getBlue() );
    Color red = Graphics.getColor( 255, 0, 0 );
    Color red2 = Graphics.getColor( 255, 0, 0 );
    assertSame( red, red2 );
  }

  public void testGetFont() throws Exception {
    Font font = Graphics.getFont( "Times", 12, SWT.BOLD );
    assertNotNull( font );
  }
}
