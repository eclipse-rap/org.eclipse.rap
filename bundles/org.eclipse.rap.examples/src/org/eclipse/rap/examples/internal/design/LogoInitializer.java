/******************************************************************************* 
* Copyright (c) 2009 EclipseSource and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   EclipseSource - initial API and implementation
*******************************************************************************/ 
package org.eclipse.rap.examples.internal.design;

import org.eclipse.rap.ui.interactiondesign.layout.model.ILayoutSetInitializer;
import org.eclipse.rap.ui.interactiondesign.layout.model.LayoutSet;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;


public class LogoInitializer implements ILayoutSetInitializer {
  
  public static final String SET_ID 
    = "org.eclipse.rap.design.example.business.layoutset.logo";
  
  public static final String LOGO = "header.logo";
  public static final String LOGO_POSITION = "header.logo.position";

  public void initializeLayoutSet( final LayoutSet layoutSet ) {
    layoutSet.addImagePath( LOGO, "icons/logo.png" );
    
    // positions
    FormData fdLogo = new FormData();
    fdLogo.right = new FormAttachment( 100, -75 );
    fdLogo.top = new FormAttachment( 0, 32 );
    layoutSet.addPosition( LOGO_POSITION, fdLogo );
  }
  
}
