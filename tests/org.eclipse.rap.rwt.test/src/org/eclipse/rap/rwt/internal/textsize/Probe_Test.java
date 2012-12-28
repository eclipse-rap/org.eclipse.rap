/*******************************************************************************
 * Copyright (c) 2011, 2012 Frank Appel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Frank Appel - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.textsize;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.junit.Test;


public class Probe_Test {
  private static final String PROBE_STRING = "probeString";
  private static final FontData FONT_DATA = new FontData( "arial", 23, SWT.NONE );

  @Test
  public void testGetter() {
    Probe probe1 = new Probe( PROBE_STRING, FONT_DATA );
    Probe probe2 = new Probe( FONT_DATA );

    assertSame( FONT_DATA, probe1.getFontData() );
    assertSame( PROBE_STRING, probe1.getText() );
    assertSame( FONT_DATA, probe2.getFontData() );
    assertSame( Probe.DEFAULT_PROBE_STRING, probe2.getText() );
  }

  @Test
  public void testParamTextMustNotBeNull() {
    try {
      new Probe( null, FONT_DATA );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  @Test
  public void testParamFontDataMustNotBeNull() {
    try {
      new Probe( PROBE_STRING, null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  @Test
  public void testEquals() {
    Probe probe = new Probe( FONT_DATA );

    assertTrue( probe.equals( probe ) );
    assertTrue( probe.equals( new Probe( FONT_DATA ) ) );
    assertFalse( probe.equals( null ) );
    assertFalse( probe.equals( new Object() ) );
    assertFalse( probe.equals( new Probe( "otherText", FONT_DATA ) ) );
    assertFalse( probe.equals( new Probe( new FontData( "helvetia", 23, SWT.BOLD ) ) ) );
  }

  @Test
  public void testHashcode() {
    assertEquals( -290887150, new Probe( FONT_DATA ).hashCode() );
  }

  @Test
  public void testIsSerializable() throws Exception {
    Probe probe = new Probe( "text", new FontData( "name", 1, SWT.ITALIC ) );

    Probe deserializedProbe = Fixture.serializeAndDeserialize( probe );

    assertEquals( probe.getText(), deserializedProbe.getText() );
    assertEquals( probe.getFontData(), deserializedProbe.getFontData() );
  }

  // see bug 374914
  @Test
  public void testDefaultProbeString() {
    assertTrue( Probe.DEFAULT_PROBE_STRING.indexOf( "AzByCx" ) != -1 );
  }

}
