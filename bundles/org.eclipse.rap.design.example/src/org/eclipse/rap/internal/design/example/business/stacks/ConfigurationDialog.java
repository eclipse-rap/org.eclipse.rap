/******************************************************************************* 
* Copyright (c) 2008 EclipseSource and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   EclipseSource - initial API and implementation
*******************************************************************************/ 
package org.eclipse.rap.internal.design.example.business.stacks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.rap.internal.design.example.business.builder.DummyBuilder;
import org.eclipse.rap.internal.design.example.business.layoutsets.ConfigDialogInitializer;
import org.eclipse.rap.ui.interactiondesign.ConfigurableStack;
import org.eclipse.rap.ui.interactiondesign.layout.ElementBuilder;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.presentations.IStackPresentationSite;

/**
 * This popup dialog is used for configure presentation properties. Its opened
 * in the <code>{@link ExampleConfigAction#run()}</code> method.
 */
public class ConfigurationDialog extends PopupDialog {

  private IStackPresentationSite site;
  private ConfigAction action;
  private HashMap actionButtonMap = new HashMap();
  private List actionList = new ArrayList();
  private ElementBuilder builder;
  private Button viewMenuBox;
  private boolean viewMenuVisChanged;
  private ControlAdapter resizeListener = new ControlAdapter(){
    public void controlResized( ControlEvent e ) {
      adjustBounds();
    };
  };
  private Listener closeListener;
  
  public ConfigurationDialog( 
    final Shell parent,
    final int shellStyle,
    final IStackPresentationSite site,
    final ConfigAction action )
  {
    super( parent,
           shellStyle,
           false,
           false,
           false,
           false,
           false,
           null,
           null );

    parent.setBackgroundMode( SWT.INHERIT_NONE );
    this.site = site;
    this.action = action;
    hookResizeListener( parent );
    builder = new DummyBuilder( parent,  
                                ConfigDialogInitializer.SET_ID );
    viewMenuVisChanged = false;
  }
  
  private void hookResizeListener( Shell parent ) {
    parent.addControlListener( resizeListener );
  }

  protected void adjustBounds() {
    IWorkbench workbench = PlatformUI.getWorkbench();
    IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
    Rectangle bounds = window.getShell().getBounds();
    getShell().setBounds( bounds.x + ( bounds.width / 5 ), 
                          bounds.y + ( bounds.height / 4 ), 
                          bounds.width - ( ( bounds.width / 5 ) * 2 ), 
                          bounds.height - ( ( bounds.height / 4 ) *2 ) );      
    
  }
  
  public boolean close() {    
    // save the viewmenu visibility
    saveViewMenuVisibility();
    // Save ViewActionVisibility
    saveViewActionVisibilities();
    
    getParentShell().removeControlListener( resizeListener );
    if( getShell() != null ) {
      getShell().removeListener( SWT.Deactivate, closeListener );
      getShell().removeListener( SWT.Close, closeListener );
    }
    return super.close();
  }
  
