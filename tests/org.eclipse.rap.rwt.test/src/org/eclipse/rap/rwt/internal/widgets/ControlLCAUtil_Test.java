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

package org.eclipse.rap.rwt.internal.widgets;

import java.io.IOException;
import junit.framework.TestCase;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.RWTFixture;
import org.eclipse.rap.rwt.graphics.Rectangle;
import org.eclipse.rap.rwt.lifecycle.JSWriter;
import org.eclipse.rap.rwt.widgets.*;
import com.w4t.Fixture;

public class ControlLCAUtil_Test extends TestCase {

  public void testWriteBounds() throws Exception {
    // Ensure that bounds for an uninitialized widget are rendered
    Display display = new Display();
    Composite shell = new Shell( display , RWT.NONE );
    RWTFixture.preserveWidgets();
    Fixture.fakeResponseWriter();
    ControlLCAUtil.writeBounds( shell );
    String expected
      = "w.setSpace( 0, 0, 0, 0 );w.setMinWidth( 0 );w.setMinHeight( 0 );";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );
    // Ensure that unchanged bound do not lead to markup
    Fixture.fakeResponseWriter();
    RWTFixture.markInitialized( shell );
    RWTFixture.clearPreserved();
    RWTFixture.preserveWidgets();
    ControlLCAUtil.writeBounds( shell );
    assertEquals( "", Fixture.getAllMarkup() );
    // Ensure that bounds-changes on an already initialized widgets are rendered
    Fixture.fakeResponseWriter();
    shell.setBounds( new Rectangle( 1, 2, 3, 4 ) );
    ControlLCAUtil.writeBounds( shell );
    expected = "w.setSpace( 1, 3, 2, 4 );";
    assertTrue( Fixture.getAllMarkup().endsWith( expected ) );
  }

  public void testWriteTooolTip() throws IOException {
    Display display = new Display();
    Composite shell = new Shell( display , RWT.NONE );
    // on a not yet initialized control: no tool tip -> no markup
    Fixture.fakeResponseWriter();
    ControlLCAUtil.writeToolTip( shell );
    assertEquals( "", Fixture.getAllMarkup() );
    shell.setToolTipText( "" );
    ControlLCAUtil.writeToolTip( shell );
    assertEquals( "", Fixture.getAllMarkup() );
    // on a not yet initialized control: non-empty tool tip must be rendered
    Fixture.fakeResponseWriter();
    shell.setToolTipText( "abc" );
    ControlLCAUtil.writeToolTip( shell );
    assertTrue( Fixture.getAllMarkup().indexOf( "abc" ) != -1 );
    // on an initialized control: change tooltip from non-empty to empty
    Fixture.fakeResponseWriter();
    RWTFixture.markInitialized( shell );
    RWTFixture.clearPreserved();
    RWTFixture.preserveWidgets();
    shell.setToolTipText( null );
    ControlLCAUtil.writeToolTip( shell );
    assertTrue( Fixture.getAllMarkup().indexOf( "setToolTip" ) != -1 );
    // on an initialized control: change tooltip from non-empty to empty
    Fixture.fakeResponseWriter();
    RWTFixture.markInitialized( shell );
    shell.setToolTipText( "abc" );
    RWTFixture.clearPreserved();
    RWTFixture.preserveWidgets();
    shell.setToolTipText( "newTooltip" );
    ControlLCAUtil.writeToolTip( shell );
    assertTrue( Fixture.getAllMarkup().indexOf( "setToolTip" ) != -1 );
    assertTrue( Fixture.getAllMarkup().indexOf( "newTooltip" ) != -1 );
    // on an initialized control: change non-empty tooltip text
    Fixture.fakeResponseWriter();
    RWTFixture.markInitialized( shell );
    shell.setToolTipText( "newToolTip" );
    RWTFixture.clearPreserved();
    RWTFixture.preserveWidgets();
    shell.setToolTipText( "anotherTooltip" );
    ControlLCAUtil.writeToolTip( shell );
    assertTrue( Fixture.getAllMarkup().indexOf( "setToolTip" ) != -1 );
    assertTrue( Fixture.getAllMarkup().indexOf( "anotherTooltip" ) != -1 );
    // test actual markup - the next two lines fake situation that there is
    // already a widget reference (w)
    JSWriter writer = JSWriter.getWriterFor( shell );
    writer.newWidget( "Window" );
    Fixture.fakeResponseWriter();
    ControlLCAUtil.writeToolTip( shell );
    String expected = "wm.setToolTip( w, \"anotherTooltip\" );";
    assertEquals( expected, Fixture.getAllMarkup() );
  }

  protected void setUp() throws Exception {
    RWTFixture.setUpWithoutResourceManager();
    Fixture.fakeResponseWriter();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
