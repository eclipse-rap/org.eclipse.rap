/*******************************************************************************
 * Copyright (c) 2011 Frank Appel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Frank Appel - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.textsize;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;


public class ProbeResultStore_Test extends TestCase {
  private static final FontData FONT_DATA_2 = new FontData( "helvetia", 22, SWT.BOLD );
  private static final FontData FONT_DATA_1 = new FontData( "arial", 23, SWT.ITALIC );
  private static final Probe PROBE_OF_FONT_DATA_1 = new Probe( FONT_DATA_1 );
  private static final Point SIZE = new Point( 23, 45 );
  
  private ProbeResultStore probeResultStore;

  public void testCreateAndGetProbeResult() {
    ProbeResult created = probeResultStore.createProbeResult( PROBE_OF_FONT_DATA_1, SIZE );
    ProbeResult found = probeResultStore.getProbeResult( FONT_DATA_1 );
    
    assertNotNull( created );
    assertSame( created, found );
    assertNull( probeResultStore.getProbeResult( FONT_DATA_2 ) );
  }
  
  public void testContains() {
    probeResultStore.createProbeResult( PROBE_OF_FONT_DATA_1, SIZE );

    assertTrue( probeResultStore.containsProbeResult( FONT_DATA_1 ) );
  }
  
  public void testIsSerializable() throws Exception {
    probeResultStore.createProbeResult( PROBE_OF_FONT_DATA_1, SIZE );
    
    ProbeResultStore deserialized = Fixture.serializeAndDeserialize( probeResultStore );
    
    assertTrue( deserialized.containsProbeResult( FONT_DATA_1 ) );
  }
  
  protected void setUp() throws Exception {
    probeResultStore = new ProbeResultStore();
  }
}
