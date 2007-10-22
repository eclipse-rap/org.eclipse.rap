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

package org.eclipse.swt.internal.widgets.linkkit;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.*;

public class LinkLCA_Test extends TestCase {
  
  public void testSelectionEvent() {
    final StringBuffer log = new StringBuffer();
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    final Link link = new Link( shell, SWT.NONE );
    link.setText( "Big <a>Bang</a>" );
    link.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        log.append( "selectionEvent" );
        assertSame( link, event.getSource() );
        assertEquals( 0, event.detail );
        assertEquals( 0, event.x );
        assertEquals( 0, event.y );
        assertEquals( 0, event.width );
        assertEquals( 0, event.height );
        assertEquals( null, event.item );
        assertEquals( true, event.doit );
      }
    } );
    String linkId = WidgetUtil.getId( link );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, linkId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED + ".index", "0" );
    RWTFixture.readDataAndProcessAction( link );
    assertEquals( "selectionEvent", log.toString() );
  }
  
  public void testRender() throws Exception {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    final Link link = new Link( shell, SWT.NONE );
    link.setText( "Big <a><b>Bang</b></a>" );
    Fixture.fakeResponseWriter();
    LinkLCA lca = new LinkLCA();
    lca.renderChanges( link );
    String markup = Fixture.getAllMarkup();
    assertContains( "LinkUtil.clear( w )", markup );
    assertContains( "LinkUtil.addText( w, \"Big \" )", markup );
    assertContains( "LinkUtil.addLink( w, \"&lt;b&gt;Bang&lt;/b&gt;\", 0 )", markup );
  }
  
  public void testEscape() throws Exception {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    Link link = new Link( shell, SWT.NONE );
    link.setText( "&E<s>ca'pe\" && me" );
    Fixture.fakeResponseWriter();
    LinkLCA lca = new LinkLCA();
    lca.renderChanges( link );
    // TODO [rst] Bug in SWT Link#parse code - adjust when bug is fixed
    // String expected = "\"E&lt;s&gt;ca'pe&quot; &amp; me\"";
    String expected = "\"EE&lt;s&gt;ca'pe&quot;  me\"";
    String actual = Fixture.getAllMarkup();
    assertTrue( actual.indexOf( expected ) != -1 );
  }
  
  private void assertContains( final String expected, final String string ) {
    String message = "'" + expected + "' not contained in '" + string + "'";
    assertTrue( message , string.indexOf( expected ) != -1 );
  }

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
