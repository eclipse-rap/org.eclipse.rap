/*******************************************************************************
 * Copyright (c) 2008, 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.examples.viewer.internal;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;


public class ExamplePerspective implements IPerspectiveFactory {

  public static final String ID
    = "org.eclipse.rap.examples.viewer.examplePerspective";

  public void createInitialLayout( final IPageLayout layout ) {
    String editorArea = layout.getEditorArea();
    layout.setEditorAreaVisible( false );
    layout.addStandaloneView( NavigationView.ID,
                              false,
                              IPageLayout.LEFT,
                              0.15f,
                              editorArea );
    layout.addStandaloneView( ExampleView.ID,
                              false,
                              IPageLayout.RIGHT,
                              0.85f,
                              editorArea );
    layout.addStandaloneView( DescriptionView.ID,
                              false,
                              IPageLayout.RIGHT,
                              0.66f,
                              ExampleView.ID );
  }
}