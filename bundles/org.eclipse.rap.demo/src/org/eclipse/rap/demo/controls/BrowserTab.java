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

package org.eclipse.rap.demo.controls;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

class BrowserTab extends ExampleTab {

  private static final String DEFAULT_HTML 
    = "<html>" 
    + "<head></head>"
    + "<body><p>Hello World</p></body>"
    + "</html>";
  
  private Browser browser;

  public BrowserTab( final TabFolder folder ) {
    super( folder, "Browser" );
  }

  // TODO [rh] change signature to createStyleControls( Composite parent )
  protected void createStyleControls() {
    // TODO [rh] reactivate when implemented in Browser widget
    createStyleButton( "BORDER" );
//    createStyleButton( "FLAT" );
    createVisibilityButton();
//    createEnablementButton();
    createUrlSelector( styleComp );
    createHtmlSelector( styleComp );
  }

  protected void createExampleControls( final Composite parent ) {
    parent.setLayout( new FillLayout() );
    browser = new Browser( parent, getStyle() );
    registerControl( browser );
  }

  private void createUrlSelector( final Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayout( new RowLayout( SWT.HORIZONTAL ) );
    Label label = new Label( composite, SWT.NONE );
    label.setLayoutData( new RowData( 35, 20 ) );
    label.setText( "URL" );
    final Text text = new Text( composite, SWT.BORDER );
    text.setLayoutData( new RowData( 170, 20 ) );
    text.setText( "http://eclipse.org/rap" );
    Button button = new Button( composite, SWT.PUSH );
    button.setLayoutData( new RowData( 45, 20 ) );
    button.setText( "Go" );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        browser.setUrl( text.getText() );
      }
    } );
  }

  private void createHtmlSelector( final Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayout( new RowLayout( SWT.VERTICAL ) );
    Label label = new Label( composite, SWT.NONE );
    label.setLayoutData( new RowData( 50, 18 ) );
    label.setText( "HTML" );
    final Text text = new Text( composite, SWT.BORDER | SWT.MULTI );
    text.setText( DEFAULT_HTML );
    text.setLayoutData( new RowData( 200, 250 ) );
    Button button = new Button( composite, SWT.PUSH );
    button.setLayoutData( new RowData( 50, 20 ) );
    button.setText( "Go" );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        browser.setText( text.getText() );
      }
    } );
  }
}
