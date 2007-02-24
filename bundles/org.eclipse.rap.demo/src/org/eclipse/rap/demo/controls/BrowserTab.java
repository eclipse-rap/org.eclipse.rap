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

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.browser.Browser;
import org.eclipse.rap.rwt.events.SelectionAdapter;
import org.eclipse.rap.rwt.events.SelectionEvent;
import org.eclipse.rap.rwt.layout.*;
import org.eclipse.rap.rwt.widgets.*;

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
    Composite composite = new Composite( parent, RWT.NONE );
    composite.setLayout( new RowLayout( RWT.HORIZONTAL ) );
    Label label = new Label( composite, RWT.NONE );
    label.setLayoutData( new RowData( 35, 20 ) );
    label.setText( "URL" );
    final Text text = new Text( composite, RWT.BORDER );
    text.setLayoutData( new RowData( 170, 20 ) );
    text.setText( "http://eclipse.org/rap" );
    Button button = new Button( composite, RWT.PUSH );
    button.setLayoutData( new RowData( 45, 20 ) );
    button.setText( "Go" );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        browser.setUrl( text.getText() );
      }
    } );
  }

  private void createHtmlSelector( final Composite parent ) {
    Composite composite = new Composite( parent, RWT.NONE );
    composite.setLayout( new RowLayout( RWT.VERTICAL ) );
    Label label = new Label( composite, RWT.NONE );
    label.setLayoutData( new RowData( 50, 18 ) );
    label.setText( "HTML" );
    final Text text = new Text( composite, RWT.BORDER | RWT.MULTI );
    text.setText( DEFAULT_HTML );
    text.setLayoutData( new RowData( 200, 250 ) );
    Button button = new Button( composite, RWT.PUSH );
    button.setLayoutData( new RowData( 50, 20 ) );
    button.setText( "Go" );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        browser.setText( text.getText() );
      }
    } );
  }
}
