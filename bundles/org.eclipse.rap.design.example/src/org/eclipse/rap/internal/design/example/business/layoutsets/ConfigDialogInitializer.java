/******************************************************************************* 
* Copyright (c) 2009 EclipseSource and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   EclipseSource - initial API and implementation
*******************************************************************************/ 
package org.eclipse.rap.internal.design.example.business.layoutsets;

import org.eclipse.rap.ui.interactiondesign.layout.model.ILayoutSetInitializer;
import org.eclipse.rap.ui.interactiondesign.layout.model.LayoutSet;
import org.eclipse.rwt.graphics.Graphics;


public class ConfigDialogInitializer implements ILayoutSetInitializer {

  public static final String SET_ID 
    = "org.eclipse.rap.design.example.business.layoutset.confdialog";
  
  public static final String CONFIG_WHITE = "CONFIG_WHITE";
  public static final String CONFIG_BLACK = "CONFIG_BLACK";
  public static final String DIALOG_CLOSE = "dialog.close";
  
  public void initializeLayoutSet( final LayoutSet layoutSet ) {
    layoutSet.addColor( CONFIG_BLACK, Graphics.getColor( 0, 0, 0 ) );
    layoutSet.addColor( CONFIG_WHITE, Graphics.getColor( 255, 255, 255 ) );
    layoutSet.addImagePath( DIALOG_CLOSE, 
                            ILayoutSetConstants.IMAGE_PATH + "close.png" );
  }
}
