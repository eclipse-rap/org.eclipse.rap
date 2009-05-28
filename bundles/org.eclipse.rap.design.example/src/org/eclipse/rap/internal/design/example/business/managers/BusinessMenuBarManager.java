/******************************************************************************* 
* Copyright (c) 2008 EclipseSource and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   EclipseSource - initial API and implementation
*******************************************************************************/ 
package org.eclipse.rap.internal.design.example.business.managers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.SubContributionItem;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.rap.internal.design.example.business.layoutsets.MenuBarInitializer;
import org.eclipse.rap.internal.design.example.business.popups.MenuBarPopup;
import org.eclipse.rap.ui.interactiondesign.layout.LayoutRegistry;
import org.eclipse.rap.ui.interactiondesign.layout.model.Layout;
import org.eclipse.rap.ui.interactiondesign.layout.model.LayoutSet;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;


public class BusinessMenuBarManager extends MenuManager {
  
  private static final String PLUGIN_ID = "org.eclipse.rap.design.example";
  private Composite menuParent;
  private List buttonList = new ArrayList();

  public void fill( final Composite parent ) {
    menuParent = parent;
    RowLayout layout = new RowLayout();
    layout.marginLeft = 0; 
    layout.marginRight = 0;
    layout.marginTop = 5;
    parent.setLayout( layout );
    
    update( false, false );
  }
  
  protected void update( final boolean force, final boolean recursive ) {
    if( menuParent != null ) {
      disposeButtons();
      IContributionItem[] items = getItems();
      if( items.length > 0 && menuParent != null ) {
        for( int i = 0; i < items.length; i++ ) {
          IContributionItem item = items[ i ];
          if( item.isVisible() ) {
            makeEntry( item );         
          }
        }      
      }
      menuParent.layout( true, true );
    }
  }

  private void disposeButtons() {
    for( int i = 0; i < buttonList.size(); i++ ) {
      Control control = ( Control ) buttonList.get( i );
      control.dispose();
    }
  }

  private void makeEntry( final IContributionItem item ) {
    if( item instanceof MenuManager ) {
      makeManagerEntry( ( MenuManager ) item );
    } else if( item instanceof SubContributionItem ) {
      SubContributionItem subItem = ( SubContributionItem ) item;
      IContributionItem innerItem = subItem.getInnerItem();
      if( innerItem instanceof MenuManager ) {
        makeManagerEntry( ( MenuManager ) innerItem ); 
      }
    } else if( item instanceof ActionContributionItem ) {
      ActionContributionItem actionItem = (ActionContributionItem ) item;
      IAction action = actionItem.getAction();
      makeActionEntry( action );
    }
  }

  private void makeActionEntry( final IAction action ) {
    Button button = new Button( menuParent, SWT.PUSH | SWT.FLAT );
    button.setData( WidgetUtil.CUSTOM_VARIANT, "menuBar" );
    button.setText( action.getText() );
    button.setToolTipText( action.getToolTipText() );
    button.addSelectionListener( new SelectionAdapter(){
      public void widgetSelected(SelectionEvent e) {
        action.run();
      };
    } );
    
    // needed to clear all controls in case of an update
    buttonList.add( button );
  }

  private void makeManagerEntry( final MenuManager manager ) {
    Composite buttonParent = new Composite( menuParent, SWT.NONE );
    buttonParent.setData( WidgetUtil.CUSTOM_VARIANT, "compTrans" );
    RowLayout layout = new RowLayout();
    layout.spacing = 0;
    layout.marginLeft = -6;
    layout.marginTop = -1;
    layout.marginRight = 33;
    buttonParent.setLayout( layout );
    
    final Button textButton = new Button( buttonParent, SWT.PUSH | SWT.FLAT );
    textButton.setData( WidgetUtil.CUSTOM_VARIANT, "menuBar" );
    textButton.setText( manager.getMenuText() );
    
    Button iconButton = new Button( buttonParent, SWT.PUSH | SWT.FLAT );
    iconButton.setData( WidgetUtil.CUSTOM_VARIANT, "menuBar" );
    LayoutRegistry registry = LayoutRegistry.getInstance();
    Layout activeLayout = registry.getActiveLayout();
    LayoutSet set = activeLayout.getLayoutSet( MenuBarInitializer.SET_ID );
    String path = set.getImagePath( MenuBarInitializer.ARROW );
    ImageDescriptor imgDesc 
      = AbstractUIPlugin.imageDescriptorFromPlugin( PLUGIN_ID, path );
    iconButton.setImage( imgDesc.createImage() );   
    
    SelectionAdapter selAdapter = new SelectionAdapter(){
      public void widgetSelected( SelectionEvent e ) {
        makePopup( manager, textButton );
      }
    };
    textButton.addSelectionListener( selAdapter );
    iconButton.addSelectionListener( selAdapter );
    
    // needed to clear all controls in case of an update
    buttonList.add( buttonParent );
  }

  private void makePopup( 
    final MenuManager manager, 
    final Button textButton ) 
  {
    IContributionItem[] items = manager.getItems();
    if( items.length > 0 ) {
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
                                           null );
      
      popup.open();
      Display display = shell.getDisplay();
      Point pos = display.map( textButton, null, 0, textButton.getSize().y );      
      final Shell popupShell = popup.getShell();
      popupShell.setLocation( pos );
      popupShell.setActive();
    }
    
    
  }
  
}
