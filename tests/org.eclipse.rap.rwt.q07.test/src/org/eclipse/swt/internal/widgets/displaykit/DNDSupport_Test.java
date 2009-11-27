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
import java.util.Date;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.internal.lifecycle.*;
import org.eclipse.rwt.internal.service.RequestParams;
import org.eclipse.rwt.lifecycle.PhaseId;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.*;
import org.eclipse.swt.dnd.*;
import org.eclipse.swt.events.DragDetectEvent;
import org.eclipse.swt.events.DragDetectListener;
import org.eclipse.swt.widgets.*;


public class DNDSupport_Test extends TestCase {

  protected void setUp() throws Exception {
    RWTFixture.setUp();
    PhaseListenerRegistry.add( new PreserveWidgetsPhaseListener() );
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }

  public void testRegisterAndDisposeDragSource() {
    Display display = new Display();
    Shell shell = new Shell( display );
    String displayId = DisplayUtil.getId( display );
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Control dragSourceControl = new Label( shell, SWT.NONE );
    DragSource dragSource = new DragSource( dragSourceControl, DND.DROP_MOVE );
    Transfer[] types = new Transfer[] { TextTransfer.getInstance() };
    dragSource.setTransfer( types );
    String dndSupport = "org.eclipse.rwt.DNDSupport.getInstance()";
    String register
      = dndSupport
      + ".registerDragSource( w, [null, \"move\",null ]";
    String transferType
      = dndSupport
      + ".setDragSourceTransferTypes( w, [ \"org.eclipse.swt.dnd.TextTransfer";
    RWTFixture.executeLifeCycleFromServerThread();
    String markup = Fixture.getAllMarkup();
    assertTrue( markup.indexOf( register ) != -1 );
    assertTrue( markup.indexOf( transferType ) != -1 );
  }

  public void testRegisterAndDisposeDropTarget() {
    Display display = new Display();
    Shell shell = new Shell( display );
    String displayId = DisplayUtil.getId( display );
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Control dropTargetControl = new Label( shell, SWT.NONE );
    DropTarget dropTarget = new DropTarget( dropTargetControl, DND.DROP_COPY );
    Transfer[] types = new Transfer[] { TextTransfer.getInstance() };
    dropTarget.setTransfer( types );
    String dndSupport = "org.eclipse.rwt.DNDSupport.getInstance()";
    String register
      = dndSupport + ".registerDropTarget( w, [ \"copy\",null,null ]";
    String transferType
      = dndSupport
      + ".setDropTargetTransferTypes( w, [ \"org.eclipse.swt.dnd.TextTransfer";
    RWTFixture.executeLifeCycleFromServerThread();
    String markup = Fixture.getAllMarkup();
    assertTrue( markup.indexOf( register ) != -1 );
    assertTrue( markup.indexOf( transferType ) != -1 );
  }

  public void testCancelAfterDragDetectAndStartEvent() {
    final java.util.List log = new ArrayList();
    Display display = new Display();
    Shell shell = new Shell( display );
    Control dragSourceControl = new Label( shell, SWT.NONE );
    DragSource dragSource = new DragSource( dragSourceControl, DND.DROP_MOVE );
    Control dropTargetControl = new Label( shell, SWT.NONE );
    new DropTarget( dropTargetControl, DND.DROP_MOVE );
    dragSource.addDragListener( new DragSourceAdapter() {
      public void dragStart( final DragSourceEvent event ) {
        log.add( event );
        event.doit = false;
      }
    } );
    dragSourceControl.addDragDetectListener(  new DragDetectListener() {
      public void dragDetected( final DragDetectEvent event ) {
        log.add(  event );
      }
    } );
    shell.open();
    String displayId = DisplayUtil.getId( display );
    // Simulate request that sends a drop event
    Fixture.fakeResponseWriter();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    createDragSourceEvent( dragSourceControl, "dragStart", 1 );
    // run life cycle
    RWTFixture.executeLifeCycleFromServerThread();
    assertEquals( 2, log.size() );
    DragDetectEvent dragDetect = ( DragDetectEvent )log.get( 0 );
    assertEquals( DragDetectEvent.DRAG_DETECT, dragDetect.getID() );
    // TODO [tb] : test mapping of mouse coordinates
    assertSame( dragSourceControl, dragDetect.widget );
    DragSourceEvent dragStart = ( DragSourceEvent )log.get( 1 );
    assertEquals( DragSourceEvent.DRAG_START, dragStart.getID() );
    assertSame( dragSource, dragStart.widget );
    String markup = Fixture.getAllMarkup();
    String expected = "org.eclipse.rwt.DNDSupport.getInstance().cancel";
    assertTrue( markup.indexOf( expected ) != -1 );
  }

