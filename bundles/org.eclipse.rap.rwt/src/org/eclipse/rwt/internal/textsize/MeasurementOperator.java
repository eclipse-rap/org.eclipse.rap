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
package org.eclipse.rwt.internal.textsize;

import java.util.*;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.rwt.SessionSingletonBase;
import org.eclipse.rwt.internal.application.RWTFactory;
import org.eclipse.rwt.internal.protocol.ClientObjectFactory;
import org.eclipse.rwt.internal.protocol.IClientObject;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.SerializableCompatibility;
import org.eclipse.swt.widgets.Display;

class MeasurementOperator implements SerializableCompatibility {

  private static final String PROPERTY_STRINGS = "strings";
  private static final String METHOD_MEASURE_STRINGS = "measureStrings";
  private static final String PROPERTY_FONTS = "fonts";
  private static final String METHOD_PROBE = "probe";

  private final Set<Probe> probes;
  private final Set<MeasurementItem> items;
  private boolean isStartupProbeMeasurementPerformed;

  static MeasurementOperator getInstance() {
    return SessionSingletonBase.getInstance( MeasurementOperator.class );
  }

  MeasurementOperator() {
    probes = new HashSet<Probe>();
    items = new HashSet<MeasurementItem>();
    addStartupProbesToBuffer();
  }

  void handleMeasurementRequests() {
    if( hasProbesToMeasure() ) {
      renderFontProbing();
    }
    if( hasItemsToMeasure() ) {
      renderStringMeasurements();
    }
  }

  boolean handleMeasurementResults() {
    readMeasuredFontProbeSizes();
    return readMeasuredTextSizes();
  }

  void handleStartupProbeMeasurementResults() {
    if( !isStartupProbeMeasurementPerformed ) {
      readMeasuredFontProbeSizes();
      isStartupProbeMeasurementPerformed = true;
    }
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

  private static void renderStringMeasurements() {
    MeasurementItem[] items = MeasurementOperator.getInstance().getItems();
    if( items.length > 0 ) {
      Object[] itemsObject = new Object[ items.length ];
      for( int i = 0; i < items.length; i++ ) {
        itemsObject[ i ] = MeasurementUtil.createItemParamObject( items[ i ] );
      }
      callDisplayMethod( METHOD_MEASURE_STRINGS, PROPERTY_STRINGS, itemsObject );
    }
  }

  private static void renderFontProbing() {
    Probe[] probeList = MeasurementOperator.getInstance().getProbes();
    if( probeList.length > 0 ) {
      Object[] probesObject = new Object[ probeList.length ];
      for( int i = 0; i < probeList.length; i++ ) {
        probesObject[ i ] = MeasurementUtil.createProbeParamObject( probeList[ i ] );
      }
      callDisplayMethod( METHOD_PROBE, PROPERTY_FONTS, probesObject );
    }
  }

  private static void callDisplayMethod( String method, String property, Object value ) {
    Display display = Display.getCurrent();
    if( display != null ) {
      IClientObject clientObject = ClientObjectFactory.getClientObject( display );
      Map<String, Object> args = new HashMap<String, Object>();
      args.put( property, value );
      clientObject.call( method, args );
    }
  }
}