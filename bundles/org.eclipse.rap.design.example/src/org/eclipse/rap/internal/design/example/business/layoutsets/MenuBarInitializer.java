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


public class MenuBarInitializer implements ILayoutSetInitializer {

  public static final String SET_ID 
    = "org.eclipse.rap.design.example.business.layoutset.menubar";
  public static final String ARROW = "menubar.arrow";
  public static final String POPUP = "menubar.popup";
  public static final String POPUP_BUTTON = "menubar.popup.button";
  public static final String TOP_BG = "menubar.popup.top";
  public static final String BOTTOM_BG = "menubar.popup.bottom";
  public static final String LEFT_BG = "menubar.popup.left";
  public static final String RIGHT_BG = "menubar.popup.right";
  public static final String CORNER_LEFT = "menubar.popup.corner.left";
  public static final String CORNER_RIGHT = "menubar.popup.corner.right";
  public static final String SECOND_LAYER_CHEFRON = "menubar.popup.secondlayer";

  public void initializeLayoutSet( LayoutSet layoutSet ) {
    String path = ILayoutSetConstants.IMAGE_PATH;
    layoutSet.addImagePath( ARROW, path + "menu_arrow.png" );
    layoutSet.addImagePath( TOP_BG, path + "popup_top_bg.png" );
    layoutSet.addImagePath( BOTTOM_BG, path + "popup_bottom_bg.png" );
    layoutSet.addImagePath( LEFT_BG, path + "popup_left_bg.png" );
    layoutSet.addImagePath( RIGHT_BG, path + "popup_right_bg.png" );
    layoutSet.addImagePath( CORNER_LEFT, path + "popup_corner_left.png" );
    layoutSet.addImagePath( CORNER_RIGHT, path + "popup_corner_right.png" );
    layoutSet.addImagePath( SECOND_LAYER_CHEFRON, 
                            path + "popup_secondLayer.png" );
    
    layoutSet.addColor( POPUP, Graphics.getColor( 244, 244, 244 ) );
    layoutSet.addColor( POPUP_BUTTON, Graphics.getColor( 0, 89, 165 ) );
  }
}
