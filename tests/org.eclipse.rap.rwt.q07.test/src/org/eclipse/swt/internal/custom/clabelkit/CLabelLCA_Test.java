/*******************************************************************************
 * Copyright (c) 2009, 2010 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.custom.clabelkit;

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


public class CLabelLCA_Test extends TestCase {

  public void testScalePreserveValues() {
    Display display = new Display();
    Shell shell = new Shell( display );
    CLabel label = new CLabel( shell, SWT.NONE );
    Fixture.markInitialized( display );
    label.setText( "text" );
    label.setAlignment( SWT.LEFT );
    label.setImage( Graphics.getImage( Fixture.IMAGE_100x50 ) );
    label.setMargins( 1, 2, 3, 4 );
    Fixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( label );
    String text = ( String )adapter.getPreserved( CLabelLCA.PROP_TEXT );
    assertEquals( "text", text );
    Image image = ( Image )adapter.getPreserved( CLabelLCA.PROP_IMAGE );
    assertEquals( Graphics.getImage( Fixture.IMAGE_100x50 ), image );
    Integer alignment
      = ( Integer )adapter.getPreserved( CLabelLCA.PROP_ALIGNMENT );
    assertEquals( SWT.LEFT, alignment.intValue() );
    Integer leftMargin
      = ( Integer )adapter.getPreserved( CLabelLCA.PROP_LEFT_MARGIN );
    assertEquals( 1, leftMargin.intValue() );
    Integer topMargin
      = ( Integer )adapter.getPreserved( CLabelLCA.PROP_TOP_MARGIN );
    assertEquals( 2, topMargin.intValue() );
    Integer rightMargin
      = ( Integer )adapter.getPreserved( CLabelLCA.PROP_RIGHT_MARGIN );
    assertEquals( 3, rightMargin.intValue() );
    Integer bottomMargin
      = ( Integer )adapter.getPreserved( CLabelLCA.PROP_BOTTOM_MARGIN );
    assertEquals( 4, bottomMargin.intValue() );
  }

  /*
   * 280166: [CLabel] script injection vulnerability
   * https://bugs.eclipse.org/bugs/show_bug.cgi?id=280166
   */
  public void testTextEncoding() throws IOException {
    Display display = new Display();
    Shell shell = new Shell( display );
    CLabel label = new CLabel( shell, SWT.NONE );
    label.setText( "<bad script>" );
    AbstractWidgetLCA lca = WidgetUtil.getLCA( label );
    lca.renderChanges( label );
    String markup = Fixture.getAllMarkup();
    assertEquals( -1, markup.indexOf( "<bad script>" ) );
  }

  /*
   * 280291: [CLabel] causes NullPointerException when rendered uninitialized
   * https://bugs.eclipse.org/bugs/show_bug.cgi?id=280291
   */
  public void testWriteText() throws IOException {
    Display display = new Display();
    Shell shell = new Shell( display );
    CLabel label = new CLabel( shell, SWT.NONE );
    assertNull( label.getText() ); // assert precondition: text == null
    AbstractWidgetLCA lca = WidgetUtil.getLCA( label );
    lca.renderChanges( label );
    // the purpose of this test is to ensure that the LCA works without throwing
    // an exception - thus there is no assert
  }

  public void testNewLines() throws IOException {
    Display display = new Display();
    Shell shell = new Shell( display );
    CLabel label = new CLabel( shell, SWT.NONE );
    label.setText( "First line\nSecond Line\n" );
    AbstractWidgetLCA lca = WidgetUtil.getLCA( label );
    lca.renderChanges( label );
    String markup = Fixture.getAllMarkup();
    assertTrue( markup.indexOf( "First line<br/>Second Line<br/>" ) != -1 );
  }

  protected void setUp() throws Exception {
    Fixture.setUp();
    Fixture.fakeResponseWriter();
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
}
