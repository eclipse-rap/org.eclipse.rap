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


public class ProbeStore_Test extends TestCase {
  private static final FontData FONT_DATA = new FontData( "arial", 23, SWT.BOLD );
  
  private ProbeStore probeStore;
  private TextSizeStorage textSizeStorage;

  public void testInitialSize() {
    assertEquals( 0, probeStore.getSize() );
  }
  
  public void testProbeCreation() {
    Probe probe = createProbe();
    
    assertEquals( 1, probeStore.getSize() );
    assertSame( FONT_DATA, probe.getFontData() );
  }
  
  public void testGetProbe() {
    Probe probe = createProbe();
    
    assertSame( probe, probeStore.getProbe( FONT_DATA ) );
  }
  
  public void testGetProbes() {
    Probe probe = createProbe();
    
    Probe[] probes = probeStore.getProbes();
    
    assertEquals( 1, probes.length );
    assertSame( probe, probes[ 0 ] );
  }
  
  public void testGetProbesFromGlobalStorage() {
    textSizeStorage.storeFont( FONT_DATA );

    Probe[] probes = probeStore.getProbes();
    
    assertEquals( 1, probes.length );
    assertSame( FONT_DATA, probes[ 0 ].getFontData() );
  }
  
  protected void setUp() {
    textSizeStorage = new TextSizeStorage();
    probeStore = new ProbeStore( textSizeStorage );
  }
  
  private Probe createProbe() {
    return probeStore.createProbe( FONT_DATA );
  }
}
