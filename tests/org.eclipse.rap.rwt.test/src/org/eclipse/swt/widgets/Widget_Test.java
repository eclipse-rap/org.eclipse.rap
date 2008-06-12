/*******************************************************************************
 * Copyright (c) 2002, 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.widgets;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.lifecycle.IWidgetAdapter;
import org.eclipse.rwt.lifecycle.PhaseId;
import org.eclipse.swt.*;



public class Widget_Test extends TestCase {
  
  protected void setUp() throws Exception {
    Fixture.setUp();
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testGetAdapter() {
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
    Display display = new Display();
    Widget shell = new Shell( display );
    // ensure that Widget#getAdapter can be called after widget was disposed of
    shell.dispose();
    assertNotNull( shell.getAdapter( IWidgetAdapter.class ) );
  }
  
  public void testCheckWidget() throws InterruptedException {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    final Widget widget = new Text( shell, SWT.NONE );

    final Throwable[] throwable = new Throwable[ 1 ];
    final String[] message = new String[ 1 ];
    Thread thread = new Thread( new Runnable() {
      public void run() {
        try {
          widget.checkWidget();
          fail( "Illegal thread access expected." );
        } catch( final SWTException swte ) {
          message[ 0 ] = swte.getMessage();
        } catch( final Throwable thr ) {
          throwable[ 0 ] = thr;
        }
      }
    });
    thread.start();
    thread.join();
    assertEquals( message[ 0 ], "Invalid thread access" );
    assertNull( throwable[ 0 ] );
  }

  public void testData() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Widget widget = new Text( shell, SWT.NONE );
    
    // Test initial state
    assertEquals( null, widget.getData() );
    
    Object singleData = new Object();
    // Set/get some single data
    widget.setData( singleData );
    assertSame( singleData, widget.getData() );
    
    // Set/get some keyed data, ensure that single data remains unchanged
    Object keyedData = new Object();
    widget.setData( "key", keyedData );
    widget.setData( "null-key", null );
    assertSame( singleData, widget.getData() );
    assertSame( keyedData, widget.getData( "key" ) );
    assertSame( null, widget.getData( "null-key" ) );
    
    // Test 'deleting' a key
    widget.setData( "key", null );
    assertNull( widget.getData( "key" ) );
    
    // Test keyed data with non-existing key
    assertNull( widget.getData( "non-existing-key" ) );
    
    // Test keyed data with illegal arguments
    try {
      widget.setData( null, new Object() );
      fail( "Must not allow to set data with null key" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    try {
      widget.getData( null );
      fail( "Must not allow to get data for null key" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }
  
  public void testCheckBits() {
    int style = SWT.VERTICAL | SWT.HORIZONTAL;
    int result = Widget.checkBits( style, 
                                   SWT.VERTICAL, 
                                   SWT.HORIZONTAL, 
                                   0, 
                                   0, 
                                   0, 
                                   0 );
    assertTrue( ( result & SWT.VERTICAL ) != 0 );
    assertFalse( ( result & SWT.HORIZONTAL ) != 0 );
  }

  public void testDispose() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Widget widget = new Button( shell, SWT.NONE );

    // Ensure initial state
    assertEquals( false, widget.isDisposed() );
    
    // Test dispose the first time
    widget.dispose();
    assertEquals( true, widget.isDisposed() );

    // Disposing of an already disposed of widget does nothing
    widget.dispose();
    assertEquals( true, widget.isDisposed() );
  }

  public void testDisposeFromIllegalThread() throws InterruptedException {
    Display display = new Display();
    Shell shell = new Shell( display );
    final Widget widget = new Button( shell, SWT.NONE );
    
    final AssertionFailedError[] failure = new AssertionFailedError[ 1 ];
    Thread thread = new Thread( new Runnable() {
      public void run() {
        try {
          widget.dispose();
          fail( "Must not allow to dispose of a widget from a non-UI-thread" );
        } catch( SWTException e ) {
          // expected
        } catch( final AssertionFailedError afa ) {
          failure[ 0 ] = afa;
        }
      }
    } );
    thread.start();
    thread.join();
    
    if( failure[ 0 ] != null ) {
      throw failure[ 0 ];
    }
  }
}