  protected Control createDialogArea( final Composite parent ) {    
    
    Composite background = new Composite( parent, SWT.NONE );
    background.setLayout( new FormLayout() );
    Color white = builder.getColor( ConfigDialogInitializer.CONFIG_WHITE );
    
    // caption
    Label desc = new Label( background, SWT.NONE );
    desc.setForeground( white );
    desc.setText( "Configuration for " + site.getSelectedPart().getName() );
    FormData fdDesc = new FormData();
    desc.setLayoutData( fdDesc );
    fdDesc.top = new FormAttachment( 0, 10 );
    
    Image titleImage = site.getSelectedPart().getTitleImage();
    if( titleImage != null ) {
      Label image = new Label( background, SWT.NONE );
      image.setImage( titleImage );
      FormData fdImage = new FormData();
      image.setLayoutData( fdImage );
      fdImage.left = new FormAttachment( 0, 10 );
      fdImage.top = new FormAttachment( 0, 8 );
      fdDesc.left = new FormAttachment( image, 5 );
    } else {
      fdDesc.left = new FormAttachment( 0, 10 );
    }
    
    // close the dialog
    Button closeButton = new Button( background, SWT.PUSH );
    closeButton.setText( "close" );    
    String imageId = ConfigDialogInitializer.DIALOG_CLOSE;
    closeButton.setImage( builder.getImage( imageId ) );
    closeButton.setForeground( white );
    closeButton.setData( WidgetUtil.CUSTOM_VARIANT, "clearButton" );
    FormData fdCloseButton = new FormData();
    fdCloseButton.bottom = new FormAttachment( 0, 0 );
    fdCloseButton.right = new FormAttachment( 100, -10 );
    fdCloseButton.top = new FormAttachment( 0, 12 );
    //fdCloseButton.left = new FormAttachment( 100, -25 );
    closeButton.setLayoutData( fdCloseButton );
    closeButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        close();
      }
    } );
    
    Composite configComposite = new Composite( background, SWT.NONE );
    FormData fdConfigComposite = new FormData();
    fdConfigComposite.top = new FormAttachment( closeButton, 40 );
    fdConfigComposite.left = new FormAttachment( 0, 10 );
    fdConfigComposite.right = new FormAttachment( 100, -10 );
    fdConfigComposite.bottom = new FormAttachment( 100, -10 );
    configComposite.setLayoutData( fdConfigComposite );
    GridLayout layout = new GridLayout();
    layout.numColumns = 3;
    configComposite.setLayout( layout );
   
    // Viewmenu
    hookViewMenuArea( white, configComposite );

    // Fill with ViewActions
    loadActionSettings( configComposite );
    
    return background;   
  }

  private void hookViewMenuArea( Color white, Composite configComposite ) {    
    if( action.hasPartMenu() ) {
      Label viewMenuLabel = new Label( configComposite, SWT.NONE );
      viewMenuLabel.setForeground( white );
      viewMenuLabel.setText( "viewmenu visibility" );
      GridData gdViewMenu = new GridData( GridData.FILL_HORIZONTAL );
      gdViewMenu.horizontalSpan = 2;
      viewMenuLabel.setLayoutData( gdViewMenu );
      
      viewMenuBox = new Button( configComposite, SWT.CHECK );
      viewMenuBox.setForeground( white );
      viewMenuBox.setSelection( action.isPartMenuVisible() );
      viewMenuVisChanged = viewMenuBox.getSelection();
    }
  }
  
  protected Color getBackground() {
    return builder.getColor( ConfigDialogInitializer.CONFIG_BLACK );
   
  }

  protected Color getForeground() {
    return builder.getColor( ConfigDialogInitializer.CONFIG_WHITE );
  }
  
  private void loadActionSettings( final Composite container ) {
    
    ConfigurableStack stackPresentation 
      = ( ConfigurableStack ) action.getStackPresentation();
    IToolBarManager manager = stackPresentation.getPartToolBarManager();
    
    boolean showDesc = true;
    GridData data = new GridData();
    if( manager != null ) {
      
      String paneId = stackPresentation.getPaneId( site );
      IContributionItem[] items = manager.getItems();
      
      for( int i = 0; i < items.length; i++ ) {
        if( !( items[ i ] instanceof Separator ) ) {
          
          if( showDesc ) {
            Label actionDesc = new Label( container, SWT.NONE );
            actionDesc.setText( "Action visibility" );
            data = new GridData( GridData.FILL_HORIZONTAL );
            data.horizontalSpan = 3;
            actionDesc.setLayoutData( data );
            showDesc = false;
          }
          
          String actionId = items[ i ].getId();
          
          if( items[ i ] instanceof ActionContributionItem ) {
            // commands
            ActionContributionItem item 
              = ( ActionContributionItem) items[ i ];
            
            Label imageLabel = new Label( container, SWT.NONE );
            imageLabel.setImage( 
                         item.getAction().getImageDescriptor().createImage() );
            
            Label textLabel = new Label( container, SWT.NONE );
            if(    item.getAction().getText() != null
                && !item.getAction().getText().equals( "" ) ) {
              textLabel.setText( item.getAction().getText() );
            } else {
              textLabel.setText( item.getAction().getToolTipText() );
            }

            data = new GridData( GridData.FILL_HORIZONTAL );
            textLabel.setLayoutData( data );
            
            Button check = new Button( container, SWT.CHECK );
            check.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
            boolean selected 
              = action.isViewActionVisibile( paneId, actionId );
            check.setSelection( selected );
            actionButtonMap.put( actionId, check );
            actionList.add( actionId );
            
          } else if( items[ i ] instanceof CommandContributionItem ) {
            // commands
            CommandContributionItem item 
              = ( CommandContributionItem ) items[ i ];            
            Object[] commandInfo = getCommandInfo( item, container );
            if( commandInfo != null ) {
              Label imageLabel = new Label( container, SWT.NONE );
              imageLabel.setImage( ( Image ) commandInfo[ 0 ] );
              Label textLabel = new Label( container, SWT.NONE );
              if(    commandInfo[ 1 ] != null
                  && !commandInfo[ 1 ].equals( "" ) ) {
                textLabel.setText( ( String ) commandInfo[ 1 ] );
              } else {
                textLabel.setText( ( String  ) commandInfo[ 2 ] );
              }
              data = new GridData( GridData.FILL_HORIZONTAL );
              textLabel.setLayoutData( data );
              
              Button check = new Button( container, SWT.CHECK );
              check.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
              boolean selected 
                = action.isViewActionVisibile( paneId, item.getId() );
              check.setSelection( selected );
              actionButtonMap.put( item.getId(), check );
              actionList.add( item.getId() );
            }
          }
        }
      }
    }    
  }
  
