/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.examples.internal;

import java.io.InputStream;
import java.util.List;

import org.eclipse.rap.examples.*;
import org.eclipse.rwt.RWT;
import org.eclipse.rwt.events.BrowserHistoryEvent;
import org.eclipse.rwt.events.BrowserHistoryListener;
import org.eclipse.rwt.internal.widgets.JSExecutor;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.Version;


public class MainUi {

  private static final String RAP_PAGE_URL = "http://eclipse.org/rap/";
  private static final String WAR_DOWNLOAD_URL = "http://rap.eclipsesource.com/download/rapdemo.war";
  private static final String CONTRIBUTION_KEY = "contribution";
  private static final int HEADER_HEIGHT = 128;
  private static final int SIDEBAR_WIDTH = 200;

  private static final RGB LOGO_BG = new RGB( 65, 102, 147 );
  private static final RGB HEADER_BG = new RGB( 57, 57, 57 );

  private Composite mainArea;
  private Control sidebar;
  private Composite statusArea;
  private Browser download;

  public int createUI() {
    Display display = new Display();
    Shell shell = createMainShell( display );
    createContent( shell );
    attachHistoryListener();
    shell.open();
    selectContribution( Examples.getInstance().getContribution( "input" ) );
    while( !shell.isDisposed() ) {
      if( !display.readAndDispatch() ) {
        display.sleep();
      }
    }
    display.dispose();
    return 0;
  }

  private Shell createMainShell( Display display ) {
    Shell shell = new Shell( display, SWT.NO_TRIM );
    shell.setLayout( new GridLayout( 1, false ) );
    shell.setMaximized( true );
    shell.setData( WidgetUtil.CUSTOM_VARIANT, "mainshell" );
    return shell;
  }

  private void createContent( Shell shell ) {
    FormLayout layout = new FormLayout();
    shell.setLayout( layout );
    Control header = createHeader( shell );
    header.setLayoutData( createHeaderFormData() );
    sidebar = createSidebar( shell );
    sidebar.setLayoutData( createSidebarFormData() );
    mainArea = createMainArea( shell );
    mainArea.setLayoutData( createMainAreaFormData() );
    statusArea = createStatusArea( shell );
    statusArea.setLayoutData( createStatusAreaFormData() );
    download = createDownloadWidget( shell );
  }

  private Control createHeader( Composite parent ) {
    Composite headerComp = new Composite( parent, SWT.NONE );
    Display display = parent.getDisplay();
    headerComp.setData( WidgetUtil.CUSTOM_VARIANT, "header" );
    headerComp.setBackgroundMode( SWT.INHERIT_DEFAULT );
    headerComp.setBackground( new Color( display, HEADER_BG ) );
    Label bgLabel = new Label( headerComp, SWT.NONE );
    bgLabel.setBackground( new Color( display, LOGO_BG ) );
    bgLabel.setBounds( 0, 0, 256, 140 );
    Label logoLabel = new Label( headerComp, SWT.NONE );
    Image rapLogo = getImage( display, "RAP-logo.png" );
    logoLabel.setImage( rapLogo );
    logoLabel.setBounds( 30, 25, 196, 78 );
    logoLabel.moveAbove( bgLabel );
    makeLink( logoLabel, RAP_PAGE_URL );
    Label headerLabel = new Label( headerComp, SWT.NONE );
    headerLabel.setText( "Demo" );
    headerLabel.setForeground( display.getSystemColor( SWT.COLOR_WHITE ) );
    headerLabel.setFont( new Font( display, "Helvetica", 32, SWT.BOLD ) );
    headerLabel.setBounds( 310, 64, 400, 50 );
    return headerComp;
  }

  private Control createSidebar( Composite parent ) {
    Composite container = new Composite( parent, SWT.NONE );
    container.setData( WidgetUtil.CUSTOM_VARIANT, "sidebar" );
    GridLayout layout = new GridLayout();
    layout.marginTop = 10;
    container.setLayout( layout );
    List<IExampleContribution> contributions = Examples.getInstance().getContributions();
    for( final IExampleContribution page : contributions ) {
      createSidebarButton( container, page );
    }
    return container;
  }

