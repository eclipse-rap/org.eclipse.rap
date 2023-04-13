/*******************************************************************************
 * Copyright (c) 2002, 2021 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 *    Frank Appel - replaced singletons and static fields (Bug 337787)
 ******************************************************************************/
package org.eclipse.swt.widgets;

import static org.eclipse.rap.rwt.internal.protocol.JsonUtil.createJsonArray;
import static org.eclipse.rap.rwt.testfixture.internal.ConcurrencyTestUtil.runInThread;
import static org.eclipse.rap.rwt.testfixture.internal.SerializationTestUtil.serializeAndDeserialize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.application.EntryPoint;
import org.eclipse.rap.rwt.internal.lifecycle.IUIThreadHolder;
import org.eclipse.rap.rwt.internal.lifecycle.LifeCycleUtil;
import org.eclipse.rap.rwt.internal.lifecycle.PhaseId;
import org.eclipse.rap.rwt.internal.lifecycle.RemoteAdapter;
import org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.IDisplayAdapter;
import org.eclipse.swt.layout.FillLayout;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;


public class Display_Test {

  private Display display;

  @Before
  public void setUp() {
    Fixture.setUp();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    display = new Display();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testGetAdapter_forDisplayAdapter() {
    Object adapter = display.getAdapter( IDisplayAdapter.class );

    assertTrue( adapter instanceof IDisplayAdapter );
  }

  @Test
  public void testGetAdapter_forRemoteAdapter() {
    Object adapter = display.getAdapter( RemoteAdapter.class );

    assertTrue( adapter instanceof RemoteAdapter );
  }

  @Test
  public void testGetAdapter_forRemoteAdapter_returnsSameInstance() {
    Object adapter1 = display.getAdapter( RemoteAdapter.class );
    Object adapter2 = display.getAdapter( RemoteAdapter.class );

    assertSame( adapter1, adapter2 );
  }

  @Test
  public void testGetAdapter_forRemoteAdapter_returnsDifferentInstanceForNewDisplay() {
    RemoteAdapter adapter1 = display.getAdapter( RemoteAdapter.class );
    display.dispose();
    display = new Display();

    RemoteAdapter adapter2 = display.getAdapter( RemoteAdapter.class );

    assertNotSame( adapter1, adapter2 );
  }

  @Test
  public void testCreation_preventsMultipleDisplays() {
    try {
      new Display();
      fail();
    } catch( SWTError error ) {
      assertEquals( SWT.ERROR_NOT_IMPLEMENTED, error.code );
    }
  }

  @Test
  public void testCreation_createsAnotherDisplayAfterDispose() {
    display.dispose();

    Device secondDisplay = new Display();
    assertEquals( Display.getCurrent(), secondDisplay );
  }

  @Test
  public void testGetCurrent_returnsNullWithoutDisplay() {
    display.dispose();

    assertNull( Display.getCurrent() );
  }

  @Test
  public void testGetCurrent_returnsCreatedDisplay() {
    assertSame( Display.getCurrent(), display );
  }

  @Test
  public void testGetCurrent_returnsNullOnBackgroundThread() throws Throwable {
    final AtomicReference<Display> resultCaptor = new AtomicReference<Display>( display );

    runInThread( new Runnable() {
      @Override
      public void run() {
        resultCaptor.set( Display.getCurrent() );
      }
    } );

    assertNull( resultCaptor.get() );
  }

  @Test
  public void testGetCurrent_returnsNullOnBackgroundThreadWithContext() throws Throwable {
    final AtomicReference<Display> resultCaptor = new AtomicReference<Display>( display );

    runInThread( new Runnable() {
      @Override
      public void run() {
        RWT.getUISession( display ).exec( new Runnable() {
          @Override
          public void run() {
            resultCaptor.set( Display.getCurrent() );
          }
        } );
      }
    } );

    assertNull( resultCaptor.get() );
  }

  @Test
  public void testGetDefault_fromUIThread() {
    IUIThreadHolder uiThreadHolder = mock( IUIThreadHolder.class );
    when( uiThreadHolder.getThread() ).thenReturn( Thread.currentThread() );
    LifeCycleUtil.setUIThread( RWT.getUISession(), uiThreadHolder );

    Display display = Display.getDefault();

    assertNotNull( display );
    assertSame( display, Display.getDefault() );
  }

  @Test
  public void testGetDefault_withTerminatedUIThread() {
    IUIThreadHolder uiThreadHolder = mock( IUIThreadHolder.class );
    when( uiThreadHolder.getThread() ).thenReturn( Thread.currentThread() );
    LifeCycleUtil.setUIThread( RWT.getUISession(), uiThreadHolder );

    Display display = Display.getDefault();

    assertNotNull( display );
  }

  @Test
  public void testGetDefault_withExistingDisplayFromUIThread() {
    assertSame( display, Display.getDefault() );
  }

  @Test
  public void testGetDefault_fromBackgroundThreadWithoutContext() throws Throwable {
    final AtomicReference<Display> resultCaptor = new AtomicReference<Display>();

    Runnable runnable = new Runnable() {
      @Override
      public void run() {
        resultCaptor.set( Display.getDefault() );
      }
    };

    runInThread( runnable );

    assertNull( resultCaptor.get() );
  }

  @Test
  public void testGetDefault_fromBackgroundThreadDoesNotCreateDisplay() throws Throwable {
    final Display[] backgroundDisplay = { null };
    Runnable runnable = new Runnable() {
      @Override
      public void run() {
        backgroundDisplay[ 0 ] = Display.getDefault();
      }
    };

    runInThread( runnable );

    assertNull( backgroundDisplay[ 0 ] ) ;
  }

  @Test
  public void testGetDefault_fromBackgroundThreadWithContext() throws Throwable {
    final Display[] backgroundDisplay = { null };
    Runnable runnable = new Runnable() {
      @Override
      public void run() {
        RWT.getUISession( display ).exec( new Runnable() {
          @Override
          public void run() {
            backgroundDisplay[ 0 ] = Display.getDefault();
          }
        } );
      }
    };

    runInThread( runnable );

    assertSame( display, backgroundDisplay[ 0 ] );
  }

  @Test
  public void testGetDefault_withDisposedDisplay() {
    IUIThreadHolder uiThreadHolder = mock( IUIThreadHolder.class );
    when( uiThreadHolder.getThread() ).thenReturn( Thread.currentThread() );
    LifeCycleUtil.setUIThread( RWT.getUISession(), uiThreadHolder );
    display.dispose();

    Display newDisplay = Display.getDefault();
    assertNotNull( newDisplay );
    assertNotSame( display, newDisplay );
  }

  @Test
  public void testGetThread_fromUIThread() {
    Thread thread = display.getThread();

    assertSame( Thread.currentThread(), thread );
  }

  @Test
  public void testGetThread_fromBackgroundThread() throws Throwable {
    final AtomicReference<Thread> resultCaptor = new AtomicReference<Thread>();

    runInThread( new Runnable() {
      @Override
      public void run() {
        resultCaptor.set( display.getThread() );
      }
    } );

    assertSame( Thread.currentThread(), resultCaptor.get() );
  }

  @Test
  public void testDetachThread() {
    IDisplayAdapter adapter = display.getAdapter( IDisplayAdapter.class );

    adapter.detachThread();

    assertNull( display.getThread() );
  }

  @Test
  public void testAttachThread() {
    IDisplayAdapter adapter = display.getAdapter( IDisplayAdapter.class );
    adapter.detachThread();

    adapter.attachThread();

    assertSame( Thread.currentThread(), display.getThread() );
  }

  @Test
  public void testGetShells() {
    assertEquals( 0, display.getShells().length );
    Composite shell1 = new Shell( display , SWT.NONE );
    assertSame( shell1, display.getShells()[ 0 ] );
    Composite shell2 = new Shell( display , SWT.NONE );
    Composite[] shells = display.getShells();
    assertTrue( shell2 == shells[ 0 ] || shell2 == display.getShells()[ 1 ] );
  }

  @Test
  public void testProperties() {
    assertEquals( 0, display.getShells().length );
    Rectangle bounds = display.getBounds();
    assertNotNull( bounds );
    bounds.x += 1;
    assertTrue( bounds.x != display.getBounds().x );
  }

  @Test
  public void testBounds() {
    Object adapter = display.getAdapter( IDisplayAdapter.class );
    IDisplayAdapter displayAdapter = ( IDisplayAdapter )adapter;
    Rectangle expectedBounds = new Rectangle( 0, 10, 60, 99 );
    displayAdapter.setBounds( expectedBounds );
    Rectangle bounds = display.getBounds();
    assertEquals( expectedBounds, bounds );
    assertNotSame( expectedBounds, bounds );
  }

  @Test
  public void testClientArea() {
    Object adapter = display.getAdapter( IDisplayAdapter.class );
    IDisplayAdapter displayAdapter = ( IDisplayAdapter )adapter;
    Rectangle testRect = new Rectangle( 1, 2, 3, 4 );
    displayAdapter.setBounds( testRect );
    Rectangle clientArea = display.getClientArea();
    assertEquals( testRect, clientArea );
    assertNotSame( testRect, clientArea );
  }

  @Test
  public void testMap() {
    Shell shell = new Shell( display, SWT.NONE );
    Rectangle shellBounds = new Rectangle( 10, 10, 400, 400 );
    int shellBorder = shell.getBorderWidth();
    shell.setBounds( shellBounds );

    Rectangle actual = display.map( shell, shell, 1, 2, 3, 4 );
    Rectangle expected = new Rectangle( 1, 2, 3, 4 );
    assertEquals( expected, actual );

    actual = display.map( shell, null, 5, 6, 7, 8 );
    expected = new Rectangle( shellBounds.x + shellBorder + 5,
                              shellBounds.y + shellBorder + 6,
                              7,
                              8 );
    assertEquals( expected, actual );

    shell.setLayout( new FillLayout() );
    TabFolder folder = new TabFolder( shell, SWT.BORDER );
    int folderBorder = folder.getBorderWidth();
    shell.layout();
    actual = display.map( folder, shell, 6, 7, 8, 9 );
    expected = new Rectangle( folder.getBounds().x + folderBorder + 6,
                              folder.getBounds().y + folderBorder + 7,
                              8,
                              9 );
    assertEquals( expected, actual );

    int borders = shellBorder + folderBorder;
    actual = display.map( null, folder, 1, 2, 3, 4 );
    expected = new Rectangle( 1 - shell.getBounds().x - borders,
                              2 - shell.getBounds().y - borders,
                              3,
                              4 );
    assertEquals( expected, actual );
  }

  @Test
  public void testMap_withChildShell() {
    Shell shell = new Shell( display, SWT.NONE );
    shell.setBounds( 100, 100, 800, 600 );
    Shell childShell1 = new Shell( shell, SWT.NONE );
    childShell1.setBounds( 200, 200, 800, 600 );
    int childShell1Border = childShell1.getBorderWidth();
    Point expected = new Point( 200 + childShell1Border,
                                200 + childShell1Border );
    Point actual = display.map( childShell1, null, 0, 0 );
    assertEquals( expected, actual );
    expected = new Point( 100, 100 );
    actual = display.map( childShell1, shell, 0, 0 );
    assertEquals( expected, actual );

    Shell childShell2 = new Shell( shell, SWT.NONE );
    childShell2.setBounds( 200, 200, 800, 600 );
    expected = new Point( 14, 17 );
    actual = display.map( childShell1, childShell2, 14, 17 );
    assertEquals( expected, actual );
  }

  @Test
  public void testMap_withDifferentBorders() {
    Shell shell = new Shell( display, SWT.NONE );
    shell.setBounds( 100, 100, 800, 600 );
    Composite comp1 = new Composite( shell, SWT.BORDER );
    comp1.setBounds( 0, 0, 20, 20 );
    int comp1Offset = comp1.getBorderWidth();
    // Test with scrollable
    Composite comp2 = new Composite( shell, SWT.NONE );
    comp2.setBounds( 10, 10, 20, 20 );
    int comp2Offset = comp2.getBorderWidth();
    Rectangle actual = display.map( comp2, comp1, 1, 2, 3, 4 );
    Rectangle expected = new Rectangle( 1 + 10 + comp2Offset - comp1Offset,
                                        2 + 10 + comp2Offset - comp1Offset,
                                        3,
                                        4 );
    assertEquals( expected, actual );
    // Test with control
    Button button = new Button( shell, SWT.NONE );
    button.setBounds( 10, 10, 20, 20 );
    actual = display.map( button, comp1, 1, 2, 3, 4 );
    expected = new Rectangle( 1 + 10 - comp1Offset,
                              2 + 10 - comp1Offset,
                              3,
                              4 );
    assertEquals( expected, actual );
    // Test with CTabFolder
    CTabFolder folder = new CTabFolder( shell, SWT.CLOSE );
    CTabItem item = new CTabItem( folder, SWT.NONE );
    item.setText( "Item" );
    folder.setBounds( 10, 10, 100, 100 );
    actual = display.map( folder, comp1, 1, 2, 3, 4 );
    expected = new Rectangle( 1 + 10 - comp1Offset,
                              2 + 10 - comp1Offset,
                              3,
                              4 );
    assertEquals( expected, actual );
  }

  @Test
  public void testMap_displayToRTLComposite() {
    Shell shell = new Shell( display, SWT.NO_TRIM );
    shell.setBounds( 0, 0, 1000, 1000 );
    Composite composite = new Composite( shell, SWT.RIGHT_TO_LEFT );
    composite.setBounds( 10, 10, 200, 200 );

    Rectangle actual = display.map( null, composite, 30, 30, 10, 10 );

    assertEquals( new Rectangle( 170, 20, 10, 10 ), actual );
  }

  @Test
  public void testMap_rtlCompositeToDisplay() {
    Shell shell = new Shell( display, SWT.NO_TRIM );
    shell.setBounds( 0, 0, 1000, 1000 );
    Composite composite = new Composite( shell, SWT.RIGHT_TO_LEFT );
    composite.setBounds( 10, 10, 200, 200 );

    Rectangle actual = display.map( composite, null, 30, 30, 10, 10 );

    assertEquals( new Rectangle( 170, 40, 10, 10 ), actual );
  }

  @Test
  public void testMap_ltrCompositeToRTLComposite() {
    Shell shell = new Shell( display, SWT.NO_TRIM );
    shell.setBounds( 0, 0, 1000, 1000 );
    Composite composite1 = new Composite( shell, SWT.LEFT_TO_RIGHT );
    composite1.setBounds( 10, 10, 200, 200 );
    Composite composite2 = new Composite( composite1, SWT.RIGHT_TO_LEFT );
    composite2.setBounds( 5, 5, 100, 100 );

    Rectangle actual = display.map( composite1, composite2, 30, 30, 10, 10 );

    assertEquals( new Rectangle( 65, 25, 10, 10 ), actual );
  }

  @Test
  public void testMap_rtlCompositeToLTRComposite() {
    Shell shell = new Shell( display, SWT.NO_TRIM );
    shell.setBounds( 0, 0, 1000, 1000 );
    Composite composite1 = new Composite( shell, SWT.RIGHT_TO_LEFT );
    composite1.setBounds( 10, 10, 200, 200 );
    Composite composite2 = new Composite( composite1, SWT.LEFT_TO_RIGHT );
    composite2.setBounds( 5, 5, 100, 100 );

    Rectangle actual = display.map( composite2, composite1, 30, 30, 10, 10 );

    assertEquals( new Rectangle( 65, 35, 10, 10 ), actual );
  }

  @Test
  public void testMap_rtlCompositeToRTLComposite() {
    Shell shell = new Shell( display, SWT.NO_TRIM );
    shell.setBounds( 0, 0, 1000, 1000 );
    Composite composite1 = new Composite( shell, SWT.RIGHT_TO_LEFT );
    composite1.setBounds( 10, 10, 200, 200 );
    Composite composite2 = new Composite( composite1, SWT.RIGHT_TO_LEFT );
    composite2.setBounds( 5, 5, 100, 100 );

    Rectangle actual = display.map( composite1, composite2, 30, 30, 10, 10 );

    assertEquals( new Rectangle( 25, 25, 10, 10 ), actual );
  }

  ////////////////////////////
  // SWT Tests for Display#map

  /*
   * Verbatim copy from SWT Test_org_eclipse_swt_widgets_Display
   */
  @Test
  public void test_mapLorg_eclipse_swt_widgets_ControlLorg_eclipse_swt_widgets_ControlII() {
    try {
      Shell shell = new Shell(display, SWT.NO_TRIM);
      Button button1 = new Button(shell, SWT.PUSH);
      button1.setBounds(0,0,100,100);
      Button button2 = new Button(shell, SWT.PUSH);
      button2.setBounds(200,100,100,100);
      shell.setBounds(0,0,400,400);
      shell.open();

      Point shellOffset = shell.getLocation();
      Point result;

      result = display.map(button1, button2, 0, 0);
      assertEquals(new Point(-200,-100), result);
      result = display.map(button1, button2, -10, -20);
      assertEquals(new Point(-210,-120), result);
      result = display.map(button1, button2, 30, 40);
      assertEquals(new Point(-170,-60), result);

      result = display.map(button2, button1, 0, 0);
      assertEquals(new Point(200,100), result);
      result = display.map(button2, button1, -5, -15);
      assertEquals(new Point(195,85), result);
      result = display.map(button2, button1, 25, 35);
      assertEquals(new Point(225,135), result);

      result = display.map(null, button2, 0, 0);
      assertEquals(new Point(-200 - shellOffset.x,-100 - shellOffset.y), result);
      result = display.map(null, button2, -2, -4);
      assertEquals(new Point(-202 - shellOffset.x,-104 - shellOffset.y), result);
      result = display.map(null, button2, 6, 8);
      assertEquals(new Point(-194 - shellOffset.x,-92 - shellOffset.y), result);

      result = display.map(button2, null, 0, 0);
      assertEquals(new Point(shellOffset.x + 200,shellOffset.y + 100), result);
      result = display.map(button2, null, -3, -6);
      assertEquals(new Point(shellOffset.x + 197,shellOffset.y + 94), result);
      result = display.map(button2, null, 9, 12);
      assertEquals(new Point(shellOffset.x + 209,shellOffset.y + 112), result);

//      button1.dispose();
//      try {
//        result = display.map(button1, button2, 0, 0);
//        fail("No exception thrown for map from control being disposed");
//      } catch (IllegalArgumentException e) {
//        assertEquals("Incorrect exception thrown for map from control being disposed", SWT.ERROR_INVALID_ARGUMENT, e);
//      }
//      try {
//        result = display.map(button2, button1, 0, 0);
//        fail("No exception thrown for map to control being disposed");
//      } catch (IllegalArgumentException e) {
//        assertEquals("Incorrect exception thrown for map to control being disposed", SWT.ERROR_INVALID_ARGUMENT, e);
//      }

      shell.dispose();
    } finally {
      display.dispose();
    }
  }

  /*
   * Verbatim copy from SWT Test_org_eclipse_swt_widgets_Display
   */
  @Test
  public void test_mapLorg_eclipse_swt_widgets_ControlLorg_eclipse_swt_widgets_ControlIIII() {
    try {
      Shell shell = new Shell(display, SWT.NO_TRIM);
      Button button1 = new Button(shell, SWT.PUSH);
      button1.setBounds(0,0,100,100);
      Button button2 = new Button(shell, SWT.PUSH);
      button2.setBounds(200,100,100,100);
      shell.setBounds(0,0,400,400);
      shell.open();

      Point shellOffset = shell.getLocation();
      Rectangle result;

      result = display.map(button1, button2, 0, 0, 100, 100);
      assertEquals(new Rectangle(-200,-100,100,100), result);
      result = display.map(button1, button2, -10, -20, 130, 140);
      assertEquals(new Rectangle(-210,-120,130,140), result);
      result = display.map(button1, button2, 50, 60, 170, 180);
      assertEquals(new Rectangle(-150,-40,170,180), result);

      result = display.map(button2, button1, 0, 0, 100, 100);
      assertEquals(new Rectangle(200,100,100,100), result);
      result = display.map(button2, button1, -5, -15, 125, 135);
      assertEquals(new Rectangle(195,85,125,135), result);
      result = display.map(button2, button1, 45, 55, 165, 175);
      assertEquals(new Rectangle(245,155,165,175), result);

      result = display.map(null, button2, 0, 0, 100, 100);
      assertEquals(new Rectangle(-200 - shellOffset.x,-100 - shellOffset.y,100,100), result);
      result = display.map(null, button2, -2, -4, 106, 108);
      assertEquals(new Rectangle(-202 - shellOffset.x,-104 - shellOffset.y,106,108), result);
      result = display.map(null, button2, 10, 12, 114, 116);
      assertEquals(new Rectangle(-190 - shellOffset.x,-88 - shellOffset.y,114,116), result);

      result = display.map(button2, null, 0, 0, 100, 100);
      assertEquals(new Rectangle(shellOffset.x + 200,shellOffset.y + 100,100,100), result);
      result = display.map(button2, null, -3, -6, 109, 112);
      assertEquals(new Rectangle(shellOffset.x + 197,shellOffset.y + 94,109,112), result);
      result = display.map(button2, null, 15, 18, 121, 124);
      assertEquals(new Rectangle(shellOffset.x + 215,shellOffset.y + 118,121,124), result);

//      button1.dispose();
//      try {
//        result = display.map(button1, button2, 0, 0, 100, 100);
//        fail("No exception thrown for map from control being disposed");
//      } catch (IllegalArgumentException e) {
//        assertEquals("Incorrect exception thrown for map from control being disposed", SWT.ERROR_INVALID_ARGUMENT, e);
//      }
//      try {
//        result = display.map(button2, button1, 0, 0, 100, 100);
//        fail("No exception thrown for map to control being disposed");
//      } catch (IllegalArgumentException e) {
//        assertEquals("Incorrect exception thrown for map to control being disposed", SWT.ERROR_INVALID_ARGUMENT, e);
//      }

      shell.dispose();
    } finally {
      display.dispose();
    }
  }

  /*
   * Verbatim copy from SWT Test_org_eclipse_swt_widgets_Display
   */
  @Test
  public void test_mapLorg_eclipse_swt_widgets_ControlLorg_eclipse_swt_widgets_ControlLorg_eclipse_swt_graphics_Point() {
    try {
      Shell shell = new Shell(display, SWT.NO_TRIM);
      Button button1 = new Button(shell, SWT.PUSH);
      button1.setBounds(0,0,100,100);
      Button button2 = new Button(shell, SWT.PUSH);
      button2.setBounds(200,100,100,100);
      shell.setBounds(0,0,400,400);
      shell.open();

      Point result;
      Point point = new Point(0,0);
      Point shellOffset = shell.getLocation();

      result = display.map(button1, button2, point);
      assertEquals(new Point(-200,-100), result);
      result = display.map(button1, button2, new Point(-10,-20));
      assertEquals(new Point(-210,-120), result);
      result = display.map(button1, button2, new Point(30,40));
      assertEquals(new Point(-170,-60), result);

      result = display.map(button2, button1, point);
      assertEquals(new Point(200,100), result);
      result = display.map(button2, button1, new Point(-5,-15));
      assertEquals(new Point(195,85), result);
      result = display.map(button2, button1, new Point(25,35));
      assertEquals(new Point(225,135), result);

      result = display.map(null, button2, point);
      assertEquals(new Point(-200 - shellOffset.x,-100 - shellOffset.y), result);
      result = display.map(null, button2, new Point(-2,-4));
      assertEquals(new Point(-202 - shellOffset.x,-104 - shellOffset.y), result);
      result = display.map(null, button2, new Point(6,8));
      assertEquals(new Point(-194 - shellOffset.x,-92 - shellOffset.y), result);

      result = display.map(button2, null, point);
      assertEquals(new Point(shellOffset.x + 200,shellOffset.y + 100), result);
      result = display.map(button2, null, new Point(-3,-6));
      assertEquals(new Point(shellOffset.x + 197,shellOffset.y + 94), result);
      result = display.map(button2, null, new Point(9,12));
      assertEquals(new Point(shellOffset.x + 209,shellOffset.y + 112), result);

//      button1.dispose();
//      try {
//        result = display.map(button1, button2, point);
//        fail("No exception thrown for map from control being disposed");
//      } catch (IllegalArgumentException e) {
//        assertEquals("Incorrect exception thrown for map from control being disposed", SWT.ERROR_INVALID_ARGUMENT, e);
//      }
//      try {
//        result = display.map(button2, button1, point);
//        fail("No exception thrown for map to control being disposed");
//      } catch (IllegalArgumentException e) {
//        assertEquals("Incorrect exception thrown for map to control being disposed", SWT.ERROR_INVALID_ARGUMENT, e);
//      }
//
//      try {
//        result = display.map(button2, button1, (Point) null);
//        fail("No exception thrown for null point");
//      } catch (IllegalArgumentException e) {
//        assertEquals("Incorrect exception thrown for point being null", SWT.ERROR_NULL_ARGUMENT, e);
//      }

      shell.dispose();
    } finally {
      display.dispose();
    }
  }

  /*
   * Verbatim copy from SWT Test_org_eclipse_swt_widgets_Display
   */
  @Test
  public void test_mapLorg_eclipse_swt_widgets_ControlLorg_eclipse_swt_widgets_ControlLorg_eclipse_swt_graphics_Rectangle() {
    try {
      Shell shell = new Shell(display, SWT.NO_TRIM);
      Button button1 = new Button(shell, SWT.PUSH);
      button1.setBounds(0,0,100,100);
      Button button2 = new Button(shell, SWT.PUSH);
      button2.setBounds(200,100,100,100);
      shell.setBounds(0,0,400,400);
      shell.open();

      Rectangle result;
      Rectangle rect = new Rectangle(0,0,100,100);
      Point shellOffset = shell.getLocation();

      result = display.map(button1, button2, rect);
      assertEquals(new Rectangle(-200,-100,100,100), result);
      result = display.map(button1, button2, new Rectangle(-10, -20, 130, 140));
      assertEquals(new Rectangle(-210,-120,130,140), result);
      result = display.map(button1, button2, new Rectangle(50, 60, 170, 180));
      assertEquals(new Rectangle(-150,-40,170,180), result);

      result = display.map(button2, button1, rect);
      assertEquals(new Rectangle(200,100,100,100), result);
      result = display.map(button2, button1, new Rectangle(-5, -15, 125, 135));
      assertEquals(new Rectangle(195,85,125,135), result);
      result = display.map(button2, button1, new Rectangle(45, 55, 165, 175));
      assertEquals(new Rectangle(245,155,165,175), result);

      result = display.map(null, button2, rect);
      assertEquals(new Rectangle(-200 - shellOffset.x,-100 - shellOffset.y,100,100), result);
      result = display.map(null, button2, new Rectangle(-2, -4, 106, 108));
      assertEquals(new Rectangle(-202 - shellOffset.x,-104 - shellOffset.y,106,108), result);
      result = display.map(null, button2, new Rectangle(10, 12, 114, 116));
      assertEquals(new Rectangle(-190 - shellOffset.x,-88 - shellOffset.y,114,116), result);

      result = display.map(button2, null, rect);
      assertEquals(new Rectangle(shellOffset.x + 200,shellOffset.y + 100,100,100), result);
      result = display.map(button2, null, new Rectangle(-3, -6, 109, 112));
      assertEquals(new Rectangle(shellOffset.x + 197,shellOffset.y + 94,109,112), result);
      result = display.map(button2, null, new Rectangle(15, 18, 121, 124));
      assertEquals(new Rectangle(shellOffset.x + 215,shellOffset.y + 118,121,124), result);


//      button1.dispose();
//      try {
//        result = display.map(button1, button2, rect);
//        fail("No exception thrown for map from control being disposed");
//      } catch (IllegalArgumentException e) {
//        assertEquals("Incorrect exception thrown for map from control being disposed", SWT.ERROR_INVALID_ARGUMENT, e);
//      }
//      try {
//        result = display.map(button2, button1, rect);
//        fail("No exception thrown for map to control being disposed");
//      } catch (IllegalArgumentException e) {
//        assertEquals("Incorrect exception thrown for map to control being disposed", SWT.ERROR_INVALID_ARGUMENT, e);
//      }
//
//      try {
//        result = display.map(button2, button1, (Rectangle) null);
//        fail("No exception thrown for null point");
//      } catch (IllegalArgumentException e) {
//        assertEquals("Incorrect exception thrown for rectangle being null", SWT.ERROR_NULL_ARGUMENT, e);
//      }

      shell.dispose();
    } finally {
      display.dispose();
    }
  }

  @Test
  public void testActiveShell() {
    assertNull( display.getActiveShell() );
    Shell shell1 = new Shell( display, SWT.NONE );
    assertNull( display.getActiveShell() );
    shell1.open();
    assertSame( shell1, display.getActiveShell() );
    Shell shell2 = new Shell( display, SWT.NONE );
    shell2.open();
    assertSame( shell2, display.getActiveShell() );
    shell2.dispose();
    assertSame( shell1, display.getActiveShell() );

    // Test disposing of inactive shell
    Shell inactiveShell = new Shell( display, SWT.NONE );
    Shell activeShell = new Shell( display, SWT.NONE );
    inactiveShell.open();
    activeShell.open();
    assertSame( activeShell, display.getActiveShell() );
    inactiveShell.dispose();
    assertSame( activeShell, display.getActiveShell() );

    // Test explicitly setting the active shell
    Shell shell3 = new Shell( display, SWT.NONE );
    Shell shell4 = new Shell( display, SWT.NONE );
    shell3.open();
    shell4.open();
    assertSame( shell4, display.getActiveShell() );
    shell3.setActive();
    assertSame( shell3, display.getActiveShell() );
  }

  @Test
  public void testGetFontList_returnsEmptyListForScalable() {
    FontData[] result = display.getFontList( null, false );

    assertEquals( 0, result.length );
  }

  @Test
  public void testGetFontList_returnsNonEmptyListForNonScalable() {
    FontData[] result = display.getFontList( null, true );

    assertTrue( result.length > 0 );
  }

  @Test
  public void testGetFontList() {
    FontData[] fontList = display.getFontList( null, true );
    String firstFontName = fontList[ 0 ].getName();

    FontData[] result = display.getFontList( firstFontName, true );

    assertEquals( 1, result.length );
    assertEquals( fontList[ 0 ], result[ 0 ] );
  }

  @Test
  public void testGetFontList_returnsEmptyListForUnknownName() {
    assertEquals( 0, display.getFontList( "not existing font", true ).length );
  }

  @Test
  public void testGetSystemFont() {
    assertNotNull( display.getSystemFont() );
  }

  @Test
  public void testGetSystemImage_returns32x32Images() {
    Rectangle expected = new Rectangle( 0, 0, 32, 32 );

    Image errorImage = display.getSystemImage( SWT.ICON_ERROR );
    Image infoImage = display.getSystemImage( SWT.ICON_INFORMATION );
    Image questionImage = display.getSystemImage( SWT.ICON_QUESTION );
    Image warningImage = display.getSystemImage( SWT.ICON_WARNING );
    Image workImage = display.getSystemImage( SWT.ICON_WORKING );

    assertEquals( expected, errorImage.getBounds() );
    assertEquals( expected, infoImage.getBounds() );
    assertEquals( expected, questionImage.getBounds() );
    assertEquals( expected, warningImage.getBounds() );
    assertEquals( expected, workImage.getBounds() );
  }

  @Test
  public void testGetSystemImage_returnsNullForInvalidId() {
    assertNull( display.getSystemImage( SWT.VERTICAL ) );
  }

  @Test
  public void testGetSystemImage_returnsSharedInstances() {
    Image errorImage = display.getSystemImage( SWT.ICON_ERROR );
    assertSame( errorImage, display.getSystemImage( SWT.ICON_ERROR ) );
    Image infoImage = display.getSystemImage( SWT.ICON_INFORMATION );
    assertSame( infoImage, display.getSystemImage( SWT.ICON_INFORMATION ) );
    assertNotSame( errorImage, infoImage );
    Image workImage = display.getSystemImage( SWT.ICON_WORKING );
    // same icon is used in default theme
    assertSame( infoImage, workImage );
  }

  @Test
  public void testGetSystemColor_returnsColorsFromDefaultTheme() {
    assertEquals( new RGB( 167, 166, 170 ),
                  display.getSystemColor( SWT.COLOR_WIDGET_NORMAL_SHADOW ).getRGB() );
    assertEquals( new RGB( 133, 135, 140 ),
                  display.getSystemColor( SWT.COLOR_WIDGET_DARK_SHADOW ).getRGB() );
    assertEquals( new RGB( 220, 223, 228 ),
                  display.getSystemColor( SWT.COLOR_WIDGET_LIGHT_SHADOW ).getRGB() );
    assertEquals( new RGB( 255, 255, 255 ),
                  display.getSystemColor( SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW ).getRGB() );
    assertEquals( new RGB( 172, 168, 153 ),
                  display.getSystemColor( SWT.COLOR_WIDGET_BORDER ).getRGB() );
    assertEquals( new RGB( 74, 74, 74 ),
                  display.getSystemColor( SWT.COLOR_WIDGET_FOREGROUND ).getRGB() );
    assertEquals( new RGB( 255, 255, 255 ),
                  display.getSystemColor( SWT.COLOR_WIDGET_BACKGROUND ).getRGB() );
    assertEquals( new RGB( 255, 255, 255 ),
                  display.getSystemColor( SWT.COLOR_INFO_BACKGROUND ).getRGB() );
    assertEquals( new RGB( 74, 74, 74 ),
                  display.getSystemColor( SWT.COLOR_INFO_FOREGROUND ).getRGB() );
    assertEquals( new RGB( 255, 255, 255 ),
                  display.getSystemColor( SWT.COLOR_LIST_BACKGROUND ).getRGB() );
    assertEquals( new RGB( 74, 74, 74 ),
                  display.getSystemColor( SWT.COLOR_LIST_FOREGROUND ).getRGB() );
    assertEquals( new RGB( 0, 88, 159 ),
                  display.getSystemColor( SWT.COLOR_LIST_SELECTION ).getRGB() );
    assertEquals( new RGB( 255, 255, 255 ),
                  display.getSystemColor( SWT.COLOR_LIST_SELECTION_TEXT ).getRGB() );
    assertEquals( new RGB( 0, 128, 192 ),
                  display.getSystemColor( SWT.COLOR_TITLE_BACKGROUND ).getRGB() );
    assertEquals( new RGB( 0, 128, 192 ),
                  display.getSystemColor( SWT.COLOR_TITLE_BACKGROUND_GRADIENT ).getRGB() );
    assertEquals( new RGB( 255, 255, 255 ),
                  display.getSystemColor( SWT.COLOR_TITLE_FOREGROUND ).getRGB() );
    assertEquals( new RGB( 121, 150, 165 ),
                  display.getSystemColor( SWT.COLOR_TITLE_INACTIVE_BACKGROUND ).getRGB() );
    assertEquals( new RGB( 121, 150, 165 ),
                  display.getSystemColor( SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT ).getRGB() );
    assertEquals( new RGB( 255, 255, 255 ),
                  display.getSystemColor( SWT.COLOR_TITLE_INACTIVE_FOREGROUND ).getRGB() );
  }

  @Test
  public void testGetSystemColor_returnsFixedColors() {
    assertEquals( new RGB( 0, 0, 0 ),
                  display.getSystemColor( SWT.COLOR_BLACK ).getRGB() );
    assertEquals( new RGB( 0, 0, 255 ),
                  display.getSystemColor( SWT.COLOR_BLUE ).getRGB() );
    assertEquals( new RGB( 0, 255, 255 ),
                  display.getSystemColor( SWT.COLOR_CYAN ).getRGB() );
    assertEquals( new RGB( 0, 0, 128 ),
                  display.getSystemColor( SWT.COLOR_DARK_BLUE ).getRGB() );
    assertEquals( new RGB( 0, 128, 128 ),
                  display.getSystemColor( SWT.COLOR_DARK_CYAN ).getRGB() );
    assertEquals( new RGB( 128, 128, 128 ),
                  display.getSystemColor( SWT.COLOR_DARK_GRAY ).getRGB() );
    assertEquals( new RGB( 0, 128, 0 ),
                  display.getSystemColor( SWT.COLOR_DARK_GREEN ).getRGB() );
    assertEquals( new RGB( 128, 0, 128 ),
                  display.getSystemColor( SWT.COLOR_DARK_MAGENTA ).getRGB() );
    assertEquals( new RGB( 128, 0, 0 ),
                  display.getSystemColor( SWT.COLOR_DARK_RED ).getRGB() );
    assertEquals( new RGB( 128, 128, 0 ),
                  display.getSystemColor( SWT.COLOR_DARK_YELLOW ).getRGB() );
    assertEquals( new RGB( 192, 192, 192 ),
                  display.getSystemColor( SWT.COLOR_GRAY ).getRGB() );
    assertEquals( new RGB( 0, 255, 0 ),
                  display.getSystemColor( SWT.COLOR_GREEN ).getRGB() );
    assertEquals( new RGB( 255, 0, 255 ),
                  display.getSystemColor( SWT.COLOR_MAGENTA ).getRGB() );
    assertEquals( new RGB( 255, 0, 0 ),
                  display.getSystemColor( SWT.COLOR_RED ).getRGB() );
    assertEquals( new RGB( 255, 255, 255 ),
                  display.getSystemColor( SWT.COLOR_WHITE ).getRGB() );
    assertEquals( new RGB( 255, 255, 0 ),
                  display.getSystemColor( SWT.COLOR_YELLOW ).getRGB() );
  }

  @Test
  public void testGetSystemColor_returnsSameInstance() {
    assertSame( display.getSystemColor( SWT.COLOR_RED ), display.getSystemColor( SWT.COLOR_RED ) );
  }

  @Test
  public void testAddFilter() {
    Shell shell = new Shell( display );
    Listener listener = mock( Listener.class );

    display.addFilter( SWT.Close, listener );
    shell.close();

    ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass( Event.class );
    verify( listener ).handleEvent( eventCaptor.capture() );
    Event event = eventCaptor.getValue();
    assertEquals( SWT.Close, event.type );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testAddFilter_failsWithNullArgument() {
    display.addFilter( SWT.Dispose, null );
  }

  @Test
  public void testRemoveFilter() {
    Shell shell = new Shell( display );
    Listener listener = mock( Listener.class );
    display.addFilter( SWT.Close, listener );

    display.removeFilter( SWT.Close, listener );
    shell.close();

    verify( listener, times( 0 ) ).handleEvent( any( Event.class ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testRemoveFilter_failsWithNullArgument() {
    display .removeFilter( SWT.Dispose, null );
  }

  @Test
  public void testRemoveFilter_toleratesUnknownListener() {
    display.removeFilter( SWT.Selection, mock( Listener.class ) );
  }

  @Test
  public void testGetData_returnsSetData() {
    Object object = new Object();
    display.setData( object );

    Object result = display.getData();

    assertSame( object, result );
  }

  @Test
  public void testGetData_returnsSetDataByKey() {
    Object value1 = new Object();
    Object value2 = new Object();
    display.setData( "key1", value1 );
    display.setData( "key2", value2 );

    Object result1 = display.getData( "key1" );
    Object result2 = display.getData( "key2" );

    assertSame( value1, result1 );
    assertSame( value2, result2 );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testGetData_failsWithNullKey() {
    display.getData( null );
  }

  @Test
  public void testSetData_withNullRemovesValue() {
    display.setData( "key", new Object() );

    display.setData( "key", null );
    Object result = display.getData( "key" );

    assertNull( result );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetData_failsNullKey() {
    display.setData( null, new Object() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testTimerExec_failsWithNullArgument() {
    display.timerExec( 0, null );
  }

  @Test
  public void testTimerExec_failsFromBackgroundThread() throws Throwable {
    try {
      runInThread( new Runnable() {
        @Override
        public void run() {
          display.timerExec( 1, mock( Runnable.class ) );
        }
      } );
      fail();
    } catch( SWTException expected ) {
      assertEquals( SWT.ERROR_THREAD_INVALID_ACCESS, expected.code );
    }
  }

  @Test
  public void testTimerExec_schedulesRunnable() {
    display.dispose();
    final TimerExecScheduler scheduler = mock( TimerExecScheduler.class );
    Display display = new Display() {
      @Override
      TimerExecScheduler createTimerExecScheduler() {
        return scheduler;
      }
    };
    Runnable runnable = mock( Runnable.class );
    display.timerExec( 23, runnable );

    verify( scheduler ).schedule( 23, runnable );
  }

  @Test
  public void testTimerExec_cancelsRunnableIfTimeIsNegative() {
    display.dispose();
    final TimerExecScheduler scheduler = mock( TimerExecScheduler.class );
    Display display = new Display() {
      @Override
      TimerExecScheduler createTimerExecScheduler() {
        return scheduler;
      }
    };
    Runnable runnable = mock( Runnable.class );
    display.timerExec( -1, runnable );

    verify( scheduler ).cancel( runnable );
  }

  @Test
  public void testTimerExec_cancelsRunnableIfExists() {
    display.dispose();
    final TimerExecScheduler scheduler = mock( TimerExecScheduler.class );
    Display display = new Display() {
      @Override
      TimerExecScheduler createTimerExecScheduler() {
        return scheduler;
      }
    };
    Runnable runnable = mock( Runnable.class );
    display.timerExec( 10, runnable );
    display.timerExec( 20, runnable );

    InOrder inOrder = inOrder( scheduler );
    inOrder.verify( scheduler ).cancel( runnable );
    inOrder.verify( scheduler ).schedule( 10, runnable );
    inOrder.verify( scheduler ).cancel( runnable );
    inOrder.verify( scheduler ).schedule( 20, runnable );
  }

  @Test
  public void testGetMonitors() {
    Monitor[] monitors = display.getMonitors();

    assertNotNull( monitors );
    assertEquals( 1, monitors.length );
    assertNotNull( monitors[ 0 ] );
    // Further monitor tests can be found in Monitor_Test
  }

  @Test
  public void testGetPrimaryMonitor() {
    Monitor monitor = display.getPrimaryMonitor();

    assertNotNull( monitor );
    // Further monitor tests can be found in Monitor_Test
  }

  @Test
  public void testDisposeExecWithNullArgument() {
    display.disposeExec( null );

    display.dispose();

    assertTrue( display.isDisposed() );
  }

  @Test
  public void testDispose() {
    assertFalse( display.isDisposed() );

    display.dispose();
    assertTrue( display.isDisposed() );
    assertNull( Display.getCurrent() );

    // Ensure that calling dispose() on a disposed of Display is allowed
    display.dispose();
    assertTrue( display.isDisposed() );
  }

  @Test
  public void testDisposeNotificationsOrder() {
    // 1. display dispose listener
    // 2. shell dispose listeners
    // 3. disposeRunnable(s)
    final List<Object> log = new ArrayList<Object>();
    Shell shell = new Shell( display );
    shell.addDisposeListener( new DisposeListener() {
      @Override
      public void widgetDisposed( DisposeEvent event ) {
        log.add( event );
      }
    } );
    display.addListener( SWT.Dispose, new Listener() {
      @Override
      public void handleEvent( Event event ) {
        log.add( event );
      }
    } );
    display.disposeExec( new Runnable() {
      @Override
      public void run() {
        log.add( "disposeRunnable" );
      }
    } );
    display.dispose();
    assertEquals( 3, log.size() );
    Event displayDisposeEvent = ( Event )log.get( 0 );
    assertSame( display, displayDisposeEvent.display );
    DisposeEvent shellDisposeEvent = ( DisposeEvent )log.get( 1 );
    assertSame( shell, shellDisposeEvent.widget );
    String disposeExecRunnable = ( String )log.get( 2 );
    assertEquals( "disposeRunnable", disposeExecRunnable );
    assertTrue( display.isDisposed() );
  }

  @Test
  public void testSystemCursor() {
    Cursor arrow = display.getSystemCursor( SWT.CURSOR_ARROW );
    Cursor cross = display.getSystemCursor( SWT.CURSOR_CROSS );
    assertNotNull( arrow );
    assertNotNull( cross );
    assertNotSame( arrow, cross );
    assertFalse( arrow.equals( cross ) );
    Cursor help1 = display.getSystemCursor( SWT.CURSOR_HELP );
    Cursor help2 = display.getSystemCursor( SWT.CURSOR_HELP );
    assertSame( help1, help2 );
  }

  @Test
  public void testCloseWithoutListeners() {
    display.close();

    assertTrue( display.isDisposed() );
  }

  @Test
  public void testCloseWithListener() {
    final List<Event> log = new ArrayList<Event>();
    display.addListener( SWT.Close, new Listener() {
      @Override
      public void handleEvent( Event event ) {
        log.add( event );
      }
    } );
    display.close();
    assertTrue( display.isDisposed() );
    assertEquals( 1, log.size() );
    Event event = log.get( 0 );
    assertSame( display, event.display );
  }

  @Test
  public void testCloseWithExceptionInListener() {
    final String exceptionMessage = "exception in close event";
    display.addListener( SWT.Close, new Listener() {
      @Override
      public void handleEvent( Event event ) {
        throw new RuntimeException( exceptionMessage );
      }
    } );
    try {
      display.close();
      fail( "Exception in close-listener must interrupt close operation" );
    } catch( RuntimeException exception ) {
      assertEquals( exceptionMessage, exception.getMessage() );
    }
  }

  @Test
  public void testGetDismissalAlignment() {
    assertEquals( SWT.LEFT, display.getDismissalAlignment() );
  }

  @Test
  public void testCloseWithVetoingListener() {
    display.addListener( SWT.Close, new Listener() {
      @Override
      public void handleEvent( Event event ) {
        event.doit = false;
      }
    } );
    display.close();
    assertFalse( display.isDisposed() );
  }

  @Test
  public void testCheckDevice() throws Throwable {
    try {
      runInThread( new Runnable() {
        @Override
        public void run() {
          // access some method that calls checkDevice()
          display.getShells();
        }
      } );
      fail();
    } catch( SWTException exception ) {
      assertEquals( SWT.ERROR_THREAD_INVALID_ACCESS, exception.code );
    }
  }

  @Test
  public void testFilterWithoutListener() {
    Listener filter = mock( Listener.class );
    display.addFilter( SWT.Resize, filter );
    Widget widget = new Shell( display );

    widget.notifyListeners( SWT.Resize, new Event() );

    verify( filter ).handleEvent( any( Event.class ) );
  }

  @Test
  public void testCloseEventFilter() {
    final StringBuilder order = new StringBuilder();
    final List<Event> events = new ArrayList<Event>();
    display.addFilter( SWT.Close, new Listener() {
      @Override
      public void handleEvent( Event event ) {
        events.add( event );
        order.append( "filter, " );
      }
    } );
    display.addListener( SWT.Close, new Listener() {
      @Override
      public void handleEvent( Event event ) {
        events.add( event );
        event.doit = false;
        order.append( "listener" );
      }
    } );

    display.close();

    assertEquals( "filter, listener", order.toString() );
    assertEquals( 2, events.size() );
    Event filterEvent = events.get( 0 );
    assertSame( display, filterEvent.display );
    assertEquals( SWT.Close, filterEvent.type );
    assertNull( filterEvent.widget );
    Event listenerEevent = events.get( 1 );
    assertSame( filterEvent, listenerEevent );
  }

  @Test
  public void testDisposeEventFilter() {
    final StringBuilder order = new StringBuilder();
    final List<Event> events = new ArrayList<Event>();
    display.addFilter( SWT.Dispose, new Listener() {
      @Override
      public void handleEvent( Event event ) {
        events.add( event );
        order.append( "filter, " );
      }
    } );
    display.addListener( SWT.Dispose, new Listener() {
      @Override
      public void handleEvent( Event event ) {
        events.add( event );
        order.append( "listener" );
      }
    } );
    display.dispose();
    assertEquals( "filter, listener", order.toString() );
    assertEquals( 2, events.size() );
    Event filterEvent = events.get( 0 );
    assertSame( display, filterEvent.display );
    assertEquals( SWT.Dispose, filterEvent.type );
    assertNull( filterEvent.widget );
    Event listenerEevent = events.get( 1 );
    assertSame( filterEvent, listenerEevent );
  }

  @Test
  public void testGetCursorControl_withNoControl() {
    setCursorLocation( display, 234, 345 );

    assertNull( display.getCursorControl() );
  }

  @Test
  public void testGetCursorControl_withVisibleControl() {
    setCursorLocation( display, 234, 345 );
    Control control = new Shell( display );
    control.setBounds( 100, 100, 500, 500 );
    control.setVisible( true );

    assertSame( control, display.getCursorControl() );
  }

  @Test
  public void testGetCursorControl_withNestedControl() {
    setCursorLocation( display, 234, 345 );
    Shell shell = new Shell( display );
    shell.setBounds( 100, 100, 500, 500 );
    shell.setVisible( true );
    Control control = new Composite( shell, SWT.NONE );
    control.setBounds( 0, 0, 500, 500 );

    assertSame( control, display.getCursorControl() );
  }

  @Test
  public void testGetCursorControl_withTwiceNestedControl() {
    setCursorLocation( display, 234, 345 );
    Shell shell = new Shell( display );
    shell.setBounds( 100, 100, 500, 500 );
    shell.setVisible( true );
    Composite composite = new Composite( shell, SWT.NONE );
    composite.setBounds( 0, 0, 500, 500 );
    Button button = new Button( composite, SWT.PUSH );
    button.setBounds( 130, 240, 10, 10 );

    assertSame( button, display.getCursorControl() );
  }

  @Test
  public void testGetCursorControl_withInvisibleNestedControl() {
    setCursorLocation( display, 234, 345 );
    Shell shell = new Shell( display );
    shell.setBounds( 100, 100, 500, 500 );
    shell.setVisible( true );
    Composite composite = new Composite( shell, SWT.NONE );
    composite.setBounds( 0, 0, 500, 500 );
    Button button = new Button( composite, SWT.PUSH );
    button.setBounds( 130, 240, 10, 10 );
    button.setVisible( false );

    assertSame( composite, display.getCursorControl() );
  }

  @Test
  public void testGetCursorControl_withOverlappingControls() {
    setCursorLocation( display, 234, 345 );
    Shell shell = new Shell( display );
    shell.setBounds( 100, 100, 500, 500 );
    shell.setVisible( true );
    Composite composite = new Composite( shell, SWT.NONE );
    composite.setBounds( 0, 0, 500, 500 );
    Button button = new Button( composite, SWT.PUSH );
    button.setBounds( 130, 240, 10, 10 );
    Button overlappingButton = new Button( composite, SWT.PUSH );
    overlappingButton.setBounds( 130, 240, 10, 10 );

    assertSame( button, display.getCursorControl() );
  }

  @Test
  public void testGetCursorControl_withOverlappingAndHiddenControls() {
    setCursorLocation( display, 234, 345 );
    Shell shell = new Shell( display );
    shell.setBounds( 100, 100, 500, 500 );
    shell.setVisible( true );
    Composite composite = new Composite( shell, SWT.NONE );
    composite.setBounds( 0, 0, 500, 500 );
    Button hiddenButton = new Button( composite, SWT.PUSH );
    hiddenButton.setBounds( 130, 240, 10, 10 );
    hiddenButton.setVisible( false );
    Button overlappingButton = new Button( composite, SWT.PUSH );
    overlappingButton.setBounds( 130, 240, 10, 10 );

    assertSame( overlappingButton, display.getCursorControl() );
  }

  @Test
  public void testGetCursorControl_withDisposedControl() {
    setCursorLocation( display, 234, 345 );
    Shell shell = new Shell( display );
    shell.setBounds( 100, 100, 500, 500 );
    shell.setVisible( true );
    shell.dispose();

    assertNull( display.getCursorControl() );
  }

  @Test
  public void testAppName() {
    Display.setAppName( "App name" );

    assertEquals( "App name", Display.getAppName() );
  }

  @Test
  public void testAppName_hasDefaultValue() {
    assertNull( Display.getAppName() );
  }

  @Test
  public void testAppName_acceptsNullArgument() {
    Display.setAppName( null );

    assertNull( Display.getAppName() );
  }

  @Test
  public void testAppVersion() {
    Display.setAppVersion( "v1.3" );

    assertEquals( "v1.3", Display.getAppVersion() );
  }

  @Test
  public void testAppVersion_hasDefaultValue() {
    assertNull( Display.getAppVersion() );
  }

  @Test
  public void testAppVersion_acceptsNullArgument() {
    Display.setAppVersion( null );
    assertNull( Display.getAppVersion() );
  }

  @Test
  public void testFindDisplay_returnsDisplayForUIThread() {
    assertSame( display, Display.findDisplay( display.getThread() ) );
  }

  @Test
  public void testFindDisplay_returnsNullForNullParameter() {
    assertNull( Display.findDisplay( null ) );
  }

  @Test
  public void testFindDisplay_returnsNullWhenDisplayIsDisposed() {
    Thread displayThread = display.getThread();
    display.dispose();

    assertNull( Display.findDisplay( displayThread ) );
  }

  @Test
  public void testFindDisplay_worksFromAnotherSession() throws Throwable {
    final Thread uiThread = Thread.currentThread();
    final AtomicReference<Display> resultCaptor = new AtomicReference<Display>();

    runInThread( new Runnable() {
      @Override
      public void run() {
        Fixture.createServiceContext();
        resultCaptor.set( Display.findDisplay( uiThread ) );
      }
    } );

    assertSame( display, resultCaptor.get() );
  }

  @Test
  public void testFindDisplay_forReCreatedDisplay() {
    display.dispose();
    Display reCreatedDisplay = new Display();

    assertSame( reCreatedDisplay, Display.findDisplay( reCreatedDisplay.getThread() ) );
  }

  @Test
  public void testGetSystemTray() {
    assertNull( display.getSystemTray() );
  }

  @Test
  public void testGetMenuBar() {
    assertNull( display.getMenuBar() );
  }

  @Test
  public void testGetSystemTaskBar() {
    assertNull( display.getSystemTaskBar() );
  }

  @Test
  public void testGetSystemMenu() {
    assertNull( display.getSystemMenu() );
  }

  @Test
  public void testAsyncExec_delegatesToSynchronizer() {
    Synchronizer synchronizer = mock( Synchronizer.class );
    display.setSynchronizer( synchronizer );
    Runnable runnable = mock( Runnable.class );

    display.asyncExec( runnable );

    verify( synchronizer ).asyncExec( same( runnable ) );
  }

  @Test
  public void testAsyncExec_failsWhenDisplayIsDisposed() {
    Synchronizer synchronizer = mock( Synchronizer.class );
    display.setSynchronizer( synchronizer );
    display.dispose();

    try {
      display.asyncExec( mock( Runnable.class ) );
      fail();
    } catch( SWTException exception ) {
      assertEquals( SWT.ERROR_DEVICE_DISPOSED, exception.code );
    }
  }

  @Test
  public void testAsyncExec_wrapsExceptionsInSWTException() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    final RuntimeException exception = new RuntimeException( "bad things happen" );
    display.asyncExec( new Runnable() {
      @Override
      public void run() {
        throw exception;
      }
    } );

    try {
      display.readAndDispatch();
      fail();
    } catch( SWTException swtException ) {
      assertEquals( SWT.ERROR_FAILED_EXEC, swtException.code );
      assertSame( exception, swtException.throwable );
    }
  }

  @Test
  public void testSyncExec_delegatesToSynchronizer() {
    Synchronizer synchronizer = mock( Synchronizer.class );
    display.setSynchronizer( synchronizer );
    Runnable runnable = mock( Runnable.class );

    display.syncExec( runnable );

    verify( synchronizer ).syncExec( same( runnable ) );
  }

  @Test
  public void testSyncExec_failsWhenDisplayIsDisposed() {
    Synchronizer synchronizer = mock( Synchronizer.class );
    display.setSynchronizer( synchronizer );
    display.dispose();

    try {
      display.syncExec( mock( Runnable.class ) );
      fail();
    } catch( SWTException exception ) {
      assertEquals( SWT.ERROR_DEVICE_DISPOSED, exception.code );
    }
  }

  @Test
  public void testSyncExecIsReleasedOnSessionTimeout() throws Exception {
    final AtomicBoolean executed = new AtomicBoolean( false );
    Thread thread = new Thread( new Runnable() {
      @Override
      public void run() {
        display.syncExec( new Runnable() {
          @Override
          public void run() {
            executed.set( true );
          }
        } );
      }
    } );
    thread.setDaemon( true );
    thread.start();
    while( display.getSynchronizer().getMessageCount() < 1 ) {
      Thread.yield();
    }

    display.dispose();

    thread.join( 5000 );
    assertFalse( thread.isAlive() );
    assertFalse( executed.get() );
  }

  @Test
  public void testRemoveShell_VisibleActiveShell() {
    Shell visibleShell = new Shell( display );
    visibleShell.open();
    Shell invisibleShell = new Shell( display );
    Shell shell = new Shell( display );
    shell.open();

    shell.dispose();

    assertFalse( invisibleShell.isVisible() );
    assertSame( visibleShell, display.getActiveShell() );
  }

  @Test
  public void testRemoveShell_NoActiveShell() {
    Shell invisibleShell = new Shell( display );
    Shell shell = new Shell( display );
    shell.open();

    shell.dispose();

    assertFalse( invisibleShell.isVisible() );
    assertNull( display.getActiveShell() );
  }

  @Test
  public void testDispose_failsWhenInDisposal() {
    // See bug 389384
    Shell shell = new Shell( display );
    shell.addDisposeListener( new DisposeListener() {
      @Override
      public void widgetDisposed( DisposeEvent event ) {
        display.dispose();
      }
    } );

    try {
      display.dispose();
      fail();
    } catch( SWTException exception ) {
      assertEquals( "Device is disposed", exception.getMessage() );
    }
  }

  @Test
  public void testReadAndDispatchIgnoresEventsFromDisposedWidgets() {
    Fixture.fakePhase( PhaseId.READ_DATA );
    Widget widget = new Shell( display );
    Listener listener = mock( Listener.class );
    widget.addListener( SWT.Activate, listener );
    widget.dispose();

    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    display.readAndDispatch();

    verify( listener, never() ).handleEvent( any( Event.class ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testAddListener_withNullArgument() {
    display.addListener( 123, null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testRemoveListener_withNullArgument() {
    display.removeListener( 123, null );
  }

  @Test
  public void testAddListener() {
    Listener listener = mock( Listener.class );

    display.addListener( 123, listener );
    Event event = new Event();
    display.sendEvent( 123, event );

    verify( listener ).handleEvent( event );
  }

  @Test
  public void testRemoveListener() {
    Listener listener = mock( Listener.class );
    display.addListener( 123, listener );

    display.removeListener( 123, listener );
    display.sendEvent( 123, new Event() );

    verifyNoInteractions( listener );
  }

  @Test
  public void testSendEvent() {
    Listener listener = mock( Listener.class );
    display.addListener( 123, listener );

    display.sendEvent( 123, new Event() );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( listener ).handleEvent( captor.capture() );
    assertEquals( 123, captor.getValue().type );
    assertEquals( display, captor.getValue().display );
    assertTrue( captor.getValue().time > 0 );
  }

  @Test
  public void testSendEvent_withPredefinedTime() {
    Listener listener = mock( Listener.class );
    display.addListener( 123, listener );

    Event event = new Event();
    event.time = 4;
    display.sendEvent( 123, event );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( listener ).handleEvent( captor.capture() );
    assertEquals( 123, captor.getValue().type );
    assertEquals( display, captor.getValue().display );
    assertEquals( event.time, captor.getValue().time );
  }

  @Test
  public void testRemoveListenerWithoutAddingListener() {
    Listener listener = mock( Listener.class );

    display.removeListener( 123, listener );
    display.sendEvent( 123, new Event() );

    verifyNoInteractions( listener );
  }

  @Test
  public void testReadInitialBounds() {
    display.dispose();
    Fixture.fakeSetProperty( "w1", "bounds", createJsonArray( 1, 2, 3, 4 ) );

    Display display = new Display();

    Rectangle bounds = display.getBounds();
    assertEquals( 1, bounds.x );
    assertEquals( 2, bounds.y );
    assertEquals( 3, bounds.width );
    assertEquals( 4, bounds.height );
  }

  @Test
  public void testReadDPI() {
    display.dispose();

    Fixture.fakeSetProperty( "w1", "dpi", createJsonArray( 1, 2 ) );

    Display display = new Display();

    Point dpi = display.getDPI();
    assertEquals( 1, dpi.x );
    assertEquals( 2, dpi.y );
  }

  @Test
  public void testReadColorDepth() {
    display.dispose();
    Fixture.fakeSetProperty( "w1", "colorDepth", 32 );

    Display display = new Display();

    assertEquals( 32, display.getDepth() );
  }

  @Test
  public void testBoundsDPIAndColorDepthIsSerializable() throws Exception {
    display.dispose();
    Fixture.fakeSetProperty( "w1", "bounds", createJsonArray( 1, 2, 3, 4 ) );
    Fixture.fakeSetProperty( "w1", "dpi", createJsonArray( 1, 2 ) );
    Fixture.fakeSetProperty( "w1", "colorDepth", 32 );
    TestDisplay display = new TestDisplay();

    TestDisplay deserializedDisplay = serializeAndDeserialize( display );

    Rectangle bounds = deserializedDisplay.getBounds();
    assertEquals( 1, bounds.x );
    assertEquals( 2, bounds.y );
    assertEquals( 3, bounds.width );
    assertEquals( 4, bounds.height );
    Point dpi = deserializedDisplay.getDPI();
    assertEquals( 1, dpi.x );
    assertEquals( 2, dpi.y );
    assertEquals( 32, deserializedDisplay.getDepth() );
  }

  private static void setCursorLocation( Display display, int x, int y ) {
    IDisplayAdapter adapter = display.getAdapter( IDisplayAdapter.class );
    adapter.setCursorLocation( x, y );
  }

  public static class EnsureIdEntryPoint implements EntryPoint {
    @Override
    public int createUI() {
      Display display = new Display();
      Shell shell = new Shell( display );
      WidgetUtil.getId( shell );
      return 0;
    }
  }

  private static class TestDisplay extends Display {
    @Override
    protected void checkDevice() {
    }
  }

}
