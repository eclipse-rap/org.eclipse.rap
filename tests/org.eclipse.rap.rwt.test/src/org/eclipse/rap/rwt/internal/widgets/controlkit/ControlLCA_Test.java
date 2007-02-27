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
import org.eclipse.rap.rwt.events.*;
import org.eclipse.rap.rwt.internal.widgets.*;
import org.eclipse.rap.rwt.lifecycle.*;
import org.eclipse.rap.rwt.widgets.*;
import com.w4t.Fixture;
import com.w4t.util.browser.Mozilla1_7up;


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
    RWTFixture.markInitialized( display );
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
    shell.open();
    AbstractWidgetLCA lca = WidgetUtil.getLCA( button );
    
    // Initial JavaScript code must not contain setVisibility()
    Fixture.fakeResponseWriter();
    lca.renderInitialization( button );
    lca.renderChanges( button );
    assertTrue( Fixture.getAllMarkup().indexOf( "setVisibility" ) == -1 );
    
    // Unchanged visible attribute must not be rendered
    Fixture.fakeResponseWriter();
    RWTFixture.markInitialized( display );
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
  
  public void testWriteBounds() throws IOException {
    Fixture.fakeBrowser( new Mozilla1_7up( true, true ) );
    Display display = new Display();
    Shell shell = new Shell( display , RWT.NONE );
    Control control = new Button( shell, RWT.PUSH );
    Composite parent = control.getParent();

    // call writeBounds once to elimniate the uninteresting JavaScript prolog 
    Fixture.fakeResponseWriter();
    WidgetLCAUtil.writeBounds( control, parent, control.getBounds(), false );

    // Test without clip
    Fixture.fakeResponseWriter();
    control.setBounds( 1, 2, 100, 200 );
    WidgetLCAUtil.writeBounds( control, parent, control.getBounds(), false );
    String expected 
      = "w.setSpace( 1, 100, 2, 200 );" 
      + "w.setMinWidth( 0 );w.setMinHeight( 0 );"; 
    assertEquals( expected, Fixture.getAllMarkup() );
    
    // Test with clip
    Fixture.fakeResponseWriter();
    control.setBounds( 1, 2, 100, 200 );
    WidgetLCAUtil.writeBounds( control, parent, control.getBounds(), true );
    expected 
      = "w.setSpace( 1, 100, 2, 200 );" 
      + "w.setMinWidth( 0 );w.setMinHeight( 0 );" 
      + "w.setClipHeight( 200 );w.setClipWidth( 100 );";
    assertEquals( expected, Fixture.getAllMarkup() );
  }
  
  public void testWriteFocusListener() throws IOException {
    FocusAdapter focusListener = new FocusAdapter() {
    };
    Display display = new Display();
    Shell shell = new Shell( display );
    Label label = new Label( shell, RWT.NONE );
    label.addFocusListener( focusListener );
    Button button = new Button( shell, RWT.PUSH );
    button.addFocusListener( focusListener );
    // Test that JavaScript focus listeners are rendered for a focusable control 
    // (e.g. button) 
    Fixture.fakeResponseWriter();
    ControlLCAUtil.writeChanges( button ); // calls writeFocusListener
    String focusGained = "org.eclipse.rap.rwt.EventUtil.focusGained";
    String focusLost = "org.eclipse.rap.rwt.EventUtil.focusLost";
    String markup = Fixture.getAllMarkup();
    assertTrue( markup.indexOf( focusGained ) != -1 );
    assertTrue( markup.indexOf( focusLost ) != -1 );
    
    // Test that for a non-focusable control (e.g. label), no focus-listener
    // JavaScript code is emitted 
    Fixture.fakeResponseWriter();
    ControlLCAUtil.writeChanges( label ); 
    markup = Fixture.getAllMarkup();
    assertEquals( -1, markup.indexOf( focusGained ) );
    assertEquals( -1, markup.indexOf( focusLost ) );
  }
}
