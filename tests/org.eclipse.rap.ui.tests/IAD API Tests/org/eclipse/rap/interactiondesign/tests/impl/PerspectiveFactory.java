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

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;


public class PerspectiveFactory implements IPerspectiveFactory {

  public void createInitialLayout( IPageLayout layout ) {
    String editorArea = layout.getEditorArea();
    layout.setEditorAreaVisible( false );
    IFolderLayout topLeft = layout.createFolder( "topLeftTest",
                                                 IPageLayout.LEFT,
                                                 0.25f,
                                                 editorArea );
    topLeft.addView( "org.eclipse.rap.ui.interactiondesign.test.view" );
    
    IFolderLayout bottomLeft = layout.createFolder( "bottomLeftTest",
                                                    IPageLayout.BOTTOM,
                                                    0.50f,
                                                    "topLeftTest" );
    bottomLeft.addView( "org.eclipse.rap.ui.interactiondesign.test.view2" );
    
    
  }
}
