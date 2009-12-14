/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.displaykit;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.rwt.internal.lifecycle.RWTLifeCycle;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.lifecycle.ProcessActionRunner;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.dnd.*;
import org.eclipse.swt.events.DragDetectEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.dnd.IDNDAdapter;
import org.eclipse.swt.internal.widgets.IDisplayAdapter;
import org.eclipse.swt.widgets.*;

// TODO [rh] move these methods to DragSourceLCA
public final class DNDSupport {

  private static final String PACKAGE_PREFIX = "org.eclipse.swt.dnd";

  private static final String EVENT_DRAG_START
    = PACKAGE_PREFIX + ".dragStart";
  private static final String EVENT_DRAG_START_X
    = PACKAGE_PREFIX + ".dragStart.x";
  private static final String EVENT_DRAG_START_Y
    = PACKAGE_PREFIX + ".dragStart.y";
  private static final String EVENT_DRAG_START_TIME
    = PACKAGE_PREFIX + ".dragStart.time";
  private static final String EVENT_DRAG_ENTER
    = PACKAGE_PREFIX + ".dragEnter";
  private static final String EVENT_DRAG_ENTER_OPERATION
    = PACKAGE_PREFIX + ".dragEnter.operation";
  private static final String EVENT_DRAG_ENTER_X
    = PACKAGE_PREFIX + ".dragEnter.x";
  private static final String EVENT_DRAG_ENTER_Y
    = PACKAGE_PREFIX + ".dragEnter.y";
  private static final String EVENT_DRAG_ENTER_ITEM
  = PACKAGE_PREFIX + ".dragEnter.item";
  private static final String EVENT_DRAG_ENTER_TIME
    = PACKAGE_PREFIX + ".dragEnter.time";
  private static final String EVENT_DRAG_ENTER_SOURCE
    = PACKAGE_PREFIX + ".dragEnter.source";
  private static final String EVENT_DRAG_ENTER_FEEDBACK
    = PACKAGE_PREFIX + ".dragEnter.feedback";
  private static final String EVENT_DRAG_OVER
    = PACKAGE_PREFIX + ".dragOver";
  private static final String EVENT_DRAG_OVER_OPERATION
    = PACKAGE_PREFIX + ".dragOver.operation";
  private static final String EVENT_DRAG_OVER_FEEDBACK
  = PACKAGE_PREFIX + ".dragOver.feedback";
  private static final String EVENT_DRAG_OVER_X
    = PACKAGE_PREFIX + ".dragOver.x";
  private static final String EVENT_DRAG_OVER_Y
    = PACKAGE_PREFIX + ".dragOver.y";
  private static final String EVENT_DRAG_OVER_ITEM
    = PACKAGE_PREFIX + ".dragOver.item";
  private static final String EVENT_DRAG_OVER_SOURCE
    = PACKAGE_PREFIX + ".dragOver.source";
  private static final String EVENT_DRAG_OVER_TIME
    = PACKAGE_PREFIX + ".dragOver.time";
  private static final String EVENT_DRAG_OVER_DATATYPE
    = PACKAGE_PREFIX + ".dragOver.dataType";
  private static final String EVENT_DRAG_LEAVE
    = PACKAGE_PREFIX + ".dragLeave";
  private static final String EVENT_DRAG_LEAVE_OPERATION
    = PACKAGE_PREFIX + ".dragLeave.operation";
  private static final String EVENT_DRAG_LEAVE_X
    = PACKAGE_PREFIX + ".dragLeave.x";
  private static final String EVENT_DRAG_LEAVE_Y
    = PACKAGE_PREFIX + ".dragLeave.y";
  private static final String EVENT_DRAG_LEAVE_TIME
    = PACKAGE_PREFIX + ".dragLeave.time";
  private static final String EVENT_DROP_ACCEPT
    = PACKAGE_PREFIX + ".dropAccept";
  private static final String EVENT_DROP_ACCEPT_OPERATION
    = PACKAGE_PREFIX + ".dropAccept.operation";
  private static final String EVENT_DROP_ACCEPT_X
    = PACKAGE_PREFIX + ".dropAccept.x";
  private static final String EVENT_DROP_ACCEPT_Y
    = PACKAGE_PREFIX + ".dropAccept.y";
  private static final String EVENT_DROP_ACCEPT_ITEM
    = PACKAGE_PREFIX + ".dropAccept.item";
  private static final String EVENT_DROP_ACCEPT_SOURCE
    = PACKAGE_PREFIX + ".dropAccept.source";
  private static final String EVENT_DROP_ACCEPT_TIME
    = PACKAGE_PREFIX + ".dropAccept.time";
  private static final String EVENT_DROP_ACCEPT_DATATYPE
    = PACKAGE_PREFIX + ".dropAccept.dataType";
  private static final String EVENT_DRAG_FINISHED
    = PACKAGE_PREFIX + ".dragFinished";
  private static final String EVENT_DRAG_FINISHED_X
    = PACKAGE_PREFIX + ".dragFinished.x";
  private static final String EVENT_DRAG_FINISHED_Y
    = PACKAGE_PREFIX + ".dragFinished.y";
  private static final String EVENT_DRAG_FINISHED_TIME
    = PACKAGE_PREFIX + ".dragFinished.time";


