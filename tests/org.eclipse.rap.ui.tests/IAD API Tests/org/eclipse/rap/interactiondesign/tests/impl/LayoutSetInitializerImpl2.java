/******************************************************************************* 
* Copyright (c) 2009 EclipseSource and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License 2.0 which is
* available at https://www.eclipse.org/legal/epl-2.0
*
* SPDX-License-Identifier: EPL-2.0
*
* Contributors:
*   EclipseSource - initial API and implementation
*******************************************************************************/ 
package org.eclipse.rap.interactiondesign.tests.impl;

import org.eclipse.rap.ui.interactiondesign.layout.model.ILayoutSetInitializer;
import org.eclipse.rap.ui.interactiondesign.layout.model.LayoutSet;


public class LayoutSetInitializerImpl2 implements ILayoutSetInitializer {

  public static final String IMAGEPATH2 = "another/image.gif";
  public static final String KEY2 = "key2";

  public LayoutSetInitializerImpl2() {
  }

  public void initializeLayoutSet( LayoutSet layoutSet ) {
    layoutSet.addImagePath( KEY2, IMAGEPATH2 );
  }
}