  private void createSidebarButton( Composite parent, final IExampleContribution contribution ) {
    final Button button = new Button( parent, SWT.TOGGLE | SWT.LEFT );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "sidebar" );
    button.setData( CONTRIBUTION_KEY, contribution );
    button.setText( contribution.getTitle().replace( "&", "&&" ) );
    button.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false ) );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent e ) {
        selectContribution( contribution );
      }
    } );
  }
  
  private void selectContribution( IExampleContribution contribution ) {
    selectButton( contribution );
    activate( contribution );
  }

  private void selectButton( IExampleContribution contribution ) {
    Control[] children = ( ( Composite )sidebar ).getChildren();
    for( Control control : children ) {
      if( control instanceof Button ) {
        Button button = ( Button )control;
        Object data = button.getData( CONTRIBUTION_KEY );
        if( contribution.equals( data ) ) {
          button.setSelection( true );
        } else {
          button.setSelection( false );
        }
      }
    }
  }

  private void activate( IExampleContribution contribution ) {
    IExamplePage examplePage = contribution.createPage();
    if( examplePage != null ) {
      RWT.getBrowserHistory().createEntry( contribution.getId(), contribution.getTitle() );
      Control[] children = mainArea.getChildren();
      for( Control child : children ) {
        child.dispose();
      }
      Composite wrapper = new Composite( mainArea, SWT.NONE );
      examplePage.createControl( wrapper );
      mainArea.layout( true, true );
    }
  }

  private Composite createMainArea( Composite parent ) {
    Composite mainArea = new Composite( parent, SWT.NONE );
    mainArea.setData( WidgetUtil.CUSTOM_VARIANT, "mainarea" );
    mainArea.setLayout( new FillLayout() );
    return mainArea;
  }

  private Composite createStatusArea( Composite parent ) {
    Composite statusArea = new Composite( parent, SWT.NONE );
    statusArea.setData( WidgetUtil.CUSTOM_VARIANT, "statusarea" );
    statusArea.setLayout( ExampleUtil.createGridLayout( 2, false, 0, 10 ) );
    statusArea.setBackgroundMode( SWT.INHERIT_DEFAULT );
    Label versionLabel = new Label( statusArea, SWT.RIGHT );
    versionLabel.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, true ) );
    versionLabel.setData( WidgetUtil.CUSTOM_VARIANT, "statusarea" );
    String version = getRapVersion();
    versionLabel.setText( "Running on RAP " + version );
//    Link downloadLink = new Link( statusArea, SWT.RIGHT );
//    downloadLink.setText( "Download this demo as <a>WAR file</a>" );
//    downloadLink.setData( WidgetUtil.CUSTOM_VARIANT, "statusarea" );
//    downloadLink.addSelectionListener( new SelectionAdapter() {
//      public void widgetSelected( SelectionEvent e ) {
//        download.setUrl( WAR_DOWNLOAD_URL );
//      }
//    } );
    return statusArea;
  }

  private Browser createDownloadWidget( Composite parent ) {
    Browser download = new Browser( parent, SWT.NONE );
    download.setData( new FormData( 0, 0 ) );
    return download;
  }

  private FormData createHeaderFormData() {
    FormData data = new FormData();
    data.top = new FormAttachment( 0 );
    data.left = new FormAttachment( 0 );
    data.right = new FormAttachment( 100 );
    data.height = HEADER_HEIGHT;
    return data;
  }

  private FormData createSidebarFormData() {
    FormData data = new FormData();
    data.top = new FormAttachment( 0, HEADER_HEIGHT + 20 );
    data.left = new FormAttachment( 0, 20 );
    data.width = SIDEBAR_WIDTH;
    data.bottom = new FormAttachment( 100, -20 );
    return data;
  }

  private FormData createMainAreaFormData() {
    FormData data = new FormData();
    data.top = new FormAttachment( 0, HEADER_HEIGHT + 20 );
    data.left = new FormAttachment( 0, 230 + 10 );
    data.right = new FormAttachment( 100, -20 );
    data.bottom = new FormAttachment( 100, -30 );
    return data;
  }

  private FormData createStatusAreaFormData() {
    FormData data = new FormData();
    data.top = new FormAttachment( 100, -30 );
    data.left = new FormAttachment( 0, 230 + 10 );
    data.right = new FormAttachment( 100, -20 );
    data.bottom = new FormAttachment( 100, 0 );
    return data;
  }

  private void attachHistoryListener() {
    RWT.getBrowserHistory().addBrowserHistoryListener( new BrowserHistoryListener() {
      
      public void navigated( BrowserHistoryEvent event ) {
        IExampleContribution page = Examples.getInstance().getContribution( event.entryId );
        if( page != null ) {
          activate( page );
        }
      }
    } );
  }

  private static Image getImage( Display display, String path ) {
    ClassLoader classLoader = MainUi.class.getClassLoader();
    InputStream inputStream = classLoader.getResourceAsStream( "resources/" + path );
    Image result = null;
    if( inputStream != null ) {
      result = new Image( display, inputStream );
    }
    return result;
  }

  private static String getRapVersion() {
    Version version = FrameworkUtil.getBundle( RWT.class ).getVersion();
    StringBuilder resultBuffer = new StringBuilder( 20 );
    resultBuffer.append( version.getMajor() );
    resultBuffer.append( '.' );
    resultBuffer.append( version.getMinor() );
    resultBuffer.append( '.' );
    resultBuffer.append( version.getMicro() );
    resultBuffer.append( " (Build " );
    resultBuffer.append( version.getQualifier() );
    resultBuffer.append( ')' );
    return resultBuffer.toString();
  }

  private static void makeLink( Label control, final String url ) {
    control.setCursor( control.getDisplay().getSystemCursor( SWT.CURSOR_HAND ) );
    control.addMouseListener( new MouseAdapter() {
      public void mouseDown( MouseEvent e ) {
        JSExecutor.executeJS( "window.location.href = '" + url + "'" );
      }
    } );
  }
}
