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

package org.eclipse.swt.internal.widgets;

import java.io.IOException;
import junit.framework.TestCase;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.lifecycle.IDisplayLifeCycleAdapter;
import org.eclipse.swt.lifecycle.*;
import org.eclipse.swt.widgets.*;
import com.w4t.Fixture;
import com.w4t.engine.requests.RequestParams;
import com.w4t.util.browser.Ie6;

public class WidgetAdapter_Test extends TestCase {

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }

  public void testGetAdapterForDisplay() {
    Display display = new Display();
    Object adapter1 = display.getAdapter( IWidgetAdapter.class );
    assertTrue( adapter1 instanceof IWidgetAdapter );
    Object adapter2 = display.getAdapter( IWidgetAdapter.class );
    assertSame( adapter1, adapter2 );
    display.dispose();
    display = new Display();
    Object adapter3 = display.getAdapter( IWidgetAdapter.class );
    assertTrue( adapter3 != adapter2 );
    display.dispose();
  }

  public void testGetAdapterForShell() {
    Display display = new Display();
    Composite shell = new Shell( display , SWT.NONE );
    Object adapter1 = shell.getAdapter( IWidgetAdapter.class );
    assertTrue( adapter1 instanceof IWidgetAdapter );
    shell = new Shell( display , SWT.NONE );
    Object adapter2 = shell.getAdapter( IWidgetAdapter.class );
    assertTrue( adapter1 != adapter2 );
    display.dispose();
  }

  public void testGetAdapterForButton() {
    Display display = new Display();
    Composite shell = new Shell( display , SWT.NONE );
    Button button1 = new Button( shell, SWT.PUSH );
    Object adapter1 = button1.getAdapter( IWidgetAdapter.class );
    assertTrue( adapter1 instanceof IWidgetAdapter );
    Button button2 = new Button( shell, SWT.PUSH );
    Object adapter2 = button2.getAdapter( IWidgetAdapter.class );
    assertTrue( adapter1 != adapter2 );
    display.dispose();
  }

  public void testId() {
    Display display = new Display();
    IWidgetAdapter adapter1
      = ( IWidgetAdapter )display.getAdapter( IWidgetAdapter.class );
    display.dispose();
    display = new Display();
    IWidgetAdapter adapter2
      = ( IWidgetAdapter )display.getAdapter( IWidgetAdapter.class );
    assertTrue( adapter1.getId() != adapter2.getId() );
    display.dispose();
  }

  public void testInitializedForShell() throws IOException {
    Fixture.fakeResponseWriter();
    Fixture.fakeRequestParam( RequestParams.UIROOT, "w1" );
    Display display = new Display();
    Composite shell = new Shell( display , SWT.NONE );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( shell );
    assertEquals( false, adapter.isInitialized() );
    DisplayUtil.getLCA( display ).render( display );
    assertEquals( true, adapter.isInitialized() );
  }

  public void testInitializedForDisplay() throws IOException {
    Display display = new Display();
    IWidgetAdapter adapter = DisplayUtil.getAdapter( display );
    Fixture.fakeBrowser( new Ie6( true, true ) );
    Fixture.fakeResponseWriter();
    DisplayUtil.getLCA( display ).render( display );
    assertEquals( false, adapter.isInitialized() );
    Fixture.fakeRequestParam( RequestParams.UIROOT, "w1" );
    DisplayUtil.getLCA( display ).render( display );
    assertEquals( true, adapter.isInitialized() );
    DisplayUtil.getLCA( display ).render( display );
    assertEquals( true, adapter.isInitialized() );
  }
  
  public void testRenderRunnable() throws IOException {
    final StringBuffer log = new StringBuffer();
    Display display = new Display();
    Composite shell = new Shell( display , SWT.NONE );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( shell );
    IRenderRunnable runnable = new IRenderRunnable() {
      public void afterRender() throws IOException {
        log.append( "executed" );
      }
    };
    adapter.setRenderRunnable( runnable );
    assertSame( runnable, adapter.getRenderRunnable() );

    // ensure that renderRunnable can only be set once
    try {
      IRenderRunnable otherRunnable = new IRenderRunnable() {
        public void afterRender() throws IOException {
          // do nothing
        }
      };
      adapter.setRenderRunnable( otherRunnable );
      fail( "Must not allow to set renderRunnable twice" );
    } catch( IllegalStateException e ) {
      // expected
    }
    
    // ensure that renderRunnable is executed and cleared at the end of 
    // request/DisplayLCA
    log.setLength( 0 );
    Fixture.fakeResponseWriter();
    Fixture.fakeBrowser( new Ie6( true, true ) );
    Fixture.fakeRequestParam( RequestParams.UIROOT, "w1" );
    IDisplayLifeCycleAdapter displayLCA = DisplayUtil.getLCA( display );
    displayLCA.render( display );
    assertEquals( "executed", log.toString() );
    assertEquals( null, adapter.getRenderRunnable() );
  }
}