  public void testLeaveBeforeEnter() {
    final java.util.List log = new ArrayList();
    Transfer[] types = new Transfer[] { TextTransfer.getInstance() };
    Display display = new Display();
    Shell shell = new Shell( display );
    Control dragSourceCont = new Label( shell, SWT.NONE );
    DragSource dragSource = new DragSource( dragSourceCont, DND.DROP_MOVE );
    dragSource.setTransfer( types );
    Control dropTargetCont = new Label( shell, SWT.NONE );
    DropTarget dropTarget = new DropTarget( dropTargetCont, DND.DROP_MOVE );
    dropTarget.setTransfer( types );
    dropTarget.addDropListener( new DropTargetAdapter() {
      public void dragEnter( final DropTargetEvent event ) {
        log.add( event );
      }
      public void dragOver( final DropTargetEvent event ) {
        log.add( event );
      }
      public void dragLeave( final DropTargetEvent event ) {
        log.add( event );
      }
    } );
    shell.open();
    String displayId = DisplayUtil.getId( display );
    // Simulate request that sends a drop event
    Fixture.fakeResponseWriter();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    createDropTargetEvent( dropTargetCont, dragSourceCont, "dragEnter", 1 );
    RWTFixture.executeLifeCycleFromServerThread();
    log.clear();
    createDropTargetEvent( dropTargetCont, dragSourceCont, "dragLeave", 2 );
    createDropTargetEvent( dropTargetCont, dragSourceCont, "dragEnter", 3 );
    createDropTargetEvent( dropTargetCont, dragSourceCont, "dragOver", 4 );
    // run life cycle
    RWTFixture.executeLifeCycleFromServerThread();
    assertEquals( 3, log.size() );
    DropTargetEvent dragLeave = ( DropTargetEvent )log.get( 0 );
    assertEquals( DropTargetEvent.DRAG_LEAVE, dragLeave.getID() );
    assertSame( dropTarget, dragLeave.widget );
    DropTargetEvent dragEnter = ( DropTargetEvent )log.get( 1 );
    assertEquals( DropTargetEvent.DRAG_ENTER, dragEnter.getID() );
    assertSame( dropTarget, dragEnter.widget );
    DropTargetEvent dragOver = ( DropTargetEvent )log.get( 2 );
    assertEquals( DropTargetEvent.DRAG_OVER, dragOver.getID() );
    assertSame( dropTarget, dragOver.widget );
  }

  public void testDataTransferOnDrop() {
    final java.util.List log = new ArrayList();
    Display display = new Display();
    Shell shell = new Shell( display );
    Control dragSourceControl = new Label( shell, SWT.NONE );
    DragSource dragSource = new DragSource( dragSourceControl, DND.DROP_MOVE );
    dragSource.setTransfer( new Transfer[] { HTMLTransfer.getInstance() } );
    Control dropTargetControl = new Label( shell, SWT.NONE );
    DropTarget dropTarget = new DropTarget( dropTargetControl, DND.DROP_MOVE );
    dropTarget.setTransfer( new Transfer[] { HTMLTransfer.getInstance() } );
    dropTarget.addDropListener( new DropTargetAdapter() {
      public void dropAccept( final DropTargetEvent event ) {
        log.add( event );
      }
      public void drop( final DropTargetEvent event ) {
        log.add( event );
      }
    } );
    dragSource.addDragListener( new DragSourceAdapter(){
      public void dragSetData( final DragSourceEvent event ) {
        log.add( event );
        event.data = "Hello World!";
      }
    } );
    shell.open();
    String displayId = DisplayUtil.getId( display );
    // Simulate request that sends a drop event
    Fixture.fakeResponseWriter();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    createDropTargetEvent( dropTargetControl,
                           dragSourceControl,
                           "dropAccept",
                           1,
                           2,
                           "move",
                           1 );
    // run life cycle
    RWTFixture.executeLifeCycleFromServerThread();
    assertEquals( 3, log.size() );
    // dropAccept expected
    DropTargetEvent dropAcceptEvent = ( DropTargetEvent )log.get( 0 );
    assertEquals( DropTargetEvent.DROP_ACCEPT, dropAcceptEvent.getID() );
    assertSame( dropTarget, dropAcceptEvent.widget );
    assertEquals( 1, dropAcceptEvent.x );
    assertEquals( 2, dropAcceptEvent.y );
    assertNull( dropAcceptEvent.data );
    // dragSetData expected
    DragSourceEvent dragSetDataEvent = ( DragSourceEvent )log.get( 1 );
    assertEquals( DragSourceEvent.DRAG_SET_DATA, dragSetDataEvent.getID() );
    assertSame( dragSource, dragSetDataEvent.widget );
    assertEquals( 1, dragSetDataEvent.x );
    assertEquals( 2, dragSetDataEvent.y );
    TransferData dataType = dragSetDataEvent.dataType;
    assertTrue( HTMLTransfer.getInstance().isSupportedType( dataType ) );
    // drop expected
    DropTargetEvent dropEvent = ( DropTargetEvent )log.get( 2 );
    assertEquals( DropTargetEvent.DROP, dropEvent.getID() );
    assertSame( dropTarget, dropEvent.widget );
    assertEquals( dragSetDataEvent.dataType, dropEvent.currentDataType );
    assertEquals( 1, dropEvent.x );
    assertEquals( 2, dropEvent.y );
    assertEquals( "Hello World!", dropEvent.data );
  }

  public void testInvalidDataOnDragSetData() {
    final java.util.List log = new ArrayList();
    Display display = new Display();
    Shell shell = new Shell( display );
    Control dragSourceControl = new Label( shell, SWT.NONE );
    DragSource dragSource = new DragSource( dragSourceControl, DND.DROP_MOVE );
    dragSource.setTransfer( new Transfer[] { HTMLTransfer.getInstance() } );
    Control dropTargetControl = new Label( shell, SWT.NONE );
    DropTarget dropTarget = new DropTarget( dropTargetControl, DND.DROP_MOVE );
    dropTarget.setTransfer( new Transfer[] { HTMLTransfer.getInstance() } );
    dropTarget.addDropListener( new DropTargetAdapter() {
      public void dropAccept( final DropTargetEvent event ) {
        log.add( event );
      }
      public void drop( final DropTargetEvent event ) {
        log.add( event );
      }
    } );
    dragSource.addDragListener( new DragSourceAdapter(){
      public void dragSetData( final DragSourceEvent event ) {
        log.add( event );
        event.data = new Date();
      }
    } );
    shell.open();
    String displayId = DisplayUtil.getId( display );
    // Simulate request that sends a drop event
    Fixture.fakeResponseWriter();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    createDropTargetEvent( dropTargetControl,
                           dragSourceControl,
                           "dropAccept",
                           1,
                           2,
                           "move",
                           1 );
    // run life cycle
    try {
      RWTFixture.executeLifeCycleFromServerThread();
    } catch( SWTException e ) {
      log.add( e );
    }
    assertEquals( 3, log.size() );
    // dropAccept expected
    DropTargetEvent dropAcceptEvent = ( DropTargetEvent )log.get( 0 );
    assertEquals( DropTargetEvent.DROP_ACCEPT, dropAcceptEvent.getID() );
    // dragSetData expected
    DragSourceEvent dragSetDataEvent = ( DragSourceEvent )log.get( 1 );
    assertEquals( DragSourceEvent.DRAG_SET_DATA, dragSetDataEvent.getID() );
    // Exception expected
    SWTException exception = ( SWTException )log.get( 2 );
    assertEquals( DND.ERROR_INVALID_DATA, exception.code );
  }

