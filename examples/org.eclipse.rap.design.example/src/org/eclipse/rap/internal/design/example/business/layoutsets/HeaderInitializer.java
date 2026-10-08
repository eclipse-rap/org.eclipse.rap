/*******************************************************************************
 * Copyright (c) 2009, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.internal.design.example.business.layoutsets;

import org.eclipse.rap.internal.design.example.ILayoutSetConstants;
import org.eclipse.rap.ui.interactiondesign.layout.model.ILayoutSetInitializer;
import org.eclipse.rap.ui.interactiondesign.layout.model.LayoutSet;


public class HeaderInitializer implements ILayoutSetInitializer {  

  public void initializeLayoutSet( final LayoutSet layoutSet ) {
    // images
    String path = ILayoutSetConstants.IMAGE_PATH_BUSINESS;
    layoutSet.addImagePath( ILayoutSetConstants.HEADER_LEFT, 
                            path + "header_left.png" );  //$NON-NLS-1$
    layoutSet.addImagePath( ILayoutSetConstants.HEADER_LEFT_BG, 
                            path + "header_left_bg.png" ); //$NON-NLS-1$
    layoutSet.addImagePath( ILayoutSetConstants.HEADER_WAVE, 
                            path + "header_wave.png" ); //$NON-NLS-1$
    layoutSet.addImagePath( ILayoutSetConstants.HEADER_RIGHT_BG, 
                            path + "header_right_bg.png" ); //$NON-NLS-1$
    layoutSet.addImagePath( ILayoutSetConstants.HEADER_RIGHT, 
                            path + "header_right.png" ); //$NON-NLS-1$
  }
}
