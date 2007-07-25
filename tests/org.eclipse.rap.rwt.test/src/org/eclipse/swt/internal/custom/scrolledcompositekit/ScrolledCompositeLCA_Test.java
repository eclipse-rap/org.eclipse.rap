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

import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.internal.widgets.buttonkit.ButtonLCA;
import org.eclipse.swt.lifecycle.IWidgetAdapter;
import org.eclipse.swt.lifecycle.WidgetUtil;
import org.eclipse.swt.widgets.*;

import com.w4t.Fixture;

public class ScrolledCompositeLCA_Test extends TestCase {

  private static final String PROP_V_BAR_SELECTION = "vBarSelection";
  private static final String PROP_H_BAR_SELECTION = "hBarSelection";

  public void testPreserveValues() {
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
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
    RWTFixture.markInitialized( display );
    RWTFixture.preserveWidgets();
    assertEquals( new Integer( 23 ),
                  adapter.getPreserved( PROP_H_BAR_SELECTION ) );
    assertEquals( new Integer( 42 ),
                  adapter.getPreserved( PROP_V_BAR_SELECTION ) );
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

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
