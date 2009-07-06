/*******************************************************************************
 * Copyright (c) 2008, 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.examples.internal;

import java.util.Dictionary;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;


public class ExamplesActionBarAdvisor extends ActionBarAdvisor {

  private Action aboutAction;

  public ExamplesActionBarAdvisor( final IActionBarConfigurer configurer ) {
    super( configurer );
  }

  protected void makeActions( final IWorkbenchWindow window ) {
    ImageDescriptor helpActionImage
      = Activator.getImageDescriptor( "icons/help.png" );
    aboutAction = new Action() {
      public void run() {
        Shell shell = window.getShell();
        Bundle bundle = Platform.getBundle( PlatformUI.PLUGIN_ID );
        Dictionary headers = bundle.getHeaders();
        Object version = headers.get( Constants.BUNDLE_VERSION );
        MessageDialog.openInformation( shell,
                                       "RAP Examples",
                                       "Running on RAP version " + version );
      }
    };
    aboutAction.setText( "About" );
    aboutAction.setId( "org.eclipse.rap.examples.about" );
    aboutAction.setImageDescriptor( helpActionImage );
    register( aboutAction );
  }

  protected void fillCoolBar( final ICoolBarManager coolBar ) {
    IToolBarManager toolbar = new ToolBarManager( SWT.FLAT | SWT.RIGHT );
    coolBar.add( new ToolBarContributionItem( toolbar, "main" ) );
    toolbar.add( aboutAction );
  }
}
