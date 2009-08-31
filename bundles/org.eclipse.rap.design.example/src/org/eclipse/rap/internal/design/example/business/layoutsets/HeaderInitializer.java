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


public class HeaderInitializer implements ILayoutSetInitializer {

  public static final String SET_ID 
    = "org.eclipse.rap.design.example.layoutset.header";
  public static final String LEFT = "header.left";
  public static final String LEFT_BG = "header.left.bg";
  public static final String WAVE = "header.wave";
  public static final String RIGHT_BG = "header.right.bg";
  public static final String RIGHT = "header.right";  

  public HeaderInitializer() {
  }

  public void initializeLayoutSet( LayoutSet layoutSet ) {
    // images
    String path = ILayoutSetConstants.IMAGE_PATH;
    layoutSet.addImagePath( LEFT, path + "header_left.png" ); 
    layoutSet.addImagePath( LEFT_BG, path + "header_left_bg.png" );
    layoutSet.addImagePath( WAVE, path + "header_wave.png" );
    layoutSet.addImagePath( RIGHT_BG, path + "header_right_bg.png" );
    layoutSet.addImagePath( RIGHT, path + "header_right.png" );
  }
}
