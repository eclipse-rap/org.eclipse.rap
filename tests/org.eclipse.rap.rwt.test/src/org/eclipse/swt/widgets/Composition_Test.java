/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.internal.widgets.ControlHolder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class Composition_Test {

  @Before
  public void setUp() {
    Fixture.setUp();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testControlComposition() {
    Display display = new Display();
    Composite composite = new Shell( display , SWT.NONE );
    assertEquals( 0, composite.getChildren().length );
    Control control = new Button( composite, SWT.PUSH );
    assertSame( composite, control.getParent() );
    Control[] children = composite.getChildren();
    assertSame( control, children[ 0 ] );
    assertEquals( 1, composite.getChildren().length );
    children[ 0 ] = null;
    assertSame( control, composite.getChildren()[ 0 ] );
    try {
      new Button( null, SWT.PUSH );
      fail( "Parent composite must not be null." );
    } catch( IllegalArgumentException iae ) {
      // expected
    }
  }

  @Test
  public void testItemComposition() {
    Display display = new Display();
    Composite shell = new Shell( display , SWT.NONE );
    new Item( shell, SWT.NONE ) {
    };
    assertEquals( 0, shell.getChildren().length );
  }

  @Test
  public void testDispose() {
    final List<Object> disposedWidgets = new ArrayList<Object>();
    DisposeListener disposeListener = new DisposeListener() {
      public void widgetDisposed( DisposeEvent evt ) {
        disposedWidgets.add( evt.getSource() );
      }
    };
    Display display = new Display();
    Composite shell = new Shell( display , SWT.NONE );
    shell.addDisposeListener( disposeListener );
    Button button1 = new Button( shell, SWT.PUSH );
    button1.addDisposeListener( disposeListener );
    // Ensure that dipose removes a widget from its parent and sets isDisposed()
    button1.dispose();
    assertTrue( button1.isDisposed() );
    assertFalse( find( shell.getChildren(), button1 ) );
    assertSame( button1, disposedWidgets.get( 0 ) );
    // Ensure that dispose may be called more than once
    disposedWidgets.clear();
    button1.dispose();
    assertTrue( button1.isDisposed() );
    assertEquals( 0, disposedWidgets.size() );
    Button button2 = new Button( shell, SWT.PUSH );
    button2.addDisposeListener( disposeListener );
    shell.dispose();
    assertEquals( 2, disposedWidgets.size() );
    assertSame( shell, disposedWidgets.get( 0 ) );
    assertSame( button2, disposedWidgets.get( 1 ) );
    assertTrue( shell.isDisposed() );
    assertTrue( button2.isDisposed() );
    // the assert below may not work in the future since getChildren is
    // checkWidget()-protected
    assertEquals( 0, ControlHolder.size( shell ) );
    assertEquals( 0, Display.getCurrent().getShells().length );
    //
    disposedWidgets.clear();
    shell.dispose();
    assertEquals( 0, disposedWidgets.size() );
    assertTrue( shell.isDisposed() );
  }

  @Test
  public void testDisplay() {
    Display display = new Display();
    Composite shell = new Shell( display , SWT.NONE );
    Button button = new Button( shell, SWT.PUSH );
    assertSame( display, shell.getDisplay() );
    assertSame( display, button.getDisplay() );
  }

  private static boolean find( Widget[] children, Widget widget ) {
    boolean found = false;
    for( int i = 0; i < children.length; i++ ) {
      if( children[ i ] == widget ) {
        found = true;
      }
    }
    return found;
  }

}
