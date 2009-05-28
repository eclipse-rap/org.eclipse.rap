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


public class FooterInitializer implements ILayoutSetInitializer {

  public static final String SET_ID 
    = "org.eclipse.rap.design.example.business.layoutset.footer";
  public static final String LEFT = "footer.left";
  public static final String BG = "footer";
  public static final String RIGHT = "footer.right";
  

  public void initializeLayoutSet( LayoutSet layoutSet ) {
    String path = ILayoutSetConstants.IMAGE_PATH;
    layoutSet.addImagePath( LEFT, path + "footer_left.png" );
    layoutSet.addImagePath( BG, path + "footer_bg.png" );
    layoutSet.addImagePath( RIGHT, path + "footer_right.png" );
  }
}
