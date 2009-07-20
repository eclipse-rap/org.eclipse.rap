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

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.ContributionManager;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.internal.provisional.action.IToolBarManager2;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.rap.internal.design.example.business.CommandUtil;
import org.eclipse.rap.internal.design.example.business.CommandUtil.CommandParameter;
import org.eclipse.rap.internal.design.example.business.builder.DummyBuilder;
import org.eclipse.rap.internal.design.example.business.layoutsets.StackInitializer;
import org.eclipse.rap.ui.interactiondesign.layout.ElementBuilder;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.IWorkbenchWindowPulldownDelegate;
import org.eclipse.ui.internal.WWinPluginPulldown;
import org.eclipse.ui.menus.CommandContributionItem;


public class BusinessViewToolBarManager extends ContributionManager 
  implements IToolBarManager2, IAdaptable 
{

  private Composite control;
  private List itemList = new ArrayList();
  
  
  public BusinessViewToolBarManager() {
//    
  }
  
  public void addPropertyChangeListener( 
    final IPropertyChangeListener listener )  
  {
  }

  public ToolBar createControl( final Composite parent ) {
    return null;
  }

  public Control createControl2( final Composite parent ) {
    if( !toolBarExist() && parent != null ) {
      control = new Composite( parent, SWT.NONE );
      control.setData( WidgetUtil.CUSTOM_VARIANT, "compTrans" );
      RowLayout layout = new RowLayout();
      layout.spacing = 3;
      control.setLayout( layout );
    }
    return control;
    
  }
    
  
  private boolean toolBarExist() {
    return control != null && !control.isDisposed();
  }

  public ToolBar getControl() {
    return null;
  }

  public Control getControl2() {
    return control;
  }
  
  public int getItemCount() {
    return getItems().length;
  }

  private void makeActionButton( final Action action, final IContributionItem item ) {
    if( !itemList.contains( item.getId() ) ) {      
      int flags = SWT.PUSH;
      switch( action.getStyle() ) {
        case IAction.AS_CHECK_BOX:
          flags = SWT.TOGGLE;
        break;
        case IAction.AS_DROP_DOWN_MENU:
          flags = SWT.DROP_DOWN;
        break;
        case IAction.AS_RADIO_BUTTON:
          flags = SWT.TOGGLE;
        break;
        default:
          flags = SWT.PUSH;
        break;
      }
      
      Composite parent = control;      
      if( flags == SWT.DROP_DOWN ) {
        final Composite pulldownParent = getPulldownParent();
        parent = pulldownParent;        
        flags = SWT.PUSH;
      }  
      // normal items
      final Button button = new Button( parent, flags );    
      button.setData( item );
      button.setToolTipText( action.getToolTipText() );
      button.setImage( action.getImageDescriptor().createImage() );
      button.setData( WidgetUtil.CUSTOM_VARIANT, "clearButton" );
      button.addSelectionListener( new SelectionAdapter() {
        public void widgetSelected( final SelectionEvent e ) {
          // SWT.DROPDOWN not yet supported
          boolean checked = button.getSelection();
          int style = button.getStyle();
          if( ( style & ( SWT.TOGGLE | SWT.CHECK ) ) != 0 ) {
            if( action.getStyle() == IAction.AS_CHECK_BOX ) {
              action.setChecked( checked );
            }
          } else if( ( style & SWT.RADIO ) != 0 ) {
            if( action.getStyle() == IAction.AS_RADIO_BUTTON ) {
              action.setChecked( checked );
            }
          }
          action.run();
        }
      } );
      // pulldown
      if( action.getStyle() == SWT.DROP_DOWN ) {
        FormData fdButton = new FormData();
        button.setLayoutData( fdButton );
        fdButton.left = new FormAttachment( 0 );
        fdButton.top = new FormAttachment( 0 );
        final Button arrow = new Button( parent, SWT.PUSH );
        FormData fdArrow = new FormData();
        arrow.setLayoutData( fdArrow );
        fdArrow.left = new FormAttachment( button, 0 );
        fdArrow.top = new FormAttachment( 0, 7 );
        ElementBuilder builder 
          = new DummyBuilder( null, StackInitializer.SET_ID );
        Image image = builder.getImage( StackInitializer.VIEW_PULLDOWN );
        arrow.setImage( image );
        arrow.setData( WidgetUtil.CUSTOM_VARIANT, "clearButton" );
        arrow.addSelectionListener( new SelectionAdapter() {
          public void widgetSelected( final SelectionEvent e ) {          
            Display display = arrow.getDisplay();
            Point newLoc = display.map( arrow, null, 0, 10 );
            Menu menu = getActionMenu( action, item );
            if( menu != null ) {
              menu.setVisible( true );
              menu.setLocation( newLoc );
            }
          };
        } ); 
      }
      itemList.add( item.getId() );
    }
  }

  private Menu getActionMenu( 
    final Action action, 
    final IContributionItem item ) 
  {
    Menu menu = null;
    // pulldown 
    if( item instanceof CommandContributionItem ) {
      CommandContributionItem commandItem = ( CommandContributionItem ) item;
      CommandParameter param 
        = CommandUtil.extractCommandInformation( commandItem, control );
      if( param.getMenu() != null ) {
        menu = param.getMenu();
      }
    } else if( action instanceof WWinPluginPulldown ) {
      WWinPluginPulldown pulldown = ( WWinPluginPulldown ) action;
      menu = pulldown.getMenuCreator().getMenu( control );              
    } else if( action instanceof IMenuCreator ) {
      IMenuCreator creator = ( IMenuCreator ) action;
      menu = creator.getMenu( control );        
    } else if( action instanceof IWorkbenchWindowPulldownDelegate ) {
      IWorkbenchWindowPulldownDelegate delegate 
        = ( IWorkbenchWindowPulldownDelegate ) action;
      menu = delegate.getMenu( control );
    }
    return menu;
  }

  private Composite getPulldownParent() {
    final Composite pulldownParent = new Composite( control, SWT.NONE );        
    pulldownParent.setLayout( new FormLayout() );
    pulldownParent.setData( WidgetUtil.CUSTOM_VARIANT, "compTrans" );
    
    pulldownParent.addDisposeListener( new DisposeListener() {          
      public void widgetDisposed( final DisposeEvent event ) {
        Control[] children = pulldownParent.getChildren();
        for( int i = 0; i < children.length; i++ ) {
          Control control = children[ i ];
          control.dispose();
        }
      }
    } );
    return pulldownParent;
  }

  public void removePropertyChangeListener( 
    final IPropertyChangeListener listener ) 
  {
  }
  
  public void update( final boolean force ) {
    itemList.clear();
    Control[] children = control.getChildren();
    for( int i = 0; i < children.length; i++ ) {
      children[ i ].dispose();
    }
    
    IContributionItem[] items = getItems();
    for( int i = 0; i < items.length; i++ ) {
      IContributionItem item = items[ i ];
      if( item.isVisible() && item instanceof ActionContributionItem ) {
        // actions
        IAction action = ( ( ActionContributionItem ) item ).getAction();
        makeActionButton( ( Action ) action, item );
      } else if( item.isVisible() && item instanceof CommandContributionItem ) {
        // commands
        makeCommandButton( item );
      }
    }  
    
    control.pack();
    control.layout( true );
  }

  private void makeCommandButton( final IContributionItem item ) {
    CommandContributionItem comamndItem = ( CommandContributionItem ) item;
    final Action action = CommandUtil.wrapCommand( comamndItem, control );
    makeActionButton( action, item );
  }

  public void dispose() {
    if( control != null && !control.isDisposed() ) {
      control.dispose();
    }
    IContributionItem[] items = getItems();
    for( int i = 0; i < items.length; i++ ) {
        items[ i ].dispose();
    }

  }

  public Object getAdapter( Class adapter ) {
    Object result = null;
   
    return result;
  } 
}
