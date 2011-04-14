/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/

package org.eclipse.rap.demo.controls;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.rwt.widgets.ExternalBrowser;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.*;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

final class BrowserTab extends ExampleTab {

  private static final String PROP_PROGRESS_LISTENER = "progressListener";

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
  private BrowserFunction function;

  public BrowserTab( CTabFolder folder ) {
    super( folder, "Browser" );
  }

  protected void createStyleControls( Composite parent ) {
    createStyleButton( "BORDER", SWT.BORDER );
    createVisibilityButton();
    createEnablementButton();
    createUrlAndHTMLSelector( parent );
    createPropertyCheckbox( "Add Progress Listener", PROP_PROGRESS_LISTENER );
    createExternalBrowserSelector( parent );
    createBrowserFunctionSelector( parent );
  }

  protected void createExampleControls( Composite parent ) {
    parent.setLayout( new FillLayout() );
    browser = new Browser( parent, getStyle() );
    if( hasCreateProperty( PROP_PROGRESS_LISTENER ) ) {
      browser.addProgressListener( new ProgressListener() {
        public void changed( final ProgressEvent event ) {
          log( "changed: " + event );
        }

        public void completed( final ProgressEvent event ) {
          log( "completed: " + event );
        }
      } );
    }
    registerControl( browser );
  }

  private void createUrlAndHTMLSelector( Composite parent ) {
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
    txtHTML.setLayoutData( new GridData( 110, 100 ) );

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

  private void createExternalBrowserSelector( Composite parent ) {
    Group group = new Group( parent, SWT.NONE );
    group.setLayout( new GridLayout( 2, false ) );
    group.setText( "External Browser" );
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

  private void createBrowserFunctionSelector( Composite parent ) {
    Group group = new Group( parent, SWT.NONE );
    group.setText( "BrowserFunction" );
    group.setLayout( new GridLayout( 3, false ) );
    final Label lblHTML = new Label( group, SWT.NONE );
    lblHTML.setText( "HTML" );
    lblHTML.setLayoutData( new GridData( GridData.VERTICAL_ALIGN_BEGINNING ) );
    final Text txtHTML = new Text( group, SWT.BORDER | SWT.MULTI );
    txtHTML.setText( createBrowserFunctionHTML() );
    txtHTML.setLayoutData( new GridData( 200, 200 ) );

    Button btnHTML = new Button( group, SWT.PUSH );
    btnHTML.setText( "Go" );
    btnHTML.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        browser.setText( txtHTML.getText() );
        function = new CustomFunction( browser, "theJavaFunction" );
      }
    } );
    btnHTML.setLayoutData( new GridData( GridData.VERTICAL_ALIGN_BEGINNING ) );
    GridData buttonsGridData = new GridData();
    buttonsGridData.horizontalSpan = 3;
    Button createButton = new Button( group, SWT.PUSH );
    createButton.setLayoutData( buttonsGridData );
    createButton.setText( "Create theJavaFunction" );
    createButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event) {
        function = new CustomFunction( browser, "theJavaFunction" );
      }
    } );
    Button disposeButton = new Button( group, SWT.PUSH );
    disposeButton.setLayoutData( buttonsGridData );
    disposeButton.setText( "Dispose theJavaFunction" );
    disposeButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event) {
        function.dispose();
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

  private static int computeStyle( boolean locationBar, boolean statusBar, boolean navigationBar ) {
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

  private String createBrowserFunctionHTML() {
    StringBuffer buffer = new StringBuffer();
    buffer.append( "<html>\n" );
    buffer.append( "<head>\n" );
    buffer.append( "<script language=\"JavaScript\">\n" );
    buffer.append( "function function1() {\n" );
    buffer.append( "    var result;\n" );
    buffer.append( "    try {\n" );
    buffer.append( "        result = theJavaFunction(12, false, null, [3.6, ['swt', true]], 'eclipse');\n" );
    buffer.append( "    } catch (e) {\n" );
    buffer.append( "        alert('a java error occurred: ' + e.message);\n" );
    buffer.append( "        return;\n" );
    buffer.append( "    }\n" );
//    buffer.append( "    for (var i = 0; i < result.length; i++) {\n" );
//    buffer.append( "        alert('returned ' + i + ': ' + result[i]);\n" );
//    buffer.append( "    }\n" );
    buffer.append( "}\n" );
    buffer.append( "</script>\n" );
    buffer.append( "</head>\n" );
    buffer.append( "<body>\n" );
    buffer.append( "<input id=button type=\"button\" value=\"Push to Invoke Java\" onclick=\"function1();\">\n" );
    buffer.append( "</body>\n" );
    buffer.append( "</html>\n" );
    return buffer.toString();
  }

  private class CustomFunction extends BrowserFunction {

    CustomFunction( Browser browser, String name ) {
      super( browser, name );
    }

    public Object function( Object[] arguments ) {
      StringBuffer buffer = new StringBuffer();
      buffer.append( "theJavaFunction() called from javascript with args:\n" );
      dumpArguments( arguments, "", buffer );
      String title = "BrowserFunction called";
      MessageDialog.openInformation( getShell(), title, buffer.toString() );

      Object returnValue = new Object[]{
        new Short( ( short )3 ),
        new Boolean( true ),
        null,
        new Object[] { "a string", new Boolean( false ) },
        "hi",
        new Float( 2.0f / 3.0f )
      };
      //int z = 3 / 0; // uncomment to cause a java error instead
      return returnValue;
    }

    private void dumpArguments( Object[] arguments, String tabString, StringBuffer buffer ) {
      String tab = tabString + "    ";
      for( int i = 0; i < arguments.length; i++ ) {
        Object arg = arguments[ i ];
        if( arg == null ) {
          buffer.append( tab );
          buffer.append( "-->null\n" );
        } else {
          buffer.append( tab );
          buffer.append( "-->" );
          buffer.append( arg.getClass().getName() );
          buffer.append( ": " );
          buffer.append( arg.toString() );
          buffer.append( "\n" );
          if( arg.getClass().isArray() ) {
            Object[] arg1 = ( Object[] )arg;
            dumpArguments( arg1, tab, buffer );
          }
        }
      }
    }
  }
}
