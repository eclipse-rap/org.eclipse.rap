/*******************************************************************************
 * Copyright (c) 2007, 2010 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.textsize;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.rwt.SessionSingletonBase;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;


final class ProbeResultStore {

  private final Map probeResults;

  static ProbeResultStore getInstance() {
    return ( ProbeResultStore )SessionSingletonBase.getInstance( ProbeResultStore.class );
  }
  
  ProbeResultStore() {
    probeResults = new HashMap();
  }
   
  ProbeResult createProbeResult( Probe probe, Point size ) {
    ProbeResult result = new ProbeResult( probe, size );
    probeResults.put( probe.getFontData(), result );
    return result;
  }

  ProbeResult getProbeResult( FontData fontData ) {
    return ( ProbeResult )probeResults.get( fontData );
  }
  
  boolean containsProbeResult( FontData fontData ) {
    return getProbeResult( fontData ) != null;
  }
}