/*******************************************************************************
 * Copyright (c) 2009, 2011 EclipseSource and others. All rights reserved.
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
import org.eclipse.rwt.lifecycle.PhaseId;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.dnd.*;
import org.eclipse.swt.events.DragDetectEvent;
import org.eclipse.swt.events.DragDetectListener;
import org.eclipse.swt.widgets.*;


public class DNDSupport_Test extends TestCase {

  private class LogingDropTargetListener implements DropTargetListener {

    public void dragEnter( DropTargetEvent event ) {
      events.add( event );
    }

    public void dragLeave( DropTargetEvent event ) {
      events.add( event );
    }

    public void dragOperationChanged( DropTargetEvent event ) {
      events.add( event );
    }

    public void dragOver( DropTargetEvent event ) {
      events.add( event );
    }

    public void drop( DropTargetEvent event ) {
      events.add( event );
    }

    public void dropAccept( DropTargetEvent event ) {
      events.add( event );
    }
    
  }
  
  private Display display;
  private Shell shell;
  private java.util.List events;

  protected void setUp() throws Exception {
    Fixture.setUp();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    display = new Display();
    shell = new Shell( display );
    events = new ArrayList();
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testRegisterAndDisposeDragSource() {
    Fixture.fakeNewRequest( display );
    Control dragSourceControl = new Label( shell, SWT.NONE );
    DragSource dragSource = new DragSource( dragSourceControl, DND.DROP_MOVE );
    Transfer[] types = new Transfer[] { TextTransfer.getInstance() };
    dragSource.setTransfer( types );
    String dndSupport = "org.eclipse.rwt.DNDSupport.getInstance()";
    String register = dndSupport + ".registerDragSource( w, [null, \"move\",null ]";
    int dataType = TextTransfer.getInstance().getSupportedTypes()[ 0 ].type;
    String transferType = dndSupport + ".setDragSourceTransferTypes( w, [ \"" + dataType;
    Fixture.executeLifeCycleFromServerThread();
    String markup = Fixture.getAllMarkup();
    assertTrue( markup.indexOf( register ) != -1 );
    assertTrue( markup.indexOf( transferType ) != -1 );
  }

  public void testRegisterAndDisposeDropTarget() {
    Fixture.fakeNewRequest( display );
    Control dropTargetControl = new Label( shell, SWT.NONE );
    DropTarget dropTarget = new DropTarget( dropTargetControl, DND.DROP_COPY );
    Transfer[] types = new Transfer[] { TextTransfer.getInstance() };
    dropTarget.setTransfer( types );
    String dndSupport = "org.eclipse.rwt.DNDSupport.getInstance()";
    String register = dndSupport + ".registerDropTarget( w, [ \"copy\",null,null ]"; 
    int dataType = TextTransfer.getInstance().getSupportedTypes()[ 0 ].type;
    String transferType = dndSupport + ".setDropTargetTransferTypes( w, [ \"" + dataType;
    Fixture.executeLifeCycleFromServerThread();
    String markup = Fixture.getAllMarkup();
    assertTrue( markup.indexOf( register ) != -1 );
    assertTrue( markup.indexOf( transferType ) != -1 );
  }

  public void testCancelAfterDragDetectAndStartEvent() {
    shell.setLocation( 5, 5 );
    Control dragSourceControl = new Label( shell, SWT.NONE );
    dragSourceControl.setLocation( 10, 20 );
    DragSource dragSource = new DragSource( dragSourceControl, DND.DROP_MOVE );
    Control dropTargetControl = new Label( shell, SWT.NONE );
    new DropTarget( dropTargetControl, DND.DROP_MOVE );
    dragSource.addDragListener( new DragSourceAdapter() {
      public void dragStart( final DragSourceEvent event ) {
        events.add( event );
        event.doit = false;
      }
    } );
    dragSourceControl.addDragDetectListener(  new DragDetectListener() {
      public void dragDetected( final DragDetectEvent event ) {
        events.add(  event );
      }
    } );
    shell.open();
    // Simulate request that sends a drop event
    Fixture.fakeNewRequest( display );
    createDragSourceEvent( dragSourceControl, "dragStart", 1 );
    // run life cycle
    Fixture.executeLifeCycleFromServerThread();
    assertEquals( 2, events.size() );
    DragDetectEvent dragDetect = ( DragDetectEvent )events.get( 0 );
    assertEquals( DragDetectEvent.DRAG_DETECT, dragDetect.getID() );
    assertEquals( -17, dragDetect.x );
    assertEquals( -27, dragDetect.y );
    assertSame( dragSourceControl, dragDetect.widget );
    DragSourceEvent dragStart = ( DragSourceEvent )events.get( 1 );
    assertEquals( DragSourceEvent.DRAG_START, dragStart.getID() );
    assertSame( dragSource, dragStart.widget );
    String markup = Fixture.getAllMarkup();
    String expected = "org.eclipse.rwt.DNDSupport.getInstance().cancel";
    assertTrue( markup.indexOf( expected ) != -1 );
  }

  public void testLeaveBeforeEnter() {
    Transfer[] types = new Transfer[] { TextTransfer.getInstance() };
    Control dragSourceCont = new Label( shell, SWT.NONE );
    DragSource dragSource = new DragSource( dragSourceCont, DND.DROP_MOVE );
    dragSource.setTransfer( types );
    Control dropTargetCont = new Label( shell, SWT.NONE );
    DropTarget dropTarget = new DropTarget( dropTargetCont, DND.DROP_MOVE );
    dropTarget.setTransfer( types );
    dropTarget.addDropListener( new LogingDropTargetListener() );
    shell.open();
    // Simulate request that sends a drop event
    Fixture.fakeNewRequest( display );
    createDropTargetEvent( dropTargetCont, dragSourceCont, "dragEnter", 1 );
    Fixture.executeLifeCycleFromServerThread();
    events.clear();
    createDropTargetEvent( dropTargetCont, dragSourceCont, "dragLeave", 2 );
    createDropTargetEvent( dropTargetCont, dragSourceCont, "dragEnter", 3 );
    createDropTargetEvent( dropTargetCont, dragSourceCont, "dragOver", 4 );
    // run life cycle
    Fixture.executeLifeCycleFromServerThread();
    assertEquals( 3, events.size() );
    DropTargetEvent dragLeave = ( DropTargetEvent )events.get( 0 );
    assertEquals( DropTargetEvent.DRAG_LEAVE, dragLeave.getID() );
    assertSame( dropTarget, dragLeave.widget );
    DropTargetEvent dragEnter = ( DropTargetEvent )events.get( 1 );
    assertEquals( DropTargetEvent.DRAG_ENTER, dragEnter.getID() );
    assertSame( dropTarget, dragEnter.widget );
    DropTargetEvent dragOver = ( DropTargetEvent )events.get( 2 );
    assertEquals( DropTargetEvent.DRAG_OVER, dragOver.getID() );
    assertSame( dropTarget, dragOver.widget );
  }

  public void testDataTransferOnDrop() {
    Control dragSourceControl = new Label( shell, SWT.NONE );
    DragSource dragSource = new DragSource( dragSourceControl, DND.DROP_MOVE );
    dragSource.setTransfer( new Transfer[] { HTMLTransfer.getInstance() } );
    Control dropTargetControl = new Label( shell, SWT.NONE );
    DropTarget dropTarget = new DropTarget( dropTargetControl, DND.DROP_MOVE );
    dropTarget.setTransfer( new Transfer[] { HTMLTransfer.getInstance() } );
    dropTarget.addDropListener( new DropTargetAdapter() {
      public void dropAccept( DropTargetEvent event ) {
        events.add( event );
      }
      public void drop( DropTargetEvent event ) {
        events.add( event );
      }
    });
    dragSource.addDragListener( new DragSourceAdapter(){
      public void dragSetData( DragSourceEvent event ) {
        events.add( event );
        event.data = "Hello World!";
      }
    } );
    shell.open();
    // Simulate request that sends a drop event
    Fixture.fakeNewRequest( display );
    int typeId = HTMLTransfer.getInstance().getSupportedTypes()[ 0 ].type;
    createDropTargetEvent( dropTargetControl,
                           dragSourceControl,
                           "dropAccept",
                           1,
                           2,
                           "move",
                           typeId,
                           1 );
    // run life cycle
    Fixture.readDataAndProcessAction( display );
    assertEquals( 3, events.size() );
    // dropAccept expected
    DropTargetEvent dropAcceptEvent = ( DropTargetEvent )events.get( 0 );
    assertEquals( DropTargetEvent.DROP_ACCEPT, dropAcceptEvent.getID() );
    assertSame( dropTarget, dropAcceptEvent.widget );
    assertEquals( 1, dropAcceptEvent.x );
    assertEquals( 2, dropAcceptEvent.y );
    assertNull( dropAcceptEvent.data );
    // dragSetData expected
    DragSourceEvent dragSetDataEvent = ( DragSourceEvent )events.get( 1 );
    assertEquals( DragSourceEvent.DRAG_SET_DATA, dragSetDataEvent.getID() );
    assertSame( dragSource, dragSetDataEvent.widget );
    assertEquals( 1, dragSetDataEvent.x );
    assertEquals( 2, dragSetDataEvent.y );
    TransferData dataType = dragSetDataEvent.dataType;
    assertTrue( HTMLTransfer.getInstance().isSupportedType( dataType ) );
    // drop expected
    DropTargetEvent dropEvent = ( DropTargetEvent )events.get( 2 );
    assertEquals( DropTargetEvent.DROP, dropEvent.getID() );
    assertSame( dropTarget, dropEvent.widget );
    assertEquals( dragSetDataEvent.dataType, dropEvent.currentDataType );
    assertEquals( 1, dropEvent.x );
    assertEquals( 2, dropEvent.y );
    assertEquals( "Hello World!", dropEvent.data );
  }

  public void testInvalidDataOnDragSetData() {
    Control dragSourceControl = new Label( shell, SWT.NONE );
    DragSource dragSource = new DragSource( dragSourceControl, DND.DROP_MOVE );
    dragSource.setTransfer( new Transfer[] { TextTransfer.getInstance() } );
    Control dropTargetControl = new Label( shell, SWT.NONE );
    DropTarget dropTarget = new DropTarget( dropTargetControl, DND.DROP_MOVE );
    dropTarget.setTransfer( new Transfer[] { TextTransfer.getInstance() } );
    dropTarget.addDropListener( new DropTargetAdapter() {
      public void dropAccept( DropTargetEvent event ) {
        events.add( event );
      }
      public void drop( DropTargetEvent event ) {
        events.add( event );
      }
    } );
    dragSource.addDragListener( new DragSourceAdapter(){
      public void dragSetData( DragSourceEvent event ) {
        events.add( event );
        event.data = new Date();
      }
    } );
    shell.open();
    // Simulate request that sends a drop event
    Fixture.fakeNewRequest( display );
    createDropTargetEvent( dropTargetControl, dragSourceControl, "dropAccept", 1 );
    // run life cycle
    try {
      Fixture.executeLifeCycleFromServerThread();
    } catch( SWTException e ) {
      events.add( e );
    }
    assertEquals( 3, events.size() );
    // dropAccept expected
    DropTargetEvent dropAcceptEvent = ( DropTargetEvent )events.get( 0 );
    assertEquals( DropTargetEvent.DROP_ACCEPT, dropAcceptEvent.getID() );
    // dragSetData expected
    DragSourceEvent dragSetDataEvent = ( DragSourceEvent )events.get( 1 );
    assertEquals( DragSourceEvent.DRAG_SET_DATA, dragSetDataEvent.getID() );
    // Exception expected
    SWTException exception = ( SWTException )events.get( 2 );
    assertEquals( DND.ERROR_INVALID_DATA, exception.code );
  }

  public void testChangeDataTypeOnDrop() {
    final TransferData[] originalDataType = new TransferData[ 1 ];
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
      public void dropAccept( DropTargetEvent event ) {
        originalDataType[ 0 ] = event.currentDataType;
        boolean isHTMLType = HTMLTransfer.getInstance().isSupportedType( event.currentDataType );
        TransferData newTransferData;
        if( isHTMLType ) {
          newTransferData = TextTransfer.getInstance().getSupportedTypes()[ 0 ];
        } else {
          newTransferData = HTMLTransfer.getInstance().getSupportedTypes()[ 0 ];
        }
        event.currentDataType = newTransferData;
        events.add( event );
      }
      public void drop( DropTargetEvent event ) {
        events.add( event );
      }
    } );
    dragSource.addDragListener( new DragSourceAdapter(){
      public void dragSetData( DragSourceEvent event ) {
        event.data = "data";
        events.add( event );
      }
    } );
    shell.open();
    // Simulate request that sends a drop event
    Fixture.fakeNewRequest( display );
    createDropTargetEvent( dropTargetControl, dragSourceControl, "dropAccept", 1 );
    // run life cycle
    Fixture.readDataAndProcessAction( display );
    assertEquals( 3, events.size() );
    // dropAccept expected
    DropTargetEvent dropAcceptEvent = ( DropTargetEvent )events.get( 0 );
    assertEquals( DropTargetEvent.DROP_ACCEPT, dropAcceptEvent.getID() );
    // dragSetData expected
    DragSourceEvent dragSetDataEvent = ( DragSourceEvent )events.get( 1 );
    assertEquals( DragSourceEvent.DRAG_SET_DATA, dragSetDataEvent.getID() );
    TransferData dataType = dragSetDataEvent.dataType;
    assertTrue( dataType != originalDataType[ 0 ] );
    // drop expected
    DropTargetEvent dropEvent = ( DropTargetEvent )events.get( 2 );
    assertEquals( DropTargetEvent.DROP, dropEvent.getID() );
    assertTrue( dragSetDataEvent.dataType == dropEvent.currentDataType );
  }

  public void testChangeDataTypeInvalidOnDrop() {
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
      public void dropAccept( DropTargetEvent event ) {
        RTFTransfer rtfTransfer = RTFTransfer.getInstance();
        event.currentDataType = rtfTransfer.getSupportedTypes()[ 0 ];
        events.add( event );
      }
      public void drop( DropTargetEvent event ) {
        events.add( event );
      }
    } );
    dragSource.addDragListener( new DragSourceAdapter(){
      public void dragSetData( DragSourceEvent event ) {
        events.add( event );
      }
    } );
    shell.open();
    // Simulate request that sends a drop event
    Fixture.fakeNewRequest( display );
    createDropTargetEvent( dropTargetControl, dragSourceControl, "dropAccept", 1 );
    // run life cycle
    Fixture.readDataAndProcessAction( display );
    // Invalid TransferData => no dropAccept
    assertEquals( 1, events.size() );
    DropTargetEvent dropAcceptEvent = ( DropTargetEvent )events.get( 0 );
    assertEquals( DropTargetEvent.DROP_ACCEPT, dropAcceptEvent.getID() );
  }

  public void testNoDropAfterDropAcceptEvent() {
    Control dragSourceCont = new Label( shell, SWT.NONE );
    DragSource dragSource = new DragSource( dragSourceCont, DND.DROP_MOVE );
    dragSource.setTransfer( new Transfer[] { HTMLTransfer.getInstance() } );
    Control dropTargetCont = new Label( shell, SWT.NONE );
    DropTarget dropTarget = new DropTarget( dropTargetCont, DND.DROP_MOVE );
    dropTarget.setTransfer( new Transfer[] { HTMLTransfer.getInstance() } );
    dropTarget.addDropListener( new DropTargetAdapter() {
      public void dropAccept( DropTargetEvent event ) {
        events.add( event );
        // prevent drop event
        event.detail = DND.DROP_NONE;
      }
      public void drop( DropTargetEvent event ) {
        events.add( event );
      }
    } );
    dragSource.addDragListener( new DragSourceListener() {
      public void dragStart( DragSourceEvent event ) {
        events.add( event );
      }
      public void dragSetData( DragSourceEvent event ) {
        events.add( event );
      }
      public void dragFinished( DragSourceEvent event ) {
        events.add( event );
      }
    } );
    shell.open();
    // Simulate request that sends a drop event
    Fixture.fakeNewRequest( display );
    createDropTargetEvent( dropTargetCont, dragSourceCont, "dropAccept", 1 );
    createDragSourceEvent( dragSourceCont, "dragFinished", 2 );
    // run life cycle
    Fixture.readDataAndProcessAction( display );
    assertEquals( 2, events.size() );
    DropTargetEvent dropAcceptEvent = ( DropTargetEvent )events.get( 0 );
    assertEquals( DropTargetEvent.DROP_ACCEPT, dropAcceptEvent.getID() );
    DragSourceEvent event = ( DragSourceEvent )events.get( 1 );
    assertEquals( DragSourceEvent.DRAG_END, event.getID() );
    assertSame( dragSource, event.widget );
    assertTrue( event.doit ); // Actual SWT behavior

  }

  public void testDropOverNonTarget() {
    Control dragSourceControl = new Label( shell, SWT.NONE );
    DragSource dragSource = new DragSource( dragSourceControl, DND.DROP_MOVE );
    dragSource.addDragListener( new DragSourceAdapter() {
      public void dragFinished( final DragSourceEvent event ) {
        events.add( event );
      }
    } );
    Control dropTargetControl = new Label( shell, SWT.NONE );
    DropTarget dropTarget = new DropTarget( dropTargetControl, DND.DROP_MOVE );
    dropTarget.addDropListener( new LogingDropTargetListener() );
    shell.open();
    // Simulate request that sends a drop event 'somewhere', but outside a valid
    // drop target
    Fixture.fakeNewRequest( display );
    createDragSourceEvent( dragSourceControl, "dragFinished", 1 );
    // run life cycle
    Fixture.readDataAndProcessAction( display );
    assertEquals( 1, events.size() );
    assertTrue( events.get( 0 ) instanceof DragSourceEvent );
    DragSourceEvent event = ( DragSourceEvent )events.get( 0 );
    assertEquals( DragSourceEvent.DRAG_END, event.getID() );
    assertSame( dragSource, event.widget );
    assertTrue( event.doit ); // Actual SWT behavior
  }

  public void testDropOverTarget() {
    Control dragSourceCont = new Label( shell, SWT.NONE );
    DragSource dragSource = new DragSource( dragSourceCont, DND.DROP_MOVE );
    dragSource.setTransfer( new Transfer[]{ TextTransfer.getInstance() } );
    dragSource.addDragListener( new DragSourceAdapter() {
      public void dragSetData( DragSourceEvent event ) {
        event.data = "text";
        events.add( event );
      }
      public void dragFinished( DragSourceEvent event ) {
        events.add( event );
      }
    } );
    Control dropTargetCont = new Label( shell, SWT.NONE );
    DropTarget dropTarget = new DropTarget( dropTargetCont, DND.DROP_MOVE );
    dropTarget.setTransfer( new Transfer[]{ TextTransfer.getInstance() } );
    dropTarget.addDropListener( new LogingDropTargetListener() );
    shell.open();
    // Simulate request that sends a drop event over a valid drop target
    Fixture.fakeNewRequest( display );
    createDropTargetEvent( dropTargetCont, dragSourceCont, "dropAccept", 1 );
    createDragSourceEvent( dragSourceCont, "dragFinished", 2 );
    // run life cycle
    Fixture.readDataAndProcessAction( display );
    assertEquals( 5, events.size() );
    // 1. expect dragLeave event
    assertTrue( events.get( 0 ) instanceof DropTargetEvent );
    DropTargetEvent dropTargetEvent = ( DropTargetEvent )events.get( 0 );
    assertEquals( DropTargetEvent.DRAG_LEAVE, dropTargetEvent.getID() );
    // 2. expect dropAccept event
    assertTrue( events.get( 1 ) instanceof DropTargetEvent );
    dropTargetEvent = ( DropTargetEvent )events.get( 1 );
    assertEquals( DropTargetEvent.DROP_ACCEPT, dropTargetEvent.getID() );
    // 3. expect dragSetData event
    assertTrue( events.get( 2 ) instanceof DragSourceEvent );
    DragSourceEvent dragSourceEvent = ( DragSourceEvent )events.get( 2 );
    assertEquals( DragSourceEvent.DRAG_SET_DATA, dragSourceEvent.getID() );
    // 4. expect drop event
    assertTrue( events.get( 3 ) instanceof DropTargetEvent );
    dropTargetEvent = ( DropTargetEvent )events.get( 3 );
    assertEquals( DropTargetEvent.DROP, dropTargetEvent.getID() );
    // 5. expect dragFinished event
    assertTrue( events.get( 4 ) instanceof DragSourceEvent );
    dragSourceEvent = ( DragSourceEvent )events.get( 4 );
    assertEquals( DragSourceEvent.DRAG_END, dragSourceEvent.getID() );
    assertTrue( dragSourceEvent.doit );
  }

  public void testChangeDetailInDropAccept() {
    int operations = DND.DROP_MOVE | DND.DROP_COPY;
    Control dragSourceCont = new Label( shell, SWT.NONE );
    DragSource dragSource = new DragSource( dragSourceCont, operations );
    dragSource.setTransfer( new Transfer[]{ TextTransfer.getInstance() } );
    Control dropTargetCont = new Label( shell, SWT.NONE );
    DropTarget dropTarget = new DropTarget( dropTargetCont, operations );
    dropTarget.setTransfer( new Transfer[]{ TextTransfer.getInstance() } );
    dragSource.addDragListener( new DragSourceAdapter() {
      public void dragFinished( DragSourceEvent event ) {
        events.add(  event );
      }
      public void dragSetData( DragSourceEvent event ) {
        event.data = "text data";
        events.add(  event );
      }
    } );
    dropTarget.addDropListener( new DropTargetAdapter() {
      public void dropAccept( DropTargetEvent event ) {
        events.add( event );
        event.detail = DND.DROP_COPY;
      }
      public void drop( DropTargetEvent event ) {
        events.add( event );
      }
    } );
    shell.open();
    // Simulate request that sends a drop event over a valid drop target
    Fixture.fakeNewRequest( display );
    createDropTargetEvent( dropTargetCont, dragSourceCont, "dropAccept", 1 );
    createDragSourceEvent( dragSourceCont, "dragFinished", 2 );
    // run life cycle
    Fixture.readDataAndProcessAction( display );
    assertEquals( 4, events.size() );
    // 1. expect dropAccept event
    DropTargetEvent dropTargetEvent = ( DropTargetEvent )events.get( 0 );
    assertEquals( DropTargetEvent.DROP_ACCEPT, dropTargetEvent.getID() );
    assertEquals( DND.DROP_COPY, dropTargetEvent.detail );
    // 2. expect dragSetData event
    DragSourceEvent dragSourceEvent = ( DragSourceEvent )events.get( 1 );
    assertEquals( DragSourceEvent.DRAG_SET_DATA, dragSourceEvent.getID() );
    assertEquals( DND.DROP_NONE, dragSourceEvent.detail );
    // 3. expect drop event
    dropTargetEvent = ( DropTargetEvent )events.get( 2 );
    assertEquals( DropTargetEvent.DROP, dropTargetEvent.getID() );
    assertEquals( DND.DROP_COPY, dropTargetEvent.detail );
    // 4. expect dragFinished event
    dragSourceEvent = ( DragSourceEvent )events.get( 3 );
    assertEquals( DragSourceEvent.DRAG_END, dragSourceEvent.getID() );
    assertTrue( dragSourceEvent.doit );
    assertEquals( DND.DROP_COPY, dragSourceEvent.detail );
  }

  public void testChangeDetailInvalidInDropAccept() {
    int operations = DND.DROP_MOVE | DND.DROP_COPY;
    Control dragSourceCont = new Label( shell, SWT.NONE );
    DragSource dragSource = new DragSource( dragSourceCont, operations );
    dragSource.setTransfer( new Transfer[] { HTMLTransfer.getInstance() } );
    Control dropTargetCont = new Label( shell, SWT.NONE );
    DropTarget dropTarget = new DropTarget( dropTargetCont, DND.DROP_MOVE );
    dropTarget.setTransfer( new Transfer[] { HTMLTransfer.getInstance() } );
    dragSource.addDragListener( new DragSourceAdapter() {
      public void dragFinished( DragSourceEvent event ) {
        events.add(  event );
      }
      public void dragSetData( DragSourceEvent event ) {
        events.add(  event );
      }
    } );
    dropTarget.addDropListener( new DropTargetAdapter() {
      public void dropAccept( DropTargetEvent event ) {
        events.add( event );
        event.detail = DND.DROP_COPY;
      }
      public void drop( DropTargetEvent event ) {
        events.add( event );
      }
    } );
    shell.open();
    // Simulate request that sends a drop event over a valid drop target
    Fixture.fakeNewRequest( display );
    createDropTargetEvent( dropTargetCont, dragSourceCont, "dropAccept", 1 );
    createDragSourceEvent( dragSourceCont, "dragFinished", 2 );
    // run life cycle
    Fixture.readDataAndProcessAction( display );
    assertEquals( 2, events.size() );
    // 1. expect dropAccept event
    DropTargetEvent dropTargetEvent = ( DropTargetEvent )events.get( 0 );
    assertEquals( DropTargetEvent.DROP_ACCEPT, dropTargetEvent.getID() );
    assertEquals( DND.DROP_COPY, dropTargetEvent.detail );
    // 2. expect dragFinished event
    DragSourceEvent dragSourceEvent = ( DragSourceEvent )events.get( 1 );
    assertEquals( DragSourceEvent.DRAG_END, dragSourceEvent.getID() );
    assertTrue( dragSourceEvent.doit ); // This is still true in SWT/Win
    assertEquals( DND.DROP_NONE, dragSourceEvent.detail );
  }

  public void testDragSetDataDoitIsFalse() {
    Control dragSourceControl = new Label( shell, SWT.NONE );
    DragSource dragSource = new DragSource( dragSourceControl, DND.DROP_MOVE );
    dragSource.setTransfer( new Transfer[]{ TextTransfer.getInstance() } );
    dragSource.addDragListener( new DragSourceAdapter() {
      public void dragSetData( DragSourceEvent event ) {
        events.add( event );
        event.data = "TestData";
        event.doit = false;
      }
      public void dragFinished( DragSourceEvent event ) {
        events.add( event );
      }
    } );
    Control dropTargetControl = new Label( shell, SWT.NONE );
    DropTarget dropTarget = new DropTarget( dropTargetControl, DND.DROP_MOVE );
    dropTarget.setTransfer( new Transfer[]{ TextTransfer.getInstance() } );
    dropTarget.addDropListener( new DropTargetAdapter() {
      public void dropAccept( DropTargetEvent event ) {
        events.add( event );
      }
      public void drop( DropTargetEvent event ) {
        events.add( event );
      }
    } );
    shell.open();
    // Simulate request that sends a drop event over a valid drop target
    Fixture.fakeNewRequest( display );
    createDropTargetEvent( dropTargetControl, dragSourceControl, "dropAccept", 1 );
    createDragSourceEvent( dragSourceControl, "dragFinished", 2 );
    // run life cycle
    Fixture.readDataAndProcessAction( display );
    assertEquals( 4, events.size() );
    // 1. expect dropAccept event
    assertTrue( events.get( 0 ) instanceof DropTargetEvent );
    DropTargetEvent dropTargetEvent = ( DropTargetEvent )events.get( 0 );
    assertEquals( DropTargetEvent.DROP_ACCEPT, dropTargetEvent.getID() );
    // 2. expect dragSetData event
    assertTrue( events.get( 1 ) instanceof DragSourceEvent );
    DragSourceEvent dragSourceEvent = ( DragSourceEvent )events.get( 1 );
    assertEquals( DragSourceEvent.DRAG_SET_DATA, dragSourceEvent.getID() );
    // NOTE: This is not the behavior documented for SWT,
    //       but how SWT behaves in Windows (bug?)
    // 3. expect drop event
    assertTrue( events.get( 2 ) instanceof DropTargetEvent );
    dropTargetEvent = ( DropTargetEvent )events.get( 2 );
    assertEquals( DropTargetEvent.DROP, dropTargetEvent.getID() );
    assertNull( dropTargetEvent.data );
    // 4. expect dragFinished event
    assertTrue( events.get( 3 ) instanceof DragSourceEvent );
    dragSourceEvent = ( DragSourceEvent )events.get( 3 );
    assertEquals( DragSourceEvent.DRAG_END, dragSourceEvent.getID() );
    assertTrue( dragSourceEvent.doit );
  }

  public void testDragSetDataDataType() {
    Control dragSourceControl = new Label( shell, SWT.NONE );
    DragSource dragSource = new DragSource( dragSourceControl, DND.DROP_MOVE );
    dragSource.setTransfer( new Transfer[] { TextTransfer.getInstance() } );
    dragSource.addDragListener( new DragSourceAdapter() {
      public void dragSetData( DragSourceEvent event ) {
        events.add( event );
        event.data = "string";
      }
    } );
    Control dropTargetControl = new Label( shell, SWT.NONE );
    DropTarget dropTarget = new DropTarget( dropTargetControl, DND.DROP_MOVE );
    dropTarget.addDropListener( new DropTargetAdapter() {
      public void drop( DropTargetEvent event ) {
        events.add( event );
      }
    } );
    dropTarget.setTransfer( new Transfer[] { TextTransfer.getInstance() } );
    shell.open();
    // Simulate request that sends a drop event over a valid drop target
    Fixture.fakeNewRequest( display );
    createDropTargetEvent( dropTargetControl, dragSourceControl, "dropAccept", 0 );
    // run life cycle
    Fixture.readDataAndProcessAction( display );
    // Ensure that dataType is set to something meaningful
    DragSourceEvent setDataEvent = ( DragSourceEvent )events.get( 0 );
    assertNotNull( setDataEvent.dataType );
    DropTargetEvent dropEvent = ( DropTargetEvent )events.get( 1 );
    assertSame( setDataEvent.data, dropEvent.data );
    assertNotNull( dropEvent.currentDataType );
    assertTrue( TransferData.sameType( setDataEvent.dataType, dropEvent.currentDataType ) );
  }

  public void testResponseNoDetailChange() {
    int operations = DND.DROP_MOVE | DND.DROP_LINK | DND.DROP_COPY;
    Transfer[] types = new Transfer[] { TextTransfer.getInstance() };
    Control dragSourceControl = new Label( shell, SWT.NONE );
    DragSource dragSource = new DragSource( dragSourceControl, operations );
    dragSource.setTransfer( types );
    Control dropTargetControl = new Label( shell, SWT.NONE );
    DropTarget dropTarget = new DropTarget( dropTargetControl, operations );
    dropTarget.setTransfer( types );
    shell.open();
    Fixture.fakeNewRequest( display );
    Fixture.executeLifeCycleFromServerThread();
    Fixture.fakeNewRequest( display );
    int typeId = TextTransfer.getInstance().getSupportedTypes()[ 0 ].type;
    createDropTargetEvent( dropTargetControl,
                           dragSourceControl,
                           "dragEnter",
                           10,
                           10,
                           "copy",
                           typeId,
                           1 );
    createDropTargetEvent( dropTargetControl,
                           dragSourceControl,
                           "dragOver",
                           10,
                           10,
                           "copy",
                           typeId,
                           2 );
    Fixture.executeLifeCycleFromServerThread();
    String markup = Fixture.getAllMarkup();
    String expected 
      = "org.eclipse.rwt.DNDSupport.getInstance()"
      + ".setOperationOverwrite( ";
    assertTrue( markup.indexOf( expected ) == -1 );
  }

  public void testResponseDetailChangedOnEnter() {
    int operations = DND.DROP_MOVE | DND.DROP_LINK | DND.DROP_COPY;
    Transfer[] types = new Transfer[] { TextTransfer.getInstance() };
    Control dragSourceControl = new Label( shell, SWT.NONE );
    DragSource dragSource = new DragSource( dragSourceControl, operations );
    dragSource.setTransfer( types );
    Control dropTargetControl = new Label( shell, SWT.NONE );
    DropTarget dropTarget = new DropTarget( dropTargetControl, operations );
    dropTarget.setTransfer( types );
    dropTarget.addDropListener( new DropTargetAdapter() {
      public void dragEnter( final DropTargetEvent event ) {
        event.detail = DND.DROP_LINK;
      }
    } );
    shell.open();
    Fixture.fakeNewRequest( display );
    Fixture.executeLifeCycleFromServerThread();
    Fixture.fakeNewRequest( display );
    createDropTargetEvent( dropTargetControl, dragSourceControl, "dragEnter", 1 );
    createDropTargetEvent( dropTargetControl, dragSourceControl, "dragOver", 2 );
    Fixture.executeLifeCycleFromServerThread();
    String markup = Fixture.getAllMarkup();
    String expected
      = "org.eclipse.rwt.DNDSupport.getInstance()"
      + ".setOperationOverwrite( "
      + ( "wm.findWidgetById( \""
      + WidgetUtil.getId( dropTargetControl )
      + "\" )" )
      + ", \"link";
    assertTrue( markup.indexOf( expected ) != -1 );
  }

  public void testResponseDetailChangedOnOver() {
    int operations = DND.DROP_MOVE | DND.DROP_LINK | DND.DROP_COPY;
    Transfer[] types = new Transfer[] { TextTransfer.getInstance() };
    Control dragSourceControl = new Label( shell, SWT.NONE );
    DragSource dragSource = new DragSource( dragSourceControl, operations );
    dragSource.setTransfer( types );
    Control dropTargetControl = new Label( shell, SWT.NONE );
    DropTarget dropTarget = new DropTarget( dropTargetControl, operations );
    dropTarget.setTransfer( types );
    dropTarget.addDropListener( new DropTargetAdapter() {
      public void dragOver( DropTargetEvent event ) {
        event.detail = DND.DROP_LINK;
      }
    } );
    shell.open();
    Fixture.fakeNewRequest( display );
    Fixture.executeLifeCycleFromServerThread();
    Fixture.fakeNewRequest( display );
    int typeId = TextTransfer.getInstance().getSupportedTypes()[ 0 ].type;
    createDropTargetEvent( dropTargetControl,
                           dragSourceControl,
                           "dragEnter",
                           10,
                           10,
                           "move",
                           typeId,
                           1 );
    createDropTargetEvent( dropTargetControl,
                           dragSourceControl,
                           "dragOver",
                           10,
                           10,
                           "copy",
                           typeId,
                           2 );
    Fixture.executeLifeCycleFromServerThread();
    String markup = Fixture.getAllMarkup();
    String expected
      = "org.eclipse.rwt.DNDSupport.getInstance()"
      + ".setOperationOverwrite( "
      + ( "wm.findWidgetById( \""
      + WidgetUtil.getId( dropTargetControl )
      + "\" )" )
      + ", \"link";
    assertTrue( markup.indexOf( expected ) != -1 );
  }

  public void testDropAcceptWithDetailChangedOnEnter() {
    int operations = DND.DROP_MOVE | DND.DROP_LINK | DND.DROP_COPY;
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
      public void dragEnter( DropTargetEvent event ) {
        event.detail = DND.DROP_COPY;
        events.add(  event );
      }
      public void dragOver( DropTargetEvent event ) {
        events.add(  event );
      }
      public void drop( DropTargetEvent event ) {
        events.add(  event );
      }
    } );
    shell.open();
    Fixture.fakeNewRequest( display );
    Fixture.executeLifeCycleFromServerThread();
    Fixture.fakeNewRequest( display );
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
    Fixture.executeLifeCycleFromServerThread();
    String markup = Fixture.getAllMarkup();
    String overwrite
      = "org.eclipse.rwt.DNDSupport.getInstance().setOperationOverwrite( ";
    assertTrue( markup.indexOf( overwrite ) == -1 );
    assertEquals( 3, events.size() );
    DropTargetEvent dragEnter = ( DropTargetEvent )events.get( 0 );
    assertEquals( DropTargetEvent.DRAG_ENTER, dragEnter.getID() );
    assertEquals( DND.DROP_COPY, dragEnter.detail );
    DropTargetEvent dragOver = ( DropTargetEvent )events.get( 1 );
    assertEquals( DropTargetEvent.DRAG_OVER, dragOver.getID() );
    assertEquals( DND.DROP_COPY, dragOver.detail );
    DropTargetEvent drop = ( DropTargetEvent )events.get( 2 );
    assertEquals( DropTargetEvent.DROP, drop.getID() );
    assertEquals( DND.DROP_COPY, drop.detail );
  }

  public void testDetermineDataType() {
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
    TransferData[] dataTypes = DNDSupport.determineDataTypes( dragSource, dropTarget );
    assertTrue( dataTypes.length > 0 );
    assertTrue( HTMLTransfer.getInstance().isSupportedType( dataTypes[ 0 ] ) );
  }

  public void testResponseFeedbackChangedOnEnter() {
    int operations = DND.DROP_MOVE | DND.DROP_LINK | DND.DROP_COPY;
    Transfer[] types = new Transfer[] { TextTransfer.getInstance() };
    Control dragSourceControl = new Label( shell, SWT.NONE );
    DragSource dragSource = new DragSource( dragSourceControl, operations );
    dragSource.setTransfer( types );
    Control dropTargetControl = new Label( shell, SWT.NONE );
    DropTarget dropTarget = new DropTarget( dropTargetControl, operations );
    dropTarget.setTransfer( types );
    dropTarget.addDropListener( new DropTargetAdapter() {
      public void dragEnter( DropTargetEvent event ) {
        event.feedback = DND.FEEDBACK_SELECT;
      }
    } );
    shell.open();
    Fixture.fakeNewRequest( display );
    Fixture.executeLifeCycleFromServerThread();
    Fixture.fakeNewRequest( display );
    createDropTargetEvent( dropTargetControl,
                           dragSourceControl,
                           "dragEnter",
                           1 );
    createDropTargetEvent( dropTargetControl,
                           dragSourceControl,
                           "dragOver",
                           2 );
    Fixture.executeLifeCycleFromServerThread();
    String markup = Fixture.getAllMarkup();
    String expected
      = "org.eclipse.rwt.DNDSupport.getInstance()"
      + ".setFeedback( "
      + ( "wm.findWidgetById( \""
      + WidgetUtil.getId( dropTargetControl )
      + "\" )" )
      + ", [ \"select\" ], "
      + DND.FEEDBACK_SELECT;
    assertTrue( markup.indexOf( expected ) != -1 );
  }

  public void testResponseFeedbackChangedOnOver() {
    int operations = DND.DROP_MOVE | DND.DROP_LINK | DND.DROP_COPY;
    Transfer[] types = new Transfer[] { TextTransfer.getInstance() };
    Control dragSourceControl = new Label( shell, SWT.NONE );
    DragSource dragSource = new DragSource( dragSourceControl, operations );
    dragSource.setTransfer( types );
    Control dropTargetControl = new Label( shell, SWT.NONE );
    DropTarget dropTarget = new DropTarget( dropTargetControl, operations );
    dropTarget.setTransfer( types );
    dropTarget.addDropListener( new DropTargetAdapter() {
      public void dragOver( DropTargetEvent event ) {
        event.feedback = DND.FEEDBACK_EXPAND | DND.FEEDBACK_SCROLL;
      }
    } );
    shell.open();
    Fixture.fakeNewRequest( display );
    Fixture.executeLifeCycleFromServerThread();
    Fixture.fakeNewRequest( display );
    createDropTargetEvent( dropTargetControl,
                           dragSourceControl,
                           "dragEnter",
                           1 );
    createDropTargetEvent( dropTargetControl,
                           dragSourceControl,
                           "dragOver",
                           2 );
    Fixture.executeLifeCycleFromServerThread();
    String markup = Fixture.getAllMarkup();
    String expected
      = "org.eclipse.rwt.DNDSupport.getInstance()"
      + ".setFeedback( "
      + ( "wm.findWidgetById( \""
      + WidgetUtil.getId( dropTargetControl )
      + "\" )" )
      + ", [ \"expand\", \"scroll\" ], "
      + ( DND.FEEDBACK_EXPAND | DND.FEEDBACK_SCROLL );
    assertTrue( markup.indexOf( expected ) != -1 );
  }
  
  public void testResponseInitDataType() {
    int operations = DND.DROP_MOVE | DND.DROP_LINK | DND.DROP_COPY;
    Transfer[] types = new Transfer[] { 
      TextTransfer.getInstance(),
      RTFTransfer.getInstance()
    };
    Control dragSourceControl = new Label( shell, SWT.NONE );
    DragSource dragSource = new DragSource( dragSourceControl, operations );
    dragSource.setTransfer( types );
    Control dropTargetControl = new Label( shell, SWT.NONE );
    DropTarget dropTarget = new DropTarget( dropTargetControl, operations );
    dropTarget.setTransfer( types );
    shell.open();
    Fixture.fakeNewRequest( display );
    Fixture.executeLifeCycleFromServerThread();
    Fixture.fakeNewRequest( display );
    createDropTargetEvent( dropTargetControl,
                           dragSourceControl,
                           "dragEnter",
                           1 );
    Fixture.executeLifeCycleFromServerThread();
    String markup = Fixture.getAllMarkup();
    String expected
    = "org.eclipse.rwt.DNDSupport.getInstance()"
      + ".setDataType( "
      + ( "wm.findWidgetById( \""
          + WidgetUtil.getId( dropTargetControl )
          + "\" ), " );
    assertTrue( markup.indexOf( expected ) != -1 );
  }
  
  public void testResponseChangeDataTypeOnOver() {
    int operations = DND.DROP_MOVE | DND.DROP_LINK | DND.DROP_COPY;
    Transfer[] types = new Transfer[] { 
      TextTransfer.getInstance(),
      RTFTransfer.getInstance()
    };
    Control dragSourceControl = new Label( shell, SWT.NONE );
    DragSource dragSource = new DragSource( dragSourceControl, operations );
    dragSource.setTransfer( types );
    Control dropTargetControl = new Label( shell, SWT.NONE );
    DropTarget dropTarget = new DropTarget( dropTargetControl, operations );
    dropTarget.setTransfer( types );
    dropTarget.addDropListener( new DropTargetAdapter() {
      public void dragEnter( DropTargetEvent event ) {
        events.add( event );
      }
      public void dragOver( DropTargetEvent event ) {
        event.currentDataType = RTFTransfer.getInstance().getSupportedTypes()[ 0 ];
      }
    } );    
    shell.open();
    Fixture.fakeNewRequest( display );
    Fixture.executeLifeCycleFromServerThread();
    Fixture.fakeNewRequest( display );
    createDropTargetEvent( dropTargetControl,
                           dragSourceControl,
                           "dragEnter",
                           1 );
    createDropTargetEvent( dropTargetControl,
                           dragSourceControl,
                           "dragOver",
                           2 );
    Fixture.executeLifeCycleFromServerThread();
    String markup = Fixture.getAllMarkup();
    DropTargetEvent dragEnter = ( DropTargetEvent )events.get( 0 );
    TransferData typeOnEnter = dragEnter.currentDataType;
    assertTrue( TextTransfer.getInstance().isSupportedType( typeOnEnter ) );
    String expected
    = "org.eclipse.rwt.DNDSupport.getInstance()"
      + ".setDataType( "
      + ( "wm.findWidgetById( \""
          + WidgetUtil.getId( dropTargetControl )
          + "\" ), " 
          + RTFTransfer.getInstance().getSupportedTypes()[ 0 ].type );
    assertTrue( markup.indexOf( expected ) != -1 );
  }
  
  public void testResponseChangeDataTypeOnEnter() {
    int operations = DND.DROP_MOVE | DND.DROP_LINK | DND.DROP_COPY;
    Transfer[] types = new Transfer[] { 
      TextTransfer.getInstance(),
      RTFTransfer.getInstance()
    };
    Control dragSourceControl = new Label( shell, SWT.NONE );
    DragSource dragSource = new DragSource( dragSourceControl, operations );
    dragSource.setTransfer( types );
    Control dropTargetControl = new Label( shell, SWT.NONE );
    DropTarget dropTarget = new DropTarget( dropTargetControl, operations );
    dropTarget.setTransfer( types );
    dropTarget.addDropListener( new DropTargetAdapter() {
      public void dragEnter( DropTargetEvent event ) {
        event.currentDataType = RTFTransfer.getInstance().getSupportedTypes()[ 0 ];
      }
      public void dragOver( DropTargetEvent event ) {
        events.add( event );
      }
    } );    
    shell.open();
    Fixture.fakeNewRequest( display );
    Fixture.executeLifeCycleFromServerThread();
    Fixture.fakeNewRequest( display );
    createDropTargetEvent( dropTargetControl,
                           dragSourceControl,
                           "dragEnter",
                           1 );
    createDropTargetEvent( dropTargetControl,
                           dragSourceControl,
                           "dragOver",
                           2 );
    Fixture.executeLifeCycleFromServerThread();
    String markup = Fixture.getAllMarkup();
    DropTargetEvent dragOver = ( DropTargetEvent )events.get( 0 );
    TransferData typeOnOver = dragOver.currentDataType;
    assertTrue( RTFTransfer.getInstance().isSupportedType( typeOnOver ) );
    String expected
    = "org.eclipse.rwt.DNDSupport.getInstance()"
      + ".setDataType( "
      + ( "wm.findWidgetById( \""
          + WidgetUtil.getId( dropTargetControl )
          + "\" ), " 
          + RTFTransfer.getInstance().getSupportedTypes()[ 0 ].type );
    assertTrue( markup.indexOf( expected ) != -1 );
  }

  public void testResponseChangeDataTypeInvalid() {
    // NOTE : Setting an invalid value on currentDataType reverts the field 
    //        back to the next-best valid value. This is NOT SWT-like behavior!
    //        SWT would set null and display the DROP_NONE cursor.
    int operations = DND.DROP_MOVE | DND.DROP_LINK | DND.DROP_COPY;
    Transfer[] types = new Transfer[] { TextTransfer.getInstance() };
    Control dragSourceControl = new Label( shell, SWT.NONE );
    DragSource dragSource = new DragSource( dragSourceControl, operations );
    dragSource.setTransfer( types );
    Control dropTargetControl = new Label( shell, SWT.NONE );
    DropTarget dropTarget = new DropTarget( dropTargetControl, operations );
    dropTarget.setTransfer( types );
    dropTarget.addDropListener( new DropTargetAdapter() {
      public void dragOver( DropTargetEvent event ) {
        event.currentDataType = RTFTransfer.getInstance().getSupportedTypes()[ 0 ];
      }
    } );    
    shell.open();
    Fixture.fakeNewRequest( display );
    Fixture.executeLifeCycleFromServerThread();
    Fixture.fakeNewRequest( display );
    createDropTargetEvent( dropTargetControl, dragSourceControl, "dragOver", 1 );
    Fixture.executeLifeCycleFromServerThread();
    String markup = Fixture.getAllMarkup();
    String expected
      = "org.eclipse.rwt.DNDSupport.getInstance()"
      + ".setDataType( "
      + ( "wm.findWidgetById( \""
      + WidgetUtil.getId( dropTargetControl )
      + "\" ), " 
      + TextTransfer.getInstance().getSupportedTypes()[ 0 ].type );
    assertTrue( markup.indexOf( expected ) != -1 );
  }

  public void testOperationChangedEvent() {
    int operations = DND.DROP_MOVE | DND.DROP_LINK | DND.DROP_COPY;
    Transfer[] types = new Transfer[] { TextTransfer.getInstance() };
    Control dragSourceCont = new Label( shell, SWT.NONE );
    DragSource dragSource = new DragSource( dragSourceCont, operations );
    dragSource.setTransfer( types );
    Control dropTargetCont = new Label( shell, SWT.NONE );
    DropTarget dropTarget = new DropTarget( dropTargetCont, operations );
    dropTarget.setTransfer( types );
    dropTarget.addDropListener( new LogingDropTargetListener() );
    shell.open();
    // Simulate request that sends a drop event
    Fixture.fakeNewRequest( display );
    int dataType = TextTransfer.getInstance().getSupportedTypes()[ 0 ].type;
    createDropTargetEvent( dropTargetCont, dragSourceCont, "dragEnter", 2 );
    createDropTargetEvent( dropTargetCont, 
                           dragSourceCont, 
                           "dragOperationChanged",
                           0,
                           0,
                           "copy",
                           dataType,
                           3 );
    createDropTargetEvent( dropTargetCont, dragSourceCont, "dragOver", 5 );
    // run life cycle
    Fixture.executeLifeCycleFromServerThread();
    assertEquals( 3, events.size() );
    DropTargetEvent dragEnter = ( DropTargetEvent )events.get( 0 );
    assertEquals( DropTargetEvent.DRAG_ENTER, dragEnter.getID() );
    assertSame( dropTarget, dragEnter.widget );
    DropTargetEvent dragOperationChanged = ( DropTargetEvent )events.get( 1 );
    assertEquals( DropTargetEvent.DRAG_OPERATION_CHANGED, 
                  dragOperationChanged.getID() );
    assertTrue( ( dragOperationChanged.detail & DND.DROP_COPY ) != 0 );
    DropTargetEvent dragOver = ( DropTargetEvent )events.get( 2 );
    assertEquals( DropTargetEvent.DRAG_OVER, dragOver.getID() );
    assertSame( dropTarget, dragOver.widget );
  }
  
  public void testOperationsField() {
    int operations = DND.DROP_MOVE | DND.DROP_LINK;
    Transfer[] types = new Transfer[] { TextTransfer.getInstance() };
    Control dragSourceCont = new Label( shell, SWT.NONE );
    DragSource dragSource = new DragSource( dragSourceCont, operations );
    dragSource.setTransfer( types );
    dragSource.addDragListener( new DragSourceAdapter(){
      public void dragSetData( final DragSourceEvent event ) {
        event.data = "text";
      }
    } );    
    Control dropTargetCont = new Label( shell, SWT.NONE );
    DropTarget dropTarget = new DropTarget( dropTargetCont, operations );
    dropTarget.setTransfer( types );
    dropTarget.addDropListener( new LogingDropTargetListener() );
    shell.open();
    // Simulate request that sends a drop event
    Fixture.fakeNewRequest( display );
    createDropTargetEvent( dropTargetCont, dragSourceCont, "dragEnter", 2 );
    createDropTargetEvent( dropTargetCont, dragSourceCont, "dragOver", 5 );
    createDropTargetEvent( dropTargetCont, dragSourceCont, "dragOperationChanged", 7 );
    createDropTargetEvent( dropTargetCont, dragSourceCont, "dropAccept", 8 );
    // run life cycle
    Fixture.readDataAndProcessAction( display );
    assertEquals( 6, events.size() );
    assertEquals( operations, ( ( DropTargetEvent )events.get( 0 ) ).operations );
    assertEquals( operations, ( ( DropTargetEvent )events.get( 1 ) ).operations );
    assertEquals( operations, ( ( DropTargetEvent )events.get( 2 ) ).operations );
    assertEquals( 0, ( ( DropTargetEvent )events.get( 3 ) ).operations );
    assertEquals( operations, ( ( DropTargetEvent )events.get( 4 ) ).operations );
    assertEquals( operations, ( ( DropTargetEvent )events.get( 5 ) ).operations );
  }

  // Mirrors _sendDragSourceEvent in DNDSupport.js
  private static void createDragSourceEvent( Control control, String eventType, int time ) {
    createDragSourceEvent( control, eventType, 0, 0, "move", time );
  }

  private static void createDragSourceEvent( Control control,
                                             String eventType,
                                             int x,
                                             int y,
                                             String operation,
                                             int time )
  {
    String prefix = "org.eclipse.swt.dnd." + eventType;
    String controlId = WidgetUtil.getId( control );
    Fixture.fakeRequestParam( prefix, controlId );
    Fixture.fakeRequestParam( prefix + ".x", String.valueOf( x ) );
    Fixture.fakeRequestParam( prefix + ".y", String.valueOf( y ) );
    Fixture.fakeRequestParam( prefix + ".time", String.valueOf( time ) );
  }

  // Mirrors _sendDropTargetEvent in DNDSupport.js
  private static void createDropTargetEvent( Control control,
                                             Control source,
                                             String eventType,
                                             int time )
  {
    int dataType = TextTransfer.getInstance().getSupportedTypes()[ 0 ].type;
    createDropTargetEvent( control, 
                           source, 
                           eventType, 
                           0, 
                           0, 
                           "move", 
                           dataType,
                           time  );
  }

  private static void createDropTargetEvent( Control control,
                                             Control source,
                                             String eventType,
                                             int x,
                                             int y,
                                             String operation,
                                             int dataType,
                                             int time )
  {
    String prefix = "org.eclipse.swt.dnd." + eventType;
    String controlId = WidgetUtil.getId( control );
    String sourceId = WidgetUtil.getId( source );
    Fixture.fakeRequestParam( prefix, controlId );
    Fixture.fakeRequestParam( prefix + ".x", String.valueOf( x ) );
    Fixture.fakeRequestParam( prefix + ".y", String.valueOf( y ) );
    Fixture.fakeRequestParam( prefix + ".operation", operation );
    Fixture.fakeRequestParam( prefix + ".feedback", "0" );
    Fixture.fakeRequestParam( prefix + ".source", sourceId );
    Fixture.fakeRequestParam( prefix + ".time", String.valueOf( time ) );
    Fixture.fakeRequestParam( prefix + ".dataType", String.valueOf( dataType ) );
  }
}
