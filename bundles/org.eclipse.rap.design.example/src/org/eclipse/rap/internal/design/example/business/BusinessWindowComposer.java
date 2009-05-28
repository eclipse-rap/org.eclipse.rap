/******************************************************************************* 
* Copyright (c) 2008 EclipseSource and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   EclipseSource - initial API and implementation
*******************************************************************************/ 
package org.eclipse.rap.internal.design.example.business;

import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.internal.provisional.action.ICoolBarManager2;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.rap.internal.design.example.business.builder.FooterBuilder;
import org.eclipse.rap.internal.design.example.business.builder.HeaderBuilder;
import org.eclipse.rap.internal.design.example.business.builder.PerspectiveSwitcherBuilder;
import org.eclipse.rap.internal.design.example.business.layoutsets.FooterInitializer;
import org.eclipse.rap.internal.design.example.business.layoutsets.HeaderInitializer;
import org.eclipse.rap.internal.design.example.business.layoutsets.PerspectiveSwitcherInitializer;
import org.eclipse.rap.internal.design.example.business.managers.BusinessCoolBarManager;
import org.eclipse.rap.ui.interactiondesign.IWindowComposer;
import org.eclipse.rap.ui.interactiondesign.layout.ElementBuilder;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;


public class BusinessWindowComposer implements IWindowComposer {


  private static final int MARGIN = 35;
  private Shell shell;
  private IWorkbenchWindowConfigurer configurer;
  private ApplicationWindow window;
  private Composite headerArea;
  private Composite page;
  private Composite footer;
  private Composite overflowParent;

  public Composite createWindowContents( 
    final Shell shell,
    final IWorkbenchWindowConfigurer configurer )
  {
    // setup components
    setupComponents( shell, configurer );
    
    // create the header
    createHeader();    
    
    // create the menubar composite
    Composite menuBarComp = new Composite( shell, SWT.NONE );
    menuBarComp.setData( WidgetUtil.CUSTOM_VARIANT, "compGray" );
    FormData fdMenuBarComp = new FormData();
    menuBarComp.setLayoutData( fdMenuBarComp );
    fdMenuBarComp.top = new FormAttachment( headerArea );
    fdMenuBarComp.left = new FormAttachment( 0, MARGIN );
    fdMenuBarComp.right = new FormAttachment( 100, -MARGIN );
    if( configurer.getShowMenuBar() ) {
      createMenuBar( menuBarComp );    
    }
    
    // create the separator between menubar and page
    Label menuBarBorder = new Label( shell, SWT.NONE );
    menuBarBorder.setData( WidgetUtil.CUSTOM_VARIANT, "menuBorder" );
    FormData fdMenuBarBorder = new FormData();
    menuBarBorder.setLayoutData( fdMenuBarBorder );
    fdMenuBarBorder.left = new FormAttachment( 0, MARGIN );
    fdMenuBarBorder.right = new FormAttachment( 100, -MARGIN );
    fdMenuBarBorder.top = new FormAttachment( menuBarComp );
    fdMenuBarBorder.height = 1;    
    
    // pageBg
    Composite pageBg = new Composite( shell, SWT.NONE );
    pageBg.setData( WidgetUtil.CUSTOM_VARIANT, "compGray" );
    FormData fdPageBg = new FormData();
    pageBg.setLayoutData( fdPageBg );
    fdPageBg.left = new FormAttachment( 0, MARGIN );
    fdPageBg.right = new FormAttachment( 100, -MARGIN );
    attachPageBg( fdPageBg, menuBarBorder, menuBarComp );
    fdPageBg.bottom = new FormAttachment( 100, 0 );
    pageBg.setLayout( new FormLayout() );    
    
    // create the page Parent Composite
    page = new Composite( pageBg, SWT.NONE );
    page.setLayout( new FillLayout() );
    page.setData( WidgetUtil.CUSTOM_VARIANT, "compGray" );
    FormData fdPage = new FormData();
    page.setLayoutData( fdPage );
    fdPage.left = new FormAttachment( 0, -7 );
    fdPage.top = new FormAttachment( 0, -7 );
    fdPage.right = new FormAttachment( 100, 7 );
        
    // create Footer and attach the page
    createFooter( pageBg );  
    if( footer != null ) {
      fdPage.bottom = new FormAttachment( footer, -6 );
    } else {
      fdPage.bottom = new FormAttachment( 100, -6 );
    }
    shell.layout( true, true );
    return page;
  }

  private void createStatusLine( final Composite parent ) {
    parent.setLayout( new FillLayout( SWT.HORIZONTAL ) );
    Control statusLineControl = configurer.createStatusLineControl( parent );
    final Composite statusLineComp = ( Composite ) statusLineControl;
    statusLineComp.addControlListener( new ControlAdapter() {
      public void controlResized(ControlEvent e) {
        styleButtons( statusLineComp.getChildren() );
      };
    } );
    parent.setBackgroundMode( SWT.INHERIT_FORCE );
    styleButtons( statusLineComp.getChildren() );
    statusLineControl.moveAbove( parent );
  }

  private void setupComponents( 
    final Shell shell,
    final IWorkbenchWindowConfigurer configurer )
  {
    this.shell = shell;
    shell.setData( WidgetUtil.CUSTOM_VARIANT, "shellGray" );
    this.configurer = configurer;
    IWorkbench workbench = PlatformUI.getWorkbench();
    IWorkbenchWindow activeWbWindow = workbench.getActiveWorkbenchWindow();
    window = ( ApplicationWindow ) activeWbWindow;
    shell.setLayout( new FormLayout() );
  }

