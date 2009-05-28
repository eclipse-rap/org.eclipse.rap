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


public class CoolbarOverflowInitializer implements ILayoutSetInitializer {
  
  public static final String SET_ID 
    = "org.eclipse.rap.design.example.business.layoutset.coolbaroverflow";
  
  public static final String BG = "coolbar.layer.bg";
  public static final String RIGHT = "coolbar.layer.right";
  public static final String WAVE = "coolbar.layer.wave";
  public static final String ARROW = "coolbar.layer.arrow";

  public CoolbarOverflowInitializer() {
  }

  public void initializeLayoutSet( LayoutSet layoutSet ) {
    String path = ILayoutSetConstants.IMAGE_PATH;
    layoutSet.addImagePath( BG, path + "toolbar_overflow_layer_bg.png" );
    layoutSet.addImagePath( RIGHT, path + "toolbar_overflow_layer_right.png" );
    layoutSet.addImagePath( WAVE, path + "header_wave_layer.png" );
    layoutSet.addImagePath( ARROW, path + "toolbar_overflow_arrow.png" );
  }
}
