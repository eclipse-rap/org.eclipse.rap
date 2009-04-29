/*******************************************************************************
 * Copyright (c) 2002, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.widgets;

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.lifecycle.*;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.service.ServiceContext;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.*;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.widgets.IDisplayAdapter;
import org.eclipse.swt.layout.FillLayout;


public class Display_Test extends TestCase {

  public static final class EnsureIdEntryPoint implements IEntryPoint {
    public int createUI() {
      Display display = new Display();
      Shell shell = new Shell( display );
      WidgetUtil.getId( shell );
      return 0;
    }
  }

  public void testSingleDisplayPerSession() {
    Device display = new Display();
    assertEquals( Display.getCurrent(), display );
    try {
      new Display();
      fail( "Only one display allowed per session" );
    } catch( SWTError e ) {
      // expected
    }
  }
  
  public void testGetCurrent() throws InterruptedException {
    assertNull( Display.getCurrent() );
    final Display display = new Display();
    assertSame( display, Display.getCurrent() );
    // init with something non-null
    final Display[] displayFromBgThread = { display };
    final Display[] displayFromBgThreadWithFakeContext = { display };
    Runnable bgThread = new Runnable() {
      public void run() {
        displayFromBgThread[ 0 ] = Display.getCurrent();
        UICallBack.runNonUIThreadWithFakeContext( display, new Runnable() {
          public void run() {
            displayFromBgThreadWithFakeContext[ 0 ] = Display.getCurrent();
          }
        } );
      }
    };
    Thread thread = new Thread( bgThread );
    synchronized( thread ) {
      thread.start();
      thread.join();
    }
    assertNull( displayFromBgThread[ 0 ] );
    assertNull( displayFromBgThreadWithFakeContext[ 0 ] );
  }

  public void testGetThread() throws InterruptedException {
    Display first = new Display();
    assertSame( Thread.currentThread(), first.getThread() );
    first.dispose();

    final ServiceContext context = ContextProvider.getContext();
    final Display[] display = { null };
    Runnable runnable = new Runnable() {
      public void run() {
        ContextProvider.setContext( context );
        Fixture.fakeResponseWriter();
        display[ 0 ] = new Display();
      }
    };
    Thread thread = new Thread( runnable );
    thread.start();
    thread.join();
    assertSame( thread, display[ 0 ].getThread() );


//    final Display[] display = { new Display() };
//    final Thread[] thread = new Thread[ 1 ];
//    final ServiceContext context[] = { ContextProvider.getContext() };
//    final RWTLifeCycle lifeCycle = new RWTLifeCycle();
//    lifeCycle.addPhaseListener( new PhaseListener() {
//      private static final long serialVersionUID = 1L;
//      public void afterPhase( PhaseEvent event ) {
//      }
//      public void beforePhase( PhaseEvent event ) {
//        thread[ 0 ] = display[ 0 ].getThread();
//      }
//      public PhaseId getPhaseId() {
//        return PhaseId.PREPARE_UI_ROOT;
//      }
//    } );
//
//    Runnable runnable = new Runnable() {
//      public void run() {
//        ContextProvider.setContext( context[ 0 ] );
//        Fixture.fakeResponseWriter();
//        String id = "org.eclipse.swt.display";
//        ContextProvider.getSession().setAttribute( id, display[ 0 ] );
//        String displayId = DisplayUtil.getAdapter( display[ 0 ] ).getId();
//        Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
//        try {
//          lifeCycle.execute();
//        } catch( IOException e ) {
//          // TODO Auto-generated catch block
//          e.printStackTrace();
//          fail();
//        }
//      }
//    };
//    Thread thread1 = new Thread( runnable );
//    thread1.start();
//    thread1.join();
//    assertSame( thread1, thread[ 0 ] );
//
//    Thread thread2 = new Thread( runnable );
//    thread2.start();
//    thread2.join();
//    assertSame( thread2, thread[ 0 ] );
//    RWTFixture.tearDown();
//    assertNull( display[ 0 ].getThread() );
//    RWTFixture.setUp();
  }

  public void testGetShells() {
    Display display = new Display();
    assertEquals( 0, display.getShells().length );
    Composite shell1 = new Shell( display , SWT.NONE );
    assertSame( shell1, display.getShells()[ 0 ] );
    Composite shell2 = new Shell( display , SWT.NONE );
    Composite[] shells = display.getShells();
    assertTrue( shell2 == shells[ 0 ] || shell2 == display.getShells()[ 1 ] );
  }

  public void testProperties() {
    Display display = new Display();
    assertEquals( 0, display.getShells().length );
    Rectangle bounds = display.getBounds();
    assertNotNull( bounds );
    bounds.x += 1;
    assertTrue( bounds.x != display.getBounds().x );
  }

  public void testBounds() {
    Display display = new Display();
    Object adapter = display.getAdapter( IDisplayAdapter.class );
    IDisplayAdapter displayAdapter = ( IDisplayAdapter )adapter;
    Rectangle expectedBounds = new Rectangle( 0, 10, 60, 99 );
    displayAdapter.setBounds( expectedBounds );
    Rectangle bounds = display.getBounds();
    assertEquals( expectedBounds, bounds );
    assertNotSame( expectedBounds, bounds );
  }

  public void testClientArea() {
    Display display = new Display();
    Object adapter = display.getAdapter( IDisplayAdapter.class );
    IDisplayAdapter displayAdapter = ( IDisplayAdapter )adapter;
    Rectangle testRect = new Rectangle( 1, 2, 3, 4 );
    displayAdapter.setBounds( testRect );
    Rectangle clientArea = display.getClientArea();
    assertEquals( testRect, clientArea );
    assertNotSame( testRect, clientArea );
  }

  public void testMap() {
    Display display = new Display();
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

  public void testMapWithChildShell() {
    Display display = new Display();
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

  public void testMapWithDifferentBorders() {
    Display display = new Display();
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

  ////////////////////////////
  // SWT Tests for Display#map

  /*
   * Verbatim copy from SWT Test_org_eclipse_swt_widgets_Display
   */
  public void test_mapLorg_eclipse_swt_widgets_ControlLorg_eclipse_swt_widgets_ControlII() {
    Display display = new Display();
    try {
      Shell shell = new Shell(display, SWT.NO_TRIM);
      Button button1 = new Button(shell, SWT.PUSH);
      button1.setBounds(0,0,100,100);
      Button button2 = new Button(shell, SWT.PUSH);
      button2.setBounds(200,100,100,100);
      shell.setBounds(0,0,400,400);
      shell.open();
      
      Point shellOffset = shell.getLocation();
      // [rst] Shell offset includes 1px border in RAP
      shellOffset.x += 1;
      shellOffset.y += 1;
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
  public void test_mapLorg_eclipse_swt_widgets_ControlLorg_eclipse_swt_widgets_ControlIIII() {
    Display display = new Display();
    try {
      Shell shell = new Shell(display, SWT.NO_TRIM);
      Button button1 = new Button(shell, SWT.PUSH);
      button1.setBounds(0,0,100,100);
      Button button2 = new Button(shell, SWT.PUSH);
      button2.setBounds(200,100,100,100);
      shell.setBounds(0,0,400,400);
      shell.open();
      
      Point shellOffset = shell.getLocation();
      // [rst] Shell offset includes 1px border in RAP
      shellOffset.x += 1;
      shellOffset.y += 1;
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
  public void test_mapLorg_eclipse_swt_widgets_ControlLorg_eclipse_swt_widgets_ControlLorg_eclipse_swt_graphics_Point() {
    Display display = new Display();
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
      // [rst] Shell offset includes 1px border in RAP
      shellOffset.x += 1;
      shellOffset.y += 1;

      
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
  public void test_mapLorg_eclipse_swt_widgets_ControlLorg_eclipse_swt_widgets_ControlLorg_eclipse_swt_graphics_Rectangle() {
    Display display = new Display();
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
      // [rst] Shell offset includes 1px border in RAP
      shellOffset.x += 1;
      shellOffset.y += 1;
      
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

  public void testActiveShell() {
    // TODO [rh] This test needs to be reworked when Shell.open() is implemented
    //      since it assumes opened shells.
    Display display = new Display();
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

  public void testSystemFont() {
    Device display = new Display();
    Font systemFont = display.getSystemFont();
    assertNotNull( systemFont );
  }

  public void testSystemImage() {
    Display display = new Display();
    Image errorImage = display.getSystemImage( SWT.ICON_ERROR );
    assertEquals( new Rectangle( 0, 0, 32, 32 ), errorImage.getBounds() );
    Image infoImage = display.getSystemImage( SWT.ICON_INFORMATION );
    assertEquals( new Rectangle( 0, 0, 32, 32 ), infoImage.getBounds() );
    assertFalse( infoImage.equals( errorImage ) );
    Image workImage = display.getSystemImage( SWT.ICON_WORKING );
    assertEquals( new Rectangle( 0, 0, 32, 32 ), workImage.getBounds() );
    assertTrue( infoImage.equals( workImage ) );
    Image questionImage = display.getSystemImage( SWT.ICON_QUESTION );
    assertEquals( new Rectangle( 0, 0, 32, 32 ), questionImage.getBounds() );
    Image warningImage = display.getSystemImage( SWT.ICON_WARNING );
    assertEquals( new Rectangle( 0, 0, 32, 32 ), warningImage.getBounds() );
  }

  public void testSystemColor() {
    Display display = new Display();
    Color color;
    // Theme colors
    color = display.getSystemColor( SWT.COLOR_WIDGET_NORMAL_SHADOW );
    assertEquals( new RGB( 167, 166, 170 ), color.getRGB() );
    color = display.getSystemColor( SWT.COLOR_WIDGET_DARK_SHADOW );
    assertEquals( new RGB( 133, 135, 140 ), color.getRGB() );
    color = display.getSystemColor( SWT.COLOR_WIDGET_LIGHT_SHADOW );
    assertEquals( new RGB( 220, 223, 228 ), color.getRGB() );
    color = display.getSystemColor( SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW );
    assertEquals( new RGB( 255, 255, 255 ), color.getRGB() );
    color = display.getSystemColor( SWT.COLOR_WIDGET_BORDER );
    assertEquals( new RGB( 172, 168, 153 ), color.getRGB() );
    color = display.getSystemColor( SWT.COLOR_WIDGET_FOREGROUND );
    assertEquals( new RGB( 0, 0, 0 ), color.getRGB() );
    color = display.getSystemColor( SWT.COLOR_WIDGET_BACKGROUND );
    assertEquals( new RGB( 248, 248, 255 ), color.getRGB() );
    color = display.getSystemColor( SWT.COLOR_INFO_BACKGROUND );
    assertEquals( new RGB( 255, 255, 225 ), color.getRGB() );
    color = display.getSystemColor( SWT.COLOR_INFO_FOREGROUND );
    assertEquals( new RGB( 0, 0, 0 ), color.getRGB() );
    color = display.getSystemColor( SWT.COLOR_LIST_BACKGROUND );
    assertEquals( new RGB( 255, 255, 255 ), color.getRGB() );
    color = display.getSystemColor( SWT.COLOR_LIST_FOREGROUND );
    assertEquals( new RGB( 0, 0, 0 ), color.getRGB() );
    color = display.getSystemColor( SWT.COLOR_LIST_SELECTION );
    assertEquals( new RGB( 49, 106, 197 ), color.getRGB() );
    color = display.getSystemColor( SWT.COLOR_LIST_SELECTION_TEXT );
    assertEquals( new RGB( 255, 255, 255 ), color.getRGB() );
    color = display.getSystemColor( SWT.COLOR_TITLE_BACKGROUND );
    assertEquals( new RGB( 0, 128, 192 ), color.getRGB() );
    color = display.getSystemColor( SWT.COLOR_TITLE_BACKGROUND_GRADIENT );
    assertEquals( new RGB( 0, 128, 192 ), color.getRGB() );
    color = display.getSystemColor( SWT.COLOR_TITLE_FOREGROUND );
    assertEquals( new RGB( 255, 255, 255 ), color.getRGB() );
    color = display.getSystemColor( SWT.COLOR_TITLE_INACTIVE_BACKGROUND );
    assertEquals( new RGB( 121, 150, 165 ), color.getRGB() );
    color = display.getSystemColor( SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT );
    assertEquals( new RGB( 121, 150, 165 ), color.getRGB() );
    color = display.getSystemColor( SWT.COLOR_TITLE_INACTIVE_FOREGROUND );
    assertEquals( new RGB( 221, 221, 221 ), color.getRGB() );
    // Fix colors
    color = display.getSystemColor( SWT.COLOR_BLACK );
    assertEquals( new RGB( 0, 0, 0 ), color.getRGB() );
    color = display.getSystemColor( SWT.COLOR_BLUE );
    assertEquals( new RGB( 0, 0, 255 ), color.getRGB() );
    color = display.getSystemColor( SWT.COLOR_CYAN );
    assertEquals( new RGB( 0, 255, 255 ), color.getRGB() );
    color = display.getSystemColor( SWT.COLOR_DARK_BLUE );
    assertEquals( new RGB( 0, 0, 128 ), color.getRGB() );
    color = display.getSystemColor( SWT.COLOR_DARK_CYAN );
    assertEquals( new RGB( 0, 128, 128 ), color.getRGB() );
    color = display.getSystemColor( SWT.COLOR_DARK_GRAY );
    assertEquals( new RGB( 128, 128, 128 ), color.getRGB() );
    color = display.getSystemColor( SWT.COLOR_DARK_GREEN );
    assertEquals( new RGB( 0, 128, 0 ), color.getRGB() );
    color = display.getSystemColor( SWT.COLOR_DARK_MAGENTA );
    assertEquals( new RGB( 128, 0, 128 ), color.getRGB() );
    color = display.getSystemColor( SWT.COLOR_DARK_RED );
    assertEquals( new RGB( 128, 0, 0 ), color.getRGB() );
    color = display.getSystemColor( SWT.COLOR_DARK_YELLOW );
    assertEquals( new RGB( 128, 128, 0 ), color.getRGB() );
    color = display.getSystemColor( SWT.COLOR_GRAY );
    assertEquals( new RGB( 192, 192, 192 ), color.getRGB() );
    color = display.getSystemColor( SWT.COLOR_GREEN );
    assertEquals( new RGB( 0, 255, 0 ), color.getRGB() );
    color = display.getSystemColor( SWT.COLOR_MAGENTA );
    assertEquals( new RGB( 255, 0, 255 ), color.getRGB() );
    color = display.getSystemColor( SWT.COLOR_RED );
    assertEquals( new RGB( 255, 0, 0 ), color.getRGB() );
    color = display.getSystemColor( SWT.COLOR_WHITE );
    assertEquals( new RGB( 255, 255, 255 ), color.getRGB() );
    color = display.getSystemColor( SWT.COLOR_YELLOW );
    assertEquals( new RGB( 255, 255, 0 ), color.getRGB() );
    // Only one instance per color
    Color systemRed = display.getSystemColor( SWT.COLOR_RED );
    Color red = Graphics.getColor( 255, 0, 0 );
    assertEquals( red, systemRed );
    assertSame( red, systemRed );
    assertSame( systemRed, display.getSystemColor( SWT.COLOR_RED ) );
  }

  public void testAddAndRemoveFilter() {
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
    final int CLOSE_CALLBACK = 0;
    final int DISPOSE_CALLBACK = 1;
    final boolean[] callbackReceived = new boolean[]{ false, false };
    Listener listener = new Listener() {
      public void handleEvent( final Event e ) {
        if( e.type == SWT.Close ) {
          callbackReceived[ CLOSE_CALLBACK ] = true;
        } else if( e.type == SWT.Dispose ) {
          callbackReceived[ DISPOSE_CALLBACK ] = true;
        }
      }
    };

    // addFilter
    Display display = new Display();
    try {
      display.addFilter( SWT.Dispose, null );
      fail( "No exception thrown for addFilter with null argument" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    display.addFilter( SWT.Close, listener );
    Shell shell = new Shell( display );
    shell.close();
    assertTrue( callbackReceived[ CLOSE_CALLBACK ] );
    assertFalse( callbackReceived[ DISPOSE_CALLBACK ] );

    // removeFilter
    callbackReceived[ CLOSE_CALLBACK ] = false;
    callbackReceived[ DISPOSE_CALLBACK ] = false;
    try {
      display.removeFilter( SWT.Dispose, null );
      fail( "No exception thrown for removeFilter with null argument" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    display.removeFilter( SWT.Close, listener );
    shell = new Shell( display );
    shell.close();
    assertFalse( callbackReceived[ CLOSE_CALLBACK ] );
    assertFalse( callbackReceived[ DISPOSE_CALLBACK ] );

    // remove filter for an event that was not added before -> do nothing
    display.removeFilter( SWT.FocusIn, listener );
  }

  public void testEnsureIdIsW1() throws IOException {
    Class entryPointClass = EnsureIdEntryPoint.class;
    EntryPointManager.register( EntryPointManager.DEFAULT, entryPointClass );
    RWTFixture.fakeNewRequest();
    RWTLifeCycle lifeCycle = ( RWTLifeCycle )LifeCycleFactory.getLifeCycle();
    lifeCycle.execute();
    assertEquals( "w1", DisplayUtil.getId( RWTLifeCycle.getSessionDisplay() ) );
    EntryPointManager.deregister( EntryPointManager.DEFAULT );
  }

  public void testSetData() {
    Display display = new Display();
    display.setData( new Integer( 10 ) );
    Integer i = ( Integer )display.getData();
    assertNotNull( i );
    assertTrue( i.equals( new Integer( 10 ) ) );
  }

  public void testSetDataKey() {
    Display display = new Display();
    display.setData( "Integer", new Integer( 10 ) );
    display.setData( "String", "xyz" );
    Integer i = ( Integer )display.getData( "Integer" );
    assertNotNull( i );
    assertTrue( i.equals( new Integer( 10 ) ) );
    String s = ( String )display.getData( "String" );
    assertNotNull( s );
    assertTrue( s.equals( "xyz" ) );
    display.setData( "Integer", null );
    Object result = display.getData( "Integer" );
    assertNull( result );
    try {
      display.setData( null, "no" );
      fail( "should throw IllegalArgumentException ");
    } catch ( IllegalArgumentException e ) {
      // expected
    }
    try {
      display.getData( null );
      fail( "should throw IllegalArgumentException ");
    } catch ( IllegalArgumentException e ) {
      // expected
    }
  }

  public void testTimerExec() throws InterruptedException {
    // Ensure that parameters are checked properly
    final Display display = new Display();
    try {
      display.timerExec( 0, null );
      fail( "timerExec must throw exception when null-runnable is passed in " );
    } catch( Exception e ) {
      // expected
    }
    // Ensure that invoking from background thread works
    Thread thread = new Thread( new Runnable() {
      public void run() {
        display.timerExec( 1, new Runnable() {
          public void run() {
            // do nothing
          }
        } );
      }
    } );
    thread.start();
    thread.join();
    // Further timerExec tests can be found in UICallbackManager_Test
  }

  public void testGetMonitors() {
    final Display display = new Display();
    Monitor[] monitors = display.getMonitors();
    assertNotNull( monitors );
    assertEquals( 1, monitors.length );
    Monitor monitor = monitors[ 0 ];
    assertNotNull( monitor );
    // Further monitor tests can be found in Monitor_Test
  }

  public void testGetPrimaryMonitor() {
    final Display display = new Display();
    Monitor monitor = display.getPrimaryMonitor();
    assertNotNull( monitor );
    // Further monitor tests can be found in Monitor_Test
  }
  
  public void testDispose() {
    Display display = new Display();
    assertFalse( display.isDisposed() );
    display.dispose();
    assertTrue( display.isDisposed() );
    assertNull( Display.getCurrent() );
  }

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