  public void testChangeDataTypeOnDrop() {
    final java.util.List log = new ArrayList();
    final TransferData[] originalDataType = new TransferData[ 1 ];
    Display display = new Display();
    Shell shell = new Shell( display );
    Transfer[] transfer = new Transfer[]{
      HTMLTransfer.getInstance(),
      TextTransfer.getInstance()
    };
    Control dragSourceControl = new Label( shell, SWT.NONE );
    DragSource dragSource = new DragSource( dragSourceControl, DND.DROP_MOVE );
    dragSource.setTransfer( transfer );
    Control dropTargetControl = new Label( shell, SWT.NONE );
    DropTarget dropTarget = new DropTarget( dropTargetControl, DND.DROP_MOVE );
    dropTarget.setTransfer( transfer );
    dropTarget.addDropListener( new DropTargetAdapter() {
      public void dropAccept( final DropTargetEvent event ) {
        originalDataType[ 0 ] = event.currentDataType;
        boolean isHTMLType =
          HTMLTransfer.getInstance().isSupportedType( event.currentDataType );
        TransferData newTransferData;
        if( isHTMLType ) {
          newTransferData = TextTransfer.getInstance().getSupportedTypes()[ 0 ];
        } else {
          newTransferData = HTMLTransfer.getInstance().getSupportedTypes()[ 0 ];
        }
        event.currentDataType = newTransferData;
        log.add( event );
      }
      public void drop( final DropTargetEvent event ) {
        log.add( event );
      }
    } );
    dragSource.addDragListener( new DragSourceAdapter(){
      public void dragSetData( final DragSourceEvent event ) {
        event.data = "data";
        log.add( event );
      }
    } );
    shell.open();
    String displayId = DisplayUtil.getId( display );
    // Simulate request that sends a drop event
    Fixture.fakeResponseWriter();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    createDropTargetEvent( dropTargetControl,
                           dragSourceControl,
                           "dropAccept",
                           1,
                           2,
                           "move",
                           1 );
    // run life cycle
    RWTFixture.executeLifeCycleFromServerThread();
    assertEquals( 3, log.size() );
    // dropAccept expected
    DropTargetEvent dropAcceptEvent = ( DropTargetEvent )log.get( 0 );
    assertEquals( DropTargetEvent.DROP_ACCEPT, dropAcceptEvent.getID() );
    // dragSetData expected
    DragSourceEvent dragSetDataEvent = ( DragSourceEvent )log.get( 1 );
    assertEquals( DragSourceEvent.DRAG_SET_DATA, dragSetDataEvent.getID() );
    TransferData dataType = dragSetDataEvent.dataType;
    assertTrue( dataType != originalDataType[ 0 ] );
    // drop expected
    DropTargetEvent dropEvent = ( DropTargetEvent )log.get( 2 );
    assertEquals( DropTargetEvent.DROP, dropEvent.getID() );
    assertTrue( dragSetDataEvent.dataType == dropEvent.currentDataType );
  }

  public void testChangeDataTypeInvalidOnDrop() {
    final java.util.List log = new ArrayList();
    Display display = new Display();
    Shell shell = new Shell( display );
    Transfer[] transfer = new Transfer[]{
      HTMLTransfer.getInstance(),
      TextTransfer.getInstance()
    };
    Control dragSourceControl = new Label( shell, SWT.NONE );
    DragSource dragSource = new DragSource( dragSourceControl, DND.DROP_MOVE );
    dragSource.setTransfer( transfer );
    Control dropTargetControl = new Label( shell, SWT.NONE );
    DropTarget dropTarget = new DropTarget( dropTargetControl, DND.DROP_MOVE );
    dropTarget.setTransfer( transfer );
    dropTarget.addDropListener( new DropTargetAdapter() {
      public void dropAccept( final DropTargetEvent event ) {
        event.currentDataType
          = RTFTransfer.getInstance().getSupportedTypes()[ 0 ];
        log.add( event );
      }
      public void drop( final DropTargetEvent event ) {
        log.add( event );
      }
    } );
    dragSource.addDragListener( new DragSourceAdapter(){
      public void dragSetData( final DragSourceEvent event ) {
        log.add( event );
      }
    } );
    shell.open();
    String displayId = DisplayUtil.getId( display );
    // Simulate request that sends a drop event
    Fixture.fakeResponseWriter();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    createDropTargetEvent( dropTargetControl,
                           dragSourceControl,
                           "dropAccept",
                           1,
                           2,
                           "move",
                           1 );
    // run life cycle
    RWTFixture.executeLifeCycleFromServerThread();
    // Invalid TransferData => no dropAccept
    assertEquals( 1, log.size() );
    DropTargetEvent dropAcceptEvent = ( DropTargetEvent )log.get( 0 );
    assertEquals( DropTargetEvent.DROP_ACCEPT, dropAcceptEvent.getID() );
  }