//TODO [hs]: if 2nd layout is implemented, extend this dialog with the 
//following methods
//  
//  private void loadLayoutContent() {
//    LayoutRegistry registry = LayoutRegistry.getInstance();
//    IConfigurationElement[] elements = registry.getLayoutExtensions();
//    for( int i = 0; i < elements.length; i++ ) {
//      String id = elements[ i ].getAttribute( "id" );
//      String name = elements[ i ].getAttribute( "name" );
//      layoutCombo.add( name );
//      layoutIdList.add( id );
//    }
//    String savedLayoutId = registry.getSavedLayoutId();
//    if( layoutIdList.contains( savedLayoutId ) && 
//        !savedLayoutId.equals( IPreferenceStore.STRING_DEFAULT_DEFAULT ) ) 
//    {
//      layoutCombo.select( layoutIdList.indexOf( savedLayoutId ) );
//    }
//    
//  }
//  
//  private boolean isStandalone( final String viewId ) {
//    boolean result = false;
//    IWorkbench workbench = PlatformUI.getWorkbench();
//    IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
//    IWorkbenchPage activePage = workbenchWindow.getActivePage();    
//    IPerspectiveDescriptor perspDesc = activePage.getPerspective();
//    IViewReference ref = activePage.findViewReference( viewId );
//
//    WorkbenchPage page = ( WorkbenchPage ) activePage;
//    Perspective perspective = page.findPerspective( perspDesc );
//    if( ref != null ) {
//      result = perspective.isStandaloneView( ref );
//    }
//    
//    return result;
//  }
//  
//  private void loadStackLayoutContent() {
//    final String savedId = ConfigurableStack.getSavedStackId( site );
//    if( presiCombo != null ) {
//      IExtensionRegistry registry = Platform.getExtensionRegistry();
//      IExtensionPoint point 
//        = registry.getExtensionPoint( 
//          ConfigurableStack.STACK_PRESENTATION_EXT_ID );
//      if( point != null ) {
//        IConfigurationElement[] elements = point.getConfigurationElements();
//        
//        String type = "";
//        PresentablePart part = ( PresentablePart ) site.getSelectedPart();
//        PartPane pane = part.getPane();
//        String viewId = pane.getID();
//        if( pane instanceof ViewPane ) {
//          if( isStandalone( viewId ) ) {
//            type = PresentationFactory.KEY_STANDALONE_VIEW;
//          } else {
//            type  = PresentationFactory.KEY_VIEW;
//          }
//        } else {
//          type = PresentationFactory.KEY_EDITOR;
//        }
//        
//        for( int i = 0; i < elements.length; i++ ) {
//          if( elements[ i ].getAttribute( "type" ).equals( type ) ) {
//            presiCombo.add( elements[ i ].getAttribute( "name" ) );
//            idList.add( elements[ i ].getAttribute( "id" ) );            
//          }            
//        }
//
//        if( idList.contains( savedId ) )
//          presiCombo.select( idList.indexOf( savedId ) );
//      }
//      
//    }
//    
//    if( presiCombo.getItemCount() > 0 ) {
//      presiCombo.addModifyListener( new ModifyListener() {
//
//        public void modifyText( ModifyEvent event ) {
//          int index = presiCombo.getSelectionIndex();
//          if( idList.indexOf( savedId ) == index ) {
//            layoutChanged = false;
//          } else {
//            layoutChanged = true;
//          }          
//        }
//        
//      });
//    }
//  }

  private Object[] getCommandInfo( 
    final CommandContributionItem item, 
    final Composite container ) 
  {
    Object[] result = null;
    ToolBar toolbar = new ToolBar( container, SWT.NONE );
    toolbar.setVisible( false );
    item.fill( toolbar, -1 );
    ToolItem[] items = toolbar.getItems();
    for( int i = 0; i < items.length; i++ ) {
      result = new Object[ 3 ];
      result[ 0 ] = items[ i ].getImage();
      result[ 1 ] = items[ i ].getText();
      result[ 2 ] = items[ i ].getToolTipText();
    }
    toolbar.dispose();
    return result;
  }

  public int open() {
    int result = super.open();
    closeListener = new Listener() {
      public void handleEvent( Event event ) {
        close();
      }
    };
    getShell().addListener( SWT.Deactivate, closeListener );
    getShell().addListener( SWT.Close, closeListener );

    getShell().setAlpha( 180 );
    getShell().setBackgroundMode( SWT.INHERIT_NONE );
    
    Color black = builder.getColor( ConfigDialogInitializer.CONFIG_BLACK );
    getShell().setBackground( black );
    getShell().setActive();
    getShell().setFocus();
    getContents().setBackground( black );
    return result;
  }
  
