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
import java.util.ArrayList;
import java.util.List;

import org.eclipse.rap.examples.IExampleContribution;
import org.eclipse.rap.examples.IExamplePage;
import org.eclipse.rwt.RWT;
import org.eclipse.rwt.events.BrowserHistoryEvent;
import org.eclipse.rwt.events.BrowserHistoryListener;
import org.eclipse.rwt.internal.widgets.JSExecutor;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.Version;


public class MainUi {

  private static final String RAP_PAGE_URL = "http://eclipse.org/rap/";
  private static final int HEADER_HEIGHT = 155;
  private static final int HEADER_BAR_HEIGHT = 15;
  private static final int CENTER_AREA_WIDTH = 978;

  private static final RGB HEADER_BG = new RGB(49, 97, 156);
  private static final RGB HEADER_BAR_BG = new RGB( 52, 51, 47 );

  private Composite widgetsContainer;
  private Composite navigation;

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
    shell.setMaximized( true );
    shell.setData( WidgetUtil.CUSTOM_VARIANT, "mainshell" );
    return shell;
  }

  private void createContent( Shell shell ) {
    FormLayout layout = new FormLayout();
    shell.setLayout( layout );
    Composite header = createHeader( shell );
    header.setLayoutData( createHeaderFormData() );
    createContentArea( shell, header );
  }

  private Composite createHeader( Composite parent ) {
    Composite headerComp = new Composite( parent, SWT.NONE );
    Display display = parent.getDisplay();
    headerComp.setData( WidgetUtil.CUSTOM_VARIANT, "header" );
    headerComp.setBackgroundMode( SWT.INHERIT_DEFAULT );
    headerComp.setBackground( new Color( display, HEADER_BG ) );
    headerComp.setLayout( new FormLayout() );
    Composite headerCenterArea = createHeaderCenterArea( headerComp );
    createLogo( headerCenterArea, display );
    createTitle( headerCenterArea, display );
    createHeaderBar( headerComp, display );
    return headerComp;
  }

  private FormData createHeaderFormData() {
    FormData data = new FormData();
    data.top = new FormAttachment( 0 );
    data.left = new FormAttachment( 0 );
    data.right = new FormAttachment( 100 );
    data.height = HEADER_HEIGHT;
    return data;
  }

  private void createHeaderBar( Composite headerComp, Display display ) {
    Composite headerBar = new Composite( headerComp, SWT.NONE );
    headerBar.setBackgroundMode( SWT.INHERIT_DEFAULT );
    headerBar.setBackground( new Color( display, HEADER_BAR_BG ) );
    headerBar.setLayoutData( createHeaderBarFormData() );
  }

  private FormData createHeaderBarFormData() {
    FormData data = new FormData();
    data.bottom = new FormAttachment( 100 );
    data.left = new FormAttachment( 0 );
    data.right = new FormAttachment( 100 );
    data.top = new FormAttachment( 100, -HEADER_BAR_HEIGHT );
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

  private void createLogo( Composite headerComp, Display display ) {
    Label logoLabel = new Label( headerComp, SWT.NONE );
    Image rapLogo = getImage( display, "RAP-logo.png" );
    logoLabel.setImage( rapLogo );
    logoLabel.setLayoutData( createLogoFormData( rapLogo ) );
    makeLink( logoLabel, RAP_PAGE_URL );
  }

  private void createTitle( Composite headerComp, Display display ) {
    Label title = new Label( headerComp, SWT.NONE );
    title.setText( "Demo" );
    title.setForeground( display.getSystemColor( SWT.COLOR_WHITE ) );
    title.setFont( new Font( display, "Helvetica", 25, SWT.NONE ) );
    title.setLayoutData( createTitleFormData() );
  }

  private void createContentArea( Composite parent, Composite header ) {
    Composite contentComposite = new Composite( parent, SWT.NONE );
    contentComposite.setData(  WidgetUtil.CUSTOM_VARIANT, "mainContentArea" );
    contentComposite.setLayout( new FormLayout() );
    contentComposite.setLayoutData( createMainContentFormData( header ) );
    navigation = createNavigation( contentComposite );
    createCenterArea( contentComposite );
  }

  private FormData createMainContentFormData( Composite header ) {
    FormData data = new FormData();
    data.top = new FormAttachment( header, 0 );
    data.left = new FormAttachment( 0, 0 );
    data.right = new FormAttachment( 100, 0 );
    data.bottom = new FormAttachment( 100, 0 );
    return data;
  }

  private Composite createNavigation( Composite parent ) {
    Composite navBar = new Composite( parent, SWT.NONE);
    navBar.setLayout( new FormLayout() );
    navBar.setLayoutData( createNavBarFormData() );
    navBar.setData(  WidgetUtil.CUSTOM_VARIANT, "nav-bar" );
    Composite nav = new Composite( navBar, SWT.NONE );
    nav.setLayout( new GridLayout( 9, false ) );
    nav.setLayoutData( createNavigationFormData() );
    nav.setData(  WidgetUtil.CUSTOM_VARIANT, "navigation" );
    createNavigationControls( nav );
    return nav;
  }

  private FormData createNavBarFormData() {
    FormData data = new FormData();
    data.top = new FormAttachment( 0 );
    data.left = new FormAttachment( 0 );
    data.right = new FormAttachment( 100 );
    return data;
  }

  private FormData createNavigationFormData() {
    FormData data = new FormData();
    data.left = new FormAttachment( 50, ( -CENTER_AREA_WIDTH / 2 ) - 13 );
    data.top = new FormAttachment( 0 );
    data.bottom = new FormAttachment( 100 );
    data.width = CENTER_AREA_WIDTH;
    return data;
  }

  private void createNavigationControls( Composite parent ) {
    List<IExampleContribution> contributions = Examples.getInstance().getContributions();
    for( final IExampleContribution page : contributions ) {
      createNavigationControl( parent, page );
    }
  }

  private void createNavigationControl( Composite parent, final IExampleContribution page ) {
    String category = page.getCategory();
    if( category != null ) {
      createNavigationDropDown( parent, page );
    } else {
      createNavigationButton( parent, page );
    }
  }

  private void createNavigationButton( Composite parent, final IExampleContribution page ) {
    ToolBar toolBar = new ToolBar( parent, SWT.HORIZONTAL );
    GridData layoutData = new GridData( SWT.LEFT, SWT.LEFT, true, false );
    toolBar.setLayoutData( layoutData );
    toolBar.setData( WidgetUtil.CUSTOM_VARIANT, "navigation" );
    toolBar.setData( page.getId() );
    final ToolItem toolItem = new ToolItem( toolBar, SWT.PUSH );
    toolItem.setData( WidgetUtil.CUSTOM_VARIANT, "navigation" );
    toolItem.setText( page.getTitle().replace( "&", "&&" ) );
    toolItem.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent e ) {
        selectContribution( page );
      }
    } );
  }

  private void createNavigationDropDown( Composite parent, IExampleContribution page ) {
    DropDownNavigation navigationEntry = getNavigationEntryByCategory( parent, page.getCategory() );
    if( navigationEntry != null ) {
      navigationEntry.addNavigationItem( page );
    } else {
      new DropDownNavigation( parent, page ) {
        @Override
        protected void onSelectContribution( IExampleContribution page ) {
          MainUi.this.selectContribution( page );
        }
      };
    }
  }

  private DropDownNavigation getNavigationEntryByCategory( Composite parent, String category ) {
    DropDownNavigation result = null;
    Control[] children = parent.getChildren();
    for( Control element : children ) {
      if( element instanceof DropDownNavigation ) {
        DropDownNavigation entry = ( DropDownNavigation ) element;
        if( entry.getCategory().equals( category ) ) {
          result = entry;
          break;
        }
      }
    }
    return result;
  }

  private void createCenterArea( Composite parent ) {
    Composite centerArea = new Composite( parent, SWT.NONE );
    centerArea.setLayout( new FormLayout() );
    centerArea.setData(  WidgetUtil.CUSTOM_VARIANT, "centerArea" );
    centerArea.setLayoutData( createCenterAreaFormData() );
    widgetsContainer = createWidgetsContainer( centerArea );
  }

  private FormData createCenterAreaFormData() {
    FormData data = new FormData();
    data.left = new FormAttachment( 50, -CENTER_AREA_WIDTH / 2 );
    data.top = new FormAttachment( navigation.getParent() );
    data.bottom = new FormAttachment( 100 );
    data.width = CENTER_AREA_WIDTH;
    return data;
  }

  private Composite createWidgetsContainer( Composite parent ) {
    Composite widgetsComp = new Composite( parent, SWT.NONE );
    widgetsComp.setData(  WidgetUtil.CUSTOM_VARIANT, "widgetsComp" );
    widgetsComp.setLayout( new FillLayout() );
    widgetsComp.setLayoutData( createWidgetsContainerFormData() );
    return widgetsComp;
  }

  private FormData createWidgetsContainerFormData() {
    FormData data = new FormData();
    data.top = new FormAttachment( navigation, 15 );
    data.left = new FormAttachment( 0 );
    data.right = new FormAttachment( 100 );
    data.bottom = new FormAttachment( 100, -15 );
    return data;
  }

  private void selectContribution( IExampleContribution page ) {
    selectNavigationEntry( page );
    activate( page );
  }

  private void selectNavigationEntry( IExampleContribution page ) {
    Control[] children = navigation.getChildren();
    for( Control control : children ) {
      if( control instanceof ToolBar ) {
        changeSelectedToolBarEntry( page, (ToolBar) control );
      } else if( control instanceof DropDownNavigation ) {
        changeSelectedDropDownEntry( page, (DropDownNavigation) control );
      }
    }
  }

  private void changeSelectedToolBarEntry( IExampleContribution page, ToolBar navEntry ) {
    ToolItem item = navEntry.getItem( 0 );
    if( navEntry.getData().equals( page.getId() ) ) {
      item.setData( WidgetUtil.CUSTOM_VARIANT, "selected" );
    } else {
      item.setData( WidgetUtil.CUSTOM_VARIANT, "navigation" );
    }
  }

  private void changeSelectedDropDownEntry( IExampleContribution page,
                                            DropDownNavigation navEntry ) {
    boolean belongsToDropDownNav = pageBelongsToDropDownNav( page, navEntry );
    ToolItem item = ( (ToolBar) navEntry.getChildren()[ 0 ] ).getItem( 0 );
    if( belongsToDropDownNav ) {
      item.setData( WidgetUtil.CUSTOM_VARIANT, "selected" );
    } else {
      item.setData( WidgetUtil.CUSTOM_VARIANT, "navigation" );
    }
  }

  @SuppressWarnings("unchecked")
  private boolean pageBelongsToDropDownNav( IExampleContribution page, DropDownNavigation navEntry ) {
    boolean result = false;
    ArrayList<String> navEntryData = (ArrayList<String>) navEntry.getData();
    for( String id : navEntryData ) {
      if( page.getId().equals( id ) ) {
        result = true;
        break;
      }
    }
    return result;
  }

  private void activate( IExampleContribution page ) {
    IExamplePage examplePage = page.createPage();
    if( examplePage != null ) {
      RWT.getBrowserHistory().createEntry( page.getId(), page.getTitle() );
      Control[] children = widgetsContainer.getChildren();
      for( Control child : children ) {
        child.dispose();
      }
      Composite wrapper = new Composite( widgetsContainer, SWT.NONE );
      examplePage.createControl( wrapper );
      widgetsContainer.layout( true, true );
    }
  }

  private FormData createLogoFormData( Image rapLogo ) {
    FormData data = new FormData();
    data.left = new FormAttachment( 0 );
    int logoHeight = rapLogo.getBounds().height;
    data.top = new FormAttachment( 50, -( logoHeight / 2 ) );
    return data;
  }

  private FormData createTitleFormData() {
    FormData data = new FormData();
    data.bottom = new FormAttachment( 100, -33 );
    data.left = new FormAttachment( 0, 250 );
    return data;
  }

  private void attachHistoryListener() {
    RWT.getBrowserHistory().addBrowserHistoryListener( new BrowserHistoryListener() {

      public void navigated( BrowserHistoryEvent event ) {
        IExampleContribution page = Examples.getInstance().getContribution( event.entryId );
        if( page != null ) {
          selectContribution( page );
        }
      }
    } );
  }

  private static Image getImage( Display display, String path ) {
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

  @SuppressWarnings("restriction")
  private static void makeLink( Label control, final String url ) {
    control.setCursor( control.getDisplay().getSystemCursor( SWT.CURSOR_HAND ) );
    control.addMouseListener( new MouseAdapter() {
      @Override
      public void mouseDown( MouseEvent e ) {
        JSExecutor.executeJS( "window.location.href = '" + url + "'" );
      }
    } );
  }

}
