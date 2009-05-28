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
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;


public class PerspectiveSwitcherInitializer implements ILayoutSetInitializer {

  public static final String SET_ID 
    = "org.eclipse.rap.design.example.business.layoutset.perspective";
  public static final String CLOSE = "perspective.close";
  public static final String ACTIVE = "perspective.active";
  public static final String INACTIVE = "perspective.inactive";

  public void initializeLayoutSet( LayoutSet layoutSet ) {
    layoutSet.addImagePath( CLOSE, 
                            ILayoutSetConstants.IMAGE_PATH + "close.png" );
    
    String fontFamily = "Arial, Calibri, Tahoma, Sans-Serif";
    Font perspActiveFont = Graphics.getFont( fontFamily, 11, SWT.BOLD );
    layoutSet.addFont( ACTIVE, perspActiveFont );
    
    Font perspInactiveFont = Graphics.getFont( fontFamily, 11, SWT.NORMAL );

    layoutSet.addFont( INACTIVE, perspInactiveFont );
  }
}
