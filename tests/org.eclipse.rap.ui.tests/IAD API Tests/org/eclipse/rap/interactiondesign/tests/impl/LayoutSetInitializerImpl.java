/******************************************************************************* 
* Copyright (c) 2009 EclipseSource and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   EclipseSource - initial API and implementation
*******************************************************************************/ 
package org.eclipse.rap.interactiondesign.tests.impl;

import org.eclipse.rap.ui.interactiondesign.layout.model.ILayoutSetInitializer;
import org.eclipse.rap.ui.interactiondesign.layout.model.LayoutSet;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.swt.SWT;


public class LayoutSetInitializerImpl implements ILayoutSetInitializer {

  public static final String KEY = "key";
  public static final String IMAGEPATH = "some/image.gif";

  public LayoutSetInitializerImpl() {
  }

  public void initializeLayoutSet( LayoutSet layoutSet ) {
    layoutSet.addImagePath( KEY, IMAGEPATH );
    layoutSet.addImagePath( "conf", "img/configure.png" );
    layoutSet.addColor( "color", Graphics.getColor( 0, 0, 0 ) );
    layoutSet.addFont( "font", Graphics.getFont( "Arial", 12, SWT.BOLD ) );
  }
}