  public void testNoDropAfterDropAcceptEvent() {
    final java.util.List log = new ArrayList();
    Display display = new Display();
    Shell shell = new Shell( display );
    Control dragSourceCont = new Label( shell, SWT.NONE );
    DragSource dragSource = new DragSource( dragSourceCont, DND.DROP_MOVE );
    dragSource.setTransfer( new Transfer[] { HTMLTransfer.getInstance() } );
    Control dropTargetCont = new Label( shell, SWT.NONE );
    DropTarget dropTarget = new DropTarget( dropTargetCont, DND.DROP_MOVE );
    dropTarget.setTransfer( new Transfer[] { HTMLTransfer.getInstance() } );
    dropTarget.addDropListener( new DropTargetAdapter() {
      public void dropAccept( final DropTargetEvent event ) {
        log.add( event );
        // prevent drop event
        event.detail = DND.DROP_NONE;
      }
      public void drop( final DropTargetEvent event ) {
        log.add( event );
      }
    } );
    dragSource.addDragListener( new DragSourceListener() {
      public void dragStart( final DragSourceEvent event ) {
        log.add( event );
      }
      public void dragSetData( final DragSourceEvent event ) {
        log.add( event );
      }
      public void dragFinished( final DragSourceEvent event ) {
        log.add( event );
      }
    } );
    shell.open();
    String displayId = DisplayUtil.getId( display );
    // Simulate request that sends a drop event
    Fixture.fakeResponseWriter();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    createDropTargetEvent( dropTargetCont, dragSourceCont, "dropAccept", 1 );
    createDragSourceEvent( dragSourceCont, "dragFinished", 2 );
    // run life cycle
    RWTFixture.executeLifeCycleFromServerThread();
    assertEquals( 2, log.size() );
    DropTargetEvent dropAcceptEvent = ( DropTargetEvent )log.get( 0 );
    assertEquals( DropTargetEvent.DROP_ACCEPT, dropAcceptEvent.getID() );
    DragSourceEvent event = ( DragSourceEvent )log.get( 1 );
    assertEquals( DragSourceEvent.DRAG_END, event.getID() );
    assertSame( dragSource, event.widget );
    assertTrue( event.doit ); // Actual SWT behavior

  }

  public void testDropOverNonTarget() {
    final java.util.List log = new ArrayList();
    Display display = new Display();
    Shell shell = new Shell( display );
    Control dragSourceControl = new Label( shell, SWT.NONE );
    DragSource dragSource = new DragSource( dragSourceControl, DND.DROP_MOVE );
    dragSource.addDragListener( new DragSourceAdapter() {
      public void dragFinished( final DragSourceEvent event ) {
        log.add( event );
      }
    } );
    Control dropTargetControl = new Label( shell, SWT.NONE );
    DropTarget dropTarget = new DropTarget( dropTargetControl, DND.DROP_MOVE );
    dropTarget.addDropListener( new DropTargetAdapter() {
      public void dropAccept( final DropTargetEvent event ) {
        log.add( event );
      }
      public void drop( final DropTargetEvent event ) {
        log.add( event );
      }
    } );
    shell.open();
    String displayId = DisplayUtil.getId( display );
    // Simulate request that sends a drop event 'somewhere', but outside a valid
    // drop target
    Fixture.fakeResponseWriter();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    createDragSourceEvent( dragSourceControl, "dragFinished", 1 );
    // run life cycle
    RWTFixture.executeLifeCycleFromServerThread();
    assertEquals( 1, log.size() );
    assertTrue( log.get( 0 ) instanceof DragSourceEvent );
    DragSourceEvent event = ( DragSourceEvent )log.get( 0 );
    assertEquals( DragSourceEvent.DRAG_END, event.getID() );
    assertSame( dragSource, event.widget );
    assertTrue( event.doit ); // Actual SWT behavior
  }

  public void testDropOverTarget() {
    final java.util.List log = new ArrayList();
    Display display = new Display();
    Shell shell = new Shell( display );
    Control dragSourceCont = new Label( shell, SWT.NONE );
    DragSource dragSource = new DragSource( dragSourceCont, DND.DROP_MOVE );
    dragSource.setTransfer( new Transfer[]{ TextTransfer.getInstance() } );
    dragSource.addDragListener( new DragSourceAdapter() {
      public void dragSetData( final DragSourceEvent event ) {
        event.data = "text";
        log.add( event );
      }
      public void dragFinished( final DragSourceEvent event ) {
        log.add( event );
      }
    } );
    Control dropTargetCont = new Label( shell, SWT.NONE );
    DropTarget dropTarget = new DropTarget( dropTargetCont, DND.DROP_MOVE );
    dropTarget.setTransfer( new Transfer[]{ TextTransfer.getInstance() } );
    dropTarget.addDropListener( new DropTargetAdapter() {
      public void dropAccept( final DropTargetEvent event ) {
        log.add( event );
      }
      public void drop( final DropTargetEvent event ) {
        log.add( event );
      }
      public void dragLeave( final DropTargetEvent event ) {
        log.add( event );
      }
    } );
    shell.open();
    String displayId = DisplayUtil.getId( display );
    // Simulate request that sends a drop event over a valid drop target
    Fixture.fakeResponseWriter();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    createDropTargetEvent( dropTargetCont, dragSourceCont, "dropAccept", 1 );
    createDragSourceEvent( dragSourceCont, "dragFinished", 2 );
    // run life cycle
    RWTFixture.executeLifeCycleFromServerThread();
    assertEquals( 5, log.size() );
    // 1. expect dragLeave event
    assertTrue( log.get( 0 ) instanceof DropTargetEvent );
    DropTargetEvent dropTargetEvent = ( DropTargetEvent )log.get( 0 );
    assertEquals( DropTargetEvent.DRAG_LEAVE, dropTargetEvent.getID() );
    // 2. expect dropAccept event
    assertTrue( log.get( 1 ) instanceof DropTargetEvent );
    dropTargetEvent = ( DropTargetEvent )log.get( 1 );
    assertEquals( DropTargetEvent.DROP_ACCEPT, dropTargetEvent.getID() );
    // 3. expect dragSetData event
    assertTrue( log.get( 2 ) instanceof DragSourceEvent );
    DragSourceEvent dragSourceEvent = ( DragSourceEvent )log.get( 2 );
    assertEquals( DragSourceEvent.DRAG_SET_DATA, dragSourceEvent.getID() );
    // 4. expect drop event
    assertTrue( log.get( 3 ) instanceof DropTargetEvent );
    dropTargetEvent = ( DropTargetEvent )log.get( 3 );
    assertEquals( DropTargetEvent.DROP, dropTargetEvent.getID() );
    // 5. expect dragFinished event
    assertTrue( log.get( 4 ) instanceof DragSourceEvent );
    dragSourceEvent = ( DragSourceEvent )log.get( 4 );
    assertEquals( DragSourceEvent.DRAG_END, dragSourceEvent.getID() );
    assertTrue( dragSourceEvent.doit );
  }

