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
package org.eclipse.swt.internal.graphics;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.rwt.SessionSingletonBase;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.graphics.TextSizeProbeStore.IProbe;


final class TextSizeProbeResults {
  
  interface IProbeResult {
    IProbe getProbe();
    Point getSize();
    float getAvgCharWidth();
  }
  
  private static class ProbeResultImpl implements IProbeResult {
    private final Point size;
    private final IProbe probe;
    private float avgCharWidth;
  
    ProbeResultImpl( IProbe probe, Point size ) {
      this.probe = probe;
      this.size = size;
    }
  
    public IProbe getProbe() {
      return probe;
    }
  
    public Point getSize() {
      return size;
    }
  
    public float getAvgCharWidth() {
      if( avgCharWidth == 0 ) {
        BigDecimal width = new BigDecimal( getSize().x );
        BigDecimal charCount = new BigDecimal( getProbe().getText().length() );
        avgCharWidth = width.divide( charCount, 2, BigDecimal.ROUND_HALF_UP ).floatValue();
      }
      return avgCharWidth;
    }
  }

  static TextSizeProbeResults getInstance() {
    return ( TextSizeProbeResults )SessionSingletonBase.getInstance( TextSizeProbeResults.class );
  }
  
  private final Map probeResults;

  private TextSizeProbeResults() {
    probeResults = new HashMap();
  }
   
  IProbeResult createProbeResult( IProbe probe, Point size ) {
    IProbeResult result = new ProbeResultImpl( probe, size );
    probeResults.put( probe.getFontData(), result );
    return result;
  }

  IProbeResult getProbeResult( FontData fontData ) {
    return ( IProbeResult )probeResults.get( fontData );
  }
  
  boolean containsProbeResult( FontData fontData ) {
    return getProbeResult( fontData ) != null;
  }
}