  private void attachPageBg( 
    final FormData fdPageBg, 
    final Label menuBarBorder, 
    final Composite menuBarComp ) 
  {
    if( configurer.getShowMenuBar() && menuBarBorder != null ) {
      fdPageBg.top = new FormAttachment( menuBarBorder, 26 );
    } else {
      fdPageBg.top = new FormAttachment( headerArea, 26 );
      menuBarBorder.setVisible( false );
      menuBarComp.setVisible( false );
    }
  }

  void createMenuBar( Composite menuBarComp ) {
    MenuManager manager = window.getMenuBarManager();
    manager.fill( menuBarComp );
  }

  private void createFooter( final Composite pageBg ) {
    // create the statusline
    if( configurer.getShowStatusLine() ) {
      footer = new Composite( pageBg, SWT.NONE );
      FormData fdFooter = new FormData();
      footer.setLayoutData( fdFooter );
      
      fdFooter.left = new FormAttachment( 0, 0 );
      fdFooter.right = new FormAttachment( 100, 0 );  
      
      ElementBuilder footerBuilder = 
        new FooterBuilder( footer, FooterInitializer.SET_ID );
      footerBuilder.build();
      Composite statusLineParent = ( Composite ) footerBuilder.getControl();
      int offset = statusLineParent.getSize().y + 13;
      fdFooter.bottom = new FormAttachment( 100, -offset );
      
      createStatusLine( statusLineParent );
    } 
    
  }

  private void createHeader() {
    headerArea = new Composite( shell, SWT.NONE );
    FormData fdHeaderArea = new FormData();
    headerArea.setLayoutData( fdHeaderArea );
    fdHeaderArea.left = new FormAttachment( 0, 0 );
    fdHeaderArea.right = new FormAttachment( 100, 0 );
    
    ElementBuilder headerBuilder 
      = new HeaderBuilder( headerArea, HeaderInitializer.SET_ID );
    headerBuilder.build();  
    overflowParent = ( Composite ) headerBuilder.getAdapter( Composite.class );
        
    // create the Perspective Switcher
    if( configurer.getShowPerspectiveBar() ) {
      createPerspectiveBar( ( Composite ) headerBuilder.getControl() );
    }
    
    // create the CoolBar
    if( configurer.getShowCoolBar() ) {
      createCoolbarArea( ( Composite ) headerBuilder.getControl() );
    }    
  }

  private void createPerspectiveBar( final Composite header ) {
    // TODO [jb] This is the area on the top.
    Composite perspBar = new Composite( header, SWT.NONE );
    perspBar.setLayout( new FormLayout() );
    FormData fdPerspBar = new FormData();
    perspBar.setLayoutData( fdPerspBar );
    fdPerspBar.left = new FormAttachment( 0, 27 );
    fdPerspBar.right = new FormAttachment( 100, 0 );
    fdPerspBar.top = new FormAttachment( 0, 2 );
    fdPerspBar.height = 25;
    perspBar.setData( WidgetUtil.CUSTOM_VARIANT, "compTrans" );
    ElementBuilder perspBuilder 
      = new PerspectiveSwitcherBuilder( perspBar, 
                                        PerspectiveSwitcherInitializer.SET_ID );
    perspBuilder.build();
  }

  private void createCoolbarArea( final Composite header ) {
    // TODO [jb] This is the blue area.
    ICoolBarManager manager = window.getCoolBarManager2();
    // If no Coolbar is needed, change this method call
    createCoolBar( manager, header );
    
  }

  private void createCoolBar( 
    final ICoolBarManager manager, 
    final Composite header ) 
  {
    if( manager != null ) {
      Composite coolBar = new Composite( header, SWT.NONE );
      coolBar.setData( WidgetUtil.CUSTOM_VARIANT, "compTrans" );
      FormData fdCoolBar = new FormData();
      coolBar.setLayoutData( fdCoolBar );
      fdCoolBar.top = new FormAttachment( 0, 54 );
      fdCoolBar.left = new FormAttachment( 0, 25 );
      fdCoolBar.bottom = new FormAttachment( 100, -22 );
      fdCoolBar.right = new FormAttachment( 100 );
      coolBar.setLayout( new FillLayout() );
      
      // Create the actual coolbar
      if ( manager instanceof ICoolBarManager2 ) {
        ICoolBarManager2 coolbarManager2 = ( ICoolBarManager2 ) manager;
        coolbarManager2.createControl2( coolBar );
        if( manager instanceof BusinessCoolBarManager ) {
          BusinessCoolBarManager coolbarManager 
            = ( BusinessCoolBarManager ) manager;
          coolbarManager.setOverflowParent( overflowParent );          
        }
      }
    }
  }

  public void postWindowOpen( final IWorkbenchWindowConfigurer configurer ) {
    final Shell windowShell = configurer.getWindow().getShell();
    windowShell.setMaximized( true ); 
  }

  public void preWindowOpen( final IWorkbenchWindowConfigurer configurer ) {  
    configurer.setShowCoolBar( true );
    configurer.setShowPerspectiveBar( true );
    configurer.setShellStyle( SWT.NO_TRIM  ); 
    configurer.setShowMenuBar( true );
    configurer.setShowStatusLine( true );
  }
  
  private void styleButtons( final Control[] buttons ) {
    for( int i = 0; i < buttons.length; i++ ) {
      if( buttons[ i ] instanceof Button ) {
        buttons[ i ].setData( WidgetUtil.CUSTOM_VARIANT, "clearButton" );
      }
    }
  }

}
