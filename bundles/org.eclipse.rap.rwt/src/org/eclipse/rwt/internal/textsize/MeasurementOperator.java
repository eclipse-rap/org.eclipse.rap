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

import org.eclipse.rwt.SessionSingletonBase;
import org.eclipse.rwt.internal.engine.RWTFactory;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.textsize.TextSizeProbeStore.Probe;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;

class MeasurementOperator {
  private final Map probes;
  private final Set items;

  static MeasurementOperator getInstance() {
    return ( MeasurementOperator )SessionSingletonBase.getInstance( MeasurementOperator.class );
  }

  MeasurementOperator() {
    probes = new HashMap();
    items = new HashSet();
    addStartupProbesToBuffer();
  }

  void addItemToMeasure( MeasurementItem newItem ) {
    if( !requestContainsMeasurementResult( newItem ) ) {
      items.add( newItem );
    }
  }

  void handleMeasurementRequests() {
    if( TextSizeProbeStore.hasProbesToMeasure() ) {
      addProbesOfProbeStore();
      writeFontProbingStatement();
    }
    if( hasItemsToMeasure() ) {
      writeTextMeasurements();
    }
  }

  boolean handleMeasurementResults() {
    readMeasuredFontProbeSizes();
    return readMeasuredTextSizes();
  }
  
  void handleStartupProbeMeasurementResults() {
    readMeasuredFontProbeSizes();
  }

  int getProbeCount() {
    return probes.size();
  }

  int getItemCount() {
    return items.size();
  }
  
  MeasurementItem[] getItems() {
    MeasurementItem[] result = new MeasurementItem[ items.size() ];
    items.toArray( result );
    return result;
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
  
  private void readMeasuredFontProbeSizes() {
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

  private void createProbeResult( Probe probe, String value ) {
    Point size = getSize( value );
    TextSizeProbeResults.getInstance().createProbeResult( probe, size );
  }

  private boolean readMeasuredTextSizes() {
    int originalItemsSize = items.size();
    Iterator itemList = items.iterator();
    while( itemList.hasNext() ) {
      MeasurementItem item = ( MeasurementItem )itemList.next();
      if( requestContainsMeasurementResult( item ) ) {
        storeTextMeasurement( item );
        itemList.remove();
      }
    }
    return itemsHasBeenMeasured( originalItemsSize );
  }
  
  private void addProbesOfProbeStore() {
    Probe[] probeList = TextSizeProbeStore.getProbesToMeasure();
    addProbesToBuffer( probeList );
  }

  private void addProbesToBuffer( Probe[] probeList ) {
    for( int i = 0; i < probeList.length; i++ ) {
      probes.put( probeList[ i ].getFontData(), probeList[ i ] );
    }
  }

  private void addStartupProbesToBuffer() {
    Probe[] probeList = RWTFactory.getTextSizeProbeStore().getProbeList();
    addProbesToBuffer( probeList );
  }

  private boolean hasItemsToMeasure() {
    return !items.isEmpty();
  }

  private boolean itemsHasBeenMeasured( int originalItemsSize ) {
    return originalItemsSize != items.size();
  }
  
  private static boolean requestContainsMeasurementResult( MeasurementItem newItem ) {
    HttpServletRequest request = ContextProvider.getRequest();
    String value = request.getParameter( String.valueOf( newItem.hashCode() ) );
    return value != null;
  }

  private static Point getSize( String value ) {
    String[] split = value.split( "," );
    return new Point( Integer.parseInt( split[ 0 ] ), Integer.parseInt( split[ 1 ] ) );
  }

  private static void storeTextMeasurement( MeasurementItem item ) {
    Point size = readMeasuredItemSize( item );
    FontData fontData = item.getFontData();
    String textToMeasure = item.getTextToMeasure();
    int wrapWidth = item.getWrapWidth();
    TextSizeDataBase.store( fontData, textToMeasure, wrapWidth, size );
  }

  private static Point readMeasuredItemSize( MeasurementItem item ) {
    HttpServletRequest request = ContextProvider.getRequest();
    String name = String.valueOf( item.hashCode() );
    return getSize( request.getParameter( name ) );
  }
}