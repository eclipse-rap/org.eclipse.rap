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

import java.util.*;

import org.eclipse.rap.rwt.SingletonUtil;
import org.eclipse.rap.rwt.internal.application.RWTFactory;
import org.eclipse.rap.rwt.internal.lifecycle.DisplayUtil;
import org.eclipse.rap.rwt.internal.protocol.ClientMessage;
import org.eclipse.rap.rwt.internal.protocol.ClientMessage.CallOperation;
import org.eclipse.rap.rwt.internal.protocol.ClientObjectFactory;
import org.eclipse.rap.rwt.internal.protocol.IClientObject;
import org.eclipse.rap.rwt.internal.protocol.ProtocolUtil;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.SerializableCompatibility;
import org.eclipse.swt.widgets.Display;

class MeasurementOperator implements SerializableCompatibility {

  private static final String PROPERTY_STRINGS = "strings";
  private static final String METHOD_MEASURE_STRINGS = "measureStrings";
  private static final String PROPERTY_FONTS = "fonts";
  private static final String METHOD_PROBE = "probe";
  private static final String METHOD_STORE_PROBES = "storeProbes";
  private static final String METHOD_STORE_MEASUREMENTS = "storeMeasurements";
  private static final String PROPERTY_RESULTS = "results";

  private final Set<Probe> probes;
  private final Set<MeasurementItem> items;
  private boolean isStartupProbeMeasurementPerformed;

  static MeasurementOperator getInstance() {
    return SingletonUtil.getSessionInstance( MeasurementOperator.class );
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
    items.add( newItem );
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
    Iterator probeList = probes.iterator();
    CallOperation[] operations = getCallOperationsFor( METHOD_STORE_PROBES );
    while( probeList.hasNext() ) {
      Probe probe = ( Probe )probeList.next();
      String id = String.valueOf( probe.getFontData().hashCode() );
      Point size = readMeasuredSize( operations, id );
      if( size != null ) {
        createProbeResult( probe, size );
        probeList.remove();
      }
    }
  }

  private void createProbeResult( Probe probe, Point size ) {
    ProbeResultStore.getInstance().createProbeResult( probe, size );
  }

  private void addStartupProbesToBuffer() {
    Probe[] probeList = RWTFactory.getProbeStore().getProbes();
    probes.addAll( Arrays.asList( probeList ) );
  }

  private boolean readMeasuredTextSizes() {
    int originalItemsSize = items.size();
    Iterator itemList = items.iterator();
    CallOperation[] operations = getCallOperationsFor( METHOD_STORE_MEASUREMENTS );
    while( itemList.hasNext() ) {
      MeasurementItem item = ( MeasurementItem )itemList.next();
      String id = String.valueOf( item.hashCode() );
      Point size = readMeasuredSize( operations, id );
      if( size != null ) {
        storeTextMeasurement( item, size );
        itemList.remove();
      }
    }
    return itemsHasBeenMeasured( originalItemsSize );
  }

  private static CallOperation[] getCallOperationsFor( String methodName ) {
    CallOperation[] result = new CallOperation[ 0 ];
    Display display = Display.getCurrent();
    if( display != null ) {
      ClientMessage message = ProtocolUtil.getClientMessage();
      result = message.getAllCallOperationsFor( DisplayUtil.getId( display ), methodName );
    }
    return result;
  }

  private static Point readMeasuredSize( CallOperation[] operations, String id ) {
    Point result = null;
    for( int i = 0; i < operations.length; i++ ) {
      Map resultsMap = ( Map )operations[ i ].getProperty( PROPERTY_RESULTS );
      if( resultsMap != null ) {
        result = ProtocolUtil.toPoint( resultsMap.get( id ) );
      }
    }
    return result;
  }

  private static void storeTextMeasurement( MeasurementItem item, Point size ) {
    FontData fontData = item.getFontData();
    String textToMeasure = item.getTextToMeasure();
    int wrapWidth = item.getWrapWidth();
    int mode = item.getMode();
    TextSizeStorageUtil.store( fontData, textToMeasure, wrapWidth, mode, size );
  }

  private boolean hasItemsToMeasure() {
    return !items.isEmpty();
  }

  private boolean itemsHasBeenMeasured( int originalItemsSize ) {
    return originalItemsSize != items.size();
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