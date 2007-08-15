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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.*;
import org.eclipse.ui.part.EditorPart;

public class BarEditor extends EditorPart {

  private Text editor;

  public BarEditor() {
    super();
  }

  public void doSave( IProgressMonitor monitor ) {
    MessageDialog.openInformation( getSite().getShell(),
                                   "Foo Editor",
                                   "Saved :");
  }

  public void doSaveAs() {
  }

  public void init( IEditorSite site, IEditorInput input )
    throws PartInitException
  {
    setSite( site );
    setInput( input );
  }
  protected boolean dirty = false;

  public boolean isDirty() {
    return dirty;
  }

  protected void setDirty( boolean value ) {
    dirty = value;
    firePropertyChange( PROP_DIRTY );
  }

  public boolean isSaveAsAllowed() {
    return false;
  }

  public void createPartControl( Composite parent ) {
    parent.setLayout( new FillLayout() );
    editor = new Text( parent, SWT.MULTI );
    editor.setText( "" );
    editor.addModifyListener( new ModifyListener() {

      public void modifyText( ModifyEvent event ) {
        if( editor.getText() != "" ) {
          setDirty( true );
        } else {
          setDirty( false );
        }
      }
    } );
  }

  public void setFocus() {
    editor.setFocus();
  }
}