  public void testChangeDetailInDropAccept() {
    final java.util.List log = new ArrayList();
    Display display = new Display();
    Shell shell = new Shell( display );
    int operations = DND.DROP_MOVE | DND.DROP_COPY;
    Control dragSourceCont = new Label( shell, SWT.NONE );
    DragSource dragSource = new DragSource( dragSourceCont, operations );
    dragSource.setTransfer( new Transfer[]{ TextTransfer.getInstance() } );
    Control dropTargetCont = new Label( shell, SWT.NONE );
    DropTarget dropTarget = new DropTarget( dropTargetCont, operations );
    dropTarget.setTransfer( new Transfer[]{ TextTransfer.getInstance() } );
    dragSource.addDragListener( new DragSourceAdapter() {
      public void dragFinished( final DragSourceEvent event ) {
        log.add(  event );
      }
      public void dragSetData( final DragSourceEvent event ) {
        event.data = "text data";
        log.add(  event );
      }
    } );
    dropTarget.addDropListener( new DropTargetAdapter() {
      public void dropAccept( final DropTargetEvent event ) {
        log.add( event );
        event.detail = DND.DROP_COPY;
      }
      public void drop( final DropTargetEvent event ) {
        log.add( event );
      }
    } );
    shell.open();
    String displayId = DisplayUtil.getId( display );
    // Simulate request that sends a drop event over a valid drop target
    Fixture.fakeResponseWriter();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    createDropTargetEvent( dropTargetCont, dragSourceCont, "dropAccept", 1 );
    createDragSourceEvent( dragSourceCont, "dragFinished", 2 );
    // run life cycle
    RWTFixture.executeLifeCycleFromServerThread();
    assertEquals( 4, log.size() );
    // 1. expect dropAccept event
    DropTargetEvent dropTargetEvent = ( DropTargetEvent )log.get( 0 );
    assertEquals( DropTargetEvent.DROP_ACCEPT, dropTargetEvent.getID() );
    assertEquals( DND.DROP_COPY, dropTargetEvent.detail );
    // 2. expect dragSetData event
    DragSourceEvent dragSourceEvent = ( DragSourceEvent )log.get( 1 );
    assertEquals( DragSourceEvent.DRAG_SET_DATA, dragSourceEvent.getID() );
    assertEquals( DND.DROP_NONE, dragSourceEvent.detail );
    // 3. expect drop event
    dropTargetEvent = ( DropTargetEvent )log.get( 2 );
    assertEquals( DropTargetEvent.DROP, dropTargetEvent.getID() );
    assertEquals( DND.DROP_COPY, dropTargetEvent.detail );
    // 4. expect dragFinished event
    dragSourceEvent = ( DragSourceEvent )log.get( 3 );
    assertEquals( DragSourceEvent.DRAG_END, dragSourceEvent.getID() );
    assertTrue( dragSourceEvent.doit );
    assertEquals( DND.DROP_COPY, dragSourceEvent.detail );
  }

  public void testChangeDetailInvalidInDropAccept() {
    final java.util.List log = new ArrayList();
    Display display = new Display();
    Shell shell = new Shell( display );
    int operations = DND.DROP_MOVE | DND.DROP_COPY;
    Control dragSourceCont = new Label( shell, SWT.NONE );
    DragSource dragSource = new DragSource( dragSourceCont, operations );
    dragSource.setTransfer( new Transfer[] { HTMLTransfer.getInstance() } );
    Control dropTargetCont = new Label( shell, SWT.NONE );
    DropTarget dropTarget = new DropTarget( dropTargetCont, DND.DROP_MOVE );
    dropTarget.setTransfer( new Transfer[] { HTMLTransfer.getInstance() } );
    dragSource.addDragListener( new DragSourceAdapter() {
      public void dragFinished( final DragSourceEvent event ) {
        log.add(  event );
      }
      public void dragSetData( final DragSourceEvent event ) {
        log.add(  event );
      }
    } );
    dropTarget.addDropListener( new DropTargetAdapter() {
      public void dropAccept( final DropTargetEvent event ) {
        log.add( event );
        event.detail = DND.DROP_COPY;
      }
      public void drop( final DropTargetEvent event ) {
        log.add( event );
      }
    } );
    shell.open();
    String displayId = DisplayUtil.getId( display );
    // Simulate request that sends a drop event over a valid drop target
    Fixture.fakeResponseWriter();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    createDropTargetEvent( dropTargetCont, dragSourceCont, "dropAccept", 1 );
    createDragSourceEvent( dragSourceCont, "dragFinished", 2 );
    // run life cycle
    RWTFixture.executeLifeCycleFromServerThread();
    assertEquals( 2, log.size() );
    // 1. expect dropAccept event
    DropTargetEvent dropTargetEvent = ( DropTargetEvent )log.get( 0 );
    assertEquals( DropTargetEvent.DROP_ACCEPT, dropTargetEvent.getID() );
    assertEquals( DND.DROP_COPY, dropTargetEvent.detail );
    // 2. expect dragFinished event
    DragSourceEvent dragSourceEvent = ( DragSourceEvent )log.get( 1 );
    assertEquals( DragSourceEvent.DRAG_END, dragSourceEvent.getID() );
    assertTrue( dragSourceEvent.doit ); // This is still true in SWT/Win
    assertEquals( DND.DROP_NONE, dragSourceEvent.detail );
  }

