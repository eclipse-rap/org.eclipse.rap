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
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.externalbrowser.ExternalBrowser;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

class BrowserTab extends ExampleTab {

  private static final String DEFAULT_HTML 
    = "<html>" 
    + "<head></head>"
    + "<body><p>Hello World</p></body>"
    + "</html>";
  
  private Browser browser;

  public BrowserTab( final CTabFolder folder ) {
    super( folder, "Browser" );
  }

  protected void createStyleControls( final Composite parent ) {
    // TODO [rh] reactivate when implemented in Browser widget
    createStyleButton( "BORDER", SWT.BORDER );
    createVisibilityButton();
//    createEnablementButton();
    createUrlSelector( parent );
    createHtmlSelector( parent );
    createExternalBrowserSelector( parent );
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
    text.setLayoutData( new RowData( 200, 150 ) );
    Button button = new Button( composite, SWT.PUSH );
    button.setLayoutData( new RowData( 50, 20 ) );
    button.setText( "Go" );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        browser.setText( text.getText() );
      }
    } );
  }

  private void createExternalBrowserSelector( final Composite parent ) {
    Composite group = new Composite( parent, SWT.NONE );
    group.setLayout( new GridLayout( 2, false ) );
    Label lblExternalBrowser = new Label( group, SWT.NONE );
    lblExternalBrowser.setText( "External Browser" );
    lblExternalBrowser.setLayoutData( horizontalSpan2() );
    Label lblId = new Label( group, SWT.NONE );
    lblId.setText( "Id" );
    final Text txtId = new Text( group, SWT.BORDER );
    txtId.setLayoutData( grapExcessHorizontalSpace() );
    txtId.setText( "1" );
    Label lblUrl = new Label( group, SWT.NONE );
    lblUrl.setText( "URL" );
    final Text txtUrl = new Text( group, SWT.BORDER );
    txtUrl.setLayoutData( grapExcessHorizontalSpace() );
    txtUrl.setText( "http://eclipse.org/rap" );
    final Button cbLocationBar = new Button( group, SWT.CHECK );
    cbLocationBar.setLayoutData( horizontalSpan2() );
    cbLocationBar.setText( "LOCATION_BAR" );
    final Button cbNavigationBar = new Button( group, SWT.CHECK );
    cbNavigationBar.setLayoutData( horizontalSpan2() );
    cbNavigationBar.setText( "NAVIGATION_BAR" );
    final Button cbStatusBar = new Button( group, SWT.CHECK );
    cbStatusBar.setLayoutData( horizontalSpan2() );
    cbStatusBar.setText( "STATUS" );
    Button btnOpen = new Button( group, SWT.PUSH );
    btnOpen.setLayoutData( horizontalSpan2() );
    btnOpen.setText( "open( id, url, style )" );
    btnOpen.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        int style = 0;
        if( cbLocationBar.getSelection() ) {
          style |= ExternalBrowser.LOCATION_BAR;
        }
        if( cbNavigationBar.getSelection() ) {
          style |= ExternalBrowser.NAVIGATION_BAR;
        }
        if( cbStatusBar.getSelection() ) {
          style |= ExternalBrowser.STATUS;
        }
        ExternalBrowser.open( txtId.getText(), txtUrl.getText(), style );
      }
    } );
    Button btnClose = new Button( group, SWT.PUSH );
    btnClose.setLayoutData( horizontalSpan2() );
    btnClose.setText( "close( id )" );
    btnClose.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        ExternalBrowser.close( txtId.getText() );
      }
    } );
  }
  
  private static GridData horizontalSpan2() {
    GridData result = new GridData();
    result.horizontalSpan = 2;
    return result;
  }
  
  private static GridData grapExcessHorizontalSpace() {
    GridData result = new GridData( SWT.FILL, SWT.CENTER, true, false );
//    result.grabExcessHorizontalSpace = true;
    return result;
  }
}