//TODO [hs]: if 2nd layout is implemented, extend this dialog with the 
//following methods
//  
//  private void saveLayoutId() {
//    int selectionIndex = layoutCombo.getSelectionIndex();
//    if( selectionIndex != -1 ) {
//      String id = ( String ) layoutIdList.get( selectionIndex );
//      
//      LayoutRegistry registry = LayoutRegistry.getInstance();
//      String savedLayoutId = registry.getSavedLayoutId();
//      if( !savedLayoutId.equals( id ) ) {
//        registry.setActiveLayout( id, true );
//      }
//      
//    }
//    
//  }
//  
//  private void reloadStackLayout() {
//    if( layoutChanged && ( action != null ) ) {
//      String id = ( String ) idList.get( presiCombo.getSelectionIndex() );      
//
//      action.fireLayoutChange( id );
//    }
//  }
//  
//  private void saveStackPresentationId() {
//    int index = presiCombo.getSelectionIndex();
//    if( index > -1) {
//      action.saveStackPresentationId( ( String ) idList.get( index ) );
//    }
//  }
  
  private void saveViewActionVisibilities() {
    ConfigurableStack stackPresentation 
    = ( ConfigurableStack ) action.getStackPresentation();
    String paneId = stackPresentation.getPaneId( site );
    
    for( int i = 0; i < actionList.size(); i++ ) {
      String actionId = ( String ) actionList.get( i );
      Button check = ( Button ) actionButtonMap.get( actionId );
      action.saveViewActionVisibility( paneId, actionId, check.getSelection() );
    }
    action.fireToolBarChange();
  }
  
  private void saveViewMenuVisibility() {
    if( viewMenuBox != null ) {
      boolean selection = viewMenuBox.getSelection();
      if( selection != viewMenuVisChanged ) {
        action.savePartMenuVisibility( selection );
        action.fireToolBarChange();
      }
    }
    
  }
  
}
