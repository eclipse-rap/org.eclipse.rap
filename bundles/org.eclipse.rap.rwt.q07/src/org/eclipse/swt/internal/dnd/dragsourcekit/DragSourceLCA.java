/*******************************************************************************
 * Copyright (c) 2009, 2011 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.dnd.dragsourcekit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.dnd.*;
import org.eclipse.swt.internal.dnd.IDNDAdapter;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;


public final class DragSourceLCA extends AbstractWidgetLCA {

  private static final String JSFUNC_REGISTER
    = "org.eclipse.rwt.DNDSupport.getInstance().registerDragSource";
  private static final String JSFUNC_DEREGISTER
    = "org.eclipse.rwt.DNDSupport.getInstance().deregisterDragSource";
  private static final String JSFUNC_SET_TRANSFER_TYPES
    = "org.eclipse.rwt.DNDSupport.getInstance().setDragSourceTransferTypes";
  private static final String JSFUNC_SET_OPERATION_OVERWRITE
    = "org.eclipse.rwt.DNDSupport.getInstance().setOperationOverwrite";
  private static final String JSFUNC_SET_FEEDBACK
    = "org.eclipse.rwt.DNDSupport.getInstance().setFeedback";
  private static final String JSFUNC_SET_DATATYPE
    = "org.eclipse.rwt.DNDSupport.getInstance().setDataType";
  private static final String JSFUNC_CANCEL
    = "org.eclipse.rwt.DNDSupport.getInstance().cancel";

  private static final Transfer[] DEFAULT_TRANSFER = new Transfer[ 0 ];

  private static final String PROP_CONTROL = "control";
  private static final String PROP_TRANSFER = "transfer";

  public void preserveValues( Widget widget ) {
    DragSource dragSource = ( DragSource )widget;
    IWidgetAdapter adapter = WidgetUtil.getAdapter( dragSource );
    adapter.preserve( PROP_CONTROL, dragSource.getControl() );
    adapter.preserve( PROP_TRANSFER, dragSource.getTransfer() );
  }

  public void readData( Widget widget ) {
  }

  public void renderInitialization( Widget widget ) throws IOException {
    DragSource dragSource = ( DragSource )widget;
    JSWriter writer = JSWriter.getWriterFor( dragSource );
    String[] operations = DNDLCAUtil.convertOperations( dragSource.getStyle() );
    Object[] args = new Object[]{ dragSource.getControl(), operations };
    writer.callStatic( JSFUNC_REGISTER, args );
  }

  public void renderChanges( Widget widget ) throws IOException {
    DragSource dragSource = ( DragSource )widget;
    writeTransfer( dragSource );
    writeDetail( dragSource );
    writeFeedback( dragSource );
    writeDataType( dragSource );
    writeCancel( dragSource );
  }

  public void renderDispose( Widget widget ) throws IOException {
    DragSource dragSource = ( DragSource )widget;
    IWidgetAdapter adapter = WidgetUtil.getAdapter( dragSource );
    Control control = ( Control )adapter.getPreserved( PROP_CONTROL );
    JSWriter writer = JSWriter.getWriterFor( dragSource );
    writer.callStatic( JSFUNC_DEREGISTER, new Object[]{ control } );
  }

  private static void writeTransfer( DragSource dragSource ) throws IOException {
    Transfer[] newValue = dragSource.getTransfer();
    if( WidgetLCAUtil.hasChanged( dragSource, PROP_TRANSFER, newValue, DEFAULT_TRANSFER ) ) {
        JSWriter writer = JSWriter.getWriterFor( dragSource );
        Object[] args = new Object[]{
          dragSource.getControl(),
          DNDLCAUtil.convertTransferTypes( newValue )
        };
        writer.callStatic( JSFUNC_SET_TRANSFER_TYPES, args );
    }
  }

  private void writeDetail( DragSource dragSource ) throws IOException {
    IDNDAdapter dndAdapter = ( IDNDAdapter )dragSource.getAdapter( IDNDAdapter.class  );
    if( dndAdapter.hasDetailChanged() ) {
      JSWriter writer = JSWriter.getWriterFor( dragSource );
      Object[] args = new Object[]{
        dndAdapter.getDetailChangedControl(),
        convertOperation( dndAdapter.getDetailChangedValue() )
      };
      writer.callStatic( JSFUNC_SET_OPERATION_OVERWRITE, args );
    }
  }

  private void writeFeedback( DragSource dragSource ) throws IOException {
    IDNDAdapter dndAdapter = ( IDNDAdapter )dragSource.getAdapter( IDNDAdapter.class  );
    if( dndAdapter.hasFeedbackChanged() ) {
      JSWriter writer = JSWriter.getWriterFor( dragSource );
      int value = dndAdapter.getFeedbackChangedValue();
      Object[] args = new Object[]{
        dndAdapter.getFeedbackChangedControl(),
        convertFeedback( value ),
        new Integer( value )
      };
      writer.callStatic( JSFUNC_SET_FEEDBACK, args );
    }
  }

  private void writeDataType( DragSource dragSource ) throws IOException {
    IDNDAdapter dndAdapter = ( IDNDAdapter )dragSource.getAdapter( IDNDAdapter.class  );
    if( dndAdapter.hasDataTypeChanged() ) {
      JSWriter writer = JSWriter.getWriterFor( dragSource );
      TransferData value = dndAdapter.getDataTypeChangedValue();
      Object[] args = new Object[]{
        dndAdapter.getDataTypeChangedControl(),
        new Integer( value.type )
      };
      writer.callStatic( JSFUNC_SET_DATATYPE, args );
    }
  }
  
  private static void writeCancel( DragSource dragSource ) throws IOException {
    IDNDAdapter dndAdapter = ( IDNDAdapter )dragSource.getAdapter( IDNDAdapter.class  );
    if( dndAdapter.isCanceled() ) {
      JSWriter writer = JSWriter.getWriterFor( dragSource );
      writer.callStatic( JSFUNC_CANCEL, null );
    }
  }

  private static String convertOperation( int operation ) {
    String result = "none";
    switch( operation ) {
      case DND.DROP_COPY:
        result = "copy";
      break;
      case DND.DROP_MOVE:
        result = "move";
      break;
      case DND.DROP_LINK:
        result = "link";
      break;
    }
    return result;
  }

  private static String[] convertFeedback( int feedback ) {
    List list = new ArrayList();
    if( ( feedback & DND.FEEDBACK_EXPAND ) != 0 ) {
      list.add( "expand" );
    }
    if( ( feedback & DND.FEEDBACK_INSERT_AFTER ) != 0 ) {
      list.add( "after" );
    }
    if( ( feedback & DND.FEEDBACK_INSERT_BEFORE ) != 0 ) {
      list.add( "before" );
    }
    if( ( feedback & DND.FEEDBACK_SCROLL ) != 0 ) {
      list.add( "scroll" );
    }
    if( ( feedback & DND.FEEDBACK_SELECT ) != 0 ) {
      list.add( "select" );
    }
    String[] result = new String[ list.size() ];
    for( int i = 0; i < list.size(); i++ ) {
      result[ i ] = ( String )list.get( i );
    }
    return result;
  }
}