  public void testDragSetDataDoitIsFalse() {
    final java.util.List log = new ArrayList();
    Display display = new Display();
    Shell shell = new Shell( display );
    Control dragSourceControl = new Label( shell, SWT.NONE );
    DragSource dragSource = new DragSource( dragSourceControl, DND.DROP_MOVE );
    dragSource.setTransfer( new Transfer[]{ TextTransfer.getInstance() } );
    dragSource.addDragListener( new DragSourceAdapter() {
      public void dragSetData( final DragSourceEvent event ) {
        log.add( event );
        event.data = "TestData";
        event.doit = false;
      }
      public void dragFinished( final DragSourceEvent event ) {
        log.add( event );
      }
    } );
    Control dropTargetControl = new Label( shell, SWT.NONE );
    DropTarget dropTarget = new DropTarget( dropTargetControl, DND.DROP_MOVE );
    dropTarget.setTransfer( new Transfer[]{ TextTransfer.getInstance() } );
    dropTarget.addDropListener( new DropTargetAdapter() {
      public void dropAccept( final DropTargetEvent event ) {
        log.add( event );
      }
      public void drop( final DropTargetEvent event ) {
        log.add( event );
      }
    } );
    shell.open();
    String displayId = DisplayUtil.getId( display );
    // Simulate request that sends a drop event over a valid drop target
    Fixture.fakeResponseWriter();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    createDropTargetEvent( dropTargetControl,
                           dragSourceControl,
                           "dropAccept",
                           1 );
    createDragSourceEvent( dragSourceControl, "dragFinished", 2 );
    // run life cycle
    RWTFixture.executeLifeCycleFromServerThread();
    assertEquals( 4, log.size() );
    // 1. expect dropAccept event
    assertTrue( log.get( 0 ) instanceof DropTargetEvent );
    DropTargetEvent dropTargetEvent = ( DropTargetEvent )log.get( 0 );
    assertEquals( DropTargetEvent.DROP_ACCEPT, dropTargetEvent.getID() );
    // 2. expect dragSetData event
    assertTrue( log.get( 1 ) instanceof DragSourceEvent );
    DragSourceEvent dragSourceEvent = ( DragSourceEvent )log.get( 1 );
    assertEquals( DragSourceEvent.DRAG_SET_DATA, dragSourceEvent.getID() );
    // NOTE: This is not the behavior documented for SWT,
    //       but how SWT behaves in Windows (bug?)
    // 3. expect drop event
    assertTrue( log.get( 2 ) instanceof DropTargetEvent );
    dropTargetEvent = ( DropTargetEvent )log.get( 2 );
    assertEquals( DropTargetEvent.DROP, dropTargetEvent.getID() );
    assertNull( dropTargetEvent.data );
    // 4. expect dragFinished event
    assertTrue( log.get( 3 ) instanceof DragSourceEvent );
    dragSourceEvent = ( DragSourceEvent )log.get( 3 );
    assertEquals( DragSourceEvent.DRAG_END, dragSourceEvent.getID() );
    assertTrue( dragSourceEvent.doit );
  }

  public void testDragSetDataDataType() {
    final java.util.List log = new ArrayList();
    Display display = new Display();
    Shell shell = new Shell( display );
    Control dragSourceControl = new Label( shell, SWT.NONE );
    DragSource dragSource = new DragSource( dragSourceControl, DND.DROP_MOVE );
    dragSource.setTransfer( new Transfer[] { TextTransfer.getInstance() } );
    dragSource.addDragListener( new DragSourceAdapter() {
      public void dragSetData( final DragSourceEvent event ) {
        log.add( event );
        event.data = "string";
      }
    } );
    Control dropTargetControl = new Label( shell, SWT.NONE );
    DropTarget dropTarget = new DropTarget( dropTargetControl, DND.DROP_MOVE );
    dropTarget.addDropListener( new DropTargetAdapter() {
      public void drop( final DropTargetEvent event ) {
        log.add( event );
      }
    } );
    dropTarget.setTransfer( new Transfer[] { TextTransfer.getInstance() } );
    shell.open();
    String displayId = DisplayUtil.getId( display );
    // Simulate request that sends a drop event over a valid drop target
    Fixture.fakeResponseWriter();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    createDropTargetEvent( dropTargetControl,
                           dragSourceControl,
                           "dropAccept",
                           0 );
    // run life cycle
    RWTFixture.executeLifeCycleFromServerThread();
    // Ensure that dataType is set to something meaningful
    DragSourceEvent setDataEvent = ( DragSourceEvent )log.get( 0 );
    assertNotNull( setDataEvent.dataType );
    DropTargetEvent dropEvent = ( DropTargetEvent )log.get( 1 );
    assertSame( setDataEvent.data, dropEvent.data );
    assertNotNull( dropEvent.currentDataType );
    boolean sameType = TransferData.sameType( setDataEvent.dataType,
                                              dropEvent.currentDataType );
    assertTrue( sameType );
  }

