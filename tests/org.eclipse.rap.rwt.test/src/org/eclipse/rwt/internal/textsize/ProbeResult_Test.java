/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.textsize;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;


public class ProbeResult_Test extends TestCase {

  public void testGetProbe() {
    Probe probe = createProbe( "" );
    ProbeResult probeResult = new ProbeResult( probe, null );
    assertSame( probe, probeResult.getProbe() );
  }
  
  public void testSize() {
    Point size = new Point( 0, 0 );
    ProbeResult probeResult = new ProbeResult( null, size );
    assertSame( size, probeResult.getSize() );
  }
  
  public void testGetAvgSizeFor10CharsLengthAnd10PixelsWidth() {
    ProbeResult probeResult = createProbeResult( "0123456789", 10 );
    assertEquals( 1, probeResult.getAvgCharWidth(), 0.01 );
  }
  
  public void testGetAvgSizeFor10CharsLengthAnd20PixelsWidth() {
    ProbeResult probeResult = createProbeResult( "0123456789", 20 );
    assertEquals( 2, probeResult.getAvgCharWidth(), 0.01 );
  }
  
  public void testGetAvgSizeFor10CharsLengthAnd5PixelsWidth() {
    ProbeResult probeResult = createProbeResult( "0123456789", 5 );
    assertEquals( 0.5, probeResult.getAvgCharWidth(), 0.01 );
  }
  
  public void testGetAvgSizeFor10CharsLengthAnd2PixelsWidth() {
    ProbeResult probeResult = createProbeResult( "0123456789", 2 );
    assertEquals( 0.2, probeResult.getAvgCharWidth(), 0.01 );
  }
  
  public void testIsSerializable() throws Exception {
    ProbeResult probeResult = createProbeResult( "0123456789", 2 );
    
    ProbeResult deserializeProbeResult = Fixture.serializeAndDeserialize( probeResult );
    
    assertEquals( probeResult.getSize(), deserializeProbeResult.getSize() );
    assertEquals( probeResult.getProbe(), deserializeProbeResult.getProbe() );
  }

  private static ProbeResult createProbeResult( String text, int width ) {
    Probe probe = createProbe( text );
    return new ProbeResult( probe, new Point( width, 0 ) );
  }

  private static Probe createProbe( String text ) {
    return new Probe( text, new FontData( "font-name", 1, SWT.NORMAL ) );
  }
}
