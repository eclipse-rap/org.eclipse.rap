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

package org.eclipse.swt.internal.widgets.shellkit;

import java.io.IOException;
import junit.framework.TestCase;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.internal.engine.PhaseListenerRegistry;
import org.eclipse.swt.internal.lifecycle.PreserveWidgetsPhaseListener;
import org.eclipse.swt.internal.lifecycle.RWTLifeCycle;
import org.eclipse.swt.internal.widgets.IShellAdapter;
import org.eclipse.swt.lifecycle.*;
import org.eclipse.swt.widgets.*;
import com.w4t.Fixture;
import com.w4t.engine.requests.RequestParams;


public class ShellLCA_Test extends TestCase {
  
  public void testReadDataForClosed() {
    final StringBuffer log = new StringBuffer();
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    shell.open();
    shell.addShellListener( new ShellAdapter() {
      public void shellClosed( final ShellEvent event ) {
        log.append( "closed" );
      }
    } );
    String shellId = WidgetUtil.getId( shell );
    Fixture.fakeRequestParam( JSConst.EVENT_SHELL_CLOSED, shellId );
    RWTFixture.readDataAndProcessAction( shell );
    assertEquals( "closed", log.toString() );
  }
  
  public void testReadDataForActiveControl() {
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    Label label = new Label( shell, SWT.NONE );
    Label otherLabel = new Label( shell, SWT.NONE );
    String displayId = DisplayUtil.getId( display );
    String shellId = WidgetUtil.getId( shell );
    String labelId = WidgetUtil.getId( label );
    String otherLabelId = WidgetUtil.getId( otherLabel );
    
    setActiveControl( shell, otherLabel );
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId  );
    Fixture.fakeRequestParam( shellId + ".activeControl", labelId );
    RWTFixture.readDataAndProcessAction( display );
    assertSame( label, getActiveControl( shell ) );
    
    // Ensure that if there is both, an avtiveControl parameter and a
    // controlActivated event, the activeControl parameter is ignored
    setActiveControl( shell, otherLabel );
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId  );
    Fixture.fakeRequestParam( shellId + ".activeControl", otherLabelId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_ACTIVATED, labelId );
    RWTFixture.readDataAndProcessAction( display );
    assertSame( label, getActiveControl( shell ) );
  }
  
  public void testShellActivate() throws IOException {
    final StringBuffer activateEventLog = new StringBuffer();
    ActivateListener activateListener = new ActivateListener() {
      public void activated( final ActivateEvent event ) {
        Shell shell = ( Shell )event.getSource();
        activateEventLog.append( "activated:" + shell.getData() + "|" );
      }
      public void deactivated( final ActivateEvent event ) {
        Shell shell = ( Shell )event.getSource();
        activateEventLog.append( "deactivated:" + shell.getData() + "|" );
      }
    };
    final StringBuffer shellEventLog = new StringBuffer();
    ShellListener shellListener = new ShellAdapter() {
      public void shellActivated( ShellEvent event ) {
        Shell shell = ( Shell )event.getSource();
        shellEventLog.append( "activated:" + shell.getData() + "|" );
      }
      public void shellDeactivated( ShellEvent event ) {
        Shell shell = ( Shell )event.getSource();
        shellEventLog.append( "deactivated:" + shell.getData() + "|" );
      }
    };
    Display display = new Display();
    Shell shellToActivate = new Shell( display, SWT.NONE );
    shellToActivate.setData( "shellToActivate" );
    shellToActivate.open();
    Shell activeShell = new Shell( display, SWT.NONE );
    activeShell.setData( "activeShell" );
    activeShell.open();
    String displayId = DisplayUtil.getId( display );
    String shellToActivateId = WidgetUtil.getId( shellToActivate );

    // Set precondition and assert it
    PhaseListenerRegistry.add( new PreserveWidgetsPhaseListener() );
    activeShell.setActive();
    assertSame( activeShell, display.getActiveShell() );
    
    // Simulate shell activation without event listeners
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId  );
    Fixture.fakeRequestParam( displayId + ".activeShell", shellToActivateId );
    new RWTLifeCycle().execute();
    assertSame( shellToActivate, display.getActiveShell() );

    // Set precondition and assert it
    RWTFixture.markInitialized( activeShell );
    RWTFixture.markInitialized( shellToActivate );
    RWTFixture.fakeUIThread();
    activeShell.setActive();
    assertSame( activeShell, display.getActiveShell() );
    RWTFixture.removeUIThread();
    
    // Simulate shell activation with event listeners
    ActivateEvent.addListener( shellToActivate, activateListener );
    ActivateEvent.addListener( activeShell, activateListener );
    shellToActivate.addShellListener( shellListener );
    activeShell.addShellListener( shellListener );
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId  );
    Fixture.fakeRequestParam( JSConst.EVENT_SHELL_ACTIVATED, 
                              shellToActivateId );
    new RWTLifeCycle().execute();
    assertSame( shellToActivate, display.getActiveShell() );
    String expected = "deactivated:activeShell|activated:shellToActivate|";
    assertEquals( expected, activateEventLog.toString() );
    assertEquals( expected, shellEventLog.toString() );
    // Ensure that no setActive javaScript code is rendered for client-side
    // activated Shell
    assertEquals( -1, Fixture.getAllMarkup().indexOf( "setActive" ) );
  }
  
  public void testDisposeSingleShell() throws IOException { 
    Display display = new Display(); 
    Shell shell = new Shell( display ); 
    shell.open(); 
    String displayId = DisplayUtil.getId( display ); 
    String shellId = WidgetUtil.getId( shell ); 
    RWTFixture.fakeNewRequest(); 
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId ); 
    Fixture.fakeRequestParam( JSConst.EVENT_SHELL_CLOSED, shellId ); 
    RWTLifeCycle lifeCycle = new RWTLifeCycle(); 
    lifeCycle.execute(); 
    assertEquals( 0, display.getShells().length ); 
    assertEquals( null, display.getActiveShell() ); 
    assertEquals( true, shell.isDisposed() );
  } 
  
  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }
  
  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
  
  private static Control getActiveControl( final Shell shell ) {
    Object adapter = shell.getAdapter( IShellAdapter.class );
    IShellAdapter shellAdapter = ( IShellAdapter )adapter;
    return shellAdapter.getActiveControl();
  }

  private static void setActiveControl( final Shell shell, 
                                        final Control control ) 
  {
    Object adapter = shell.getAdapter( IShellAdapter.class );
    IShellAdapter shellAdapter = ( IShellAdapter )adapter;
    shellAdapter.setActiveControl( control );
  }
}