  public void testResponseNoDetailChange() {
    int operations = DND.DROP_MOVE | DND.DROP_LINK | DND.DROP_COPY;
    Transfer[] types = new Transfer[] { TextTransfer.getInstance() };
    Display display = new Display();
    Shell shell = new Shell( display );
    Control dragSourceControl = new Label( shell, SWT.NONE );
    DragSource dragSource = new DragSource( dragSourceControl, operations );
    dragSource.setTransfer( types );
    Control dropTargetControl = new Label( shell, SWT.NONE );
    DropTarget dropTarget = new DropTarget( dropTargetControl, operations );
    dropTarget.setTransfer( types );
    shell.open();
    // Simulate request that sends a drop event
    String displayId = DisplayUtil.getId( display );
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    RWTFixture.executeLifeCycleFromServerThread();
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    createDropTargetEvent( dropTargetControl,
                           dragSourceControl,
                           "dragEnter",
                           10,
                           10,
                           "copy",
                           1 );
    createDropTargetEvent( dropTargetControl,
                           dragSourceControl,
                           "dragOver",
                           10,
                           10,
                           "copy",
                           2 );
    RWTFixture.executeLifeCycleFromServerThread();
    String markup = Fixture.getAllMarkup();
    String expected =   "org.eclipse.rwt.DNDSupport.getInstance()"
                      + ".setOperationOverwrite( ";
    assertTrue( markup.indexOf( expected ) == -1 );
  }

  public void testResponseDetailChangedOnEnter() {
    int operations = DND.DROP_MOVE | DND.DROP_LINK | DND.DROP_COPY;
    Transfer[] types = new Transfer[] { TextTransfer.getInstance() };
    Display display = new Display();
    Shell shell = new Shell( display );
    Control dragSourceControl = new Label( shell, SWT.NONE );
    DragSource dragSource = new DragSource( dragSourceControl, operations );
    dragSource.setTransfer( types );
    Control dropTargetControl = new Label( shell, SWT.NONE );
    String qxWidget =   "wm.findWidgetById( \""
                      + WidgetUtil.getId( dropTargetControl )
                      + "\" )";
    DropTarget dropTarget = new DropTarget( dropTargetControl, operations );
    dropTarget.setTransfer( types );
    dropTarget.addDropListener( new DropTargetAdapter() {
      public void dragEnter( final DropTargetEvent event ) {
        event.detail = DND.DROP_LINK;
      }
    } );
    shell.open();
    // Simulate request that sends a drop event
    String displayId = DisplayUtil.getId( display );
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    RWTFixture.executeLifeCycleFromServerThread();
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    createDropTargetEvent( dropTargetControl,
                           dragSourceControl,
                           "dragEnter",
                           10,
                           10,
                           "move",
                           1 );
    createDropTargetEvent( dropTargetControl,
                           dragSourceControl,
                           "dragOver",
                           10,
                           10,
                           "copy",
                           2 );
    RWTFixture.executeLifeCycleFromServerThread();
    String markup = Fixture.getAllMarkup();
    String expected =   "org.eclipse.rwt.DNDSupport.getInstance()"
                      + ".setOperationOverwrite( "
                      + qxWidget
                      + ", \"link";
    assertTrue( markup.indexOf( expected ) != -1 );
  }

  public void testResponseDetailChangedOnOver() {
    int operations = DND.DROP_MOVE | DND.DROP_LINK | DND.DROP_COPY;
    Transfer[] types = new Transfer[] { TextTransfer.getInstance() };
    Display display = new Display();
    Shell shell = new Shell( display );
    Control dragSourceControl = new Label( shell, SWT.NONE );
    DragSource dragSource = new DragSource( dragSourceControl, operations );
    dragSource.setTransfer( types );
    Control dropTargetControl = new Label( shell, SWT.NONE );
    String qxWidget =   "wm.findWidgetById( \""
                      + WidgetUtil.getId( dropTargetControl )
                      + "\" )";
    DropTarget dropTarget = new DropTarget( dropTargetControl, operations );
    dropTarget.setTransfer( types );
    dropTarget.addDropListener( new DropTargetAdapter() {
      public void dragOver( final DropTargetEvent event ) {
        event.detail = DND.DROP_LINK;
      }
    } );
    shell.open();
    // Simulate request that sends a drop event
    String displayId = DisplayUtil.getId( display );
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    RWTFixture.executeLifeCycleFromServerThread();
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    createDropTargetEvent( dropTargetControl,
                           dragSourceControl,
                           "dragEnter",
                           10,
                           10,
                           "move",
                           1 );
    createDropTargetEvent( dropTargetControl,
                           dragSourceControl,
                           "dragOver",
                           10,
                           10,
                           "copy",
                           2 );
    RWTFixture.executeLifeCycleFromServerThread();
    String markup = Fixture.getAllMarkup();
    String expected =   "org.eclipse.rwt.DNDSupport.getInstance()"
                      + ".setOperationOverwrite( "
                      + qxWidget
                      + ", \"link";
    assertTrue( markup.indexOf( expected ) != -1 );
  }

