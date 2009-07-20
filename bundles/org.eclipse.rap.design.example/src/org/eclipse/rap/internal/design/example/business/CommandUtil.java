/******************************************************************************* 
* Copyright (c) 2009 EclipseSource and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   EclipseSource - initial API and implementation
*******************************************************************************/ 
package org.eclipse.rap.internal.design.example.business;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.CommandManager;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.IMenuService;


public class CommandUtil {
  
  public static class CommandParameter {    
    private Menu menu;
    private int style;
    private Image icon;
    private Command command;
    private String text;
    private String tooltipText;

    public CommandParameter( 
      final Command command, 
      final String id, 
      final Image icon, 
      final int style, 
      final Menu menu,
      final String text,
      final String tooltipText ) 
    {
      this.command = command;
      this.icon = icon;
      this.style = style;
      this.menu = menu;
      this.text = text;
      this.tooltipText = tooltipText;
    }
    
    public Command getCommand() {
      return command;
    }
    
    public Menu getMenu() {
      return menu;
    }
    
    public int getStyle() {
      return style;
    }
    
    public Image getIcon() {
      return icon;
    }
    
    public String getText() {
      return text;
    }

    public String getTooltipText() {
      return tooltipText;
    }
  }
  
  /*
   * Wraps a command contrib item to an action
   */
  public static Action wrapCommand( 
    final CommandContributionItem item, 
    final Composite parent ) 
  {
    Action result = null;    
    final CommandParameter param = extractCommandInformation( item, parent );    
    String text = param.getTooltipText();
    String toolTipText = param.getTooltipText();
    int style = IAction.AS_PUSH_BUTTON;
    if( param.getStyle() == CommandContributionItem.STYLE_PULLDOWN ) {
      style = IAction.AS_DROP_DOWN_MENU;
    } else if( param.getStyle() == CommandContributionItem.STYLE_CHECK ) {
      style = IAction.AS_CHECK_BOX;
    } else if( param.getStyle() == CommandContributionItem.STYLE_RADIO ) {
      style = IAction.AS_RADIO_BUTTON;
    }
    result = new Action( text, style ){
      public void run() {
        Command command = param.getCommand();
        executeCommand( command );
      }
    };
    ImageDescriptor desc = new ImageDescriptor() {
      public Image createImage() {
        return param.getIcon();
      }      
    };
    result.setImageDescriptor( desc );
    result.setToolTipText( toolTipText );    
    return result;
  }
  
  public static CommandParameter extractCommandInformation( 
    final CommandContributionItem item, 
    final Composite parent ) 
  {
    // extract command
    CommandParameter result = null;
    CommandManager manager = new CommandManager();
    Command command = manager.getCommand( item.getId() );
    IWorkbench workbench = PlatformUI.getWorkbench();
    IWorkbenchWindow activeWindow = workbench.getActiveWorkbenchWindow();
    Object service = activeWindow.getService( ICommandService.class );     
    if( !command.isDefined() ) {
      if( service != null && service instanceof ICommandService ) {
        ICommandService commandService = ( ICommandService ) service;        
        command = commandService.getCommand( item.getId() );
      }
    }
    // extract image
    Image icon = null;
    String text = "";
    String tooltipText = "";
    int style = CommandContributionItem.STYLE_PUSH;
    ToolBar toolbar = new ToolBar( parent, SWT.NONE );
    toolbar.setVisible( false );
    item.fill( toolbar, -1 );
    ToolItem[] toolItems = toolbar.getItems();
    if( toolItems.length <= 0 ) {
      throw new IllegalStateException( "Command information not extractable");
    }
    ToolItem toolItem = toolItems[ 0 ];
    style = toolItem.getStyle();
    icon = toolItem.getImage();
    text = toolItem.getToolTipText();
    tooltipText = toolItem.getToolTipText();

    // extract menu
    Menu menu = null;
    if( style == CommandContributionItem.STYLE_PULLDOWN ) {
      final MenuManager menuManager = new MenuManager();      
      Object abstractService = activeWindow.getService( IMenuService.class );
      IMenuService menuService = ( IMenuService ) abstractService;
      String identifier = "menu:" + item.getId();
      menuService.populateContributionManager( menuManager, identifier );
      menu = menuManager.createContextMenu( parent );
    }
    result = new CommandParameter( 
                                 command, 
                                 item.getId(), 
                                 icon, 
                                 style, 
                                 menu, 
                                 text,
                                 tooltipText );
    
    toolbar.dispose();
    return result;
  }
  
  public static void executeCommand( final Command command ) {
    if( command != null ) {
      IWorkbench workbench = PlatformUI.getWorkbench();
      Object service = workbench.getService( IHandlerService.class );
      if( service != null && service instanceof IHandlerService ) {
        IHandlerService handlerService = ( IHandlerService ) service;
        try {
          handlerService.executeCommand( command.getId(), new Event() );
        } catch( ExecutionException e ) {
          e.printStackTrace();
        } catch( NotDefinedException e ) {
          e.printStackTrace();
        } catch( NotEnabledException e ) {
          e.printStackTrace();
        } catch( NotHandledException e ) {
          e.printStackTrace();
        }
      }
    }
  }
}