  private DNDSupport() {
    // prevent instantiation
  }

  public static void processEvents() {
    ProcessActionRunner.add( new Runnable() {
      public void run() {
        processDragStart();
        if( isLeaveBeforeEnter() ) {
          processDragLeave();
          processDragEnter();
          processDragOver();
        } else {
          processDragEnter();
          processDragOver();
          processDragLeave();
        }
        processDragFinished();
        // TODO [tb] : operationChanged event
      }
    } );
  }

  private static void processDragStart() {
    Control control = readControlParam( EVENT_DRAG_START );
    if( control != null ) {
      DragSource dragSource = getDragSource( control );
      Point point = readXYParams( EVENT_DRAG_START_X, EVENT_DRAG_START_Y );
      Point mappedPoint = control.getDisplay().map( null, control, point );
      DragDetectEvent dragDetectEvent
        = createDragDetectEvent( control, mappedPoint );
      dragDetectEvent.processEvent();
      DragSourceEvent dragStartEvent
        = createDragStartEvent( dragSource, point, dragDetectEvent.time );
      dragStartEvent.processEvent();
      if( dragStartEvent.doit == false ) {
        getDNDAdapter( dragSource ).cancel();
      }
    }
  }

  private static void processDragEnter() {
    Control control = readControlParam( EVENT_DRAG_ENTER );
    if( control != null ) {
      DropTarget dropTarget = getDropTarget( control );
      Control sourceControl = readControlParam( EVENT_DRAG_ENTER_SOURCE );
      DragSource dragSource = getDragSource( sourceControl );
      Point point = readXYParams( EVENT_DRAG_ENTER_X, EVENT_DRAG_ENTER_Y );
      DropTargetEvent event
        = new DropTargetEvent( dropTarget, DropTargetEvent.DRAG_ENTER );
      int operation = readOperationParam( EVENT_DRAG_ENTER_OPERATION );
      int feedback = readIntParam( EVENT_DRAG_ENTER_FEEDBACK );
      Item item = readItemParam( EVENT_DRAG_ENTER_ITEM );
      TransferData[] validDataTypes
        = determineDataTypes( dragSource, dropTarget );
      TransferData dataType = validDataTypes[ 0 ];
      event.detail = operation;
      event.feedback = feedback;
      event.currentDataType = dataType;
      event.dataTypes = validDataTypes;
      event.item = item;
      event.x = point.x;
      event.y = point.y;
      event.time = readIntParam( EVENT_DRAG_ENTER_TIME );
      event.processEvent();
      if( event.detail != operation ) {
        changeOperation( dragSource, dropTarget, event.detail );
      }
      // no check, dataType is always changed from null to a valid value:
      changeDataType( dragSource, dropTarget, event.currentDataType );      
      if( event.feedback != feedback ) {
        getDNDAdapter( dragSource ).setFeedbackChanged( control, event.feedback );        
      }
    }
  }

