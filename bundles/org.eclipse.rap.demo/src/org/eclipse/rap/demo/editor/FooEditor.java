/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Innoopract Informationssysteme GmbH - initial API and
 * implementation
 ******************************************************************************/
package org.eclipse.rap.demo.editor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.*;
import org.eclipse.ui.part.MultiPageEditorPart;

public class FooEditor extends MultiPageEditorPart {

  private BarEditor editor;
  private BazEditor treeeditor;

  public FooEditor() {
    super();
  }

  public void doSave( final IProgressMonitor monitor ) {
    editor.setDirty( false );
    treeeditor.setDirty( false );
  }

  public void doSaveAs() {
  }

  public boolean isSaveAsAllowed() {
    return false;
  }

  public void init( final IEditorSite site, final IEditorInput input )
    throws PartInitException
  {
    super.init( site, input );
    setPartName( input.getName() );
  }

  protected void createPages() {
    editor = new BarEditor();
    treeeditor = new BazEditor();
    int index;
    try {
      ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
      index = addPage( editor, getEditorInput() );
      setPageText( index, "Source" );
      setPageImage( index, sharedImages.getImage( ISharedImages.IMG_OBJ_FILE ) );
      index = addPage( treeeditor, getEditorInput() );
      setPageText( index, "Design" );
      setPageImage( index, sharedImages.getImage( ISharedImages.IMG_OBJ_FOLDER ) );
    } catch( PartInitException e ) {
      e.printStackTrace();
    }
  }
}
