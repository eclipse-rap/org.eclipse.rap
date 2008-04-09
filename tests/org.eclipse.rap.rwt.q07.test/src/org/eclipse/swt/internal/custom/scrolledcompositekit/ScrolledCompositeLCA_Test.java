/*******************************************************************************
 * Copyright (c) 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.internal.custom.scrolledcompositekit;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.lifecycle.IWidgetAdapter;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.events.ActivateAdapter;
import org.eclipse.swt.internal.events.ActivateEvent;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.internal.widgets.buttonkit.ButtonLCA;
import org.eclipse.swt.widgets.*;


public class ScrolledCompositeLCA_Test extends TestCase {

  private static final String PROP_V_BAR_SELECTION = "vBarSelection";
  private static final String PROP_H_BAR_SELECTION = "hBarSelection";

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }

  public void testPreserveValues() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    int scStyle = SWT.H_SCROLL | SWT.V_SCROLL;
    ScrolledComposite sc = new ScrolledComposite( shell, scStyle );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( sc );
    assertEquals( null, adapter.getPreserved( PROP_H_BAR_SELECTION ) );
    assertEquals( null, adapter.getPreserved( PROP_V_BAR_SELECTION ) );
    sc.getHorizontalBar().setSelection( 23 );
    sc.getVerticalBar().setSelection( 42 );
    assertEquals( 23, sc.getHorizontalBar().getSelection() );
    assertEquals( 42, sc.getVerticalBar().getSelection() );
    sc.getHorizontalBar().setVisible( true );
    sc.getVerticalBar().setVisible( true );
    Rectangle rectangle = new Rectangle( 12, 30, 20, 40 );
    sc.setBounds( rectangle );
    RWTFixture.markInitialized( display );
    RWTFixture.preserveWidgets();
    assertEquals( new Integer( 23 ),
                  adapter.getPreserved( PROP_H_BAR_SELECTION ) );
    assertEquals( new Integer( 42 ),
                  adapter.getPreserved( PROP_V_BAR_SELECTION ) );
    Object bounds = adapter.getPreserved( ScrolledCompositeLCA.PROP_BOUNDS );
    assertEquals( rectangle, bounds );
    Object overflow = adapter.getPreserved( ScrolledCompositeLCA.PROP_OVERFLOW );
    assertEquals( "scroll", overflow );
    // bound
    sc.setBounds( rectangle );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sc );
    assertEquals( rectangle, adapter.getPreserved( Props.BOUNDS ) );
    RWTFixture.clearPreserved();
    // z-index
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sc );
    assertTrue( adapter.getPreserved( Props.Z_INDEX ) != null );
    RWTFixture.clearPreserved();
    // menu
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sc );
    assertEquals( null, adapter.getPreserved( Props.MENU ) );
    RWTFixture.clearPreserved();
    Menu menu = new Menu( sc );
    MenuItem item = new MenuItem( menu, SWT.NONE );
    item.setText( "1 Item" );
    sc.setMenu( menu );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sc );
    assertEquals( menu, adapter.getPreserved( Props.MENU ) );
    // visible
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sc );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.VISIBLE ) );
    RWTFixture.clearPreserved();
    sc.setVisible( false );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sc );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.VISIBLE ) );
    RWTFixture.clearPreserved();
    // enabled
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sc );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.ENABLED ) );
    RWTFixture.clearPreserved();
    sc.setEnabled( false );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sc );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.ENABLED ) );
    RWTFixture.clearPreserved();
    // control_listeners
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sc );
    Boolean hasListeners
     = ( Boolean )adapter.getPreserved( Props.CONTROL_LISTENERS );
    assertEquals( Boolean.FALSE, hasListeners );
    RWTFixture.clearPreserved();
    sc.addControlListener( new ControlListener() {

      public void controlMoved( final ControlEvent e ) {
      }

      public void controlResized( final ControlEvent e ) {
      }
    } );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sc );
    hasListeners = ( Boolean )adapter.getPreserved( Props.CONTROL_LISTENERS );
    assertEquals( Boolean.TRUE, hasListeners );
    RWTFixture.clearPreserved();
    // foreground background font
    Color background = Graphics.getColor( 122, 33, 203 );
    sc.setBackground( background );
    Color foreground = Graphics.getColor( 211, 178, 211 );
    sc.setForeground( foreground );
    Font font = Graphics.getFont( "font", 12, SWT.BOLD );
    sc.setFont( font );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sc );
    assertEquals( background, adapter.getPreserved( Props.BACKGROUND ) );
    assertEquals( foreground, adapter.getPreserved( Props.FOREGROUND ) );
    assertEquals( font, adapter.getPreserved( Props.FONT ) );
    RWTFixture.clearPreserved();
    // tab_index
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sc );
    assertTrue( adapter.getPreserved( Props.Z_INDEX ) != null );
    RWTFixture.clearPreserved();
    // tooltiptext
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sc );
    assertEquals( null, sc.getToolTipText() );
    RWTFixture.clearPreserved();
    sc.setToolTipText( "some text" );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sc );
    assertEquals( "some text", sc.getToolTipText() );
    RWTFixture.clearPreserved();
    // activate_listeners Focus_listeners
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sc );
    hasListeners = ( Boolean )adapter.getPreserved( Props.FOCUS_LISTENER );
    assertEquals( Boolean.FALSE, hasListeners );
    RWTFixture.clearPreserved();
    sc.addFocusListener( new FocusListener() {

      public void focusGained( final FocusEvent event ) {
      }

      public void focusLost( final FocusEvent event ) {
      }
    } );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sc );
    hasListeners = ( Boolean )adapter.getPreserved( Props.FOCUS_LISTENER );
    assertEquals( Boolean.TRUE, hasListeners );
    RWTFixture.clearPreserved();
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sc );
    hasListeners = ( Boolean )adapter.getPreserved( Props.ACTIVATE_LISTENER );
    assertEquals( Boolean.FALSE, hasListeners );
    RWTFixture.clearPreserved();
    ActivateEvent.addListener( sc, new ActivateAdapter() {
    } );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sc );
    hasListeners = ( Boolean )adapter.getPreserved( Props.ACTIVATE_LISTENER );
    assertEquals( Boolean.TRUE, hasListeners );
    RWTFixture.clearPreserved();
    display.dispose();
  }

  public void testNoBounds() throws Exception {
    // For direct children of ScrolledComposites, no bounds must not be written.
    // This results in negative locations which destroys client-side layout.
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    int scStyle = SWT.H_SCROLL | SWT.V_SCROLL;
    ScrolledComposite sc = new ScrolledComposite( shell, scStyle );
    Button button = new Button( sc, SWT.PUSH );
    RWTFixture.markInitialized( display );
    RWTFixture.preserveWidgets();
    button.setSize( 300, 400 );
    Fixture.fakeResponseWriter();
    ButtonLCA lca = new ButtonLCA();
    lca.renderChanges( button );
    assertTrue( Fixture.getAllMarkup().indexOf( "setSpace" ) == -1 );
  }
}
