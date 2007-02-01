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

package org.eclipse.rap.rwt.internal.widgets.labelkit;

import java.io.IOException;
import junit.framework.TestCase;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.RWTFixture;
import org.eclipse.rap.rwt.internal.widgets.*;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.widgets.*;
import com.w4t.Fixture;

public class LabelLCA_Test extends TestCase {

  public void testPreserveValues() {
    Display display = new Display();
    Composite shell = new Shell( display , RWT.NONE );
    Label label = new Label( shell, RWT.NONE );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( label );
    assertEquals( null, adapter.getPreserved( Props.TEXT ) );
    label.setText( "xyz" );
    RWTFixture.preserveWidgets();
    assertEquals( label.getText(), adapter.getPreserved( Props.TEXT ) );
    display.dispose();
  }

  public void testRenderText() throws IOException {
    Display display = new Display();
    Shell shell = new Shell( display , RWT.NONE );
    Label label = new Label( shell, RWT.NONE );
    LabelLCA lca = new LabelLCA();
    ControlLCAUtil.preserveValues( label );
    RWTFixture.markInitialized( label );
    Fixture.fakeResponseWriter();
    label.setText( "test" );
    lca.renderChanges( label );
    String expected 
      = "LabelUtil.setText( wm.findWidgetById( \"w1\" ), \"test\" );";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );
    Fixture.fakeResponseWriter();
    label.setText( "\ntest" );
    lca.renderChanges( label );
    expected 
      = "LabelUtil.setText( wm.findWidgetById( \"w1\" ), \"<br/>test\" );";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );
    Fixture.fakeResponseWriter();
    label.setText( "te\nst" );
    lca.renderChanges( label );
    expected 
      = "LabelUtil.setText( wm.findWidgetById( \"w1\" ), \"te<br/>st\" );";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );
    Fixture.fakeResponseWriter();
    label.setText( "test\n" );
    lca.renderChanges( label );
    expected 
      = "LabelUtil.setText( wm.findWidgetById( \"w1\" ), \"test<br/>\" );";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );
    Fixture.fakeResponseWriter();
    label.setText( "te\n\nst" );
    lca.renderChanges( label );
    expected 
      = "LabelUtil.setText( wm.findWidgetById( \"w1\" ), \"te<br/><br/>st\" );";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );
    Fixture.fakeResponseWriter();
    label.setText( "te\ns\nt" );
    lca.renderChanges( label );
    expected 
      = "LabelUtil.setText( wm.findWidgetById( \"w1\" ), \"te<br/>s<br/>t\" );";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );
  }

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
