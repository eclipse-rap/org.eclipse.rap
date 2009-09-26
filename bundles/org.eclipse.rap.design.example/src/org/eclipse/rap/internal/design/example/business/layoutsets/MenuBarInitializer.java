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

import org.eclipse.rap.internal.design.example.ILayoutSetConstants;
import org.eclipse.rap.ui.interactiondesign.layout.model.ILayoutSetInitializer;
import org.eclipse.rap.ui.interactiondesign.layout.model.LayoutSet;
import org.eclipse.rwt.graphics.Graphics;


public class MenuBarInitializer implements ILayoutSetInitializer {

  public void initializeLayoutSet( final LayoutSet layoutSet ) {
    String path = ILayoutSetConstants.IMAGE_PATH_BUSINESS;
    layoutSet.addImagePath( ILayoutSetConstants.MENUBAR_ARROW, 
                            path + "menu_arrow.png" );
    layoutSet.addImagePath( ILayoutSetConstants.MENUBAR_TOP_BG, 
                            path + "popup_top_bg.png" );
    layoutSet.addImagePath( ILayoutSetConstants.MENUBAR_BOTTOM_BG, 
                            path + "popup_bottom_bg.png" );
    layoutSet.addImagePath( ILayoutSetConstants.MENUBAR_LEFT_BG, 
                            path + "popup_left_bg.png" );
    layoutSet.addImagePath( ILayoutSetConstants.MENUBAR_RIGHT_BG, 
                            path + "popup_right_bg.png" );
    layoutSet.addImagePath( ILayoutSetConstants.MENUBAR_CORNER_LEFT, 
                            path + "popup_corner_left.png" );
    layoutSet.addImagePath( ILayoutSetConstants.MENUBAR_CORNER_RIGHT, 
                            path + "popup_corner_right.png" );
    layoutSet.addImagePath( ILayoutSetConstants.MENUBAR_SECOND_LAYER_CHEFRON, 
                            path + "popup_secondLayer.png" );
    
    layoutSet.addColor( ILayoutSetConstants.MENUBAR_POPUP, 
                        Graphics.getColor( 244, 244, 244 ) );
    layoutSet.addColor( ILayoutSetConstants.MENUBAR_POPUP_BUTTON, 
                        Graphics.getColor( 0, 89, 165 ) );
  }
}
