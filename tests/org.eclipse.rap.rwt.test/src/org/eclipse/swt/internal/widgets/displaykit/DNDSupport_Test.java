/*******************************************************************************
 * Copyright (c) 2009, 2012 EclipseSource and others.
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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DNDSupport_Test {

  private Display display;
  private Shell shell;
  private List<TypedEvent> events;
  private List<Integer> eventTypes;
  private Control sourceControl;
  private Control targetControl;
  private DragSource dragSource;
  private DropTarget dropTarget;
  private Transfer[] transfers;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display );
    shell.open();
    sourceControl = new Label( shell, SWT.NONE );
    targetControl = new Label( shell, SWT.NONE );
    events = new ArrayList<TypedEvent>();
    eventTypes = new ArrayList<Integer>();
    Fixture.fakeNewRequest();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    transfers = new Transfer[] {
      HTMLTransfer.getInstance(),
      TextTransfer.getInstance()
    };
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testDragDetectEvent() {
    createDragSource( DND.DROP_MOVE );
    DragDetectListener listener = mock( DragDetectListener.class );
    sourceControl.addDragDetectListener( listener );

    fakeDragSourceEvent( "dragStart", 1 );
    Fixture.executeLifeCycleFromServerThread();

    verify( listener, times( 1 ) ).dragDetected( any( DragDetectEvent.class ) );
  }

  @Test
  public void testDragStartEvent() {
    createDragSource( DND.DROP_MOVE );
    DragSourceListener listener = mock( DragSourceListener.class );
    dragSource.addDragListener( listener );

    fakeDragSourceEvent( "dragStart", 1 );
    Fixture.executeLifeCycleFromServerThread();

    verify( listener, times( 1 ) ).dragStart( any( DragSourceEvent.class ) );
  }

  @Test
  public void testCancelStart() {
    createDragSource( DND.DROP_MOVE );
    dragSource.addDragListener( new DragSourceAdapter() {
      @Override
      public void dragStart( DragSourceEvent event ) {
        event.doit = false;
      }
    } );

    fakeDragSourceEvent( "dragStart", 1 );
    Fixture.executeLifeCycleFromServerThread();

    assertNotNull( getProtocolMessage().findCallOperation( dragSource, "cancel" ) );
  }

  @Test
  public void testDragDetectAndDragStartOrder() {
    createDragSource( DND.DROP_MOVE );
    addLogger( dragSource );
    sourceControl.addDragDetectListener( new DragDetectListener() {
      public void dragDetected( DragDetectEvent event ) {
        events.add( event );
        eventTypes.add( Integer.valueOf( SWT.DragDetect ) );
      }
    } );

    fakeDragSourceEvent( "dragStart", 1 );
    Fixture.executeLifeCycleFromServerThread();

    int[] expected = new int[]{ SWT.DragDetect, DND.DragStart };
    assertTrue( Arrays.equals( expected, getEventOrder() ) );
  }

  @Test
  public void testDragStartCoordinates() {
    shell.setLocation( 5, 5 );
    sourceControl.setLocation( 10, 20 );
    createDragSource( DND.DROP_MOVE );
    new DropTarget( targetControl, DND.DROP_MOVE );
    dragSource.addDragListener( new DragSourceAdapter() {
      @Override
      public void dragStart( DragSourceEvent event ) {
        events.add( event );
      }
    } );

    Fixture.fakeNewRequest();
    createDragSourceEvent( "dragStart", 20, 30, "move", 1 );
    Fixture.executeLifeCycleFromServerThread();

    DragSourceEvent dragSourceEvent = ( DragSourceEvent )events.get( 0 );
    assertEquals( 4, dragSourceEvent.x );
    assertEquals( 4, dragSourceEvent.y );
  }

  @Test
  public void testDropTargetLeaveBeforeEnter() {
    createDragSource( DND.DROP_MOVE );
    createDropTarget( DND.DROP_MOVE );
    fakeDropTargetEvent( "dragEnter", 1 );
    Fixture.executeLifeCycleFromServerThread();
    addLogger( dropTarget );

    Fixture.fakeNewRequest();
    fakeDropTargetEvent( "dragLeave", 2 );
    fakeDropTargetEvent( "dragEnter", 3 );
    fakeDropTargetEvent( "dragOver", 4 );
    Fixture.executeLifeCycleFromServerThread();

    int[] expected = new int[]{ DND.DragLeave, DND.DragEnter, DND.DragOver };
    assertTrue( Arrays.equals( expected, getEventOrder() ) );
  }

  @Test
  public void testDropOverValidTarget() {
    createDragSource( DND.DROP_MOVE );
    createDropTarget( DND.DROP_MOVE );
    addLogger( dragSource );
    addLogger( dropTarget );
    addSetDragDataListener( dragSource, "text" );

    fakeDropTargetEvent( "dropAccept", 1 );
    fakeDragSourceEvent( "dragFinished", 2 );
    Fixture.readDataAndProcessAction( display );

    int[] expected = new int[]{ DND.DragLeave, DND.DropAccept, DND.DragSetData, DND.Drop, DND.DragEnd };
    assertTrue( Arrays.equals( expected, getEventOrder() ) );
  }

  @Test
  public void testDataTransferOnDrop() {
    createDragSource( DND.DROP_MOVE );
    createDropTarget( DND.DROP_MOVE );
    addLogger( dragSource );
    addLogger( dropTarget );
    addSetDragDataListener( dragSource, "Hello World!" );


    createDropTargetEvent( "dropAccept", 0, 0, "move", getTextType(), 1 );
    Fixture.readDataAndProcessAction( display );

    DragSourceEvent dragSetDataEvent = getDragSourceEvent( 2 );
    DropTargetEvent dropEvent = getDropTargetEvent( 3 );
    assertEquals( dragSetDataEvent.dataType, dropEvent.currentDataType );
    assertEquals( "Hello World!", dropEvent.data );
  }

  @Test
  public void testInvalidDataOnDragSetData() {
    createDragSource( DND.DROP_MOVE );
    createDropTarget( DND.DROP_MOVE );
    addLogger( dragSource );
    addLogger( dropTarget );
    addSetDragDataListener( dragSource, new Date() );

    SWTException exception = null;

    fakeDropTargetEvent( "dropAccept", 1 );
    try {
      Fixture.executeLifeCycleFromServerThread();
      fail();
    } catch( SWTException e ) {
      exception = e;
    }

    int[] expected = new int[]{ DND.DragLeave, DND.DropAccept, DND.DragSetData };
    assertTrue( Arrays.equals( expected, getEventOrder() ) );
    assertEquals( DND.ERROR_INVALID_DATA, exception.code );
  }

  @Test
  public void testChangeDataTypeOnDrop() {
    createDragSource( DND.DROP_MOVE );
    createDropTarget( DND.DROP_MOVE );
    final TransferData[] originalType = new TransferData[ 1 ];
    dropTarget.addDropListener( new DropTargetAdapter() {
      @Override
      public void dropAccept( DropTargetEvent event ) {
        originalType[ 0 ] = event.currentDataType;
        boolean isFirstType = event.currentDataType.type == getType( transfers[ 0 ] ).type;
        event.currentDataType = getType( transfers[ isFirstType ? 1 : 0 ] );
      }
      @Override
      public void drop( DropTargetEvent event ) {
        events.add( event );
      }
    } );
    addLogger( dragSource );
    addSetDragDataListener( dragSource, "data" );


    fakeDropTargetEvent( "dropAccept", 1 );
    Fixture.readDataAndProcessAction( display );

    DragSourceEvent dragSetDataEvent = getDragSourceEvent( 0 );
    DropTargetEvent dropEvent = getDropTargetEvent( 1 );
    assertTrue( dragSetDataEvent.dataType.type != originalType[ 0 ].type );
    assertTrue( dragSetDataEvent.dataType.type == dropEvent.currentDataType.type );
  }

  @Test
  public void testChangeDataTypeInvalidOnDrop() {
    createDragSource( DND.DROP_MOVE );
    createDropTarget( DND.DROP_MOVE );
    dropTarget.addDropListener( new DropTargetAdapter() {
      @Override
      public void dropAccept( DropTargetEvent event ) {
        RTFTransfer rtfTransfer = RTFTransfer.getInstance();
        event.currentDataType = rtfTransfer.getSupportedTypes()[ 0 ];
      }
    } );
    addLogger( dragSource );
    addLogger( dropTarget );

    fakeDropTargetEvent( "dropAccept", 1 );
    Fixture.readDataAndProcessAction( display );

    int[] expected = new int[]{ DND.DragLeave, DND.DropAccept };
    assertTrue( Arrays.equals( expected, getEventOrder() ) );
  }

  @Test
  public void testNoDropAfterDropAcceptEvent() {
    createDragSource( DND.DROP_MOVE );
    createDropTarget( DND.DROP_MOVE );
    dropTarget.addDropListener( new DropTargetAdapter() {
      @Override
      public void dropAccept( DropTargetEvent event ) {
        events.add( event );
        eventTypes.add( Integer.valueOf( DND.DropAccept ) );
        event.detail = DND.DROP_NONE;
      }
      @Override
      public void drop( DropTargetEvent event ) {
        events.add( event );
        eventTypes.add( Integer.valueOf( DND.Drop ) );
      }
    } );
    addLogger( dragSource );

    fakeDropTargetEvent( "dropAccept", 1 );
    fakeDragSourceEvent( "dragFinished", 2 );
    Fixture.readDataAndProcessAction( display );

    int[] expected = new int[]{ DND.DropAccept, DND.DragEnd };
    assertTrue( Arrays.equals( expected, getEventOrder() ) );
    assertTrue( getDragSourceEvent( 1 ).doit ); // Actual SWT behavior
  }

  @Test
  public void testDropOverNonTarget() {
    createDragSource( DND.DROP_MOVE );
    createDropTarget( DND.DROP_MOVE );
    addLogger( dragSource );
    addLogger( dropTarget );

    fakeDragSourceEvent( "dragFinished", 1 );
    Fixture.readDataAndProcessAction( display );

    assertEquals( 1, events.size() );
    DragSourceEvent event = getDragSourceEvent( 0 );
    assertEquals( DND.DragEnd, getEventType( 0 ) );
    assertTrue( event.doit ); // Actual SWT behavior
  }

  @Test
  public void testChangeDetailInDropAccept() {
    createDragSource( DND.DROP_MOVE | DND.DROP_COPY );
    createDropTarget( DND.DROP_MOVE | DND.DROP_COPY );
    addLogger( dragSource );
    addSetDragDataListener( dragSource, "text Data" );
    dropTarget.addDropListener( new DropTargetAdapter() {
      @Override
      public void dropAccept( DropTargetEvent event ) {
        events.add( event );
        event.detail = DND.DROP_COPY;
      }
      @Override
      public void drop( DropTargetEvent event ) {
        events.add( event );
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

  @Test
  public void testChangeDetailInvalidInDropAccept() {
    createDragSource( DND.DROP_MOVE | DND.DROP_COPY );
    createDropTarget( DND.DROP_MOVE );
    addLogger( dragSource );
    dropTarget.addDropListener( new DropTargetAdapter() {
      @Override
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

  @Test
  public void testDragSetDataDoitIsFalse() {
    createDragSource( DND.DROP_MOVE );
    createDropTarget( DND.DROP_MOVE );
    addLogger( dragSource );
    addLogger( dropTarget );
    dragSource.addDragListener( new DragSourceAdapter() {
      @Override
      public void dragSetData( DragSourceEvent event ) {
        event.data = "TestData";
        event.doit = false;
      }
    } );

    fakeDropTargetEvent( "dropAccept", 1 );
    fakeDragSourceEvent( "dragFinished", 2 );
    Fixture.readDataAndProcessAction( display );

    int[] expected = new int[]{
      DND.DragLeave,
      DND.DropAccept,
      DND.DragSetData,
      DND.Drop,
      DND.DragEnd
    };
    assertTrue( Arrays.equals( expected, getEventOrder() ) );
    // NOTE: This is not the behavior documented for SWT, but how SWT behaves in Windows (SWT bug?)
    assertNull( getDropTargetEvent( 3 ).data );
    assertTrue( getDragSourceEvent( 4 ).doit );
  }

  @Test
  public void testDragSetDataDataType() {
    createDragSource( DND.DROP_MOVE );
    createDropTarget( DND.DROP_MOVE );
    addSetDragDataListener( dragSource, "string" );
    addLogger( dropTarget );
    addLogger( dragSource );

    fakeDropTargetEvent( "dropAccept", 0 );
    Fixture.readDataAndProcessAction( display );

    DragSourceEvent setDataEvent = getDragSourceEvent( 2 );
    DropTargetEvent dropEvent = getDropTargetEvent( 3 );
    assertNotNull( setDataEvent.dataType );
    assertEquals( setDataEvent.data, dropEvent.data );
    assertTrue( TransferData.sameType( setDataEvent.dataType, dropEvent.currentDataType ) );
  }

  @Test
  public void testResponseNoDetailChange() {
    createDragSource( DND.DROP_MOVE | DND.DROP_LINK | DND.DROP_COPY );
    createDropTarget( DND.DROP_MOVE | DND.DROP_LINK | DND.DROP_COPY );

    createDropTargetEvent( "dragEnter", 10, 10, "copy", getTextType(), 1 );
    createDropTargetEvent( "dragOver", 10, 10, "copy", getTextType(), 2 );
    Fixture.executeLifeCycleFromServerThread();

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( dragSource, "changeDetail" ) );
  }

  @Test
  public void testResponseDetailChangedOnEnter() {
    createDragSource( DND.DROP_MOVE | DND.DROP_LINK | DND.DROP_COPY );
    createDropTarget( DND.DROP_MOVE | DND.DROP_LINK | DND.DROP_COPY );
    dropTarget.addDropListener( new DropTargetAdapter() {
      @Override
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

  @Test
  public void testResponseDetailChangedOnOver() {
    createDragSource( DND.DROP_MOVE | DND.DROP_LINK | DND.DROP_COPY );
    createDropTarget( DND.DROP_MOVE | DND.DROP_LINK | DND.DROP_COPY );
    dropTarget.addDropListener( new DropTargetAdapter() {
      @Override
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

  @Test
  public void testDropAcceptWithDetailChangedOnEnter() {
    createDragSource( DND.DROP_MOVE | DND.DROP_LINK | DND.DROP_COPY );
    createDropTarget( DND.DROP_MOVE | DND.DROP_LINK | DND.DROP_COPY );
    addSetDragDataListener( dragSource, "some data" );
    dropTarget.addDropListener( new DropTargetAdapter() {
      @Override
      public void dragEnter( DropTargetEvent event ) {
        event.detail = DND.DROP_COPY;
      }
    } );
    addLogger( dropTarget );

    Fixture.executeLifeCycleFromServerThread(); // clear pending message operations
    Fixture.fakeNewRequest();
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

  @Test
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

  @Test
  public void testResponseFeedbackChangedOnEnter() throws JSONException {
    createDragSource( DND.DROP_MOVE | DND.DROP_LINK | DND.DROP_COPY );
    createDropTarget( DND.DROP_MOVE | DND.DROP_LINK | DND.DROP_COPY );
    dropTarget.addDropListener( new DropTargetAdapter() {
      @Override
      public void dragEnter( DropTargetEvent event ) {
        event.feedback = DND.FEEDBACK_SELECT;
      }
    } );
    Fixture.executeLifeCycleFromServerThread(); // clear message
    Fixture.fakeNewRequest();

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

  @Test
  public void testResponseFeedbackChangedOnOver() throws JSONException {
    createDragSource( DND.DROP_MOVE | DND.DROP_LINK | DND.DROP_COPY );
    createDropTarget( DND.DROP_MOVE | DND.DROP_LINK | DND.DROP_COPY );
    dropTarget.addDropListener( new DropTargetAdapter() {
      @Override
      public void dragOver( DropTargetEvent event ) {
        event.feedback = DND.FEEDBACK_EXPAND | DND.FEEDBACK_SCROLL;
      }
    } );
    Fixture.executeLifeCycleFromServerThread(); // clear message
    Fixture.fakeNewRequest();

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

  @Test
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

    Fixture.fakeNewRequest();
    fakeDropTargetEvent( "dragEnter", 1 );
    Fixture.executeLifeCycleFromServerThread();

    Message message = Fixture.getProtocolMessage();
    CallOperation call = message.findCallOperation( dragSource, "changeDataType" );
    assertEquals( getId( targetControl ), call.getProperty( "control" ) );
  }

  @Test
  public void testResponseChangeDataTypeOnOver() {
    createDragSource( DND.DROP_MOVE | DND.DROP_LINK | DND.DROP_COPY );
    createDropTarget( DND.DROP_MOVE | DND.DROP_LINK | DND.DROP_COPY );
    addLogger( dropTarget );
    dropTarget.addDropListener( new DropTargetAdapter() {
      @Override
      public void dragOver( DropTargetEvent event ) {
        event.currentDataType = TextTransfer.getInstance().getSupportedTypes()[ 0 ];
      }
    } );
    Fixture.executeLifeCycleFromServerThread();
    Fixture.fakeNewRequest();

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

  @Test
  public void testResponseChangeDataTypeOnEnter() {
    int operations = DND.DROP_MOVE | DND.DROP_LINK | DND.DROP_COPY;
    createDragSource( operations );
    createDropTarget( operations );
    addLogger( dropTarget );
    dropTarget.addDropListener( new DropTargetAdapter() {
      @Override
      public void dragEnter( DropTargetEvent event ) {
        event.currentDataType = TextTransfer.getInstance().getSupportedTypes()[ 0 ];
      }
    } );
    Fixture.executeLifeCycleFromServerThread();
    Fixture.fakeNewRequest();

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

  @Test
  public void testResponseChangeDataTypeInvalid() {
    // NOTE : Setting an invalid value on currentDataType reverts the field
    //        back to the next-best valid value. This is NOT SWT-like behavior!
    //        SWT would set null and display the DROP_NONE cursor.
    createDragSource( DND.DROP_MOVE | DND.DROP_LINK | DND.DROP_COPY );
    createDropTarget( DND.DROP_MOVE | DND.DROP_LINK | DND.DROP_COPY );
    dragSource.setTransfer( new Transfer[] { TextTransfer.getInstance() } );
    dropTarget.setTransfer( new Transfer[] { TextTransfer.getInstance() } );
    dropTarget.addDropListener( new DropTargetAdapter() {
      @Override
      public void dragOver( DropTargetEvent event ) {
        event.currentDataType = RTFTransfer.getInstance().getSupportedTypes()[ 0 ];
      }
    } );
    Fixture.executeLifeCycleFromServerThread();
    Fixture.fakeNewRequest();

    fakeDropTargetEvent( "dragOver", 1 );
    Fixture.executeLifeCycleFromServerThread();

    Message message = Fixture.getProtocolMessage();
    CallOperation call = message.findCallOperation( dragSource, "changeDataType" );
    assertEquals( getId( targetControl ), call.getProperty( "control" ) );
    Integer expectedType = new Integer( getTextType() );
    assertEquals( expectedType, call.getProperty( "dataType" ) );

  }

  @Test
  public void testOperationChangedEvent() {
    createDragSource( DND.DROP_MOVE | DND.DROP_LINK | DND.DROP_COPY );
    createDropTarget( DND.DROP_MOVE | DND.DROP_LINK | DND.DROP_COPY );
    addLogger( dropTarget );

    fakeDropTargetEvent( "dragEnter", 2 );
    createDropTargetEvent( "dragOperationChanged", 0, 0, "copy", getTextType(), 3 );
    fakeDropTargetEvent( "dragOver", 5 );
    Fixture.executeLifeCycleFromServerThread();

    DropTargetEvent dragOperationChanged = getDropTargetEvent( 1 );
    assertEquals( DND.DragOperationChanged, getEventType( 1 ) );
    assertTrue( ( dragOperationChanged.detail & DND.DROP_COPY ) != 0 );
    assertEquals( DND.DragOver, getEventType( 2 ) );
  }

  @Test
  public void testOperationsField() {
    createDragSource( DND.DROP_MOVE | DND.DROP_LINK );
    createDropTarget( DND.DROP_MOVE | DND.DROP_LINK );
    dragSource.addDragListener( new DragSourceAdapter(){
      @Override
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

    assertEquals( 6, events.size() );
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
    Map<String, Object> parameters = new HashMap<String, Object>();
    parameters.put( "x", String.valueOf( x ) );
    parameters.put( "y", String.valueOf( y ) );
    parameters.put( "time", String.valueOf( time ) );
    Fixture.fakeNotifyOperation( getId( sourceControl ), eventType, parameters );
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
    Map<String, Object> parameters = new HashMap<String, Object>();
    parameters.put( "x", String.valueOf( x ) );
    parameters.put( "y", String.valueOf( y ) );
    parameters.put( "time", String.valueOf( time ) );
    parameters.put( "operation", operation );
    parameters.put( "feedback", String.valueOf( 0 ) );
    parameters.put( "source", getId( sourceControl ) );
    parameters.put( "dataType", String.valueOf( dataType ) );
    Fixture.fakeNotifyOperation( getId( targetControl ), eventType, parameters );

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

  private int getTextType() {
    return getType( TextTransfer.getInstance()).type;
  }

  private void addLogger( DragSource dragSource ) {
    dragSource.addDragListener( new DragSourceListener() {
      public void dragStart( DragSourceEvent event ) {
        events.add( event );
        eventTypes.add( Integer.valueOf( DND.DragStart ) );
      }
      public void dragSetData( DragSourceEvent event ) {
        events.add( event );
        eventTypes.add( Integer.valueOf( DND.DragSetData ) );
      }
      public void dragFinished( DragSourceEvent event ) {
        events.add( event );
        eventTypes.add( Integer.valueOf( DND.DragEnd ) );
      }
    } );
  }

  private void addLogger( DropTarget dropTarget ) {
    dropTarget.addDropListener( new LoggingDropTargetListener() );
  }

  private int[] getEventOrder() {
    int[] result = new int[ eventTypes.size() ];
    for( int i = 0; i < result.length; i++ ) {
      result[ i ] = eventTypes.get( i ).intValue();
    }
    return result;
  }

  private void addSetDragDataListener( DragSource dragSource, final Object data ) {
    dragSource.addDragListener( new DragSourceAdapter() {
      @Override
      public void dragSetData( DragSourceEvent event ) {
        event.data = data;
      }
    } );
  }

  private int getEventType( int index ) {
    return eventTypes.get( index ).intValue();
  }

  private DropTargetEvent getDropTargetEvent( int index ) {
    return ( DropTargetEvent )events.get( index );
  }

  private DragSourceEvent getDragSourceEvent( int index ) {
    return ( DragSourceEvent )events.get( index );
  }

  private class LoggingDropTargetListener implements DropTargetListener {
    public void dragEnter( DropTargetEvent event ) {
      events.add( event );
      eventTypes.add( Integer.valueOf( DND.DragEnter ) );
    }
    public void dragLeave( DropTargetEvent event ) {
      events.add( event );
      eventTypes.add( Integer.valueOf( DND.DragLeave ) );
    }
    public void dragOperationChanged( DropTargetEvent event ) {
      events.add( event );
      eventTypes.add( Integer.valueOf( DND.DragOperationChanged ) );
    }
    public void dragOver( DropTargetEvent event ) {
      events.add( event );
      eventTypes.add( Integer.valueOf( DND.DragOver ) );
    }
    public void drop( DropTargetEvent event ) {
      events.add( event );
      eventTypes.add( Integer.valueOf( DND.Drop ) );
    }
    public void dropAccept( DropTargetEvent event ) {
      events.add( event );
      eventTypes.add( Integer.valueOf( DND.DropAccept ) );
    }
  }

}
