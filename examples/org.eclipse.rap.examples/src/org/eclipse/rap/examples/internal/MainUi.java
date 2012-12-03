/*******************************************************************************
 * Copyright (c) 2011, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.examples.internal;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.rap.examples.ExampleUtil;
import org.eclipse.rap.examples.IExampleContribution;
import org.eclipse.rap.examples.IExamplePage;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.application.EntryPoint;
import org.eclipse.rap.rwt.client.service.BrowserHistory;
import org.eclipse.rap.rwt.client.service.BrowserHistoryEvent;
import org.eclipse.rap.rwt.client.service.BrowserHistoryListener;
import org.eclipse.rap.rwt.client.service.JavaScriptExecutor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.Version;


public class MainUi implements EntryPoint {

  private static final String RAP_PAGE_URL = "http://eclipse.org/rap/";
  private static final int CONTENT_MIN_HEIGHT = 800;
  private static final int HEADER_HEIGHT = 140;
  private static final int CENTER_AREA_WIDTH = 998;

  private Composite centerArea;
  private Navigation navigation;
  private Composite navBar;

  public int createUI() {
    Display display = new Display();
    Shell shell = createMainShell( display );
    shell.setLayout( new FillLayout() );
    ScrolledComposite scrolledArea = createScrolledArea( shell );
    Composite content = createContent( scrolledArea );
    scrolledArea.setContent( content );
    attachHistoryListener();
    shell.open();
    selectInitialContribution();
    while( !shell.isDisposed() ) {
      if( !display.readAndDispatch() ) {
        display.sleep();
      }
    }
    display.dispose();
    return 0;
  }

  private void selectInitialContribution() {
    IExampleContribution contribution = Examples.getInstance().findInitialContribution();
    if( contribution != null ) {
      selectContribution( contribution );
    }
  }

  private void attachHistoryListener() {
    BrowserHistory history = RWT.getClient().getService( BrowserHistory.class );
    if( history != null ) {
      history.addBrowserHistoryListener( new BrowserHistoryListener() {
        public void navigated( BrowserHistoryEvent event ) {
          IExampleContribution contribution = Examples.getInstance().getContribution( event.entryId );
          if( contribution != null ) {
            selectContribution( contribution );
          }
        }
      } );
    }
  }

  private Shell createMainShell( Display display ) {
    Shell shell = new Shell( display, SWT.NO_TRIM );
    shell.setMaximized( true );
    shell.setData( RWT.CUSTOM_VARIANT, "mainshell" );
    return shell;
  }

  private ScrolledComposite createScrolledArea( Composite parent ) {
    ScrolledComposite scrolledComp = new ScrolledComposite( parent, SWT.V_SCROLL | SWT.H_SCROLL );
    scrolledComp.setMinHeight( CONTENT_MIN_HEIGHT );
    scrolledComp.setMinWidth( CENTER_AREA_WIDTH );
    scrolledComp.setExpandVertical( true );
    scrolledComp.setExpandHorizontal( true );
    return scrolledComp;
  }

  private Composite createContent( ScrolledComposite scrolledArea ) {
    Composite comp = new Composite( scrolledArea, SWT.NONE );
    comp.setLayout( new FormLayout() );
    Composite header = createHeader( comp );
    header.setLayoutData( createHeaderFormData() );
    createContentBody( comp, header );
    return comp;
  }

  private Composite createHeader( Composite parent ) {
    Composite comp = new Composite( parent, SWT.NONE );
    comp.setData( RWT.CUSTOM_VARIANT, "header" );
    comp.setBackgroundMode( SWT.INHERIT_DEFAULT );
    comp.setLayout( new FormLayout() );
    Composite headerCenterArea = createHeaderCenterArea( comp );
    createLogo( headerCenterArea );
    createTitle( headerCenterArea );
    return comp;
  }

  private FormData createHeaderFormData() {
    FormData data = new FormData();
    data.top = new FormAttachment( 0 );
    data.left = new FormAttachment( 0 );
    data.right = new FormAttachment( 100 );
    data.height = HEADER_HEIGHT;
    return data;
  }

  private Composite createHeaderCenterArea( Composite parent ) {
    Composite headerCenterArea = new Composite( parent, SWT.NONE );
    headerCenterArea.setLayout( new FormLayout() );
    headerCenterArea.setLayoutData( createHeaderCenterAreaFormData() );
    return headerCenterArea;
  }

  private FormData createHeaderCenterAreaFormData() {
    FormData data = new FormData();
    data.left = new FormAttachment( 50, -CENTER_AREA_WIDTH / 2 );
    data.top = new FormAttachment( 0 );
    data.bottom = new FormAttachment( 100 );
    data.width = CENTER_AREA_WIDTH;
    return data;
  }

  private void createLogo( Composite headerComp ) {
    Label logoLabel = new Label( headerComp, SWT.NONE );
    Image rapLogo = MainUi.getImage( headerComp.getDisplay(), "RAP-logo.png" );
    logoLabel.setImage( rapLogo );
    logoLabel.setLayoutData( createLogoFormData( rapLogo ) );
    makeLink( logoLabel, RAP_PAGE_URL );
  }

  private void createTitle( Composite headerComp ) {
    Label title = new Label( headerComp, SWT.NONE );
    title.setText( "Demo" );
    title.setLayoutData( createTitleFormData() );
    title.setData( RWT.CUSTOM_VARIANT, "title" );
  }

  private void createContentBody( Composite parent, Composite header ) {
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setData( RWT.CUSTOM_VARIANT, "mainContentArea" );
    composite.setLayout( new FormLayout() );
    composite.setLayoutData( createContentBodyFormData( header ) );
    navigation = createNavigation( composite );
    Composite footer = createFooter( composite );
    centerArea = createCenterArea( composite, footer );
  }

  private Composite createCenterArea( Composite parent, Composite footer ) {
    Composite centerArea = new Composite( parent, SWT.NONE );
    centerArea.setLayout( new FillLayout() );
    centerArea.setLayoutData( createCenterAreaFormData( footer ) );
    centerArea.setData( RWT.CUSTOM_VARIANT, "centerArea" );
    return centerArea;
  }

  private FormData createCenterAreaFormData( Composite footer ) {
    FormData data = new FormData();
    data.top = new FormAttachment( navBar, 0, SWT.BOTTOM );
    data.bottom = new FormAttachment( footer, 0, SWT.TOP );
    data.left = new FormAttachment( 50, ( -CENTER_AREA_WIDTH / 2 ) -10  );
    data.width = CENTER_AREA_WIDTH + 10;
    return data;
  }

  private Composite createFooter( Composite contentComposite ) {
    Composite footer = new Composite( contentComposite, SWT.NONE );
    footer.setBackgroundMode( SWT.INHERIT_DEFAULT );
    footer.setLayout( new FormLayout() );
    footer.setData( RWT.CUSTOM_VARIANT, "footer" );
    footer.setLayoutData( createFooterFormData() );
    Label label = new Label( footer, SWT.NONE );
    label.setData( RWT.CUSTOM_VARIANT, "footerLabel" );
    label.setText( "RAP version: " + getRapVersion() );
    label.setLayoutData( createFooterLabelFormData( footer ) );
    return footer;
  }

  private FormData createFooterFormData() {
    FormData data = new FormData();
    data.left = new FormAttachment( 50, ( -CENTER_AREA_WIDTH / 2 ) );
    data.top = new FormAttachment( 100, -40 );
    data.bottom = new FormAttachment( 100 );
    data.width = CENTER_AREA_WIDTH -10 - 2;
    return data;
  }

  private FormData createFooterLabelFormData( Composite footer ) {
    FormData data = new FormData();
    data.top = new FormAttachment( 50, -10 );
    data.right = new FormAttachment( 100, -15 );
    return data;
  }

  private FormData createContentBodyFormData( Composite header ) {
    FormData data = new FormData();
    data.top = new FormAttachment( header, 0 );
    data.left = new FormAttachment( 0, 0 );
    data.right = new FormAttachment( 100, 0 );
    data.bottom = new FormAttachment( 100, 0 );
    return data;
  }

  private Navigation createNavigation( Composite parent ) {
    navBar = new Composite( parent, SWT.NONE );
    navBar.setLayout( new FormLayout() );
    navBar.setLayoutData( createNavBarFormData() );
    navBar.setData( RWT.CUSTOM_VARIANT, "nav-bar" );
    Navigation navigation = new Navigation( navBar ) {
      @Override
      protected void selectContribution( IExampleContribution contribution ) {
        MainUi.this.selectContribution( contribution );
      }
    };
    Control navigationControl = navigation.getControl();
    navigationControl.setLayoutData( createNavigationFormData() );
    navigationControl.setData( RWT.CUSTOM_VARIANT, "navigation" );
    return navigation;
  }

  private void selectContribution( IExampleContribution contribution ) {
    navigation.selectNavigationEntry( contribution );
    activate( contribution );
  }

  private void activate( IExampleContribution contribution ) {
    IExamplePage examplePage = contribution.createPage();
    if( examplePage != null ) {
      BrowserHistory history = RWT.getClient().getService( BrowserHistory.class );
      if( history != null ) {
        history.createEntry( contribution.getId(), contribution.getTitle() );
      }
      Control[] children = centerArea.getChildren();
      for( Control child : children ) {
        child.dispose();
      }
      Composite contentComp = ExampleUtil.initPage( contribution.getTitle(), centerArea );
      examplePage.createControl( contentComp );
      centerArea.layout( true, true );
    }
  }

  private static FormData createLogoFormData( Image rapLogo ) {
    FormData data = new FormData();
    data.left = new FormAttachment( 0 );
    int logoHeight = rapLogo.getBounds().height;
    data.top = new FormAttachment( 50, -( logoHeight / 2 ) );
    return data;
  }

  private static FormData createTitleFormData() {
    FormData data = new FormData();
    data.bottom = new FormAttachment( 100, -26 );
    data.left = new FormAttachment( 0, 250 );
    return data;
  }

  private static FormData createNavBarFormData() {
    FormData data = new FormData();
    data.top = new FormAttachment( 0 );
    data.left = new FormAttachment( 0 );
    data.right = new FormAttachment( 100 );
    return data;
  }

  private static FormData createNavigationFormData() {
    FormData data = new FormData();
    data.left = new FormAttachment( 50, ( -CENTER_AREA_WIDTH / 2 ) - 7 );
    data.top = new FormAttachment( 0 );
    data.bottom = new FormAttachment( 100 );
    data.width = CENTER_AREA_WIDTH;
    return data;
  }

  public static Image getImage( Display display, String path ) {
    ClassLoader classLoader = MainUi.class.getClassLoader();
    InputStream inputStream = classLoader.getResourceAsStream( "resources/" + path );
    Image result = null;
    if( inputStream != null ) {
      try {
        result = new Image( display, inputStream );
      } finally {
        try {
          inputStream.close();
        } catch( IOException e ) {
          // ignore
        }
      }
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
      @Override
      public void mouseDown( MouseEvent e ) {
        JavaScriptExecutor executor = RWT.getClient().getService( JavaScriptExecutor.class );
        if( executor != null ) {
          executor.execute( "window.location.href = '" + url + "'" );
        }
      }
    } );
  }

}