  private static void processDragOver() {
    Control control = readControlParam( EVENT_DRAG_OVER );
    if( control != null ) {
      DropTarget dropTarget = getDropTarget( control );
      Control sourceControl = readControlParam( EVENT_DRAG_OVER_SOURCE );
      DragSource dragSource = getDragSource( sourceControl );
      IDNDAdapter dndAdapter = getDNDAdapter( dragSource );
      int operation;
      if( dndAdapter.hasDetailChanged() ) {
        operation = dndAdapter.getDetailChangedValue();
      } else {
        operation = readOperationParam( EVENT_DRAG_OVER_OPERATION );
      }
      int feedback;      
      if( dndAdapter.hasFeedbackChanged() ) {
        feedback = dndAdapter.getFeedbackChangedValue();
      } else {
        feedback = readIntParam( EVENT_DRAG_OVER_FEEDBACK );
      }      
      TransferData dataType;
      if( dndAdapter.hasDataTypeChanged() ) {
        dataType = dndAdapter.getDataTypeChangedValue();
      } else {
        dataType = readDataTypeParam( EVENT_DRAG_OVER_DATATYPE );
      }
      Point point = readXYParams( EVENT_DRAG_OVER_X, EVENT_DRAG_OVER_Y );
      Item item = readItemParam( EVENT_DRAG_OVER_ITEM );
      DropTargetEvent event
        = new DropTargetEvent( dropTarget, DropTargetEvent.DRAG_OVER );
      event.detail = operation;
      event.feedback = feedback;
      event.currentDataType = dataType;
      event.dataTypes = determineDataTypes( dragSource, dropTarget );
      event.item = item;
      event.x = point.x;
      event.y = point.y;
      event.time = readIntParam( EVENT_DRAG_OVER_TIME );
      event.processEvent();
      if( event.detail != operation ) {
        changeOperation( dragSource, dropTarget, event.detail );
      }
      if( event.currentDataType != dataType ) {
        changeDataType( dragSource, dropTarget, event.currentDataType );
      }
      if( event.feedback != feedback ) {
        getDNDAdapter( dragSource ).setFeedbackChanged( control, event.feedback );        
      }      
    }
  }

  private static void processDragLeave() {
    Control control = readControlParam( EVENT_DRAG_LEAVE );
    if( control != null ) {
      DropTarget dropTarget = getDropTarget( control );
      Point point = readXYParams( EVENT_DRAG_LEAVE_X, EVENT_DRAG_LEAVE_Y );
      int operation = readOperationParam( EVENT_DRAG_LEAVE_OPERATION );
      int time = readIntParam( EVENT_DRAG_LEAVE_TIME );
      fireDragLeave( operation, dropTarget, point, time );
    }
  }

