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

package org.eclipse.rap.rwt.internal.widgets.shellkit;

import junit.framework.TestCase;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.RWTFixture;
import org.eclipse.rap.rwt.events.ShellEvent;
import org.eclipse.rap.rwt.events.ShellListener;
import org.eclipse.rap.rwt.internal.widgets.IShellAdapter;
import org.eclipse.rap.rwt.lifecycle.*;
import org.eclipse.rap.rwt.widgets.*;
import com.w4t.Fixture;
import com.w4t.engine.requests.RequestParams;


public class ShellLCA_Test extends TestCase {
  
  public void testReadDataForClosed() {
    final StringBuffer log = new StringBuffer();
    Display display = new Display();
    Shell shell = new Shell( display , RWT.NONE );
    shell.addShellListener( new ShellListener() {
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
    Shell shell = new Shell( display , RWT.NONE );
    Label label = new Label( shell, RWT.NONE );
    Label otherLabel = new Label( shell, RWT.NONE );
    String shellId = WidgetUtil.getId( shell );
    String labelId = WidgetUtil.getId( label );
    String displayId = DisplayUtil.getId( display );
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
