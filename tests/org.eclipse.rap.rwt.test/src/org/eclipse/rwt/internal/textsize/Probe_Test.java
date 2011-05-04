/*******************************************************************************
 * Copyright (c) 2011 Frank Appel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Frank Appel - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.textsize;

import junit.framework.TestCase;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;


public class Probe_Test extends TestCase {
  private static final String PROBE_STRING = "probeString";
  private static final FontData FONT_DATA = new FontData( "arial", 23, SWT.NONE );

  public void testGetter() {
    Probe probe1 = new Probe( PROBE_STRING, FONT_DATA );
    Probe probe2 = new Probe( FONT_DATA );
    
    assertSame( FONT_DATA, probe1.getFontData() );
    assertSame( PROBE_STRING, probe1.getText() );
    assertSame( FONT_DATA, probe2.getFontData() );
    assertSame( Probe.DEFAULT_PROBE_STRING, probe2.getText() );
  }
  
  public void testParamTextMustNotBeNull() {
    try {
      new Probe( null, FONT_DATA );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  public void testParamFontDataMustNotBeNull() {
    try {
      new Probe( PROBE_STRING, null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }
  
  public void testEquals() {
    Probe probe1 = new Probe( FONT_DATA );
    
    assertTrue( probe1.equals( probe1 ) );
    assertTrue( probe1.equals( new Probe( FONT_DATA ) ) );
    assertFalse( probe1.equals( null ) );
    assertFalse( probe1.equals( new Object() ) );
    assertFalse( probe1.equals( new Probe( "otherText", FONT_DATA ) ) );
    assertFalse( probe1.equals( new Probe( new FontData( "helvetia", 23, SWT.BOLD ) ) ) );
  }
  
  public void testHashcode() {
    assertEquals( 930413418, new Probe( FONT_DATA ).hashCode() );
  }
}
