/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Innoopract Informationssysteme GmbH - initial API and
 * implementation
 ******************************************************************************/
package org.eclipse.rap.demo;

import org.eclipse.rap.jface.action.*;
import org.eclipse.rap.jface.resource.ImageDescriptor;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.ui.IWorkbenchActionConstants;
import org.eclipse.rap.ui.IWorkbenchWindow;
import org.eclipse.rap.ui.actions.ActionFactory;
import org.eclipse.rap.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.rap.ui.entrypoint.ActionBarAdvisor;
import org.eclipse.rap.ui.entrypoint.IActionBarConfigurer;
import org.eclipse.rap.ui.plugin.AbstractUIPlugin;

public class DemoActionBarAdvisor extends ActionBarAdvisor {

  private IWorkbenchAction exitAction;
  private Action aboutAction;

  public DemoActionBarAdvisor( final IActionBarConfigurer configurer ) {
    super( configurer );
  }

  protected void makeActions( final IWorkbenchWindow window ) {
    ImageDescriptor image 
      = AbstractUIPlugin.imageDescriptorFromPlugin( "org.eclipse.rap.demo", 
                                                    "icons/sample.gif" );
    exitAction = ActionFactory.QUIT.create( window );
    exitAction.setImageDescriptor( image );
    register( exitAction );
    
    aboutAction = new Action() {
      public void run() {
        System.out.println( "run about action..." );
      }
    };
    aboutAction.setText( "About" );
    aboutAction.setId( "org.eclipse.rap.demo.about" );
    aboutAction.setImageDescriptor( image );
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
    IToolBarManager toolbar = new ToolBarManager( RWT.FLAT | RWT.RIGHT );
    coolBar.add( new ToolBarContributionItem( toolbar, name ) );
    toolbar.add( exitAction );
    toolbar.add( aboutAction );
  }
}
