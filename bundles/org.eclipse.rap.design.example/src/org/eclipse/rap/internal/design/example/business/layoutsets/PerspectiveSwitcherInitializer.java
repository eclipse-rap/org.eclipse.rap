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


public class PerspectiveSwitcherInitializer implements ILayoutSetInitializer {

  public static final String SET_ID 
    = "org.eclipse.rap.design.example.layoutset.perspective";
  public static final String CLOSE = "perspective.close";
  public static final String LEFT_ACTIVE = "perspective.left.active";
  public static final String RIGHT_ACTIVE = "perspective.right.active";
  public static final String BG = "perspective.bg";

  public void initializeLayoutSet( LayoutSet layoutSet ) {
    layoutSet.addImagePath( CLOSE, 
                            ILayoutSetConstants.IMAGE_PATH + "close.png" );
    layoutSet.addImagePath( LEFT_ACTIVE, 
                            ILayoutSetConstants.IMAGE_PATH 
                            + "perspective_left_active.png" );
    layoutSet.addImagePath( RIGHT_ACTIVE, 
                            ILayoutSetConstants.IMAGE_PATH 
                            + "perspective_right_active.png" );
    layoutSet.addImagePath( BG, 
                            ILayoutSetConstants.IMAGE_PATH 
                            + "perspective_bg.png" );    
  }
}
