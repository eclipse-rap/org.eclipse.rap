/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.events;

import java.io.IOException;
import junit.framework.TestCase;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.lifecycle.RWTLifeCycle;
import org.eclipse.swt.lifecycle.DisplayUtil;
import org.eclipse.swt.lifecycle.WidgetUtil;
import org.eclipse.swt.widgets.*;
import com.w4t.Fixture;
import com.w4t.engine.requests.RequestParams;


public class FocusEvent_Test extends TestCase {
  
  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }
  
  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
  
  public void testFocusLost() throws IOException {
    final StringBuffer log = new StringBuffer();
    Display display = new Display();
    Shell shell = new Shell( display );
    final Control unfocusControl = new Button( shell, SWT.PUSH );
    shell.open();
    unfocusControl.setFocus();
    unfocusControl.addFocusListener( new FocusAdapter() {
      public void focusLost( final FocusEvent event ) {
        log.append( "focusLost" );
        assertSame( unfocusControl, event.getSource() );
      }
      public void focusGained( final FocusEvent e ) {
        fail( "Unexpected event: focusGained" );
      }
    } );
    Control focusControl = new Button( shell, SWT.PUSH );
    String displayId = DisplayUtil.getId( display );
    String focusControlId = WidgetUtil.getId( focusControl );

    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( displayId + ".focusControl", focusControlId );
    Fixture.fakeRequestParam( "org.eclipse.swt.events.focusLost", 
                              focusControlId );
    new RWTLifeCycle().execute();
    assertEquals( "focusLost", log.toString() );
  }
  
  public void testFocusGained() throws IOException {
    final StringBuffer log = new StringBuffer();
    Display display = new Display();
    Shell shell = new Shell( display );
    shell.open();
    final Control control = new Button( shell, SWT.PUSH );
    control.addFocusListener( new FocusAdapter() {
      public void focusLost( final FocusEvent e ) {
        fail( "Unexpected event: focusLost" );
      }
      public void focusGained( final FocusEvent event ) {
        log.append( "focusGained" );
        assertSame( control, event.getSource() );
      }
    } );
    String displayId = DisplayUtil.getId( display );
    String controlId = WidgetUtil.getId( control );
    
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( displayId + ".focusControl", controlId );
    Fixture.fakeRequestParam( "org.eclipse.swt.events.focusGained", 
                              controlId );
    new RWTLifeCycle().execute();
    assertEquals( "focusGained", log.toString() );
  }
}
