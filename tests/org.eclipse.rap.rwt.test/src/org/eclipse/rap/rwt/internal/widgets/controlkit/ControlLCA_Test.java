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

package org.eclipse.rap.rwt.internal.widgets.controlkit;

import java.io.IOException;
import junit.framework.TestCase;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.RWTFixture;
import org.eclipse.rap.rwt.events.ControlEvent;
import org.eclipse.rap.rwt.events.ControlListener;
import org.eclipse.rap.rwt.internal.widgets.IWidgetAdapter;
import org.eclipse.rap.rwt.internal.widgets.Props;
import org.eclipse.rap.rwt.lifecycle.AbstractWidgetLCA;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.widgets.*;
import com.w4t.Fixture;


public class ControlLCA_Test extends TestCase {

  protected void setUp() throws Exception {
    RWTFixture.setUpWithoutResourceManager();
  }
  
  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
  
  public void testPreserveValues() {
    Display display = new Display();
    Composite shell = new Shell( display , RWT.NONE );
    ControlListener controlListener = new ControlListener() {
      public void controlMoved( final ControlEvent event ) {
      }
      public void controlResized( final ControlEvent event ) {
      }
    };
    shell.addControlListener( controlListener );
    RWTFixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( shell );
    assertEquals( adapter.getPreserved( Props.BOUNDS ), shell.getBounds() );
    Boolean listeners;
    listeners = ( Boolean )adapter.getPreserved( Props.CONTROL_LISTENERS );
    assertEquals( Boolean.TRUE, listeners );
  }
  
  public void testWriteVisibility() throws IOException {
    Display display = new Display();
    Shell shell = new Shell( display , RWT.NONE );
    Button button = new Button( shell, RWT.PUSH );
    AbstractWidgetLCA lca = WidgetUtil.getLCA( button );
    
    // Initial JavaScript code must not contain setVisibility()
    Fixture.fakeResponseWriter();
    lca.renderInitialization( button );
    lca.renderChanges( button );
    assertTrue( Fixture.getAllMarkup().indexOf( "setVisibility" ) == -1 );
    
    // Unchanged visible attribute must not be rendered
    Fixture.fakeResponseWriter();
    RWTFixture.markInitialized( button );
    RWTFixture.preserveWidgets();
    lca.renderInitialization( button );
    lca.renderChanges( button );
    assertTrue( Fixture.getAllMarkup().indexOf( "setVisibility" ) == -1 );

    // Changed visible attribute must not be rendered
    Fixture.fakeResponseWriter();
    RWTFixture.preserveWidgets();
    button.setVisible( false );
    lca.renderInitialization( button );
    lca.renderChanges( button );
    assertTrue( Fixture.getAllMarkup().indexOf( "setVisibility" ) != -1 );
  }
}
