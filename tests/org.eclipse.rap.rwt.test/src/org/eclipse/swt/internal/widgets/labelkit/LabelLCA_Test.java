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

package org.eclipse.swt.internal.widgets.labelkit;

import java.io.IOException;
import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.*;

public class LabelLCA_Test extends TestCase {

  public void testPreserveValues() {
    Display display = new Display();
    Composite shell = new Shell( display , SWT.NONE );
    Label label = new Label( shell, SWT.NONE );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( label );
    assertEquals( null, adapter.getPreserved( Props.TEXT ) );
    label.setText( "xyz" );
    RWTFixture.markInitialized( display );
    RWTFixture.preserveWidgets();
    assertEquals( label.getText(), adapter.getPreserved( Props.TEXT ) );
    display.dispose();
  }

  public void testRenderText() throws IOException {
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    Label label = new Label( shell, SWT.NONE );
    LabelLCA lca = new LabelLCA();
    ControlLCAUtil.preserveValues( label );
    RWTFixture.markInitialized( label );
    Fixture.fakeResponseWriter();
    label.setText( "test" );
    lca.renderChanges( label );
    String expected
      = "LabelUtil.setText( wm.findWidgetById( \"w2\" ), \"test\" );";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );
    label.setText( "\ntest" );
    lca.renderChanges( label );
    expected
      = "LabelUtil.setText( wm.findWidgetById( \"w2\" ), \"<br/>test\" );";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );
    label.setText( "te\nst" );
    lca.renderChanges( label );
    expected
      = "LabelUtil.setText( wm.findWidgetById( \"w2\" ), \"te<br/>st\" );";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );
    label.setText( "test\n" );
    lca.renderChanges( label );
    expected
      = "LabelUtil.setText( wm.findWidgetById( \"w2\" ), \"test<br/>\" );";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );
    label.setText( "te\n\nst" );
    lca.renderChanges( label );
    expected
      = "LabelUtil.setText( wm.findWidgetById( \"w2\" ), \"te<br/><br/>st\" );";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );
    label.setText( "te\ns\nt" );
    lca.renderChanges( label );
    expected
      = "LabelUtil.setText( wm.findWidgetById( \"w2\" ), \"te<br/>s<br/>t\" );";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );
  }
  
  public void testEscape() throws Exception {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    Label label = new Label( shell, SWT.NONE );
    label.setText( "&E<s>ca'pe\" && text" );
    label.setToolTipText( "&E<s>ca'pe\" && tooltip" );
    Fixture.fakeResponseWriter();
    StandardLabelLCA lca = new StandardLabelLCA();
    lca.renderChanges( label );
    String expected1 = "\"E&lt;s&gt;ca'pe&quot; &amp; text\"";
    String expected2 = "\"&amp;E&lt;s&gt;ca'pe&quot; &amp;&amp; tooltip\"";
    String actual = Fixture.getAllMarkup();
    assertTrue( actual.indexOf( expected1 ) != -1 );
    assertTrue( actual.indexOf( expected2 ) != -1 );
  }
  
  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }
  
  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
