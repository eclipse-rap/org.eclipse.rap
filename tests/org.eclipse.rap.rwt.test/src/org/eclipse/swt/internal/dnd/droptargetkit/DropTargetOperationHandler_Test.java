/*******************************************************************************
 * Copyright (c) 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.dnd.droptargetkit;

import static org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil.getId;
import static org.eclipse.swt.internal.dnd.DNDUtil.getDetailChangedValue;
import static org.eclipse.swt.internal.dnd.DNDUtil.hasDataTypeChanged;
import static org.eclipse.swt.internal.dnd.DNDUtil.hasDetailChanged;
import static org.eclipse.swt.internal.dnd.DNDUtil.hasFeedbackChanged;
import static org.eclipse.swt.internal.dnd.droptargetkit.DropTargetOperationHandler.determineDataTypes;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.rwt.dnd.ClientFileTransfer;
import org.eclipse.rap.rwt.internal.client.ClientFileImpl;
import org.eclipse.rap.rwt.internal.lifecycle.PhaseId;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.HTMLTransfer;
import org.eclipse.swt.dnd.RTFTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.internal.dnd.DNDEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;


public class DropTargetOperationHandler_Test {

  private Table sourceControl;
  private Item item;
  private DragSource dragSource;
  private Control targetControl;
  private DropTarget dropTarget;
  private DropTargetOperationHandler handler;
  private Transfer[] transfers;

  @Before
  public void setUp() {
    Fixture.setUp();
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    transfers = new Transfer[] {
      HTMLTransfer.getInstance(),
      TextTransfer.getInstance()
    };
    sourceControl = new Table( shell, SWT.NONE );
    item = new TableItem( sourceControl, SWT.NONE );
    dragSource = spy( new DragSource( sourceControl, DND.DROP_MOVE | DND.DROP_COPY ) );
    dragSource.setTransfer( transfers );
    sourceControl.setData( DND.DRAG_SOURCE_KEY, dragSource );
    targetControl = new Label( shell, SWT.NONE );
    dropTarget = spy( new DropTarget( targetControl, DND.DROP_MOVE | DND.DROP_COPY ) );
    dropTarget.setTransfer( transfers );
    handler = new DropTargetOperationHandler( dropTarget );
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testHandleNotifyDragEnter_notifiesListeners() {
    JsonObject properties = new JsonObject()
      .add( "x", 10 )
      .add( "y", 20 )
      .add( "time", 3 )
      .add( "operation", "move" )
      .add( "feedback", DND.FEEDBACK_INSERT_AFTER )
      .add( "item", getId( item ) )
      .add( "source", getId( sourceControl ) );

    handler.handleNotify( "DragEnter", properties );

    ArgumentCaptor<DNDEvent> captor = ArgumentCaptor.forClass( DNDEvent.class );
    verify( dropTarget ).notifyListeners( eq( DND.DragEnter ), captor.capture() );
    DNDEvent event = captor.getValue();
    assertEquals( 10, event.x );
    assertEquals( 20, event.y );
    assertEquals( 3, event.time );
    assertEquals( DND.DROP_MOVE, event.detail );
    assertEquals( DND.FEEDBACK_INSERT_AFTER, event.feedback );
    assertEquals( DND.DROP_MOVE | DND.DROP_COPY, event.operations );
    assertSame( item, event.item );
    assertEquals( 2, event.dataTypes.length );
    assertEquals( getHTMLTransferDataType(), event.dataType.type );
  }

  @Test
  public void testHandleNotifyDragEnter_notifiesListeners_nullItem() {
    JsonObject properties = new JsonObject()
      .add( "x", 10 )
      .add( "y", 20 )
      .add( "time", 3 )
      .add( "operation", "move" )
      .add( "feedback", DND.FEEDBACK_INSERT_AFTER )
      .add( "item", JsonObject.NULL )
      .add( "source", getId( sourceControl ) );

    handler.handleNotify( "DragEnter", properties );

    ArgumentCaptor<DNDEvent> captor = ArgumentCaptor.forClass( DNDEvent.class );
    verify( dropTarget ).notifyListeners( eq( DND.DragEnter ), captor.capture() );
    DNDEvent event = captor.getValue();
    assertNull( event.item );
  }

  @Test
  public void testHandleNotifyDragEnter_alwaysSetsDataTypeField() {
    JsonObject properties = new JsonObject()
      .add( "x", 10 )
      .add( "y", 20 )
      .add( "time", 3 )
      .add( "operation", "move" )
      .add( "feedback", DND.FEEDBACK_INSERT_AFTER )
      .add( "item", JsonObject.NULL )
      .add( "source", getId( sourceControl ) );

    handler.handleNotify( "DragEnter", properties );

    assertTrue( hasDataTypeChanged() );
  }

  @Test
  public void testHandleNotifyDragEnter_setsChangedEventFields() {
    dropTarget.addDropListener( new DropTargetAdapter() {
      @Override
      public void dragEnter( DropTargetEvent event ) {
        event.feedback = DND.FEEDBACK_INSERT_BEFORE;
        event.detail = DND.DROP_COPY;
      }
    } );
    JsonObject properties = new JsonObject()
      .add( "x", 10 )
      .add( "y", 20 )
      .add( "time", 3 )
      .add( "operation", "move" )
      .add( "feedback", DND.FEEDBACK_INSERT_AFTER )
      .add( "item", JsonObject.NULL )
      .add( "source", getId( sourceControl ) );

    handler.handleNotify( "DragEnter", properties );

    assertTrue( hasFeedbackChanged() );
    assertTrue( hasDetailChanged() );
    assertTrue( hasDataTypeChanged() );
  }

  @Test
  public void testHandleNotifyDragOperationChanged_notifiesListeners() {
    JsonObject properties = new JsonObject()
      .add( "x", 10 )
      .add( "y", 20 )
      .add( "time", 3 )
      .add( "operation", "move" )
      .add( "feedback", DND.FEEDBACK_INSERT_AFTER )
      .add( "dataType", 123 )
      .add( "item", getId( item ) )
      .add( "source", getId( sourceControl ) );

    handler.handleNotify( "DragOperationChanged", properties );

    ArgumentCaptor<DNDEvent> captor = ArgumentCaptor.forClass( DNDEvent.class );
    verify( dropTarget ).notifyListeners( eq( DND.DragOperationChanged ), captor.capture() );
    DNDEvent event = captor.getValue();
    assertEquals( 10, event.x );
    assertEquals( 20, event.y );
    assertEquals( 3, event.time );
    assertEquals( DND.DROP_MOVE, event.detail );
    assertEquals( DND.FEEDBACK_INSERT_AFTER, event.feedback );
    assertEquals( DND.DROP_MOVE | DND.DROP_COPY, event.operations );
    assertSame( item, event.item );
    assertEquals( 2, event.dataTypes.length );
    assertEquals( 123, event.dataType.type );
  }

  @Test
  public void testHandleNotifyDragOperationChanged_setsChangedEventFields() {
    dropTarget.addDropListener( new DropTargetAdapter() {
      @Override
      public void dragOperationChanged( DropTargetEvent event ) {
        event.feedback = DND.FEEDBACK_INSERT_BEFORE;
        event.detail = DND.DROP_COPY;
        event.currentDataType = new TransferData();
        event.currentDataType.type = getTextTransferDataType();
      }
    } );
    JsonObject properties = new JsonObject()
      .add( "x", 10 )
      .add( "y", 20 )
      .add( "time", 3 )
      .add( "operation", "move" )
      .add( "feedback", DND.FEEDBACK_INSERT_AFTER )
      .add( "item", JsonObject.NULL )
      .add( "source", getId( sourceControl ) );

    handler.handleNotify( "DragOperationChanged", properties );

    assertTrue( hasFeedbackChanged() );
    assertTrue( hasDetailChanged() );
    assertTrue( hasDataTypeChanged() );
  }

  @Test
  public void testHandleNotifyDragOver_notifiesListeners() {
    JsonObject properties = new JsonObject()
      .add( "x", 10 )
      .add( "y", 20 )
      .add( "time", 3 )
      .add( "operation", "move" )
      .add( "feedback", DND.FEEDBACK_INSERT_AFTER )
      .add( "dataType", 123 )
      .add( "item", getId( item ) )
      .add( "source", getId( sourceControl ) );

    handler.handleNotify( "DragOver", properties );

    ArgumentCaptor<DNDEvent> captor = ArgumentCaptor.forClass( DNDEvent.class );
    verify( dropTarget ).notifyListeners( eq( DND.DragOver ), captor.capture() );
    DNDEvent event = captor.getValue();
    assertEquals( 10, event.x );
    assertEquals( 20, event.y );
    assertEquals( 3, event.time );
    assertEquals( DND.DROP_MOVE, event.detail );
    assertEquals( DND.FEEDBACK_INSERT_AFTER, event.feedback );
    assertEquals( DND.DROP_MOVE | DND.DROP_COPY, event.operations );
    assertSame( item, event.item );
    assertEquals( 2, event.dataTypes.length );
    assertEquals( 123, event.dataType.type );
  }

  @Test
  public void testHandleNotifyDragOver_setsChangedEventFields() {
    dropTarget.addDropListener( new DropTargetAdapter() {
      @Override
      public void dragOver( DropTargetEvent event ) {
        event.feedback = DND.FEEDBACK_INSERT_BEFORE;
        event.detail = DND.DROP_COPY;
        event.currentDataType = new TransferData();
        event.currentDataType.type = getTextTransferDataType();
      }
    } );
    JsonObject properties = new JsonObject()
      .add( "x", 10 )
      .add( "y", 20 )
      .add( "time", 3 )
      .add( "operation", "move" )
      .add( "feedback", DND.FEEDBACK_INSERT_AFTER )
      .add( "item", JsonObject.NULL )
      .add( "source", getId( sourceControl ) );

    handler.handleNotify( "DragOver", properties );

    assertTrue( hasFeedbackChanged() );
    assertTrue( hasDetailChanged() );
    assertTrue( hasDataTypeChanged() );
  }

  @Test
  public void testHandleNotifyDragLeave_notifiesListeners() {
    JsonObject properties = new JsonObject()
      .add( "x", 10 )
      .add( "y", 20 )
      .add( "time", 3 )
      .add( "operation", "move" );

    handler.handleNotify( "DragLeave", properties );

    ArgumentCaptor<DNDEvent> captor = ArgumentCaptor.forClass( DNDEvent.class );
    verify( dropTarget ).notifyListeners( eq( DND.DragLeave ), captor.capture() );
    DNDEvent event = captor.getValue();
    assertEquals( 10, event.x );
    assertEquals( 20, event.y );
    assertEquals( 3, event.time );
    assertEquals( DND.DROP_MOVE, event.detail );
  }

  @Test
  public void testHandleNotifyDragAccept_notifiesListeners() {
    JsonObject properties = new JsonObject()
      .add( "x", 10 )
      .add( "y", 20 )
      .add( "time", 3 )
      .add( "operation", "move" )
      .add( "dataType", 123 )
      .add( "item", getId( item ) )
      .add( "source", getId( sourceControl ) );

    handler.handleNotify( "DropAccept", properties );

    ArgumentCaptor<DNDEvent> captor = ArgumentCaptor.forClass( DNDEvent.class );
    verify( dropTarget ).notifyListeners( eq( DND.DropAccept ), captor.capture() );
    DNDEvent event = captor.getValue();
    assertEquals( 10, event.x );
    assertEquals( 20, event.y );
    assertEquals( 3, event.time );
    assertEquals( DND.DROP_MOVE, event.detail );
    assertEquals( DND.DROP_MOVE | DND.DROP_COPY, event.operations );
    assertSame( item, event.item );
    assertEquals( 123, event.dataType.type );
  }

  @Test
  public void testHandleNotifyDragAccept_notifiesDragLeaveListeners() {
    JsonObject properties = new JsonObject()
      .add( "x", 10 )
      .add( "y", 20 )
      .add( "time", 3 )
      .add( "operation", "move" )
      .add( "dataType", 123 )
      .add( "item", getId( item ) )
      .add( "source", getId( sourceControl ) );

    handler.handleNotify( "DropAccept", properties );

    ArgumentCaptor<DNDEvent> captor = ArgumentCaptor.forClass( DNDEvent.class );
    verify( dropTarget ).notifyListeners( eq( DND.DragLeave ), captor.capture() );
    DNDEvent event = captor.getValue();
    assertEquals( 10, event.x );
    assertEquals( 20, event.y );
    assertEquals( 3, event.time );
    assertEquals( DND.DROP_MOVE, event.detail );
  }

  @Test
  public void testHandleNotifyDragAccept_notifiesDragSetDataListeners() {
    final AtomicReference<Event> eventRef = new AtomicReference<Event>();
    dragSource.addListener( DND.DragSetData, new Listener() {
      public void handleEvent( Event event ) {
        event.data = "some html";
        eventRef.set( event );
      }
    } );
    JsonObject properties = new JsonObject()
      .add( "x", 10 )
      .add( "y", 20 )
      .add( "time", 3 )
      .add( "operation", "move" )
      .add( "dataType", getHTMLTransferDataType() )
      .add( "item", getId( item ) )
      .add( "source", getId( sourceControl ) );

    handler.handleNotify( "DropAccept", properties );

    Event event = eventRef.get();
    assertEquals( 10, event.x );
    assertEquals( 20, event.y );
    assertEquals( "some html", event.data );
  }

  @Test
  public void testHandleNotifyDragAccept_notifiesDropListeners() {
    dragSource.addListener( DND.DragSetData, new Listener() {
      public void handleEvent( Event event ) {
        event.data = "some html";
      }
    } );
    JsonObject properties = new JsonObject()
      .add( "x", 10 )
      .add( "y", 20 )
      .add( "time", 3 )
      .add( "operation", "move" )
      .add( "dataType", getHTMLTransferDataType() )
      .add( "item", getId( item ) )
      .add( "source", getId( sourceControl ) );

    handler.handleNotify( "DropAccept", properties );

    ArgumentCaptor<DNDEvent> captor = ArgumentCaptor.forClass( DNDEvent.class );
    verify( dropTarget ).notifyListeners( eq( DND.Drop ), captor.capture() );
    DNDEvent event = captor.getValue();
    assertEquals( 10, event.x );
    assertEquals( 20, event.y );
    assertEquals( DND.DROP_MOVE, event.detail );
    assertEquals( DND.DROP_MOVE | DND.DROP_COPY, event.operations );
    assertSame( item, event.item );
    assertEquals( getHTMLTransferDataType(), event.dataType.type );
    assertEquals( "some html", event.data );
  }

  @Test
  public void testHandleNotifyDragAccept_setsDetailField() {
    dragSource.addListener( DND.DragSetData, new Listener() {
      public void handleEvent( Event event ) {
        event.data = "some html";
      }
    } );
    dropTarget.addListener( DND.Drop, new Listener() {
      public void handleEvent( Event event ) {
        event.detail = 123;
      }
    } );
    JsonObject properties = new JsonObject()
      .add( "x", 10 )
      .add( "y", 20 )
      .add( "time", 3 )
      .add( "operation", "move" )
      .add( "dataType", getHTMLTransferDataType() )
      .add( "item", getId( item ) )
      .add( "source", getId( sourceControl ) );

    handler.handleNotify( "DropAccept", properties );

    assertEquals( 123, getDetailChangedValue() );
  }

  @Test
  public void testHandleNotifyDragAccept_filesPropertySetsClientFileTransfer() {
    JsonObject properties = createFileDropProperties();

    handler.handleNotify( "DropAccept", properties );

    ArgumentCaptor<DNDEvent> captor = ArgumentCaptor.forClass( DNDEvent.class );
    verify( dropTarget ).notifyListeners( eq( DND.DropAccept ), captor.capture() );
    assertTrue( ClientFileTransfer.getInstance().isSupportedType( captor.getValue().dataType ) );
  }

  @Test
  public void testHandleNotifyDragAccept_filesPropertyPreventsDragLeave() {
    JsonObject properties = createFileDropProperties();

    handler.handleNotify( "DropAccept", properties );

    verify( dropTarget, never() ).notifyListeners( eq( DND.DragLeave ), any( DNDEvent.class ) );
  }

  @Test
  public void testHandleNotifyDragAccept_filesPropertyIsConvertedToClientFiles() {
    dropTarget.setTransfer( new Transfer[]{ ClientFileTransfer.getInstance() } );
    JsonObject properties = createFileDropProperties();

    handler.handleNotify( "DropAccept", properties );

    ArgumentCaptor<DNDEvent> captor = ArgumentCaptor.forClass( DNDEvent.class );
    verify( dropTarget ).notifyListeners( eq( DND.Drop ), captor.capture() );
    ClientFileImpl[] remoteFiles = ( ClientFileImpl[] )captor.getValue().data;
    assertEquals( 2, remoteFiles.length );
    assertEquals( "f1", remoteFiles[ 0 ].getFileId() );
    assertEquals( "foo.txt", remoteFiles[ 0 ].getName() );
    assertEquals( "text/plain", remoteFiles[ 0 ].getType() );
    assertEquals( 9000, remoteFiles[ 0 ].getSize() );
    assertEquals( "f2", remoteFiles[ 1 ].getFileId() );
    assertEquals( "bar.html", remoteFiles[ 1 ].getName() );
    assertEquals( "text/html", remoteFiles[ 1 ].getType() );
    assertEquals( 100000000000L, remoteFiles[ 1 ].getSize() );
  }

  private JsonObject createFileDropProperties() {
    JsonObject file1 = new JsonObject()
      .add( "name",  "foo.txt" )
      .add( "type",  "text/plain" )
      .add( "size",  9000 );
    JsonObject file2 = new JsonObject()
      .add( "name",  "bar.html" )
      .add( "type",  "text/html" )
      .add( "size",  100000000000L );
    return new JsonObject()
      .add( "x", 10 )
      .add( "y", 20 )
      .add( "time", 3 )
      .add( "operation", "move" )
      .add( "files", new JsonObject().add( "f1", file1 ).add( "f2", file2 ) );
  }

  @Test
  public void testDetermineDataType() {
    dropTarget.setTransfer( new Transfer[]{
      RTFTransfer.getInstance(),
      HTMLTransfer.getInstance()
    } );

    TransferData[] dataTypes = determineDataTypes( dragSource, dropTarget );

    assertEquals( 1, dataTypes.length );
    assertTrue( HTMLTransfer.getInstance().isSupportedType( dataTypes[ 0 ] ) );
  }

  @Test
  public void testDetermineDataType_withoutDragSource() {
    dropTarget.setTransfer( new Transfer[]{
      RTFTransfer.getInstance(),
      HTMLTransfer.getInstance()
    } );

    TransferData[] dataTypes = determineDataTypes( null, dropTarget );

    assertEquals( 2, dataTypes.length );
    assertTrue( RTFTransfer.getInstance().isSupportedType( dataTypes[ 0 ] ) );
    assertTrue( HTMLTransfer.getInstance().isSupportedType( dataTypes[ 1 ] ) );
  }

  private static int getHTMLTransferDataType() {
    return "html".hashCode();
  }

  private static int getTextTransferDataType() {
    return "text".hashCode();
  }

}