  private static void processDragFinished() {
    int operation = DND.DROP_NONE;
    Control dropTargetControl = readControlParam( EVENT_DROP_ACCEPT );
    if( dropTargetControl != null ) {
      DropTarget dropTarget = getDropTarget( dropTargetControl );
      Control sourceControl = readControlParam( EVENT_DROP_ACCEPT_SOURCE );
      DragSource dragSource = getDragSource( sourceControl );
      IDNDAdapter dndAdapter = getDNDAdapter( dragSource );
      if( dndAdapter.hasDetailChanged() ) {
        operation = dndAdapter.getDetailChangedValue();
      } else {
        operation = readOperationParam( EVENT_DROP_ACCEPT_OPERATION );
      }
      TransferData dataType;
      if( dndAdapter.hasDataTypeChanged() ) {
        dataType = dndAdapter.getDataTypeChangedValue();
      } else {
        dataType = readDataTypeParam( EVENT_DROP_ACCEPT_DATATYPE );
      }
      Point point = readXYParams( EVENT_DROP_ACCEPT_X, EVENT_DROP_ACCEPT_Y );
      Item item = readItemParam( EVENT_DROP_ACCEPT_ITEM );
      int time = readIntParam( EVENT_DROP_ACCEPT_TIME );
      // fire DRAG_LEAVE, which is suppressed by the client
      fireDragLeave( operation, dropTarget, point, time );
      // fire DROP_ACCEPT
      DropTargetEvent event
        = createDropAcceptEvent( dropTarget, operation, point, dataType, item );
      event.processEvent();
      operation = checkOperation( dragSource, dropTarget, event.detail );
      TransferData[] validDataTypes
        = determineDataTypes( dragSource, dropTarget );                
      dataType = checkDataType( event.currentDataType, validDataTypes );
      if( operation != DND.DROP_NONE && dataType != null ) {
        // fire DRAG_SET_DATA
        DragSourceEvent setDataEvent
          = createDragSetDataEvent( dragSource, dataType, point );
        setDataEvent.processEvent();
        // Check data
        Object data = transferData( dropTarget, dataType, setDataEvent );
        // fire DROP
        DropTargetEvent dropEvent
          = new DropTargetEvent( dropTarget, DropTargetEvent.DROP );
        dropEvent.detail = operation;
        dropEvent.currentDataType = dataType;
        dropEvent.dataTypes = validDataTypes;
        dropEvent.item = item;
        dropEvent.x = point.x;
        dropEvent.y = point.y;
        dropEvent.data = data;
        dropEvent.processEvent();
        operation = checkOperation( dragSource, dropTarget, dropEvent.detail );
      }
    }
    fireDragFinished( operation );
  }

  //////////////////////////
  // Create and fire events

  private static DragDetectEvent createDragDetectEvent( final Control control,
                                                        final Point point )
  {
    DragDetectEvent result = new DragDetectEvent( control );
    result.x = point.x;
    result.y = point.y;
    result.button = 1;
    result.time = readIntParam( EVENT_DRAG_START_TIME );
    return result;
  }

  private static DragSourceEvent createDragStartEvent(
    final DragSource dragSource,
    final Point point,
    final int time )
  {
    DragSourceEvent result
      = new DragSourceEvent( dragSource, DragSourceEvent.DRAG_START );
    result.detail = DND.DROP_NONE;
    result.x = point.x;
    result.y = point.y;
    result.doit = true;
    result.time = time;
    return result;
  }

  private static DragSourceEvent createDragSetDataEvent(
    final DragSource dragSource,
    final TransferData dataType,
    final Point point )
  {
    DragSourceEvent result
      = new DragSourceEvent( dragSource, DragSourceEvent.DRAG_SET_DATA );
    result.detail = DND.DROP_NONE;
    result.dataType = dataType;
    result.x = point.x;
    result.y = point.y;
    result.data = null;
    result.doit = true;
    return result;
  }

  private static DropTargetEvent createDropAcceptEvent(
    final DropTarget dropTarget,
    final int operation,
    final Point point,
    final TransferData dataType, 
    final Item item )
  {
    DropTargetEvent result
      = new DropTargetEvent( dropTarget, DropTargetEvent.DROP_ACCEPT );
    result.detail = operation;
    result.x = point.x;
    result.y = point.y;
    result.item = item;
    result.currentDataType = dataType;
    return result;
  }

  private static void fireDragLeave( final int operation,
                                     final DropTarget dropTarget,
                                     final Point point,
                                     final int time )
  {
    DropTargetEvent event
      = new DropTargetEvent( dropTarget, DropTargetEvent.DRAG_LEAVE );
    event.detail = operation;
    event.x = point.x;
    event.y = point.y;
    event.time = time;
    event.processEvent();
  }

