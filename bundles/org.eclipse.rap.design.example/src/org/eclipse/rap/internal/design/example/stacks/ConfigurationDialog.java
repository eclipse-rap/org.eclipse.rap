/******************************************************************************* 
* Copyright (c) 2008 EclipseSource and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   EclipseSource - initial API and implementation
*******************************************************************************/ 
package org.eclipse.rap.internal.design.example.stacks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.rap.internal.design.example.ILayoutSetConstants;
import org.eclipse.rap.internal.design.example.builder.DummyBuilder;
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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
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

  private static final int OFFSET = 3;
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
  private Label lastImageLabel;
  private Label description;
  private Shell modalBackground;
  
  public ConfigurationDialog( 
    final Shell parent,
    final int shellStyle,
    final IStackPresentationSite site,
    final ConfigAction action )
  {
    super( parent,
           shellStyle,
           true,
           false,
           false,
           false,
           false,
           null,
           null );

    //parent.setBackgroundMode( SWT.INHERIT_NONE );
    this.site = site;
    this.action = action;
    hookResizeListener( parent );
    builder = new DummyBuilder( parent,  
                                ILayoutSetConstants.SET_ID_CONFIG_DIALOG );
    viewMenuVisChanged = false;
  }
  
  private void hookResizeListener( Shell parent ) {
    parent.addControlListener( resizeListener );
  }

  protected void adjustBounds() {
    getShell().layout();
    IWorkbench workbench = PlatformUI.getWorkbench();
    IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
    Rectangle bounds = window.getShell().getBounds();
    int newWidth = getShell().getBounds().width + 20;
    int newHeight = getShell().getBounds().height + 20;
    getShell().setBounds( bounds.x + ( bounds.width / 2 ) - ( newWidth / 2 ), 
                          bounds.y + ( bounds.height / 2 ) - ( newHeight / 2 ), 
                          newWidth, 
                          newHeight );          
  }
  
  public boolean close( final boolean save ) { 
    if( save ) {
      // save the viewmenu visibility
      saveViewMenuVisibility();
      // Save ViewActionVisibility
      saveViewActionVisibilities();
    }
    return close();
  }
  
  public boolean close() {
    getParentShell().removeControlListener( resizeListener );
    modalBackground.close();
    modalBackground.dispose();
    action.fireToolBarChange();
    return super.close();
  }
  
  protected Control createDialogArea( final Composite parent ) {        
    Composite background = new Composite( parent, SWT.NONE );
    background.setLayout( new FormLayout() );
    Color white = builder.getColor( ILayoutSetConstants.CONFIG_WHITE );
        
    Composite configComposite = new Composite( background, SWT.NONE );
    FormData fdConfigComposite = new FormData();
    fdConfigComposite.top = new FormAttachment( 0, 0 );
    fdConfigComposite.left = new FormAttachment( 0, 10 );
    fdConfigComposite.right = new FormAttachment( 100, -10 );
    fdConfigComposite.bottom = new FormAttachment( 100, -10 );
    configComposite.setLayoutData( fdConfigComposite );
    configComposite.setLayout( new FormLayout() );
   
    // Fill with ViewActions
    loadActionSettings( configComposite );
    
    // Viewmenu
    hookViewMenuArea( white, configComposite );
    
    // OK / Cancel buttons
    Button cancel = new Button( configComposite, SWT.PUSH );
    cancel.setText( "Cancel" );
    FormData fdCancel = new FormData();
    cancel.setLayoutData( fdCancel );
    fdCancel.bottom = new FormAttachment( 100, 0 );
    fdCancel.right = new FormAttachment( 100, 0 );
    fdCancel.width = 90;
    cancel.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent e ) {
        close( false );
      };
    } );
    
    Button ok = new Button( configComposite, SWT.PUSH );
    ok.setText( "OK" );
    FormData fdOK = new FormData();
    ok.setLayoutData( fdOK );
    fdOK.right = new FormAttachment( cancel, -OFFSET );
    fdOK.bottom = fdCancel.bottom;
    fdOK.width = 90;
    ok.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent e ) {
        close( true );
      };
    } );
    ok.moveAbove( cancel );    
    return background;   
  }

  private void hookViewMenuArea( Color white, Composite configComposite ) {    
    if( action.hasPartMenu() ) {
      viewMenuBox = new Button( configComposite, SWT.CHECK );
      viewMenuBox.setForeground( white );
      viewMenuBox.setSelection( action.isPartMenuVisible() );
      viewMenuVisChanged = viewMenuBox.getSelection();
      FormData fdViewMenuBox = new FormData();
      viewMenuBox.setLayoutData( fdViewMenuBox );
      viewMenuBox.setData( WidgetUtil.CUSTOM_VARIANT, "configMenuButton" );
      if( lastImageLabel != null ) {
        fdViewMenuBox.top = new FormAttachment( lastImageLabel, OFFSET );
        fdViewMenuBox.left = new FormAttachment( lastImageLabel, OFFSET + 5 );
      } else {
        fdViewMenuBox.top = new FormAttachment( description, OFFSET );
        fdViewMenuBox.left = new FormAttachment( 0, OFFSET + 5 );
      }
      viewMenuBox.setText( "viewmenu" );      
    }
  }
  
  private void loadActionSettings( final Composite container ) {    
    ConfigurableStack stackPresentation 
      = ( ConfigurableStack ) action.getStackPresentation();
    IToolBarManager manager = stackPresentation.getPartToolBarManager();    
    description = null;
    description = new Label( container, SWT.NONE );
    description.setText( "Visible actions" );
    FormData fdActionDesc = new FormData();
    description.setLayoutData( fdActionDesc );
    if( viewMenuBox != null ) {
      fdActionDesc.top = new FormAttachment( viewMenuBox, OFFSET );              
    } else {
      fdActionDesc.top = new FormAttachment( 0, OFFSET );
    }
    fdActionDesc.left = new FormAttachment( 0, OFFSET );
    if( manager != null ) {      
      String paneId = stackPresentation.getPaneId( site );
      IContributionItem[] items = manager.getItems();
      
      for( int i = 0; i < items.length; i++ ) {
        if( !( items[ i ] instanceof Separator ) ) {                    
          // handle items
          String itemId = items[ i ].getId();  
          String text = null;
          Image icon = null;
          if( items[ i ] instanceof ActionContributionItem ) {
            // actions
            ActionContributionItem item = ( ActionContributionItem ) items[ i ];
            icon = item.getAction().getImageDescriptor().createImage();
            if( item.getAction().getText() != null
                && !item.getAction().getText().equals( "" ) ) {
              text = item.getAction().getText();
            } else {
              text = item.getAction().getToolTipText();
            }            
          } else if( items[ i ] instanceof CommandContributionItem ) {
            // commands
            CommandContributionItem item 
              = ( CommandContributionItem ) items[ i ];            
            Object[] commandInfo = getCommandInfo( item, container );
            if( commandInfo != null ) {
              icon = ( Image ) commandInfo[ 0 ];
              if( commandInfo[ 1 ] != null
                  && !commandInfo[ 1 ].equals( "" ) ) {
                text = ( String ) commandInfo[ 1 ];
              } else {
                text = ( String  ) commandInfo[ 2 ];
              }              
            }                        
          }
          Label imageLabel = new Label( container, SWT.NONE );
          imageLabel.setImage( icon );        
          FormData fdImageLabel = new FormData();
          imageLabel.setLayoutData( fdImageLabel );
          if( lastImageLabel != null ) {
            fdImageLabel.top = new FormAttachment( lastImageLabel, OFFSET );
            lastImageLabel = imageLabel;
          } else {
            fdImageLabel.top = new FormAttachment( description, OFFSET );
            lastImageLabel = imageLabel;
          }
          fdImageLabel.left = new FormAttachment( 0, OFFSET * 4 );   
          
          Button check = new Button( container, SWT.CHECK );
          check.setText( text );
          check.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
          boolean selected 
            = action.isViewActionVisibile( paneId, itemId );
          FormData fdCheck = new FormData();
          check.setLayoutData( fdCheck );
          fdCheck.left = new FormAttachment( imageLabel, OFFSET + 5 );
          fdCheck.top = fdImageLabel.top;
          check.setSelection( selected );
          check.setData( WidgetUtil.CUSTOM_VARIANT, "configMenuButton" );
          actionButtonMap.put( itemId, check );
          actionList.add( itemId );
          lastImageLabel = imageLabel;
        }
      }
    }    
  }

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
    IWorkbench workbench = PlatformUI.getWorkbench();
    IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
    Rectangle bounds = window.getShell().getBounds();
    modalBackground 
      = new Shell( getParentShell(), SWT.NO_TRIM );
    modalBackground.setAlpha( 80 );   
    modalBackground.setBounds( bounds );
    modalBackground.open();
    
    int result = super.open();
    Shell shell = getShell();
    shell.setBackgroundMode( SWT.INHERIT_NONE );
    shell.setText( "Configuration for " + site.getSelectedPart().getName() );
    shell.setImage( builder.getImage( ILayoutSetConstants.CONFIG_DIALOG_ICON ) );
    shell.setActive();
    shell.setFocus();  
    action.fireToolBarChange();
    adjustBounds();
    return result;
  }
  
  private void saveViewActionVisibilities() {
    ConfigurableStack stackPresentation 
    = ( ConfigurableStack ) action.getStackPresentation();
    String paneId = stackPresentation.getPaneId( site );
    
    for( int i = 0; i < actionList.size(); i++ ) {
      String actionId = ( String ) actionList.get( i );
      Button check = ( Button ) actionButtonMap.get( actionId );
      action.saveViewActionVisibility( paneId, actionId, check.getSelection() );
    }
  }
  
  private void saveViewMenuVisibility() {
    if( viewMenuBox != null ) {
      boolean selection = viewMenuBox.getSelection();
      if( selection != viewMenuVisChanged ) {
        action.savePartMenuVisibility( selection );        
      }
    }
    
  }
  
}
