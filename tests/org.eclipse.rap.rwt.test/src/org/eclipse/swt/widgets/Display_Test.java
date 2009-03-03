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
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
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
    } catch( IllegalStateException e ) {
      // expected
    }
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

  public void testBounds() throws Exception {
    Display display = new Display();
    Object adapter = display.getAdapter( IDisplayAdapter.class );
    IDisplayAdapter displayAdapter = ( IDisplayAdapter )adapter;
    Rectangle expectedBounds = new Rectangle( 0, 10, 60, 99 );
    displayAdapter.setBounds( expectedBounds );
    Rectangle bounds = display.getBounds();
    assertEquals( expectedBounds, bounds );
    assertNotSame( expectedBounds, bounds );
  }
  
  public void testClientArea() throws Exception {
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
    shell.setBounds( shellBounds );

    Rectangle actual = display.map( shell, shell, 1, 2, 3, 4 );
    Rectangle expected = new Rectangle( 1, 2, 3, 4 );
    assertEquals( expected, actual );

    actual = display.map( shell, null, 5, 6, 7, 8 );
    expected = new Rectangle( shellBounds.x + 5,
                              shellBounds.y + 6,
                              7,
                              8 );
    assertEquals( expected, actual );

    shell.setLayout( new FillLayout() );
    TabFolder folder = new TabFolder( shell, SWT.NONE );
    shell.layout();
    actual = display.map( folder, shell, 6, 7, 8, 9 );
    expected = new Rectangle( folder.getBounds().x + 6,
                              folder.getBounds().y + 7,
                              8,
                              9 );
    assertEquals( expected, actual );

    actual = display.map( null, folder, 1, 2, 3, 4 );
    expected = new Rectangle( 1 - shell.getBounds().x - folder.getBounds().x,
                              2 - shell.getBounds().y - folder.getBounds().y,
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
    Point expected = new Point( 200, 200 );
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
    assertEquals( "w1", DisplayUtil.getId( Display.getCurrent() ) );
    EntryPointManager.deregister( EntryPointManager.DEFAULT );
  }
  
  public void testSetData() {
    Display display = new Display();
    display.setData( new Integer( 10 ) );
    Integer i = ( Integer )display.getData();
    assertNotNull( i );
    assertTrue( i.equals( new Integer( 10 ) ) );
  }
  
  public void testSetDataKey() throws Exception {
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

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
