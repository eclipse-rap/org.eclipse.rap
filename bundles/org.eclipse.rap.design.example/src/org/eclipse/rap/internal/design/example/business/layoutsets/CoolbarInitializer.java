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


public class CoolbarInitializer implements ILayoutSetInitializer {
  
  public static final String SET_ID 
    = "org.eclipse.rap.design.example.business.layoutset.coolbar";
  
  public static final String OVERFLOW_INACTIVE = "coolbar.overflow.inactive";
  public static final String OVERFLOW_ACTIVE = "coolbar.overflow.active";
  public static final String BUTTON_BG = "coolbar.button.bg";
  public static final String OVERFLOW_COLOR = "coolbar.overflow.color";
  public static final String ARROW = "coolbar.arrow";

  public CoolbarInitializer() {
  }

  public void initializeLayoutSet( LayoutSet layoutSet ) {
    String path = ILayoutSetConstants.IMAGE_PATH;
    layoutSet.addImagePath( OVERFLOW_INACTIVE, 
                            path + "toolbar_overflow_hover.png" );
    layoutSet.addImagePath( OVERFLOW_ACTIVE, 
                            path + "toolbar_overflow_hover_active.png" );
    layoutSet.addImagePath( BUTTON_BG, path + "toolbarButtonBg.png" );
    layoutSet.addImagePath( ARROW, path + "toolbar_arrow.png" );
    layoutSet.addColor( OVERFLOW_COLOR, Graphics.getColor( 0, 81, 148 ) );
  }
}
