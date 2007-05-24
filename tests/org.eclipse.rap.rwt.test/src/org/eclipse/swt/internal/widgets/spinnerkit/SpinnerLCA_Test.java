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

package org.eclipse.swt.internal.widgets.spinnerkit;

import java.io.IOException;
import junit.framework.TestCase;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.internal.lifecycle.RWTLifeCycle;
import org.eclipse.swt.lifecycle.*;
import org.eclipse.swt.widgets.*;
import com.w4t.Fixture;
import com.w4t.engine.requests.RequestParams;

public class SpinnerLCA_Test extends TestCase {

  public void testReadData() throws IOException {
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    Spinner spinner = new Spinner( shell, SWT.NONE );
    String displayId = DisplayUtil.getId( display );
    String spinnerId = WidgetUtil.getId( spinner );
    
    // simulate valid client-side selection 
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( spinnerId + ".selection", "77" );
    new RWTLifeCycle().execute();
    RWTFixture.fakeUIThread();
    assertEquals( 77, spinner.getSelection() );

    // simulate invalid client-side selection 
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( spinnerId + ".selection", "777" );
    RWTFixture.fakeUIThread();
    spinner.setSelection( 1 );
    new RWTLifeCycle().execute();
    RWTFixture.fakeUIThread();
    assertEquals( spinner.getMaximum(), spinner.getSelection() );
    RWTFixture.removeUIThread();
  }
  
  public void testModifyEvent() throws IOException {
    final StringBuffer log = new StringBuffer();
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    final Spinner spinner = new Spinner( shell, SWT.NONE );
    shell.open();
    String displayId = DisplayUtil.getId( display );
    String spinnerId = WidgetUtil.getId( spinner );

    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( spinnerId + ".selection", "1" );
    Fixture.fakeRequestParam( JSConst.EVENT_MODIFY_TEXT, spinnerId );
    new RWTLifeCycle().execute();
    assertEquals( "", log.toString() );
    
    log.setLength( 0 );
    spinner.addModifyListener( new ModifyListener() {
      public void modifyText( final ModifyEvent event ) {
        assertEquals( spinner, event.getSource() );
        log.append( "modifyText" );
      }
    } );
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( spinnerId + ".selection", "2" );
    Fixture.fakeRequestParam( JSConst.EVENT_MODIFY_TEXT, spinnerId );
    new RWTLifeCycle().execute();
    assertEquals( "modifyText", log.toString() );
  }
  
  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
