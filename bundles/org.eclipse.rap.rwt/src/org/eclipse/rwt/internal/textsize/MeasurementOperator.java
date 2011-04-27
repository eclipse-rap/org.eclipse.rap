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

import java.io.IOException;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.rwt.internal.engine.RWTFactory;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.textsize.TextSizeProbeStore.Probe;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;

class MeasurementOperator {
  private final Map probes;
  private final Set items;

  MeasurementOperator() {
    probes = new HashMap();
    items = new HashSet();
    addStartupProbesToBuffer();
  }
  
  void handleMeasurementRequests() {
    if( TextSizeProbeStore.hasProbesToMeasure() ) {
      addProbesOfRequestToBuffer();
      writeFontProbingStatement();
    }
    if( MeasurementUtil.hasItemsToMeasure() ) {
      addItemsOfRequestToBuffer();
      writeTextMeasurements();
    }
  }

  boolean handleMeasurementResults() {
    readMeasuredFontProbeSizes();
    return readMeasuredTextSizes();
  }
  
  void readMeasuredFontProbeSizes() {
    HttpServletRequest request = ContextProvider.getRequest();
    Iterator probeList = probes.values().iterator();
    while( probeList.hasNext() ) {
      Probe probe = ( Probe )probeList.next();
      String name = String.valueOf( probe.getFontData().hashCode() );
      String value = request.getParameter( name );
      if( value != null ) {
        createProbeResult( probe, value );
        probeList.remove();
      }
    }
  }

  int getProbeCount() {
    return probes.size();
  }

  int getItemCount() {
    return items.size();
  }

  private void writeTextMeasurements() {
    try {
      // TODO [fappel]: remove return Type of facade method
      TextSizeDeterminationFacade.writeStringMeasurements();
    } catch( IOException shouldNotHappen ) {
      throw new RuntimeException( shouldNotHappen );
    }
  }

  private void writeFontProbingStatement() {
    try {
      // TODO [fappel]: remove return Type of facade method
      TextSizeDeterminationFacade.writeFontProbing();
    } catch( IOException shouldNotHappen ) {
      throw new RuntimeException( shouldNotHappen );
    }
  }

  private void createProbeResult( Probe probe, String value ) {
    Point size = getSize( value );
    TextSizeProbeResults.getInstance().createProbeResult( probe, size );
  }

  private boolean readMeasuredTextSizes() {
    int itemsSizeBefore = items.size();
    HttpServletRequest request = ContextProvider.getRequest();
    Iterator itemList = items.iterator();
    while( itemList.hasNext() ) {
      MeasurementItem item = ( MeasurementItem )itemList.next();
      String name = String.valueOf( item.hashCode() );
      String value = request.getParameter( name );
      if( value != null ) {
        storeMeasuredText( item, value );
        itemList.remove();
      }
    }
    return itemsSizeBefore != items.size();
  }

  private void storeMeasuredText( MeasurementItem item, String value ) {
    Point size = getSize( value );
    FontData fontData = item.getFontData();
    String textToMeasure = item.getTextToMeasure();
    int wrapWidth = item.getWrapWidth();
    TextSizeDataBase.store( fontData, textToMeasure, wrapWidth, size );
  }
  
  private void addItemsOfRequestToBuffer() {
    MeasurementItem[] itemList = MeasurementUtil.getItemsToMeasure();
    items.addAll( Arrays.asList( itemList ) );
  }

  private void addProbesOfRequestToBuffer() {
    Probe[] probeList = TextSizeProbeStore.getProbesToMeasure();
    addProbesToBuffer( probeList );
  }

  private void addProbesToBuffer( Probe[] probeList ) {
    for( int i = 0; i < probeList.length; i++ ) {
      probes.put( probeList[ i ].getFontData(), probeList[ i ] );
    }
  }

  private static Point getSize( String value ) {
    String[] split = value.split( "," );
    return new Point( Integer.parseInt( split[ 0 ] ), Integer.parseInt( split[ 1 ] ) );
  }

  private void addStartupProbesToBuffer() {
    Probe[] probeList = RWTFactory.getTextSizeProbeStore().getProbeList();
    addProbesToBuffer( probeList );
  }
}