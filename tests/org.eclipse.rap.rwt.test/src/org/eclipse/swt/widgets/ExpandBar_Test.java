/*******************************************************************************
 * Copyright (c) 2008, 2013 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.widgets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ExpandEvent;
import org.eclipse.swt.events.ExpandListener;
import org.eclipse.swt.graphics.Font;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ExpandBar_Test {

  private Display display;
  private Shell shell;

  @Before
  public void setUp() {
    Fixture.setUp();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    display = new Display();
    shell = new Shell( display );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testInitialValues() {
    ExpandBar expandBar = new ExpandBar( shell, SWT.NONE );
    assertEquals( ExpandItem.CHEVRON_SIZE, expandBar.getBandHeight() );
    assertEquals( 4, expandBar.getSpacing() );
    assertEquals( 0, expandBar.getItemCount() );
    assertNull( expandBar.getBackgroundImage() );
    assertNull( expandBar.getMenu() );
  }

  @Test
  public void testCreation() {
    ExpandBar expandBar = new ExpandBar( shell, SWT.NONE );
    assertEquals( 0, expandBar.getItemCount() );
    assertEquals( 0, expandBar.getItems().length );
    ExpandItem item = new ExpandItem( expandBar, SWT.NONE );
    assertEquals( 1, expandBar.getItemCount() );
    assertEquals( 1, expandBar.getItems().length );
    assertEquals( item, expandBar.getItem( 0 ) );
    assertEquals( item, expandBar.getItems()[ 0 ] );
    try {
      expandBar.getItem( 4 );
      fail( "Index out of bounds" );
    } catch( IllegalArgumentException iae ) {
      // expected
    }
    assertSame( display, item.getDisplay() );
    item.dispose();
    assertEquals( 0, expandBar.getItemCount() );
    assertEquals( 0, expandBar.getItems().length );
    // search operation indexOf
    item = new ExpandItem( expandBar, SWT.NONE );
    assertEquals( 0, expandBar.indexOf( item ) );
  }

  @Test
  public void testStyle() {
    ExpandBar expandBar = new ExpandBar( shell, SWT.NONE );
    assertTrue( ( expandBar.getStyle() & SWT.V_SCROLL ) == 0 );
    assertTrue( ( expandBar.getStyle() & SWT.BORDER ) == 0 );
    expandBar = new ExpandBar( shell, SWT.V_SCROLL );
    assertTrue( ( expandBar.getStyle() & SWT.V_SCROLL ) != 0 );
    expandBar = new ExpandBar( shell, SWT.BORDER );
    assertTrue( ( expandBar.getStyle() & SWT.BORDER ) != 0 );
    expandBar = new ExpandBar( shell, SWT.BORDER | SWT.V_SCROLL );
    assertTrue( ( expandBar.getStyle() & SWT.BORDER ) != 0 );
    assertTrue( ( expandBar.getStyle() & SWT.V_SCROLL ) != 0 );
  }

  @Test
  public void testBandHeight() {
    ExpandBar expandBar = new ExpandBar( shell, SWT.NONE );
    assertEquals( ExpandItem.CHEVRON_SIZE, expandBar.getBandHeight() );
    Font font = new Font( display, "font", 30, SWT.BOLD );
    expandBar.setFont( font );
    assertEquals( 34, expandBar.getBandHeight() );
  }

  @Test
  public void testSpacing() {
    ExpandBar expandBar = new ExpandBar( shell, SWT.NONE );
    assertEquals( 4, expandBar.getSpacing() );
    expandBar.setSpacing( 8 );
    assertEquals( 8, expandBar.getSpacing() );
  }

  @Test
  public void testDispose() {
    ExpandBar expandBar = new ExpandBar( shell, SWT.NONE );
    ExpandItem item = new ExpandItem( expandBar, SWT.NONE );
    expandBar.dispose();
    assertTrue( expandBar.isDisposed() );
    assertTrue( item.isDisposed() );
  }

  @Test
  public void testExpandListener() {
    ExpandBar expandBar = new ExpandBar( shell, SWT.NONE );
    final StringBuilder log = new StringBuilder();
    ExpandListener expandListener = new ExpandListener() {
      public void itemCollapsed( ExpandEvent e ) {
        log.append( "collapsed" );
      }
      public void itemExpanded( ExpandEvent e ) {
        log.append( "expanded|" );
      }
    };
    expandBar.addExpandListener( expandListener );
    expandBar.notifyListeners( SWT.Expand, new Event() );
    assertEquals( "expanded|", log.toString() );
    expandBar.notifyListeners( SWT.Collapse, new Event() );
    assertEquals( "expanded|collapsed", log.toString() );
  }

  @Test
  public void testIndexOfWithNullItem() {
    ExpandBar expandBar = new ExpandBar( shell, SWT.NONE );
    try {
      expandBar.indexOf( null );
      fail( "No exception thrown for expandItem == null" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  @Test
  public void testIndexOfWithDisposedItem() {
    ExpandBar expandBar = new ExpandBar( shell, SWT.NONE );
    ExpandItem item = new ExpandItem( expandBar, SWT.NONE );
    item.dispose();
    try {
      expandBar.indexOf( item );
      fail( "No exception thrown for disposed expandItem" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  // bug 301005
  @Test
  public void testSetFontNull() {
    ExpandBar expandBar = new ExpandBar( shell, SWT.NONE );
    try {
      expandBar.setFont( null );
    } catch( Throwable e ) {
      fail( "setFont() must accept null value" );
    }
  }

  @Test
  public void testDisposeWithFontDisposeInDisposeListener() {
    ExpandBar expandBar = new ExpandBar( shell, SWT.NONE );
    new ExpandItem( expandBar, SWT.NONE );
    new ExpandItem( expandBar, SWT.NONE );
    final Font font = new Font( display, "font-name", 10, SWT.NORMAL );
    expandBar.setFont( font );
    expandBar.addDisposeListener( new DisposeListener() {
      public void widgetDisposed( DisposeEvent event ) {
        font.dispose();
      }
    } );
    expandBar.dispose();
  }

  @Test
  public void testIsSerializable() throws Exception {
    ExpandBar expandBar = new ExpandBar( shell, SWT.NONE );
    new ExpandItem( expandBar, SWT.NONE );

    ExpandBar deserializedExpandBar = Fixture.serializeAndDeserialize( expandBar );

    assertEquals( 1, deserializedExpandBar.getItemCount() );
  }

  @Test
  public void testAddExpandListener() {
    ExpandBar expandBar = new ExpandBar( shell, SWT.NONE );

    expandBar.addExpandListener( mock( ExpandListener.class ) );

    assertTrue( expandBar.isListening( SWT.Expand ) );
    assertTrue( expandBar.isListening( SWT.Collapse ) );
  }

  @Test
  public void testRemoveExpandListener() {
    ExpandBar expandBar = new ExpandBar( shell, SWT.NONE );
    ExpandListener listener = mock( ExpandListener.class );
    expandBar.addExpandListener( listener );

    expandBar.removeExpandListener( listener );

    assertFalse( expandBar.isListening( SWT.Expand ) );
    assertFalse( expandBar.isListening( SWT.Collapse ) );
  }

  @Test
  public void testAddExpandListenerWithNullArgument() {
    ExpandBar expandBar = new ExpandBar( shell, SWT.NONE );

    try {
      expandBar.addExpandListener( null );
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testRemoveExpandListenerWithNullArgument() {
    ExpandBar expandBar = new ExpandBar( shell, SWT.NONE );

    try {
      expandBar.removeExpandListener( null );
    } catch( IllegalArgumentException expected ) {
    }
  }
}
