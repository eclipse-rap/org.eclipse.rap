/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.ui.forms.internal.widgets.formtextkit;

import org.eclipse.rwt.lifecycle.IWidgetAdapter;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.HyperlinkSettings;
import org.eclipse.ui.forms.internal.widgets.FormsControlLCA_Test;
import org.eclipse.ui.forms.internal.widgets.IFormTextAdapter;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.internal.forms.widgets.Paragraph;

public class FormTextLCA_Test extends FormsControlLCA_Test {

  public void testPreserveValues() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    FormText formText = new FormText( shell, SWT.WRAP );
    String text = "<form>"
      + "<p>First paragraph</p>"
      + "<li>First bullet</li>"
      + "<li>Second bullet</li>"
      + "<li>Third bullet</li>"
      + "<p>Second paragraph</p>"
      + "</form>";
    formText.setText( text, true, false );
    RWTFixture.markInitialized( display );
    RWTFixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( formText );
    String prop = FormTextLCA.PROP_PARAGRAPHS;
    Paragraph[] paragraphs
      = ( Paragraph[] )adapter.getPreserved( prop );
    assertEquals( 5, paragraphs.length );
    for( int i = 0; i < paragraphs.length; i++ ) {
      assertSame( paragraphs[ i ], getAdapter( formText ).getParagraphs()[ i ] );
    }
    prop = FormTextLCA.PROP_HYPERLINK_SETTINGS;
    HyperlinkSettings settings
      = ( HyperlinkSettings )adapter.getPreserved( prop );
    assertSame( settings, formText.getHyperlinkSettings() );
    // Test preserved control properties
    testPreserveControlProperties( formText );
    display.dispose();
  }

  private static IFormTextAdapter getAdapter( final FormText formText ) {
    Object adapter = formText.getAdapter( IFormTextAdapter.class );
    return ( IFormTextAdapter )adapter;
  }

}
