/******************************************************************************* 
* Copyright (c) 2009 EclipseSource and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   EclipseSource - initial API and implementation
*******************************************************************************/ 
package org.eclipse.rap.internal.design.example.fancy.layoutsets;

import org.eclipse.rap.internal.design.example.ILayoutSetConstants;
import org.eclipse.rap.ui.interactiondesign.layout.model.ILayoutSetInitializer;
import org.eclipse.rap.ui.interactiondesign.layout.model.LayoutSet;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;


public class CoolbarInitializer implements ILayoutSetInitializer {

  public void initializeLayoutSet( final LayoutSet layoutSet ) {
    String path = ILayoutSetConstants.IMAGE_PATH_FANCY;
    layoutSet.addImagePath( ILayoutSetConstants.COOLBAR_OVERFLOW_INACTIVE, 
                            path + "toolbar_overflow_hover.png" );
    layoutSet.addImagePath( ILayoutSetConstants.COOLBAR_OVERFLOW_ACTIVE, 
                            path + "toolbar_overflow_hover_active.png" );
    layoutSet.addImagePath( ILayoutSetConstants.COOLBAR_BUTTON_BG, 
                            path + "toolbarButtonBg.png" );
    layoutSet.addImagePath( ILayoutSetConstants.COOLBAR_ARROW, 
                            path + "toolbar_arrow.png" );
    layoutSet.addColor( ILayoutSetConstants.COOLBAR_OVERFLOW_COLOR, 
                        Graphics.getColor( 39, 157, 219 ) );
    FormData fdButton = new FormData();
    fdButton.left = new FormAttachment( 0, 0);
    fdButton.top = new FormAttachment( 0, 73 );
    layoutSet.addPosition( ILayoutSetConstants.COOLBAR_BUTTON_POS, fdButton );
  }
}
