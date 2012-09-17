/*******************************************************************************
 * Copyright (c) 2009, 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.displaykit;


import static org.eclipse.rap.rwt.lifecycle.WidgetUtil.getId;
import static org.eclipse.rap.rwt.testfixture.Fixture.getProtocolMessage;
import static org.eclipse.swt.dnd.DragSourceEvent.DRAG_END;
import static org.eclipse.swt.dnd.DragSourceEvent.DRAG_SET_DATA;
import static org.eclipse.swt.dnd.DragSourceEvent.DRAG_START;
import static org.eclipse.swt.dnd.DropTargetEvent.DRAG_ENTER;
import static org.eclipse.swt.dnd.DropTargetEvent.DRAG_LEAVE;
import static org.eclipse.swt.dnd.DropTargetEvent.DRAG_OVER;
import static org.eclipse.swt.dnd.DropTargetEvent.DROP;
import static org.eclipse.swt.dnd.DropTargetEvent.DROP_ACCEPT;
import static org.eclipse.swt.events.DragDetectEvent.DRAG_DETECT;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.CallOperation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.HTMLTransfer;
import org.eclipse.swt.dnd.RTFTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.events.DragDetectEvent;
import org.eclipse.swt.events.DragDetectListener;
import org.eclipse.swt.events.TypedEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.json.JSONArray;
import org.json.JSONException;

public class DNDSupport_Test extends TestCase {

  private Display display;
  private Shell shell;
  private java.util.List<TypedEvent> log;
  private Control sourceControl;
  private Control targetControl;
  private DragSource dragSource;
  private DropTarget dropTarget;
  private Transfer[] transfers;

  protected void setUp() throws Exception {
    Fixture.setUp();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    display = new Display();
    shell = new Shell( display );
    shell.open();
    sourceControl = new Label( shell, SWT.NONE );
    targetControl = new Label( shell, SWT.NONE );
    log = new ArrayList<TypedEvent>();
    Fixture.fakeNewRequest( display );
    transfers = new Transfer[] {
      HTMLTransfer.getInstance(),
      TextTransfer.getInstance()
    };
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testDragDetectEvent() {
    createDragSource( DND.DROP_MOVE );
    DragDetectListener listener = mock( DragDetectListener.class );
    sourceControl.addDragDetectListener( listener );

    fakeDragSourceEvent( "dragStart", 1 );
    Fixture.executeLifeCycleFromServerThread();

    verify( listener, times( 1 ) ).dragDetected( any( DragDetectEvent.class ) );
  }

  public void testDragStartEvent() {
    createDragSource( DND.DROP_MOVE );
    DragSourceListener listener = mock( DragSourceListener.class );
    dragSource.addDragListener( listener );

    fakeDragSourceEvent( "dragStart", 1 );
    Fixture.executeLifeCycleFromServerThread();

    verify( listener, times( 1 ) ).dragStart( any( DragSourceEvent.class ) );
  }

  public void testCancelStart() {
    createDragSource( DND.DROP_MOVE );
    dragSource.addDragListener( new DragSourceAdapter() {
      public void dragStart( DragSourceEvent event ) {
        event.doit = false;
      }
    } );

    fakeDragSourceEvent( "dragStart", 1 );
    Fixture.executeLifeCycleFromServerThread();

    assertNotNull( getProtocolMessage().findCallOperation( dragSource, "cancel" ) );
  }

  public void testDragDetectAndDragStartOrder() {
    createDragSource( DND.DROP_MOVE );
    addLogger( dragSource );
    sourceControl.addDragDetectListener( new DragDetectListener() {
      public void dragDetected( DragDetectEvent event ) {
        log.add( event );
      }
    } );

    fakeDragSourceEvent( "dragStart", 1 );
    Fixture.executeLifeCycleFromServerThread();

    int[] expected = new int[]{ DRAG_DETECT, DRAG_START };
    assertTrue( Arrays.equals( expected, getEventOrder() ) );
  }

  public void testDragStartCoordinates() {
    shell.setLocation( 5, 5 );
    sourceControl.setLocation( 10, 20 );
    createDragSource( DND.DROP_MOVE );
    new DropTarget( targetControl, DND.DROP_MOVE );
    dragSource.addDragListener( new DragSourceAdapter() {
      public void dragStart( DragSourceEvent event ) {
        log.add( event );
      }
    } );

    Fixture.fakeNewRequest( display );
    createDragSourceEvent( "dragStart", 20, 30, "move", 1 );
    Fixture.executeLifeCycleFromServerThread();

    DragSourceEvent dragSourceEvent = ( DragSourceEvent )log.get( 0 );
    assertEquals( 4, dragSourceEvent.x );
    assertEquals( 4, dragSourceEvent.y );
  }

  public void testDropTargetLeaveBeforeEnter() {
    createDragSource( DND.DROP_MOVE );
    createDropTarget( DND.DROP_MOVE );
    fakeDropTargetEvent( "dragEnter", 1 );
    Fixture.executeLifeCycleFromServerThread();
    addLogger( dropTarget );

    fakeDropTargetEvent( "dragLeave", 2 );
    fakeDropTargetEvent( "dragEnter", 3 );
    fakeDropTargetEvent( "dragOver", 4 );
    Fixture.executeLifeCycleFromServerThread();

    int[] expected = new int[]{ DRAG_LEAVE, DRAG_ENTER, DRAG_OVER };
    assertTrue( Arrays.equals( expected, getEventOrder() ) );
  }


  public void testDropOverValidTarget() {
    createDragSource( DND.DROP_MOVE );
    createDropTarget( DND.DROP_MOVE );
    addLogger( dragSource );
    addLogger( dropTarget );
    addSetDataListener( dragSource, "text" );

    fakeDropTargetEvent( "dropAccept", 1 );
    fakeDragSourceEvent( "dragFinished", 2 );
    Fixture.readDataAndProcessAction( display );

    int[] expected = new int[]{ DRAG_LEAVE, DROP_ACCEPT, DRAG_SET_DATA, DROP, DRAG_END };
    assertTrue( Arrays.equals( expected, getEventOrder() ) );
  }

  public void testDataTransferOnDrop() {
    createDragSource( DND.DROP_MOVE );
    createDropTarget( DND.DROP_MOVE );
    addLogger( dragSource );
    addLogger( dropTarget );
    addSetDataListener( dragSource, "Hello World!" );


    createDropTargetEvent( "dropAccept", 0, 0, "move", getTextType(), 1 );
    Fixture.readDataAndProcessAction( display );

    DragSourceEvent dragSetDataEvent = getDragSourceEvent( 2 );
    DropTargetEvent dropEvent = getDropTargetEvent( 3 );
    assertEquals( dragSetDataEvent.dataType, dropEvent.currentDataType );
    assertEquals( "Hello World!", dropEvent.data );
  }

  public void testInvalidDataOnDragSetData() {
    createDragSource( DND.DROP_MOVE );
    createDropTarget( DND.DROP_MOVE );
    addLogger( dragSource );
    addLogger( dropTarget );
    addSetDataListener( dragSource, new Date() );

    SWTException exception = null;

    fakeDropTargetEvent( "dropAccept", 1 );
    try {
      Fixture.executeLifeCycleFromServerThread();
      fail();
    } catch( SWTException e ) {
      exception = e;
    }

    int[] expected = new int[]{ DRAG_LEAVE, DROP_ACCEPT, DRAG_SET_DATA };
    assertTrue( Arrays.equals( expected, getEventOrder() ) );
    assertEquals( DND.ERROR_INVALID_DATA, exception.code );
  }

  public void testChangeDataTypeOnDrop() {
    createDragSource( DND.DROP_MOVE );
    createDropTarget( DND.DROP_MOVE );
    final TransferData[] originalType = new TransferData[ 1 ];
    dropTarget.addDropListener( new DropTargetAdapter() {
      public void dropAccept( DropTargetEvent event ) {
        originalType[ 0 ] = event.currentDataType;
        boolean isFirstType = event.currentDataType.type == getType( transfers[ 0 ] ).type;
        event.currentDataType = getType( transfers[ isFirstType ? 1 : 0 ] );
      }
      public void drop( DropTargetEvent event ) {
        log.add( event );
      }
    } );
    addLogger( dragSource );
    addSetDataListener( dragSource, "data" );


    fakeDropTargetEvent( "dropAccept", 1 );
    Fixture.readDataAndProcessAction( display );

    DragSourceEvent dragSetDataEvent = getDragSourceEvent( 0 );
    DropTargetEvent dropEvent = getDropTargetEvent( 1 );
    assertTrue( dragSetDataEvent.dataType.type != originalType[ 0 ].type );
    assertTrue( dragSetDataEvent.dataType.type == dropEvent.currentDataType.type );
  }

  public void testChangeDataTypeInvalidOnDrop() {
    createDragSource( DND.DROP_MOVE );
    createDropTarget( DND.DROP_MOVE );
    dropTarget.addDropListener( new DropTargetAdapter() {
      public void dropAccept( DropTargetEvent event ) {
        RTFTransfer rtfTransfer = RTFTransfer.getInstance();
        event.currentDataType = rtfTransfer.getSupportedTypes()[ 0 ];
      }
    } );
    addLogger( dragSource );
    addLogger( dropTarget );

    fakeDropTargetEvent( "dropAccept", 1 );
    Fixture.readDataAndProcessAction( display );

    int[] expected = new int[]{ DRAG_LEAVE, DROP_ACCEPT };
    assertTrue( Arrays.equals( expected, getEventOrder() ) );
  }

  public void testNoDropAfterDropAcceptEvent() {
    createDragSource( DND.DROP_MOVE );
    createDropTarget( DND.DROP_MOVE );
    dropTarget.addDropListener( new DropTargetAdapter() {
      public void dropAccept( DropTargetEvent event ) {
        log.add( event );
        event.detail = DND.DROP_NONE;
      }
      public void drop( DropTargetEvent event ) {
        log.add( event );
      }
    } );
    addLogger( dragSource );

    fakeDropTargetEvent( "dropAccept", 1 );
    fakeDragSourceEvent( "dragFinished", 2 );
    Fixture.readDataAndProcessAction( display );

    int[] expected = new int[]{ DROP_ACCEPT, DRAG_END };
    assertTrue( Arrays.equals( expected, getEventOrder() ) );
    assertTrue( getDragSourceEvent( 1 ).doit ); // Actual SWT behavior
  }

  public void testDropOverNonTarget() {
    createDragSource( DND.DROP_MOVE );
    createDropTarget( DND.DROP_MOVE );
    addLogger( dragSource );
    addLogger( dropTarget );

    fakeDragSourceEvent( "dragFinished", 1 );
    Fixture.readDataAndProcessAction( display );

    assertEquals( 1, log.size() );
    DragSourceEvent event = getDragSourceEvent( 0 );
    assertEquals( DRAG_END, event.getID() );
    assertTrue( event.doit ); // Actual SWT behavior
  }

  public void testChangeDetailInDropAccept() {
    createDragSource( DND.DROP_MOVE | DND.DROP_COPY );
    createDropTarget( DND.DROP_MOVE | DND.DROP_COPY );
    addLogger( dragSource );
    addSetDataListener( dragSource, "text Data" );
    dropTarget.addDropListener( new DropTargetAdapter() {
      public void dropAccept( DropTargetEvent event ) {
        log.add( event );
        event.detail = DND.DROP_COPY;
      }
      public void drop( DropTargetEvent event ) {
        log.add( event );
      }
    } );

    fakeDropTargetEvent( "dropAccept", 1 );
    fakeDragSourceEvent( "dragFinished", 2 );
    Fixture.readDataAndProcessAction( display );

    assertEquals( DND.DROP_COPY, getDropTargetEvent( 0 ).detail );
    assertEquals( DND.DROP_NONE, getDragSourceEvent( 1 ).detail );
    assertEquals( DND.DROP_COPY, getDropTargetEvent( 2 ).detail );
    assertEquals( DND.DROP_COPY, getDragSourceEvent( 3 ).detail );
    assertTrue( getDragSourceEvent( 3 ).doit );
  }

  public void testChangeDetailInvalidInDropAccept() {
    createDragSource( DND.DROP_MOVE | DND.DROP_COPY );
    createDropTarget( DND.DROP_MOVE );
    addLogger( dragSource );
    dropTarget.addDropListener( new DropTargetAdapter() {
      public void dropAccept( DropTargetEvent event ) {
        event.detail = DND.DROP_COPY;
      }
    } );

    fakeDropTargetEvent( "dropAccept", 1 );
    fakeDragSourceEvent( "dragFinished", 2 );
    Fixture.readDataAndProcessAction( display );

    assertTrue( getDragSourceEvent( 0 ).doit ); // This is still true in SWT/Win
    assertEquals( DND.DROP_NONE, getDragSourceEvent( 0 ).detail );
  }

  public void testDragSetDataDoitIsFalse() {
    createDragSource( DND.DROP_MOVE );
    createDropTarget( DND.DROP_MOVE );
    addLogger( dragSource );
    addLogger( dropTarget );
    dragSource.addDragListener( new DragSourceAdapter() {
      public void dragSetData( DragSourceEvent event ) {
        event.data = "TestData";
        event.doit = false;
      }
    } );

    fakeDropTargetEvent( "dropAccept", 1 );
    fakeDragSourceEvent( "dragFinished", 2 );
    Fixture.readDataAndProcessAction( display );

    int[] expected = new int[]{ DRAG_LEAVE, DROP_ACCEPT, DRAG_SET_DATA, DROP, DRAG_END };
    assertTrue( Arrays.equals( expected, getEventOrder() ) );
    // NOTE: This is not the behavior documented for SWT, but how SWT behaves in Windows (SWT bug?)
    assertNull( getDropTargetEvent( 3 ).data );
    assertTrue( getDragSourceEvent( 4 ).doit );
  }

  public void testDragSetDataDataType() {
    createDragSource( DND.DROP_MOVE );
    createDropTarget( DND.DROP_MOVE );
    addLogger( dropTarget );
    addLogger( dragSource );
    addSetDataListener( dragSource, "string" );

    fakeDropTargetEvent( "dropAccept", 0 );
    Fixture.readDataAndProcessAction( display );

    DragSourceEvent setDataEvent = getDragSourceEvent( 2 );
    DropTargetEvent dropEvent = getDropTargetEvent( 3 );
    assertNotNull( setDataEvent.dataType );
    assertSame( setDataEvent.data, dropEvent.data );
    assertTrue( TransferData.sameType( setDataEvent.dataType, dropEvent.currentDataType ) );
  }

  public void testResponseNoDetailChange() {
    createDragSource( DND.DROP_MOVE | DND.DROP_LINK | DND.DROP_COPY );
    createDropTarget( DND.DROP_MOVE | DND.DROP_LINK | DND.DROP_COPY );

    createDropTargetEvent( "dragEnter", 10, 10, "copy", getTextType(), 1 );
    createDropTargetEvent( "dragOver", 10, 10, "copy", getTextType(), 2 );
    Fixture.executeLifeCycleFromServerThread();

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( dragSource, "changeDetail" ) );
  }

  public void testResponseDetailChangedOnEnter() {
    createDragSource( DND.DROP_MOVE | DND.DROP_LINK | DND.DROP_COPY );
    createDropTarget( DND.DROP_MOVE | DND.DROP_LINK | DND.DROP_COPY );
    dropTarget.addDropListener( new DropTargetAdapter() {
      public void dragEnter( DropTargetEvent event ) {
        event.detail = DND.DROP_LINK;
      }
    } );

    fakeDropTargetEvent( "dragEnter", 1 );
    fakeDropTargetEvent( "dragOver", 2 );
    Fixture.executeLifeCycleFromServerThread();

    Message message = Fixture.getProtocolMessage();
    CallOperation call = message.findCallOperation( dragSource, "changeDetail" );
    assertEquals( getId( targetControl ), call.getProperty( "control" ) );
    assertEquals( "DROP_LINK", call.getProperty( "detail" ) );
  }

  public void testResponseDetailChangedOnOver() {
    createDragSource( DND.DROP_MOVE | DND.DROP_LINK | DND.DROP_COPY );
    createDropTarget( DND.DROP_MOVE | DND.DROP_LINK | DND.DROP_COPY );
    dropTarget.addDropListener( new DropTargetAdapter() {
      public void dragOver( DropTargetEvent event ) {
        event.detail = DND.DROP_LINK;
      }
    } );

    createDropTargetEvent( "dragEnter", 10, 10, "move", getTextType(), 1 );
    createDropTargetEvent( "dragOver", 10, 10, "copy", getTextType(), 2 );
    Fixture.executeLifeCycleFromServerThread();

    Message message = Fixture.getProtocolMessage();
    CallOperation call = message.findCallOperation( dragSource, "changeDetail" );
    assertEquals( getId( targetControl ), call.getProperty( "control" ) );
    assertEquals( "DROP_LINK", call.getProperty( "detail" ) );
  }

  public void testDropAcceptWithDetailChangedOnEnter() {
    createDragSource( DND.DROP_MOVE | DND.DROP_LINK | DND.DROP_COPY );
    createDropTarget( DND.DROP_MOVE | DND.DROP_LINK | DND.DROP_COPY );
    addSetDataListener( dragSource, "some data" );
    addLogger( dropTarget );
    dropTarget.addDropListener( new DropTargetAdapter() {
      public void dragEnter( DropTargetEvent event ) {
        event.detail = DND.DROP_COPY;
      }
    } );

    Fixture.executeLifeCycleFromServerThread(); // clear pending message operations
    Fixture.fakeNewRequest( display );
    fakeDropTargetEvent( "dragEnter", 1 );
    fakeDropTargetEvent( "dragOver", 2 );
    fakeDropTargetEvent( "dropAccept", 3 );
    fakeDragSourceEvent( "dragFinished", 3 );
    Fixture.executeLifeCycleFromServerThread();

    Message message = Fixture.getProtocolMessage();
    assertEquals( 0, message.getOperationCount() );
    assertEquals( DND.DROP_COPY, getDropTargetEvent( 0 ).detail );
    assertEquals( DND.DROP_COPY, getDropTargetEvent( 1 ).detail );
    assertEquals( DND.DROP_COPY, getDropTargetEvent( 2 ).detail );
    assertEquals( DND.DROP_COPY, getDropTargetEvent( 3 ).detail );
    assertEquals( DND.DROP_COPY, getDropTargetEvent( 4 ).detail );
  }

  public void testDetermineDataType() {
    createDragSource( DND.DROP_MOVE );
    createDropTarget( DND.DROP_MOVE );
    dropTarget.setTransfer( new Transfer[]{
      RTFTransfer.getInstance(),
      HTMLTransfer.getInstance()
    } );

    TransferData[] dataTypes = DNDSupport.determineDataTypes( dragSource, dropTarget );

    assertEquals( 1, dataTypes.length );
    assertTrue( HTMLTransfer.getInstance().isSupportedType( dataTypes[ 0 ] ) );
  }

  public void testResponseFeedbackChangedOnEnter() throws JSONException {
    createDragSource( DND.DROP_MOVE | DND.DROP_LINK | DND.DROP_COPY );
    createDropTarget( DND.DROP_MOVE | DND.DROP_LINK | DND.DROP_COPY );
    dropTarget.addDropListener( new DropTargetAdapter() {
      public void dragEnter( DropTargetEvent event ) {
        event.feedback = DND.FEEDBACK_SELECT;
      }
    } );
    Fixture.executeLifeCycleFromServerThread(); // clear message
    Fixture.fakeNewRequest( display );

    fakeDropTargetEvent( "dragEnter", 1 );
    fakeDropTargetEvent( "dragOver", 2 );
    Fixture.executeLifeCycleFromServerThread();

    Message message = Fixture.getProtocolMessage();
    CallOperation call = message.findCallOperation( dragSource, "changeFeedback" );
    assertEquals( getId( targetControl ), call.getProperty( "control" ) );
    assertEquals( new Integer( DND.FEEDBACK_SELECT ), call.getProperty( "flags" ) );
    JSONArray feedbackArr = ( JSONArray )call.getProperty( "feedback" );
    assertEquals( 1, feedbackArr.length() );
    assertEquals( "FEEDBACK_SELECT", feedbackArr.getString( 0 ) );
  }

  public void testResponseFeedbackChangedOnOver() throws JSONException {
    createDragSource( DND.DROP_MOVE | DND.DROP_LINK | DND.DROP_COPY );
    createDropTarget( DND.DROP_MOVE | DND.DROP_LINK | DND.DROP_COPY );
    dropTarget.addDropListener( new DropTargetAdapter() {
      public void dragOver( DropTargetEvent event ) {
        event.feedback = DND.FEEDBACK_EXPAND | DND.FEEDBACK_SCROLL;
      }
    } );
    Fixture.executeLifeCycleFromServerThread(); // clear message
    Fixture.fakeNewRequest( display );

    fakeDropTargetEvent( "dragEnter", 1 );
    fakeDropTargetEvent( "dragOver", 2 );
    Fixture.executeLifeCycleFromServerThread();

    Message message = Fixture.getProtocolMessage();
    CallOperation call = message.findCallOperation( dragSource, "changeFeedback" );
    assertEquals( getId( targetControl ), call.getProperty( "control" ) );
    Integer expectedFlags = new Integer( DND.FEEDBACK_SCROLL | DND.FEEDBACK_EXPAND );
    assertEquals( expectedFlags, call.getProperty( "flags" ) );
    JSONArray feedbackArr = ( JSONArray )call.getProperty( "feedback" );
    assertEquals( 2, feedbackArr.length() );
    assertEquals( "FEEDBACK_EXPAND", feedbackArr.getString( 0 ) );
    assertEquals( "FEEDBACK_SCROLL", feedbackArr.getString( 1 ) );

  }

  public void testResponseInitDataType() {
    createDragSource( DND.DROP_MOVE | DND.DROP_LINK | DND.DROP_COPY );
    createDropTarget( DND.DROP_MOVE | DND.DROP_LINK | DND.DROP_COPY );
    Transfer[] types = new Transfer[] {
      TextTransfer.getInstance(),
      RTFTransfer.getInstance()
    };
    dragSource.setTransfer( types );
    dropTarget.setTransfer( types );
    Fixture.executeLifeCycleFromServerThread();

    Fixture.fakeNewRequest( display );
    fakeDropTargetEvent( "dragEnter", 1 );
    Fixture.executeLifeCycleFromServerThread();

    Message message = Fixture.getProtocolMessage();
    CallOperation call = message.findCallOperation( dragSource, "changeDataType" );
    assertEquals( getId( targetControl ), call.getProperty( "control" ) );
  }

  public void testResponseChangeDataTypeOnOver() {
    createDragSource( DND.DROP_MOVE | DND.DROP_LINK | DND.DROP_COPY );
    createDropTarget( DND.DROP_MOVE | DND.DROP_LINK | DND.DROP_COPY );
    addLogger( dropTarget );
    dropTarget.addDropListener( new DropTargetAdapter() {
      public void dragOver( DropTargetEvent event ) {
        event.currentDataType = TextTransfer.getInstance().getSupportedTypes()[ 0 ];
      }
    } );
    Fixture.executeLifeCycleFromServerThread();
    Fixture.fakeNewRequest( display );

    fakeDropTargetEvent( "dragEnter", 1 );
    fakeDropTargetEvent( "dragOver", 2 );
    Fixture.executeLifeCycleFromServerThread();

    DropTargetEvent dragEnter = getDropTargetEvent( 0 );
    assertTrue( HTMLTransfer.getInstance().isSupportedType( dragEnter.currentDataType ) );
    Message message = Fixture.getProtocolMessage();
    CallOperation call = message.findCallOperation( dragSource, "changeDataType" );
    assertEquals( getId( targetControl ), call.getProperty( "control" ) );
    assertEquals( new Integer( getTextType() ), call.getProperty( "dataType" ) );
  }

  public void testResponseChangeDataTypeOnEnter() {
    int operations = DND.DROP_MOVE | DND.DROP_LINK | DND.DROP_COPY;
    createDragSource( operations );
    createDropTarget( operations );
    addLogger( dropTarget );
    dropTarget.addDropListener( new DropTargetAdapter() {
      public void dragEnter( DropTargetEvent event ) {
        event.currentDataType = TextTransfer.getInstance().getSupportedTypes()[ 0 ];
      }
    } );
    Fixture.executeLifeCycleFromServerThread();
    Fixture.fakeNewRequest( display );

    fakeDropTargetEvent( "dragEnter", 1 );
    fakeDropTargetEvent( "dragOver", 2 );
    Fixture.executeLifeCycleFromServerThread();

    DropTargetEvent dragOver = getDropTargetEvent( 1 );
    assertTrue( TextTransfer.getInstance().isSupportedType( dragOver.currentDataType ) );
    Message message = Fixture.getProtocolMessage();
    CallOperation call = message.findCallOperation( dragSource, "changeDataType" );
    assertEquals( getId( targetControl ), call.getProperty( "control" ) );
    Integer expectedType = new Integer( TextTransfer.getInstance().getSupportedTypes()[ 0 ].type );
    assertEquals( expectedType, call.getProperty( "dataType" ) );
  }

  public void testResponseChangeDataTypeInvalid() {
    // NOTE : Setting an invalid value on currentDataType reverts the field
    //        back to the next-best valid value. This is NOT SWT-like behavior!
    //        SWT would set null and display the DROP_NONE cursor.
    createDragSource( DND.DROP_MOVE | DND.DROP_LINK | DND.DROP_COPY );
    createDropTarget( DND.DROP_MOVE | DND.DROP_LINK | DND.DROP_COPY );
    dragSource.setTransfer( new Transfer[] { TextTransfer.getInstance() } );
    dropTarget.setTransfer( new Transfer[] { TextTransfer.getInstance() } );
    dropTarget.addDropListener( new DropTargetAdapter() {
      public void dragOver( DropTargetEvent event ) {
        event.currentDataType = RTFTransfer.getInstance().getSupportedTypes()[ 0 ];
      }
    } );
    Fixture.executeLifeCycleFromServerThread();
    Fixture.fakeNewRequest( display );

    fakeDropTargetEvent( "dragOver", 1 );
    Fixture.executeLifeCycleFromServerThread();

    Message message = Fixture.getProtocolMessage();
    CallOperation call = message.findCallOperation( dragSource, "changeDataType" );
    assertEquals( getId( targetControl ), call.getProperty( "control" ) );
    Integer expectedType = new Integer( getTextType() );
    assertEquals( expectedType, call.getProperty( "dataType" ) );

  }

  public void testOperationChangedEvent() {
    createDragSource( DND.DROP_MOVE | DND.DROP_LINK | DND.DROP_COPY );
    createDropTarget( DND.DROP_MOVE | DND.DROP_LINK | DND.DROP_COPY );
    addLogger( dropTarget );

    fakeDropTargetEvent( "dragEnter", 2 );
    createDropTargetEvent( "dragOperationChanged", 0, 0, "copy", getTextType(), 3 );
    fakeDropTargetEvent( "dragOver", 5 );
    Fixture.executeLifeCycleFromServerThread();

    DropTargetEvent dragOperationChanged = getDropTargetEvent( 1 );
    assertEquals( DropTargetEvent.DRAG_OPERATION_CHANGED, dragOperationChanged.getID() );
    assertTrue( ( dragOperationChanged.detail & DND.DROP_COPY ) != 0 );
    DropTargetEvent dragOver = getDropTargetEvent( 2 );
    assertEquals( DRAG_OVER, dragOver.getID() );
  }

  public void testOperationsField() {
    createDragSource( DND.DROP_MOVE | DND.DROP_LINK );
    createDropTarget( DND.DROP_MOVE | DND.DROP_LINK );
    dragSource.addDragListener( new DragSourceAdapter(){
      public void dragSetData( DragSourceEvent event ) {
        event.data = "text";
      }
    } );
    addLogger( dropTarget );

    fakeDropTargetEvent( "dragEnter", 2 );
    fakeDropTargetEvent( "dragOver", 5 );
    fakeDropTargetEvent( "dragOperationChanged", 7 );
    fakeDropTargetEvent( "dropAccept", 8 );
    Fixture.readDataAndProcessAction( display );

    assertEquals( 6, log.size() );
    assertEquals( DND.DROP_MOVE | DND.DROP_LINK, getDropTargetEvent( 0 ).operations );
    assertEquals( DND.DROP_MOVE | DND.DROP_LINK, getDropTargetEvent( 1 ).operations );
    assertEquals( DND.DROP_MOVE | DND.DROP_LINK, getDropTargetEvent( 2 ).operations );
    assertEquals( 0, getDropTargetEvent( 3 ).operations );
    assertEquals( DND.DROP_MOVE | DND.DROP_LINK, getDropTargetEvent( 4 ).operations );
    assertEquals( DND.DROP_MOVE | DND.DROP_LINK, getDropTargetEvent( 5 ).operations );
  }

  /////////
  // Helper

  private void fakeDragSourceEvent( String eventType, int time ) {
    createDragSourceEvent( eventType, 0, 0, "move", time );
  }

  private void createDragSourceEvent( String eventType, int x, int y, String operation, int time ) {
    String prefix = "org.eclipse.swt.dnd." + eventType;
    String controlId = getId( sourceControl );
    Fixture.fakeRequestParam( prefix, controlId );
    Fixture.fakeRequestParam( prefix + ".x", String.valueOf( x ) );
    Fixture.fakeRequestParam( prefix + ".y", String.valueOf( y ) );
    Fixture.fakeRequestParam( prefix + ".time", String.valueOf( time ) );
  }

  private void fakeDropTargetEvent( String eventType, int time ) {
    createDropTargetEvent( eventType, 0, 0, "move", getTextType(), time  );
  }

  private void createDropTargetEvent( String eventType,
                                      int x,
                                      int y,
                                      String operation,
                                      int dataType,
                                      int time )
  {
    String prefix = "org.eclipse.swt.dnd." + eventType;
    String controlId = getId( targetControl );
    String sourceId = getId( sourceControl );
    Fixture.fakeRequestParam( prefix, controlId );
    Fixture.fakeRequestParam( prefix + ".x", String.valueOf( x ) );
    Fixture.fakeRequestParam( prefix + ".y", String.valueOf( y ) );
    Fixture.fakeRequestParam( prefix + ".operation", operation );
    Fixture.fakeRequestParam( prefix + ".feedback", "0" );
    Fixture.fakeRequestParam( prefix + ".source", sourceId );
    Fixture.fakeRequestParam( prefix + ".time", String.valueOf( time ) );
    Fixture.fakeRequestParam( prefix + ".dataType", String.valueOf( dataType ) );
  }

  private void createDropTarget( int style ) {
    dropTarget = new DropTarget( targetControl, style );
    dropTarget.setTransfer( transfers );
  }

  private void createDragSource( int style ) {
    dragSource = new DragSource( sourceControl, style );
    dragSource.setTransfer( transfers );
  }

  TransferData getType( Transfer transfer ) {
    return transfer.getSupportedTypes()[ 0 ];
  }

  private int getHTMLType() {
    return getType( HTMLTransfer.getInstance() ).type;
  }

  private int getTextType() {
    return getType( TextTransfer.getInstance()).type;
  }

  private void addLogger( DragSource dragSource ) {
    dragSource.addDragListener( new DragSourceListener() {
      public void dragStart( DragSourceEvent event ) {
        log.add( event );
      }
      public void dragSetData( DragSourceEvent event ) {
        log.add( event );
      }
      public void dragFinished( DragSourceEvent event ) {
        log.add( event );
      }
    } );
  }

  private void addLogger( DropTarget dropTarget ) {
    dropTarget.addDropListener( new LogingDropTargetListener() );
  }

  private class LogingDropTargetListener implements DropTargetListener {
    public void dragEnter( DropTargetEvent event ) {
      log.add( event );
    }
    public void dragLeave( DropTargetEvent event ) {
      log.add( event );
    }
    public void dragOperationChanged( DropTargetEvent event ) {
      log.add( event );
    }
    public void dragOver( DropTargetEvent event ) {
      log.add( event );
    }
    public void drop( DropTargetEvent event ) {
      log.add( event );
    }
    public void dropAccept( DropTargetEvent event ) {
      log.add( event );
    }
  }

  private int[] getEventOrder() {
    int[] result = new int[ log.size() ];
    for( int i = 0; i < result.length; i++ ) {
      result[ i ] = log.get( i ).getID();
    }
    return result;
  }

  private void addSetDataListener( DragSource dragSource, final Object data ) {
    dragSource.addDragListener( new DragSourceAdapter() {
      public void dragSetData( DragSourceEvent event ) {
        event.data = data;
      }
    } );
  }

  private DropTargetEvent getDropTargetEvent( int i ) {
    return ( DropTargetEvent )log.get( i );
  }

  private DragSourceEvent getDragSourceEvent( int i ) {
    return ( DragSourceEvent )log.get( i );
  }

}
