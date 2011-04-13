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
package org.eclipse.swt.internal.graphics;

import java.util.*;

import org.eclipse.rwt.internal.engine.RWTFactory;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.service.IServiceStateInfo;
import org.eclipse.swt.graphics.FontData;


public class TextSizeProbeStore {

  public interface IProbe {
    FontData getFontData();
    String getText();
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
    = TextSizeProbeResults.class.getName() + "#probeRequests";

  private final Map probes; 
  
  TextSizeProbeStore() {
    probes = new HashMap();
  }
  
  IProbe[] getProbeList() {
    IProbe[] result;
    synchronized( probes ) {
      if( probes.isEmpty() ) {
        // TODO [rh] store TextSizeStorageRegistry in a field and initialize it during configuration
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
  
  IProbe createProbe( FontData fontData, String probeText ) {
    IProbe result = new ProbeImpl( probeText, fontData );
    synchronized( probes ) {
      probes.put( fontData, result );
    }
    RWTFactory.getTextSizeStorageRegistry().obtain().storeFont( fontData );
    return result;
  }

  IProbe getProbe( FontData font ) {
    synchronized( probes ) {
      return ( IProbe )probes.get( font );
    }
  }
  
  static void addProbeRequest( FontData fontData ) {
    IProbe probe = RWTFactory.getTextSizeProbeStore().getProbe( fontData );
    if( probe == null ) {
      String probeString = getProbeString( fontData );
      probe = RWTFactory.getTextSizeProbeStore().createProbe( fontData, probeString );
    }
    getProbeRequestsInternal().add( probe );
  }

  static IProbe[] getProbeRequests() {
    Set probeRequests = getProbeRequestsInternal();
    IProbe[] result = new IProbe[ probeRequests.size() ];
    probeRequests.toArray( result );
    return result;
  }

  private static String getProbeString( FontData fontData ) {
    // TODO [fappel]: probe string determination different from default
    return DEFAULT_PROBE;
  }

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