  private static void fireDragFinished( final int operation ) {
    Control dragSourceControl = readControlParam( EVENT_DRAG_FINISHED );
    if( dragSourceControl != null ) {
      // fire DRAG_END
      DragSource dragSource = getDragSource( dragSourceControl );
      IDNDAdapter dndAdapter = getDNDAdapter( dragSource ); 
      dndAdapter.cancelDetailChanged();
      dndAdapter.cancelFeedbackChanged();
      dndAdapter.cancelDataTypeChanged();
      Point point = readXYParams( EVENT_DRAG_FINISHED_X,
                                  EVENT_DRAG_FINISHED_Y );
      DragSourceEvent event
        = new DragSourceEvent( dragSource, DragSourceEvent.DRAG_END );
      event.x = point.x;
      event.y = point.y;
      event.detail = operation;
      // NOTE : Doit is always true in SWT/Win, but should be false
      //        if no drop occurred. (According to documentation.)
      event.doit = true;
      event.time = readIntParam( EVENT_DRAG_FINISHED_TIME );
      event.processEvent();
    }
  }

  //////////////////
  // Helping methods

  private static IDNDAdapter getDNDAdapter( final DragSource dragSource ) {
    return ( IDNDAdapter )dragSource.getAdapter( IDNDAdapter.class );
  }

  private static DropTarget getDropTarget( final Control control ) {
    return ( DropTarget )control.getData( DND.DROP_TARGET_KEY );
  }
  
  private static DragSource getDragSource( final Control control ) {
    return ( DragSource )control.getData( DND.DRAG_SOURCE_KEY );
  }

  private static Object transferData( final DropTarget dropTarget,
                                      final TransferData dataType,
                                      final DragSourceEvent setDataEvent )
  {
    Object data = null;
    if( setDataEvent.doit ) {
      Transfer transfer = findTransferByType( dataType, dropTarget );
      transfer.javaToNative( setDataEvent.data, dataType );
      data = transfer.nativeToJava( dataType );
    }
    return data;
  }

  static TransferData[] determineDataTypes( final DragSource dragSource,
                                            final DropTarget dropTarget )
  {
    java.util.List supportedTypes = new ArrayList();
    Transfer[] dragSourceTransfers = dragSource.getTransfer();
    Transfer[] dropTargetTransfers = dropTarget.getTransfer();
    for( int i = 0; i < dragSourceTransfers.length; i++ ) {
      TransferData[] dataTypes = dragSourceTransfers[ i ].getSupportedTypes();
      for( int j = 0; j < dropTargetTransfers.length; j++ ) {
        for( int k = 0; k < dataTypes.length; k++ ) {
          if( dropTargetTransfers[ j ].isSupportedType( dataTypes[ k ] ) ) {
            supportedTypes.add( dataTypes[ k ] );
          }
        }
      }
    }
    TransferData[] result = new TransferData[ supportedTypes.size() ];
    for( int i = 0; i < supportedTypes.size(); i++ ) {
      result[ i ] = ( TransferData )supportedTypes.get( i );
    }
    return result;
  }

  private static Transfer findTransferByType( final TransferData type,
                                              final DropTarget dropTarget ) {
    Transfer result = null;
    Transfer[] supported = dropTarget.getTransfer();
    for( int i = 0; result == null && i < supported.length; i++ ) {
      if( supported[ i ].isSupportedType( type ) ) {
        result = supported[ i ];
      }
    }
    return result;
  }

  private static Widget findWidgetById( final String id ) {
    Widget result = null;
    Display display = RWTLifeCycle.getSessionDisplay();
    Shell[] shells = getDisplayAdapter( display ).getShells();
    for( int i = 0; result == null && i < shells.length; i++ ) {
      Widget widget = WidgetUtil.find( shells[ i ], id );
      if( widget != null ) {
        result = widget;
      }
    }
    return result;
  }

  private static IDisplayAdapter getDisplayAdapter( final Display display ) {
    Object adapter = display.getAdapter( IDisplayAdapter.class );
    return ( IDisplayAdapter )adapter;
  }

  private static String readStringParam( final String paramName ) {
    HttpServletRequest request = ContextProvider.getRequest();
    return request.getParameter( paramName );
  }

