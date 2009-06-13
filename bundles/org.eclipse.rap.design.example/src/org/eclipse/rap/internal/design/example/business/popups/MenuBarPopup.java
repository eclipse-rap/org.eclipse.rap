/******************************************************************************* 
* Copyright (c) 2009 EclipseSource and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   EclipseSource - initial API and implementation
*******************************************************************************/ 
package org.eclipse.rap.internal.design.example.business.popups;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.rap.internal.design.example.business.builder.DummyBuilder;
import org.eclipse.rap.internal.design.example.business.builder.MenuBarPopupBilder;
import org.eclipse.rap.internal.design.example.business.layoutsets.MenuBarInitializer;
import org.eclipse.rap.ui.interactiondesign.layout.ElementBuilder;
import org.eclipse.rap.ui.interactiondesign.layout.LayoutRegistry;
import org.eclipse.rap.ui.interactiondesign.layout.model.Layout;
import org.eclipse.rap.ui.interactiondesign.layout.model.LayoutSet;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ContributionItemFactory;
import org.eclipse.ui.internal.ShowViewMenu;
import org.eclipse.ui.menus.CommandContributionItem;


public class MenuBarPopup extends PopupDialog {

  private IContributionItem[] items;
  private LayoutSet layoutSet;
  private MenuBarPopup parentPopup;
  private Listener closeListener = new Listener() {
    public void handleEvent( Event event ) {
      if( getShell() != null ) {
        getShell().removeListener( SWT.Close, this );
        getShell().removeListener( SWT.Deactivate, this );
        getShell().removeListener( SWT.Dispose, this );
        getShell().removeListener( SWT.FocusOut, this );
        close();
      }        
    }
  };

  public MenuBarPopup( Shell parent,
                       int shellStyle,
                       boolean takeFocusOnOpen,
                       boolean persistSize,
                       boolean persistLocation,
                       boolean showDialogMenu,
                       boolean showPersistActions,
                       String titleText,
                       String infoText,
                       MenuManager manager,
                       MenuBarPopup parentPopup )
  {
    super( parent,
           shellStyle,
           takeFocusOnOpen,
           persistSize,
           persistLocation,
           showDialogMenu,
           showPersistActions,
           titleText,
           infoText );
    this.items = manager.getItems();
    this.parentPopup = parentPopup;
    LayoutRegistry registry = LayoutRegistry.getInstance();
    Layout activeLayout = registry.getActiveLayout();
    layoutSet = activeLayout.getLayoutSet( MenuBarInitializer.SET_ID ); 
  }
  
  protected Control createDialogArea( final Composite parent ) {
    getShell().setData( WidgetUtil.CUSTOM_VARIANT, "menuBarPopup" );
    getShell().setAlpha( 210 );
    addListeners();
    ElementBuilder popupBuilder 
      = new MenuBarPopupBilder( parent, MenuBarInitializer.SET_ID );
    popupBuilder.build();
    
    Composite content = ( Composite ) popupBuilder.getControl();
    RowLayout layout = new RowLayout( SWT.VERTICAL );
    layout.marginLeft = 11;
    layout.marginRight = 16;
    layout.marginTop = 2;
    layout.spacing = 1;
    layout.marginBottom = 0;
    content.setLayout( layout );

    for( int i = 0; i < items.length; i++ ) {
      IContributionItem item = items[ i ];
      if( item.isVisible() ) {
        if( item instanceof ActionContributionItem ) {
          // Actions
          ActionContributionItem actionItem = ( ActionContributionItem ) item;
          makeActionEntry( actionItem.getAction(), content );
        } else if( item instanceof MenuManager ) {
          // MenuManagers
          MenuManager manager = ( MenuManager ) item;
          makeMenuManagerEntry( manager, content );
        } else if( item instanceof CommandContributionItem ) {
          // Commands                
          CommandContributionItem command = ( CommandContributionItem ) item;
          makeCommandEntry( command, content );
        } else if( item instanceof ShowViewMenu ) {
          // ViewMenu
          makeViewEntries( content );
        }
      }
    }

    return content;    
  }

  private void makeViewEntries( final Composite content ) {
    final Menu menu = new Menu( content );
    IWorkbench workbench = PlatformUI.getWorkbench();
    IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
    ContributionItemFactory factory = ContributionItemFactory.VIEWS_SHORTLIST;
    final IContributionItem viewItem = factory.create( window );
    viewItem.fill( menu, 0 );
    menu.setVisible( false );
    content.addDisposeListener( new DisposeListener() {    
      public void widgetDisposed( final DisposeEvent event ) {
        menu.dispose();
        viewItem.dispose();
      }
    } );
    final MenuItem[] menuItems = menu.getItems();
    for( int i = 0; i < menuItems.length; i++ ) {
      final MenuItem item = menuItems[ i ];
      if( !item.getText().equals( "" ) ) {
        Button button = new Button( content, SWT.PUSH | SWT.FLAT );
        button.setData( WidgetUtil.CUSTOM_VARIANT, "menuBar" );        
        button.setText( item.getText() );
        button.addSelectionListener( new SelectionAdapter() {
          public void widgetSelected( SelectionEvent e ) {
            Event ev = new Event();
            ev.widget = ( Button ) e.getSource();
            item.notifyListeners( SWT.Selection, ev );
            menu.dispose();
            destroyItem( item );
            close();
            
          };
        } );
      }
    }
  }

