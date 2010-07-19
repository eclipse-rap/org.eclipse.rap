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
package org.eclipse.swt.internal.graphics;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;


public class FontDataFactory_Test extends TestCase {

  public void testFindFontData() {
    FontData fontData = FontDataFactory.findFontData( "Times", 18, SWT.NORMAL );
    assertNotNull( fontData );
    assertEquals( "Times", fontData.getName() );
    assertEquals( 18, fontData.getHeight() );
    assertEquals( SWT.NORMAL, fontData.getStyle() );
  }

  public void testFindFontDataFromFontData() {
    FontData origFontData = new FontData( "Times", 18, SWT.NORMAL );
    FontData fontData = FontDataFactory.findFontData( origFontData );
    assertNotNull( fontData );
    assertNotSame( origFontData, fontData );
    assertEquals( "Times", fontData.getName() );
    assertEquals( 18, fontData.getHeight() );
    assertEquals( SWT.NORMAL, fontData.getStyle() );
  }

  public void testSameFontData() {
    FontData fontData1 = FontDataFactory.findFontData( "Times", 18, SWT.NORMAL );
    assertNotNull( fontData1 );
    FontData fontData2 = FontDataFactory.findFontData( "Times", 18, SWT.NORMAL );
    assertNotNull( fontData2 );
    assertSame( fontData1, fontData2 );
  }

  protected void setUp() throws Exception {
    Fixture.setUp();
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
}
