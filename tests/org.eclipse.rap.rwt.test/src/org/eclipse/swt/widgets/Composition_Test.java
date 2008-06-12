/*******************************************************************************
 * Copyright (c) 2002, 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.widgets;

import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;

import org.eclipse.rwt.lifecycle.PhaseId;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;

public class Composition_Test extends TestCase {

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
    } catch( final IllegalArgumentException iae ) {
      // expected
    }
  }

  public void testItemComposition() {
    Display display = new Display();
    Composite shell = new Shell( display , SWT.NONE );
    new Item( shell, SWT.NONE ) {

      public Display getDisplay() {
        return null;
      }

      void releaseChildren() {
      }

      void releaseParent() {
      }

      void releaseWidget() {
      }
    };
    assertEquals( 0, shell.getChildren().length );
  }

  public void testDispose() {
    final List disposedWidgets = new ArrayList();
    DisposeListener disposeListener = new DisposeListener() {

      public void widgetDisposed( final DisposeEvent evt ) {
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
    assertEquals( true, button1.isDisposed() );
    assertEquals( false, find( shell.getChildren(), button1 ) );
    assertSame( button1, disposedWidgets.get( 0 ) );
    // Ensure that dispose may be called more than once
    disposedWidgets.clear();
    button1.dispose();
    assertEquals( true, button1.isDisposed() );
    assertEquals( 0, disposedWidgets.size() );
    Button button2 = new Button( shell, SWT.PUSH );
    button2.addDisposeListener( disposeListener );
    shell.dispose();
    assertEquals( 2, disposedWidgets.size() );
    assertSame( shell, disposedWidgets.get( 0 ) );
    assertSame( button2, disposedWidgets.get( 1 ) );
    assertEquals( true, shell.isDisposed() );
    assertEquals( true, button2.isDisposed() );
    // the assert below may not work in the future since getChildren is
    // checkWidget()-protected
    assertEquals( 0, shell.getChildren().length );
    assertEquals( 0, Display.getCurrent().getShells().length );
    // 
    disposedWidgets.clear();
    shell.dispose();
    assertEquals( 0, disposedWidgets.size() );
    assertEquals( true, shell.isDisposed() );
  }

  public void testDisplay() {
    Display display = new Display();
    Composite shell = new Shell( display , SWT.NONE );
    Button button = new Button( shell, SWT.PUSH );
    assertSame( display, shell.getDisplay() );
    assertSame( display, button.getDisplay() );
  }

  private static boolean find( final Widget[] children, final Widget widget ) {
    boolean found = false;
    for( int i = 0; i < children.length; i++ ) {
      if( children[ i ] == widget ) {
        found = true;
      }
    }
    return found;
  }

  protected void setUp() throws Exception {
    RWTFixture.setUp();
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
