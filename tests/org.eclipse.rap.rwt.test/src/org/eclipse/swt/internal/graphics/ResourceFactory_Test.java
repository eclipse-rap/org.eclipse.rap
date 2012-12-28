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
 *    Frank Appel - replaced singletons and static fields (Bug 337787)
 ******************************************************************************/
package org.eclipse.swt.internal.graphics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ResourceFactory_Test {

  private ResourceFactory resourceFactory;

  @Before
  public void setUp() {
    Fixture.createApplicationContext();
    Fixture.createServiceContext();
    resourceFactory = new ResourceFactory();
  }

  @After
  public void tearDown() {
    Fixture.disposeOfServiceContext();
    Fixture.disposeOfApplicationContext();
  }

  @Test
  public void testGetColor() {
    Color color = resourceFactory.getColor( 15, 127, 255 );
    assertEquals( 15, color.getRed() );
    assertEquals( 127, color.getGreen() );
    assertEquals( 255, color.getBlue() );
  }

  @Test
  public void testGetColorReturnsSharedColor() {
    Color red1 = resourceFactory.getColor( 255, 0, 0 );
    Color red2 = resourceFactory.getColor( 255, 0, 0 );
    assertSame( red1, red2 );
  }

  @Test
  public void testGetFont() {
    Font font1 = resourceFactory.getFont( new FontData( "Times", 12, SWT.BOLD ) );
    assertEquals( "Times", font1.getFontData()[ 0 ].getName() );
    assertEquals( 12, font1.getFontData()[ 0 ].getHeight() );
    assertEquals( SWT.BOLD, font1.getFontData()[ 0 ].getStyle() );
  }

  @Test
  public void testGetFontReturnsSharedFont() {
    Font font1 = resourceFactory.getFont( new FontData( "Times", 12, SWT.BOLD ) );
    Font font2 = resourceFactory.getFont( new FontData( "Times", 12, SWT.BOLD ) );
    assertSame( font1, font2 );
  }

}
