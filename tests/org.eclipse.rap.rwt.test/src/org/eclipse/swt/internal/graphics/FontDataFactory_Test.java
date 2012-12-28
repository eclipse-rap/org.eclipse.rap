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
package org.eclipse.swt.internal.graphics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.junit.Before;
import org.junit.Test;


public class FontDataFactory_Test {

  private FontDataFactory fontDataFactory;

  @Before
  public void setUp() {
    fontDataFactory = new FontDataFactory();
  }

  @Test
  public void testFindFontData() {
    FontData fontData = new FontData( "Times", 18, SWT.NORMAL );
    FontData result = fontDataFactory.findFontData( fontData );
    assertNotNull( result );
    assertNotSame( fontData, result );
    assertEquals( "Times", result.getName() );
    assertEquals( 18, result.getHeight() );
    assertEquals( SWT.NORMAL, result.getStyle() );
  }

  @Test
  public void testFindFontDataReturnsSafeCopy() {
    FontData fontData = new FontData( "Times", 18, SWT.NORMAL );
    FontData result = fontDataFactory.findFontData( fontData );
    assertNotSame( fontData, result );
    fontData.setHeight( 23 );
    assertEquals( 18, result.getHeight() );
  }

  @Test
  public void testFindFontDataReturnsSharedInstance() {
    FontData fontData1 = new FontData( "Times", 18, SWT.NORMAL );
    FontData result1 = fontDataFactory.findFontData( fontData1 );
    FontData fontData2 = new FontData( "Times", 18, SWT.NORMAL );
    FontData result2 = fontDataFactory.findFontData( fontData2 );
    assertSame( result1, result2 );
  }

}