  public void testDropAcceptWithDetailChangedOnEnter() {
    final java.util.List log = new ArrayList();
    int operations = DND.DROP_MOVE | DND.DROP_LINK | DND.DROP_COPY;
    Display display = new Display();
    Shell shell = new Shell( display );
    Control dragSourceControl = new Label( shell, SWT.NONE );
    DragSource dragSource = new DragSource( dragSourceControl, operations );
    dragSource.addDragListener( new DragSourceAdapter() {
      public void dragSetData( final DragSourceEvent event ) {
        event.data = "some data";
      }
    } );
    dragSource.setTransfer( new Transfer[]{ TextTransfer.getInstance() } );
    Control dropTargetControl = new Label( shell, SWT.NONE );
    DropTarget dropTarget = new DropTarget( dropTargetControl, operations );
    dropTarget.setTransfer( new Transfer[]{ TextTransfer.getInstance() } );
    dropTarget.addDropListener( new DropTargetAdapter() {
      public void dragEnter( final DropTargetEvent event ) {
        event.detail = DND.DROP_COPY;
        log.add(  event );
      }
      public void dragOver( final DropTargetEvent event ) {
        log.add(  event );
      }
      public void drop( final DropTargetEvent event ) {
        log.add(  event );
      }
    } );
    shell.open();
    // Simulate request that sends a drop event
    String displayId = DisplayUtil.getId( display );
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    RWTFixture.executeLifeCycleFromServerThread();
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    createDropTargetEvent( dropTargetControl,
                           dragSourceControl,
                           "dragEnter",
                           1 );
    createDropTargetEvent( dropTargetControl,
                           dragSourceControl,
                           "dragOver",
                           2 );
    createDropTargetEvent( dropTargetControl,
                           dragSourceControl,
                           "dropAccept",
                           3 );
    createDragSourceEvent( dragSourceControl, "dragFinished", 3 );
    RWTFixture.executeLifeCycleFromServerThread();
    String markup = Fixture.getAllMarkup();
    String overwrite =
      "org.eclipse.rwt.DNDSupport.getInstance().setOperationOverwrite( ";
    assertTrue( markup.indexOf( overwrite ) == -1 );
    assertEquals( 3, log.size() );
    DropTargetEvent dragEnter = ( DropTargetEvent )log.get( 0 );
    assertEquals( DropTargetEvent.DRAG_ENTER, dragEnter.getID() );
    assertEquals( DND.DROP_COPY, dragEnter.detail );
    DropTargetEvent dragOver = ( DropTargetEvent )log.get( 1 );
    assertEquals( DropTargetEvent.DRAG_OVER, dragOver.getID() );
    assertEquals( DND.DROP_COPY, dragOver.detail );
    DropTargetEvent drop = ( DropTargetEvent )log.get( 2 );
    assertEquals( DropTargetEvent.DROP, drop.getID() );
    assertEquals( DND.DROP_COPY, drop.detail );
  }

  public void testDetermineDataType() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Control dragSourceControl = new Label( shell, SWT.NONE );
    DragSource dragSource = new DragSource( dragSourceControl, DND.DROP_MOVE );
    Control dropTargetControl = new Label( shell, SWT.NONE );
    DropTarget dropTarget = new DropTarget( dropTargetControl, DND.DROP_MOVE );
    Transfer[] sourceTransfers = new Transfer[]{
      TextTransfer.getInstance(),
      HTMLTransfer.getInstance()
    };
    Transfer[] targetTransfers = new Transfer[]{
      RTFTransfer.getInstance(),
      HTMLTransfer.getInstance()
    };
    dragSource.setTransfer( sourceTransfers );
    dropTarget.setTransfer( targetTransfers );
    TransferData[] dataTypes
      = DNDSupport.determineDataTypes( dragSource, dropTarget );
    assertTrue( dataTypes.length > 0 );
    assertTrue( HTMLTransfer.getInstance().isSupportedType( dataTypes[ 0 ] ) );
  }

  // Mirrors _sendDragSourceEvent in DNDSupport.js
  private static void createDragSourceEvent( final Control control,
                                             final String eventType,
                                             final int time )
  {
    createDragSourceEvent( control, eventType, 0, 0, "move", time );
  }

  private static void createDragSourceEvent( final Control control,
                                             final String eventType,
                                             final int x,
                                             final int y,
                                             final String operation,
                                             final int time )
  {
    String prefix = "org.eclipse.swt.dnd." + eventType;
    String controlId = WidgetUtil.getId( control );
    Fixture.fakeRequestParam( prefix, controlId );
    Fixture.fakeRequestParam( prefix + ".x", String.valueOf( x ) );
    Fixture.fakeRequestParam( prefix + ".y", String.valueOf( y ) );
    Fixture.fakeRequestParam( prefix + ".time", String.valueOf( time ) );
  }

  // Mirrors _sendDropTargetEvent in DNDSupport.js
  private static void createDropTargetEvent( final Control control,
                                             final Control source,
                                             final String eventType,
                                             final int time )
  {
    createDropTargetEvent( control, source, eventType, 0, 0, "move", time );
  }

  private static void createDropTargetEvent( final Control control,
                                             final Control source,
                                             final String eventType,
                                             final int x,
                                             final int y,
                                             final String operation,
                                             final int time )
  {
    String prefix = "org.eclipse.swt.dnd." + eventType;
    String controlId = WidgetUtil.getId( control );
    String sourceId = WidgetUtil.getId( source );
    Fixture.fakeRequestParam( prefix, controlId );
    Fixture.fakeRequestParam( prefix + ".x", String.valueOf( x ) );
    Fixture.fakeRequestParam( prefix + ".y", String.valueOf( y ) );
    Fixture.fakeRequestParam( prefix + ".operation", operation );
    Fixture.fakeRequestParam( prefix + ".source", sourceId );
    Fixture.fakeRequestParam( prefix + ".time", String.valueOf( time ) );
  }

}
