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

  public void testFind() {
    FontData fontData = new FontData( "Times", 18, SWT.NORMAL );
    FontData result = FontDataFactory.findFontData( fontData );
    assertNotNull( result );
    assertNotSame( fontData, result );
    assertEquals( "Times", result.getName() );
    assertEquals( 18, result.getHeight() );
    assertEquals( SWT.NORMAL, result.getStyle() );
  }

  public void testSafeCopy() {
    FontData fontData = new FontData( "Times", 18, SWT.NORMAL );
    FontData result = FontDataFactory.findFontData( fontData );
    assertNotSame( fontData, result );
    fontData.setHeight( 23 );
    assertEquals( 18, result.getHeight() );
  }

  public void testShared() {
    FontData fontData1 = new FontData( "Times", 18, SWT.NORMAL );
    FontData result1 = FontDataFactory.findFontData( fontData1 );
    FontData fontData2 = new FontData( "Times", 18, SWT.NORMAL );
    FontData result2 = FontDataFactory.findFontData( fontData2 );
    assertSame( result1, result2 );
  }

  protected void setUp() throws Exception {
    Fixture.setUp();
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
}
