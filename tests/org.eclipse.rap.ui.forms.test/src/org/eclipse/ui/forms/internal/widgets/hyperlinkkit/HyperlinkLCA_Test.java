/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.ui.forms.internal.widgets.hyperlinkkit;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.lifecycle.IWidgetAdapter;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.HyperlinkSettings;
import org.eclipse.ui.forms.internal.widgets.FormsControlLCA_Test;
import org.eclipse.ui.forms.internal.widgets.IHyperlinkAdapter;
import org.eclipse.ui.forms.widgets.Hyperlink;

public class HyperlinkLCA_Test extends FormsControlLCA_Test {

  public void testPreserveValues() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    Hyperlink hyperlink = new Hyperlink( shell, SWT.NONE );
    RWTFixture.markInitialized( display );
    RWTFixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( hyperlink );
    String text = ( String )adapter.getPreserved( HyperlinkLCA.PROP_TEXT );
    assertEquals( "", text );
    Boolean underlined
      = ( Boolean )adapter.getPreserved( HyperlinkLCA.PROP_UNDERLINED );
    assertEquals( Boolean.FALSE, underlined );
    Boolean hasListener
      = ( Boolean )adapter.getPreserved( HyperlinkLCA.PROP_SELECTION_LISTENERS );
    assertEquals( Boolean.TRUE, hasListener );
    Color activeForeground
      = ( Color )adapter.getPreserved( HyperlinkLCA.PROP_ACTIVE_FOREGROUND );
    assertEquals( null, activeForeground );
    Color activeBackground
      = ( Color )adapter.getPreserved( HyperlinkLCA.PROP_ACTIVE_BACKGROUND );
    assertEquals( null, activeBackground );
    Integer underlineMode
      = ( Integer )adapter.getPreserved( HyperlinkLCA.PROP_UNDERLINE_MODE );
    assertEquals( 0, underlineMode.intValue() );
    RWTFixture.clearPreserved();
    String newText = "click me";
    hyperlink.setText( newText );
    hyperlink.setUnderlined( true );
    Color newActiveForeground = Graphics.getColor( 0, 0, 255 );
    getAdapter( hyperlink ).setActiveForeground( newActiveForeground );
    Color newActiveBackground = Graphics.getColor( 0, 0, 128 );
    getAdapter( hyperlink ).setActiveBackground( newActiveBackground );
    int newUnderlineMode = HyperlinkSettings.UNDERLINE_HOVER;
    getAdapter( hyperlink ).setUnderlineMode( newUnderlineMode );
    RWTFixture.preserveWidgets();
    text = ( String )adapter.getPreserved( HyperlinkLCA.PROP_TEXT );
    assertEquals( newText, text );
    underlined
      = ( Boolean )adapter.getPreserved( HyperlinkLCA.PROP_UNDERLINED );
    assertEquals( Boolean.TRUE, underlined );
    activeForeground
      = ( Color )adapter.getPreserved( HyperlinkLCA.PROP_ACTIVE_FOREGROUND );
    assertEquals( newActiveForeground, activeForeground );
    activeBackground
      = ( Color )adapter.getPreserved( HyperlinkLCA.PROP_ACTIVE_BACKGROUND );
    assertEquals( newActiveBackground, activeBackground );
    underlineMode
      = ( Integer )adapter.getPreserved( HyperlinkLCA.PROP_UNDERLINE_MODE );
    assertEquals( newUnderlineMode, underlineMode.intValue() );
    // Test preserved control properties
    testPreserveControlProperties( hyperlink );
    display.dispose();
  }

  public void testSelectionEvent() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    Hyperlink hyperlink = new Hyperlink( shell, SWT.NONE );
    testDefaultSelectionEvent( hyperlink );
  }

  private void testDefaultSelectionEvent( final Hyperlink hyperlink ) {
    final StringBuffer log = new StringBuffer();
    Listener listener = new Listener() {
      public void handleEvent( Event event ) {
        assertEquals( hyperlink, event.widget );
        assertEquals( null, event.item );
        assertEquals( SWT.NONE, event.detail );
        assertEquals( 0, event.x );
        assertEquals( 0, event.y );
        assertEquals( 0, event.width );
        assertEquals( 0, event.height );
        assertEquals( true, event.doit );
        log.append( "widgetDefaultSelected" );
      }
    };
    hyperlink.addListener( SWT.DefaultSelection, listener );
    String hyperlinkId = WidgetUtil.getId( hyperlink );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_DEFAULT_SELECTED,
                              hyperlinkId );
    RWTFixture.readDataAndProcessAction( hyperlink );
    assertEquals( "widgetDefaultSelected", log.toString() );
  }

  private static IHyperlinkAdapter getAdapter( final Hyperlink hyperlink ) {
    Object adapter = hyperlink.getAdapter( IHyperlinkAdapter.class );
    return ( IHyperlinkAdapter )adapter;
  }
}