  private static int readIntParam( final String paramName ) {
    String value = readStringParam( paramName );
    return Integer.parseInt( value );
  }

  private static Control readControlParam( final String paramName ) {
    Control result = null;
    String value = readStringParam( paramName );
    if( value != null ) {
      result = ( Control )findWidgetById( value );
    }
    return result;
  }

  private static Item readItemParam( final String paramName ) {
    Item result = null;
    String value = readStringParam( paramName );
    if( value != null ) {
      result = ( Item )findWidgetById( value );
    }
    return result;
  }
  
  private static TransferData readDataTypeParam( final String paramName ) {
    TransferData result = null;
    String value = readStringParam( paramName );
    value = "null".equals( value ) ? null : value;
    if( value != null ) {
      result = new TransferData();
      result.type = Integer.parseInt( value );
    }
    return result;
  }
  
  // DND TODO [tb] : Is a check needed (like checkAndProcessMouseEvent)?
  //                 Yes, a drag would have to be canceled for invalid
  //                 coordinates on DragDetect, and dragEnter/Leave COULD
  //                 be thrown before they would be in SWT. (Severe problem?)
  private static Point readXYParams( final String xParamName,
                                     final String yParamName )
  {
    int x = readIntParam( xParamName );
    int y = readIntParam( yParamName );
    return new Point( x,y );
  }

  private static int readOperationParam( final String paramName ) {
    int result = DND.DROP_NONE;
    String value = readStringParam( paramName );
    if( "copy".equals( value ) ) {
      result = DND.DROP_COPY;
    } else if( "move".equals( value ) ) {
      result = DND.DROP_MOVE;
    } else if( "link".equals( value ) ) {
      result = DND.DROP_LINK;
    }
    return result;
  }

  private static boolean isLeaveBeforeEnter() {
    boolean result = false;
    String enter = readStringParam( EVENT_DRAG_ENTER_TIME );
    String leave = readStringParam( EVENT_DRAG_LEAVE_TIME );
    if( enter != null && leave != null ) {
      result = Integer.parseInt( leave ) <= Integer.parseInt( enter );
    }
    return result;
  }

  private static void changeOperation( final DragSource dragSource,
                                       final DropTarget dropTarget,
                                       final int detail )
  {
    int checkedOperation = checkOperation( dragSource, dropTarget, detail );
    IDNDAdapter dndAdapter = getDNDAdapter( dragSource );
    dndAdapter.setDetailChanged( dropTarget.getControl(), checkedOperation );
  }
  
  private static void changeDataType( final DragSource dragSource,
                                      final DropTarget dropTarget,
                                      final TransferData dataType )
  {
    
    TransferData[] validDataTypes
      = determineDataTypes( dragSource, dropTarget );    
    TransferData value = checkDataType( dataType, validDataTypes );
    // NOTE [tb] : If the value is not valid, another valid value will be set.
    //             This is simplified from SWT, where null would be set.
    if( value == null ) {
      value = validDataTypes[ 0 ];
    }
    IDNDAdapter dndAdapter = getDNDAdapter( dragSource );
    dndAdapter.setDataTypeChanged( dropTarget.getControl(), value );
  }
  
  private static int checkOperation( final DragSource dragSource,
                                     final DropTarget dropTarget,
                                     final int operation )
  {
    int result = DND.DROP_NONE;
    int allowedOperations = dragSource.getStyle() & dropTarget.getStyle();
    if( ( allowedOperations & operation ) != 0 ) {
      result = operation;
    }
    return result;
  }

  private static TransferData checkDataType( final TransferData dataType,
                                             final TransferData[] validTypes )
  {
    boolean isValidType = false;
    for( int i = 0; i < validTypes.length; i++ ) {
      if( !isValidType ) {
        isValidType = TransferData.sameType( dataType, validTypes[ i ] );
      }
    }
    return isValidType ? dataType : null;
  }

}

