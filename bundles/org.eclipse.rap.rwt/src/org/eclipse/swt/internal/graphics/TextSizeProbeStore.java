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
import java.util.*;

import org.eclipse.rwt.SessionSingletonBase;
import org.eclipse.rwt.internal.engine.RWTFactory;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.service.IServiceStateInfo;
import org.eclipse.swt.graphics.*;


final class TextSizeProbeStore {
  
  // TODO [fappel]: improve probe text determination...
  static String DEFAULT_PROBE;
  static {
    StringBuffer result = new StringBuffer();
    for( int i = 33; i < 122; i++ ) {
      if( i != 34 && i != 39 ) {
        result.append( ( char ) i );
      }
    }
    DEFAULT_PROBE = result.toString();
  }

  private static final String PROBE_REQUESTS
    = TextSizeProbeStore.class.getName() + "#probeRequests";
  
  private static Map probes = new HashMap(); 
  
  public interface IProbe {
    FontData getFontData();
    String getText();
  }
  
  public interface IProbeResult {
    IProbe getProbe();
    Point getSize();
    float getAvgCharWidth();
  }
  
  private static class ProbeImpl implements IProbe {
    private final String text;
    private final FontData fontData;

    ProbeImpl( String text, FontData fontData ) {
      this.text = text;
      this.fontData = fontData;
    }

    public FontData getFontData() {
      return fontData;
    }

    public String getText() {
      return text;
    }
  }
  
  private final Map probeResults;

  private TextSizeProbeStore() {
    probeResults = new HashMap();
  }
   
  static TextSizeProbeStore getInstance() {
    Object instance = SessionSingletonBase.getInstance( TextSizeProbeStore.class );
    return ( TextSizeProbeStore )instance;
  }
  
  IProbeResult getProbeResult( final FontData fontData ) {
    return ( IProbeResult )probeResults.get( fontData );
  }
  
  boolean containsProbeResult( final FontData fontData ) {
    return getProbeResult( fontData ) != null;
  }
  
  IProbeResult createProbeResult( final IProbe probe, final Point size ) {
    FontData fontData = probe.getFontData();
    IProbeResult result = new IProbeResult() {
      private float avgCharWidth;
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
    };
    probeResults.put( fontData, result );
    return result;
  }

  
  ////////////////////////////
  // application global probes
  
  public static IProbe[] getProbeList() {
    IProbe[] result;
    synchronized( probes ) {
      if( probes.isEmpty() ) {
        FontData[] fontList = RWTFactory.getTextSizeStorageRegistry().obtain().getFontList();
        for( int i = 0; i < fontList.length; i++ ) {
          createProbe( fontList[ i ], getProbeString( fontList[ i ] ) );
        }
      }
      result = new IProbe[ probes.size() ];
      probes.values().toArray( result );
    }
    return result;
  }

  private static String getProbeString( FontData fontData ) {
    // TODO [fappel]: probe string determination different from default
    return DEFAULT_PROBE;
  }
  
  static IProbe getProbe( FontData font ) {
    IProbe result;
    synchronized( probes ) {
      result = ( IProbe )probes.get( font );
    }
    return result;
  }
  
  static boolean containsProbe( FontData fontData ) {
    return getProbe( fontData ) != null;
  }
  
  static IProbe createProbe( FontData fontData, String probeText ) {
    IProbe result = new ProbeImpl( probeText, fontData );
    synchronized( probes ) {
      probes.put( fontData, result );
    }
    RWTFactory.getTextSizeStorageRegistry().obtain().storeFont( fontData );
    return result;
  }

  static void reset() {
    synchronized( probes ) {
      ITextSizeStorage registry = RWTFactory.getTextSizeStorageRegistry().obtain();
      if( registry instanceof DefaultTextSizeStorage ) {
        ( ( DefaultTextSizeStorage )registry ).resetFontList();
      }
      probes.clear();
    }
  }

  static void addProbeRequest( final FontData font ) {
    IProbe probe = getProbe( font );
    if( probe == null ) {
      FontData fontData = font;
      probe = createProbe( fontData, getProbeString( fontData ) );
    }
    getProbeRequestsInternal().add( probe );
  }
  
  static IProbe[] getProbeRequests() {
    Set probeRequests = getProbeRequestsInternal();
    IProbe[] result = new IProbe[ probeRequests.size() ];
    probeRequests.toArray( result );
    return result;
  }

  
  //////////////////
  // helping methods
  
  private static Set getProbeRequestsInternal() {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    Set result = ( Set )stateInfo.getAttribute( PROBE_REQUESTS );
    if( result == null ) {
      result = new HashSet();
      stateInfo.setAttribute( PROBE_REQUESTS, result );
    }
    return result;
  }
}