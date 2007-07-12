/*******************************************************************************
 * Copyright (c) 2007 NOMAD business software GmbH. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html Contributors: Samy
 * Abou-Shama NOMAD business software GmbH - initial Databinding migration
 ******************************************************************************/
package org.eclipse.rap.demo.databinding;

import org.eclipse.ui.*;

public class DataBindingPerspective implements IPerspectiveFactory {

  public void createInitialLayout( final IPageLayout layout ) {
    String editorArea = layout.getEditorArea();
    layout.setEditorAreaVisible( false );
    IFolderLayout topLeft = layout.createFolder( "topLeft",
                                                 IPageLayout.LEFT,
                                                 0.25f,
                                                 editorArea );
    topLeft.addView( "org.eclipse.rap.demo.databinding.DatabindingSnippetsView" );
    topLeft.addView( "org.eclipse.rap.demo.databinding.nestedselection.TestMasterDetailView" );
    // add shortcuts to show view menu
    layout.addShowViewShortcut( "org.eclipse.rap.demo.databinding.DatabindingSnippetsView" );
    // add shortcut for other perspective
    layout.addPerspectiveShortcut( "org.eclipse.rap.demo.perspective" );
    layout.addPerspectiveShortcut( "org.eclipse.rap.demo.perspective.planning" );
  }
}
