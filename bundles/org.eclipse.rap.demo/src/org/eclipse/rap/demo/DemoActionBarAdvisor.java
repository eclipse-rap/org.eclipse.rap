/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Innoopract Informationssysteme GmbH - initial API and
 * implementation
 ******************************************************************************/
package org.eclipse.rap.demo;

import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.entrypoint.ActionBarAdvisor;
import org.eclipse.ui.entrypoint.IActionBarConfigurer;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class DemoActionBarAdvisor extends ActionBarAdvisor {

  private IWorkbenchAction exitAction;
  private Action aboutAction;

  public DemoActionBarAdvisor( final IActionBarConfigurer configurer ) {
    super( configurer );
  }

  protected void makeActions( final IWorkbenchWindow window ) {
    ImageDescriptor image1 
      = AbstractUIPlugin.imageDescriptorFromPlugin( "org.eclipse.rap.demo", 
                                                    "icons/ttt.gif" );
    ImageDescriptor image2 
      = AbstractUIPlugin.imageDescriptorFromPlugin( "org.eclipse.rap.demo", 
                                                    "icons/help.gif" );
    exitAction = ActionFactory.QUIT.create( window );
    exitAction.setImageDescriptor( image1 );
    register( exitAction );
    
    aboutAction = new Action() {
      public void run() {
        MessageDialog.openInformation( window.getShell(), 
                                       "RAP Demo", "About action clicked", 
                                       null );
      }
    };
    aboutAction.setText( "About" );
    aboutAction.setId( "org.eclipse.rap.demo.about" );
    aboutAction.setImageDescriptor( image2 );
    register( aboutAction );
  }

  protected void fillMenuBar( final IMenuManager menuBar ) {
    MenuManager fileMenu = new MenuManager( "File",
                                            IWorkbenchActionConstants.M_FILE );
    MenuManager helpMenu = new MenuManager( "Help",
                                            IWorkbenchActionConstants.M_HELP );
    
    menuBar.add( fileMenu );
    fileMenu.add( exitAction );
    
    menuBar.add( helpMenu );
    helpMenu.add( aboutAction );
  }
  
  protected void fillCoolBar( ICoolBarManager coolBar ) {
    createToolBar( coolBar, "main" );
    createToolBar( coolBar, "test" );
  }

  private void createToolBar( ICoolBarManager coolBar, final String name ) {
    IToolBarManager toolbar = new ToolBarManager( SWT.FLAT | SWT.RIGHT );
    coolBar.add( new ToolBarContributionItem( toolbar, name ) );
    toolbar.add( exitAction );
    toolbar.add( aboutAction );
  }
}
