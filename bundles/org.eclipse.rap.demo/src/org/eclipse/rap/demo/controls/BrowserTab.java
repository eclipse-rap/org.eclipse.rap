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

import org.eclipse.rwt.widgets.ExternalBrowser;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

final class BrowserTab extends ExampleTab {

  private static final String DEFAULT_HTML 
    = "<html>\n"
    + "<head>\n"
    + "<script type=\"text/javascript\">\n"
    + "  function show( msg ) {\n"
    + "    alert( msg );\n"
    + "}\n"
    + "</script>\n"
    + "</head>\n"
    + "<body>\n" 
    + "  <p id=\"a\">Hello World</p>\n" 
    + "</body>\n"
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
    createUrlAndHTMLSelector( parent );
    createExternalBrowserSelector( parent );
  }

  protected void createExampleControls( final Composite parent ) {
    parent.setLayout( new FillLayout() );
    browser = new Browser( parent, getStyle() );
    registerControl( browser );
  }

  private void createUrlAndHTMLSelector( final Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayout( new GridLayout( 3, false ) );
    Label lblURL = new Label( composite, SWT.NONE );
    lblURL.setText( "URL" );
    final Text txtURL = new Text( composite, SWT.BORDER );
    txtURL.setText( "http://eclipse.org/rap" );
    Button btnURL = new Button( composite, SWT.PUSH );
    btnURL.setText( "Go" );
    btnURL.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        browser.setUrl( txtURL.getText() );
      }
    } );
    
    final Label lblHTML = new Label( composite, SWT.NONE );
    lblHTML.setText( "HTML" );
    lblHTML.setLayoutData( new GridData( GridData.VERTICAL_ALIGN_BEGINNING ) );
    final Text txtHTML = new Text( composite, SWT.BORDER | SWT.MULTI );
    txtHTML.setText( DEFAULT_HTML );
    lblHTML.addControlListener( new ControlAdapter() {
      public void controlResized( final ControlEvent evt ) {
        GridData data
          = new GridData( txtURL.getSize().x, lblHTML.getSize().y * 8 );
        txtHTML.setLayoutData( data );
      }
    } );
    
    Button btnHTML = new Button( composite, SWT.PUSH );
    btnHTML.setText( "Go" );
    btnHTML.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        browser.setText( txtHTML.getText() );
      }
    } );
    btnHTML.setLayoutData( new GridData( GridData.VERTICAL_ALIGN_BEGINNING ) );
    
    Label lblExecute = new Label( composite, SWT.NONE );
    lblExecute.setText( "Execute" );
    final Text txtExecute = new Text( composite, SWT.BORDER );
    Button btnExecButton = new Button( composite, SWT.PUSH );
    btnExecButton.setText( "Go" );
    btnExecButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        boolean result = browser.execute( txtExecute.getText() );
        String msg = result
                   ? "Execution was successful."
                   : "Execution was not successful.";
        log( msg );
      }
    });
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
        boolean locationBar = cbLocationBar.getSelection();
        boolean statusBar = cbStatusBar.getSelection();
        boolean navigationBar = cbNavigationBar.getSelection();
        int style = computeStyle( locationBar, statusBar, navigationBar );
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
    Button btnMailTo = new Button( group, SWT.PUSH );
    btnMailTo.setText( "mailto:..." );
    btnMailTo.setLayoutData( horizontalSpan2() );
    btnMailTo.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        boolean locationBar = cbLocationBar.getSelection();
        boolean statusBar = cbStatusBar.getSelection();
        boolean navigationBar = cbNavigationBar.getSelection();
        int style = computeStyle( locationBar, statusBar, navigationBar );
        ExternalBrowser.open( "mailto", "mailto:someone@nowhere.org", style );
        ExternalBrowser.close( "mailto" );
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

  private static int computeStyle( final boolean locationBar,
                                   final boolean statusBar,
                                   final boolean navigationBar )
  {
    int style = 0;
    if( locationBar ) {
      style |= ExternalBrowser.LOCATION_BAR;
    }
    if( navigationBar ) {
      style |= ExternalBrowser.NAVIGATION_BAR;
    }
    if( statusBar ) {
      style |= ExternalBrowser.STATUS;
    }
    return style;
  }
}
