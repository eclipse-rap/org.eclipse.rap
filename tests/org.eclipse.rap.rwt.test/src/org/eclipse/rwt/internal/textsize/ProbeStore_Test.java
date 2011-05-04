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

import org.eclipse.rwt.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;


public class ProbeStore_Test extends TestCase {
  private static final FontData FONT_DATA = new FontData( "arial", 23, SWT.BOLD );
  
  private ProbeStore probeStore;

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
    
    assertEquals( 1, probeStore.getProbes().length );
    assertSame( probe, probeStore.getProbes()[ 0 ] );
  }
  
  protected void setUp() {
    Fixture.setUp();
    probeStore = new ProbeStore();
  }
  
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
  
  private Probe createProbe() {
    return probeStore.createProbe( FONT_DATA );
  }
}