  private void addListeners() {    
    getShell().addListener( SWT.Deactivate, closeListener );
    getShell().addListener( SWT.Close, closeListener );
    getShell().addListener( SWT.Dispose, closeListener );
    getShell().addListener( SWT.FocusOut, closeListener );
    getShell().addControlListener( new ControlAdapter() {
      public void controlResized( ControlEvent e ) {
        if( getShell() != null ) {
          getShell().pack( true );
        }
      }
    } );
  }

  private void makeMenuManagerEntry( 
    final MenuManager manager, 
    final Composite content ) 
  {
    Composite buttonArea = new Composite( content, SWT.NONE );
    buttonArea.setData( WidgetUtil.CUSTOM_VARIANT, "compTrans" );
    buttonArea.setLayout( new FormLayout() );
        
    Button button = new Button( buttonArea, SWT.PUSH | SWT.FLAT );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "menuBar" );
    button.setText( manager.getMenuText() );
    FormData fdButton = new FormData();
    button.setLayoutData( fdButton );
    fdButton.left = new FormAttachment( 0 );
    fdButton.top = new FormAttachment( 0 );
    fdButton.bottom = new FormAttachment( 100 );
        
    final Button expand = new Button( buttonArea, SWT.PUSH | SWT.FLAT );
    expand.setData( WidgetUtil.CUSTOM_VARIANT, "menuBar" );
    ElementBuilder dummy = new DummyBuilder( buttonArea, layoutSet.getId() );
    String imageID = MenuBarInitializer.SECOND_LAYER_CHEFRON;
    Image expandImage = dummy.getImage( imageID );
    expand.setImage( expandImage );
    FormData fdExpand = new FormData();
    expand.setLayoutData( fdExpand );
    fdExpand.top = new FormAttachment( 38 );
    fdExpand.right = new FormAttachment( 100 );
    fdExpand.height = expandImage.getBounds().height;
    fdExpand.width = expandImage.getBounds().width;
    
    fdButton.right = new FormAttachment( expand, -5 );
    
    final MenuBarPopup parentPopup = this;
    SelectionAdapter adapter = new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent e ) {
        getShell().removeListener( SWT.Deactivate, closeListener );
        getShell().removeListener( SWT.FocusOut, closeListener );
        IContributionItem[] menuItems = manager.getItems();
        if( menuItems.length > 0 ) {
          IWorkbench workbench = PlatformUI.getWorkbench();
          Shell shell = workbench.getActiveWorkbenchWindow().getShell();
          MenuBarPopup popup = new MenuBarPopup( 
                                               shell,
                                               SWT.ON_TOP,
                                               false,
                                               false,
                                               false,
                                               false,
                                               false,
                                               null,
                                               null,
                                               manager,
                                               parentPopup );
          
          popup.open();
          Display display = shell.getDisplay();
          Point pos = display.map( expand, null, 0, expand.getSize().y );      
          final Shell popupShell = popup.getShell();
          popupShell.setLocation( pos );
          popupShell.setActive();
        }
      };
    };
    expand.addSelectionListener( adapter );
    button.addSelectionListener( adapter );
  }
  
  private void makeCommandEntry( 
    final CommandContributionItem command, 
    final Composite content ) 
  {
    final Menu menu = new Menu( content );
    menu.setVisible( false );
    command.fill( menu, 0 );
    MenuItem[] menuItems = menu.getItems();
    for( int j = 0; j < menuItems.length; j++ ) {
      final MenuItem item = menuItems[ j ];
      Button button = new Button( content, SWT.PUSH | SWT.FLAT );
      button.setData( WidgetUtil.CUSTOM_VARIANT, "menuBar" );
      button.setText( item.getText() );     
      button.addSelectionListener( new SelectionAdapter() {
        public void widgetSelected( SelectionEvent e ) {          
          Event ev = new Event();
          ev.widget = ( Button ) e.getSource();
          item.notifyListeners( SWT.Selection, ev );
          menu.dispose();
          destroyItem( item );          
          close();
        };
      } );
      content.addDisposeListener( new DisposeListener() {      
        public void widgetDisposed( DisposeEvent event ) {
          destroyItem( item );
        }
      } );      
    }
    
  }

  private void destroyItem( MenuItem item ) {
    item.dispose();
    item = null;
  }

  private void makeActionEntry( 
    final IAction action, 
    final Composite content )  
  {
    Button button = new Button( content, SWT.PUSH | SWT.FLAT );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "menuBar" );
    button.setText( action.getText() );
    button.addSelectionListener( new SelectionAdapter(){
      public void widgetSelected( SelectionEvent e ) {
        close();
        action.run();
      };
    } );
  }
  
  protected Color getBackground() {
    return layoutSet.getColor( MenuBarInitializer.POPUP );
  }
  
  protected Color getForeground() {
    return layoutSet.getColor( MenuBarInitializer.POPUP_BUTTON );
  }
  
  public boolean close() {
    if( parentPopup != null ) {
      parentPopup.close();
    }
    return super.close();
  }

}
