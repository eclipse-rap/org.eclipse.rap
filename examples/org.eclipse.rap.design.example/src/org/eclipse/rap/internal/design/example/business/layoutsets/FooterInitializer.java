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


public class FooterInitializer implements ILayoutSetInitializer {

  public void initializeLayoutSet( final LayoutSet layoutSet ) {
    String path = ILayoutSetConstants.IMAGE_PATH_BUSINESS;
    layoutSet.addImagePath( ILayoutSetConstants.FOOTER_LEFT, 
                            path + "footer_left.png" ); //$NON-NLS-1$
    layoutSet.addImagePath( ILayoutSetConstants.FOOTER_BG, 
                            path + "footer_bg.png" ); //$NON-NLS-1$
    layoutSet.addImagePath( ILayoutSetConstants.FOOTER_RIGHT, 
                            path + "footer_right.png" ); //$NON-NLS-1$
  }
}
