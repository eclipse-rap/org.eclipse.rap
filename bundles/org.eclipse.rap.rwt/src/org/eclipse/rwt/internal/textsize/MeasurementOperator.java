/*******************************************************************************
 * Copyright (c) 2011 Frank Appel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Frank Appel - initial API and implementation
 *    EclipseSource - bug 348056: Eliminate compiler warnings
 ******************************************************************************/
package org.eclipse.rwt.internal.textsize;

import java.util.*;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.rwt.SessionSingletonBase;
import org.eclipse.rwt.internal.engine.RWTFactory;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.SerializableCompatibility;

class MeasurementOperator implements SerializableCompatibility {
  private static final long serialVersionUID = 1L;
  
  private final Set<Probe> probes;
  private final Set<MeasurementItem> items;

  static MeasurementOperator getInstance() {
    return ( MeasurementOperator )SessionSingletonBase.getInstance( MeasurementOperator.class );
  }

  MeasurementOperator() {
    probes = new HashSet<Probe>();
    items = new HashSet<MeasurementItem>();
    addStartupProbesToBuffer();
  }

  void handleMeasurementRequests() {
    if( hasProbesToMeasure() ) {
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
  
  void addProbeToMeasure( FontData fontData ) {
    Probe probe = RWTFactory.getProbeStore().getProbe( fontData );
    if( probe == null ) {
      probe = RWTFactory.getProbeStore().createProbe( fontData );
    }
    probes.add( probe );
  }
  
  Probe[] getProbes() {
    return probes.toArray( new Probe[ probes.size() ] );
  }
  
  void addItemToMeasure( MeasurementItem newItem ) {
    if( !requestContainsMeasurementResult( newItem ) ) {
      items.add( newItem );
    }
  }
  
  int getItemCount() {
    return items.size();
  }
  
  MeasurementItem[] getItems() {
    return items.toArray( new MeasurementItem[ items.size() ] );
  }
  
  //////////////////
  // helping methods
  
  private boolean hasProbesToMeasure() {
    return !probes.isEmpty();
  }
  
  private void writeFontProbingStatement() {
    TextSizeUtilFacade.writeFontProbing();
  }
  
  private void readMeasuredFontProbeSizes() {
    HttpServletRequest request = ContextProvider.getRequest();
    Iterator probeList = probes.iterator();
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
    ProbeResultStore.getInstance().createProbeResult( probe, size );
  }
  
  private void addStartupProbesToBuffer() {
    Probe[] probeList = RWTFactory.getProbeStore().getProbes();
    probes.addAll( Arrays.asList( probeList ) );
  }

  private void writeTextMeasurements() {
    TextSizeUtilFacade.writeStringMeasurements();
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
    TextSizeStorageUtil.store( fontData, textToMeasure, wrapWidth, size );
  }

  private static Point readMeasuredItemSize( MeasurementItem item ) {
    HttpServletRequest request = ContextProvider.getRequest();
    String name = String.valueOf( item.hashCode() );
    return getSize( request.getParameter( name ) );
  }
}