/*******************************************************************************
 * Copyright (c) 2008, 2015 Innoopract Informationssysteme GmbH and others.
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

import static org.eclipse.rap.rwt.testfixture.internal.SerializationTestUtil.serializeAndDeserialize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.internal.lifecycle.WidgetLCA;
import org.eclipse.rap.rwt.testfixture.TestContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ExpandEvent;
import org.eclipse.swt.events.ExpandListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.internal.widgets.expandbarkit.ExpandBarLCA;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


public class ExpandBar_Test {

  @Rule
  public TestContext context = new TestContext();

  private Display display;
  private Shell shell;
  private ExpandBar expandBar;

  @Before
  public void setUp() {
    display = new Display();
    shell = new Shell( display );
    expandBar = new ExpandBar( shell, SWT.NONE );
  }

  @Test
  public void testInitialValues() {
    assertEquals( 4, expandBar.getSpacing() );
    assertEquals( 0, expandBar.getItemCount() );
    assertNull( expandBar.getBackgroundImage() );
    assertNull( expandBar.getMenu() );
  }

  @Test
  public void testCreation() {
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
  public void testSpacing() {
    assertEquals( 4, expandBar.getSpacing() );
    expandBar.setSpacing( 8 );
    assertEquals( 8, expandBar.getSpacing() );
  }

  @Test
  public void testDispose() {
    ExpandItem item = new ExpandItem( expandBar, SWT.NONE );
    expandBar.dispose();
    assertTrue( expandBar.isDisposed() );
    assertTrue( item.isDisposed() );
  }

  @Test
  public void testExpandListener() {
    final StringBuilder log = new StringBuilder();
    ExpandListener expandListener = new ExpandListener() {
      @Override
      public void itemCollapsed( ExpandEvent e ) {
        log.append( "collapsed" );
      }
      @Override
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

  @Test( expected = IllegalArgumentException.class )
  public void testIndexOfWithNullItem() {
    expandBar.indexOf( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testIndexOfWithDisposedItem() {
    ExpandItem item = new ExpandItem( expandBar, SWT.NONE );
    item.dispose();
    expandBar.indexOf( item );
  }

  // bug 301005
  @Test
  public void testSetFontNull() {
    try {
      expandBar.setFont( null );
    } catch( Throwable e ) {
      fail( "setFont() must accept null value" );
    }
  }

  @Test
  public void testDisposeWithFontDisposeInDisposeListener() {
    new ExpandItem( expandBar, SWT.NONE );
    new ExpandItem( expandBar, SWT.NONE );
    final Font font = new Font( display, "font-name", 10, SWT.NORMAL );
    expandBar.setFont( font );
    expandBar.addDisposeListener( new DisposeListener() {
      @Override
      public void widgetDisposed( DisposeEvent event ) {
        font.dispose();
      }
    } );
    expandBar.dispose();
  }

  @Test
  public void testIsSerializable() throws Exception {
    new ExpandItem( expandBar, SWT.NONE );

    ExpandBar deserializedExpandBar = serializeAndDeserialize( expandBar );

    assertEquals( 1, deserializedExpandBar.getItemCount() );
  }

  @Test
  public void testAddExpandListener() {
    expandBar.addExpandListener( mock( ExpandListener.class ) );

    assertTrue( expandBar.isListening( SWT.Expand ) );
    assertTrue( expandBar.isListening( SWT.Collapse ) );
  }

  @Test
  public void testRemoveExpandListener() {
    ExpandListener listener = mock( ExpandListener.class );
    expandBar.addExpandListener( listener );

    expandBar.removeExpandListener( listener );

    assertFalse( expandBar.isListening( SWT.Expand ) );
    assertFalse( expandBar.isListening( SWT.Collapse ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testAddExpandListenerWithNullArgument() {
    expandBar.addExpandListener( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testRemoveExpandListenerWithNullArgument() {
    expandBar.removeExpandListener( null );
  }

  @Test
  public void testGetAdapter_LCA() {
    assertTrue( expandBar.getAdapter( WidgetLCA.class ) instanceof ExpandBarLCA );
    assertSame( expandBar.getAdapter( WidgetLCA.class ), expandBar.getAdapter( WidgetLCA.class ) );
  }

  @Test
  public void testSetMarkupEnabled() {
    expandBar.setData( RWT.MARKUP_ENABLED, Boolean.TRUE );

    assertEquals( Boolean.TRUE, expandBar.getData( RWT.MARKUP_ENABLED ) );
  }

  @Test
  public void testSetMarkupEnabled_resetIsIgnored() {
    expandBar.setData( RWT.MARKUP_ENABLED, Boolean.TRUE );

    expandBar.setData( RWT.MARKUP_ENABLED, Boolean.FALSE );

    assertEquals( Boolean.TRUE, expandBar.getData( RWT.MARKUP_ENABLED ) );
  }

}
