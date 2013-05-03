/*******************************************************************************
 * Copyright (c) 2011, 2013 Frank Appel and others.
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

import static org.eclipse.rap.rwt.internal.service.ContextProvider.getApplicationContext;
import static org.eclipse.rap.rwt.internal.service.ContextProvider.getProtocolWriter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.SingletonUtil;
import org.eclipse.rap.rwt.internal.protocol.ClientMessage.CallOperation;
import org.eclipse.rap.rwt.internal.protocol.ProtocolMessageWriter;
import org.eclipse.rap.rwt.internal.protocol.ProtocolUtil;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.SerializableCompatibility;


class MeasurementOperator implements SerializableCompatibility {

  static final String TYPE = "rwt.client.TextSizeMeasurement";
  static final String METHOD_MEASURE_ITEMS = "measureItems";
  static final String PROPERTY_ITEMS = "items";
  static final String METHOD_STORE_MEASUREMENTS = "storeMeasurements";
  static final String PROPERTY_RESULTS = "results";

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

  void appendStartupTextSizeProbe( ProtocolMessageWriter writer ) {
    JsonValue startupProbeObject = getStartupProbeObject();
    if( startupProbeObject != null ) {
      JsonObject parameters = new JsonObject().add( PROPERTY_ITEMS, startupProbeObject );
      writer.appendCall( TYPE, METHOD_MEASURE_ITEMS, parameters );
    }
  }

  void handleMeasurementRequests() {
    renderMeasurements();
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
    Probe probe = getApplicationContext().getProbeStore().getProbe( fontData );
    if( probe == null ) {
      probe = getApplicationContext().getProbeStore().createProbe( fontData );
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

  private static JsonArray getStartupProbeObject() {
    JsonArray result = null;
    Probe[] probeList = getApplicationContext().getProbeStore().getProbes();
    if( probeList.length > 0 ) {
      result = new JsonArray();
      for( int i = 0; i < probeList.length; i++ ) {
        result.add( MeasurementUtil.createProbeParamObject( probeList[ i ] ) );
      }
    }
    return result;
  }

  private void readMeasuredFontProbeSizes() {
    Iterator probeList = probes.iterator();
    List<CallOperation> operations = getCallOperationsFor( METHOD_STORE_MEASUREMENTS );
    while( probeList.hasNext() ) {
      Probe probe = ( Probe )probeList.next();
      Point size = readMeasuredSize( operations, MeasurementUtil.getId( probe ) );
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
    Probe[] probeList = getApplicationContext().getProbeStore().getProbes();
    probes.addAll( Arrays.asList( probeList ) );
  }

  private boolean readMeasuredTextSizes() {
    int originalItemsSize = items.size();
    Iterator itemList = items.iterator();
    List<CallOperation> operations = getCallOperationsFor( METHOD_STORE_MEASUREMENTS );
    while( itemList.hasNext() ) {
      MeasurementItem item = ( MeasurementItem )itemList.next();
      Point size = readMeasuredSize( operations, MeasurementUtil.getId( item ) );
      if( size != null ) {
        storeTextMeasurement( item, size );
        itemList.remove();
      }
    }
    return itemsHasBeenMeasured( originalItemsSize );
  }

  private static List<CallOperation> getCallOperationsFor( String methodName ) {
    return ProtocolUtil.getClientMessage().getAllCallOperationsFor( TYPE, methodName );
  }

  private static Point readMeasuredSize( List<CallOperation> operations, String id ) {
    Point result = null;
    for( CallOperation operation : operations ) {
      JsonObject results = operation.getProperty( PROPERTY_RESULTS ).asObject();
      if( results != null ) {
        JsonValue value = results.get( id );
        if( value != null ) {
          result = ProtocolUtil.toPoint( value );
        }
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

  private boolean itemsHasBeenMeasured( int originalItemsSize ) {
    return originalItemsSize != items.size();
  }

  private void renderMeasurements() {
    Probe[] probes = getProbes();
    MeasurementItem[] items = getItems();
    if( probes.length > 0 || items.length > 0 ) {
      JsonArray itemsObject = new JsonArray();
      for( int i = 0; i < probes.length; i++ ) {
        itemsObject.add( MeasurementUtil.createProbeParamObject( probes[ i ] ) );
      }
      for( int i = 0; i < items.length; i++ ) {
        itemsObject.add( MeasurementUtil.createItemParamObject( items[ i ] ) );
      }
      callClientMethod( METHOD_MEASURE_ITEMS, PROPERTY_ITEMS, itemsObject );
    }
  }

  private static void callClientMethod( String method, String property, JsonValue value ) {
    getProtocolWriter().appendCall( TYPE, method, new JsonObject().add( property, value ) );
  }

}
