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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.junit.Before;
import org.junit.Test;


public class ProbeResultStore_Test {
  private static final FontData FONT_DATA_2 = new FontData( "helvetia", 22, SWT.BOLD );
  private static final FontData FONT_DATA_1 = new FontData( "arial", 23, SWT.ITALIC );
  private static final Probe PROBE_OF_FONT_DATA_1 = new Probe( FONT_DATA_1 );
  private static final Point SIZE = new Point( 23, 45 );

  private ProbeResultStore probeResultStore;

  @Before
  public void setUp() {
    probeResultStore = new ProbeResultStore();
  }

  @Test
  public void testCreateAndGetProbeResult() {
    ProbeResult created = probeResultStore.createProbeResult( PROBE_OF_FONT_DATA_1, SIZE );
    ProbeResult found = probeResultStore.getProbeResult( FONT_DATA_1 );

    assertNotNull( created );
    assertSame( created, found );
    assertNull( probeResultStore.getProbeResult( FONT_DATA_2 ) );
  }

  @Test
  public void testContains() {
    probeResultStore.createProbeResult( PROBE_OF_FONT_DATA_1, SIZE );

    assertTrue( probeResultStore.containsProbeResult( FONT_DATA_1 ) );
  }

  @Test
  public void testIsSerializable() throws Exception {
    probeResultStore.createProbeResult( PROBE_OF_FONT_DATA_1, SIZE );

    ProbeResultStore deserialized = Fixture.serializeAndDeserialize( probeResultStore );

    assertTrue( deserialized.containsProbeResult( FONT_DATA_1 ) );
  }

}
