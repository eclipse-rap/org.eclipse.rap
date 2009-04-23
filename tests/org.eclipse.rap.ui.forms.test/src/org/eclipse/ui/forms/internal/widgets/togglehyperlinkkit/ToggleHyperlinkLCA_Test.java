/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.ui.forms.internal.widgets.togglehyperlinkkit;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.lifecycle.IWidgetAdapter;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.internal.widgets.FormsControlLCA_Test;
import org.eclipse.ui.forms.widgets.ToggleHyperlink;
import org.eclipse.ui.forms.widgets.Twistie;

public class ToggleHyperlinkLCA_Test extends FormsControlLCA_Test {

  public void testPreserveValues() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    Twistie twistie = new Twistie( shell, SWT.NONE );
    RWTFixture.markInitialized( display );
    RWTFixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( twistie );
    String prop = ToggleHyperlinkLCA.PROP_EXPANDED;
    Boolean expanded = ( Boolean )adapter.getPreserved( prop );
    assertEquals( Boolean.FALSE, expanded );
    prop = ToggleHyperlinkLCA.PROP_SELECTION_LISTENERS;
    Boolean hasListener = ( Boolean )adapter.getPreserved( prop );
    assertEquals( Boolean.TRUE, hasListener );
    RWTFixture.clearPreserved();
    twistie.setExpanded( true );
    RWTFixture.preserveWidgets();
    expanded = ( Boolean )adapter.getPreserved( prop );
    assertEquals( Boolean.TRUE, expanded );
    // Test preserved control properties
    testPreserveControlProperties( twistie );
    display.dispose();
  }

  public void testSelectionEvent() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    Twistie twistie = new Twistie( shell, SWT.NONE );
    testDefaultSelectionEvent( twistie );
  }

  private void testDefaultSelectionEvent( final ToggleHyperlink hyperlink ) {
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
}
