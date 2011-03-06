/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 *    Frank Appel - replaced singletons and static fields (Bug 337787)
 ******************************************************************************/
package org.eclipse.swt.internal.graphics;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;


public class ResourceFactory_Test extends TestCase {

  public void testGetColor() {
    assertEquals( 0, ResourceFactory.colorsCount() );
    Color color = Graphics.getColor( 15, 127, 255 );
    assertEquals( 15, color.getRed() );
    assertEquals( 127, color.getGreen() );
    assertEquals( 255, color.getBlue() );
    assertEquals( 1, ResourceFactory.colorsCount() );
    Color red = Graphics.getColor( 255, 0, 0 );
    assertEquals( 2, ResourceFactory.colorsCount() );
    Color red2 = Graphics.getColor( 255, 0, 0 );
    assertEquals( 2, ResourceFactory.colorsCount() );
    assertSame( red, red2 );
  }

  public void testGetFont() {
    assertEquals( 0, ResourceFactory.fontsCount() );
    Font font1 = Graphics.getFont( "Times", 12, SWT.BOLD );
    assertEquals( "Times", font1.getFontData()[ 0 ].getName() );
    assertEquals( 12, font1.getFontData()[ 0 ].getHeight() );
    assertEquals( SWT.BOLD, font1.getFontData()[ 0 ].getStyle() );
    assertNotNull( font1 );
    assertEquals( 1, ResourceFactory.fontsCount() );
    Font font1a = Graphics.getFont( "Times", 12, SWT.BOLD );
    assertSame( font1, font1a );
    assertEquals( 1, ResourceFactory.fontsCount() );
  }

  protected void setUp() throws Exception {
    Fixture.createRWTContext();
    Fixture.createServiceContext();
  }

  protected void tearDown() throws Exception {
    Fixture.disposeOfServiceContext();
    Fixture.disposeOfRWTContext();
  }
}
