/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Innoopract Informationssysteme GmbH - initial API and
 * implementation
 ******************************************************************************/

package org.eclipse.rap.demo.controls;

import org.eclipse.rap.jface.dialogs.MessageDialog;
import org.eclipse.rap.jface.viewers.*;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.events.SelectionAdapter;
import org.eclipse.rap.rwt.events.SelectionEvent;
import org.eclipse.rap.rwt.layout.*;
import org.eclipse.rap.rwt.widgets.*;

public class ComboTab extends ExampleTab {
  
  public ComboTab( final TabFolder parent ) {
    super( parent, "Combo" );
  }

  protected void createStyleControls() {
    createStyleButton( "BORDER" );
    createVisibilityButton();
    createEnablementButton();
  }

  protected void createExampleControls( final Composite parent ) {
    parent.setLayout( new GridLayout( 2, false ) );
    int style = getStyle();
    final String[] items
      = new String[] { "Eiffel", "Java", "Python", "Ruby", "Simula", "Smalltalk" };
    // empty combo
    Combo combo1 = new Combo( parent, style );
    new Label( parent, RWT.NONE ).setText( "Empty Combo box" );
    // filled combo
    Combo combo2 = new Combo( parent, style );
    combo2.setItems( items );
    new Label( parent, RWT.NONE ).setText( "Filled Combo box" );
    // filled combo with preselection
    final Combo combo3 = new Combo( parent, style );
    combo3.setItems( items );
    combo3.select( 1 );
    combo3.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent event ) {
        int index = combo3.getSelectionIndex();
        String message = "Selected Item: " + items[ index ];
        MessageDialog.openInformation( parent.getShell(), "Info", message, null );
      }
    } );
    new Label( parent, RWT.NONE ).setText( "Filled Combo box with preselection" );
    // combo with comboviewer
    Combo combo4 = new Combo( parent, style );
    ComboViewer viewer = new ComboViewer( combo4 );
    viewer.setContentProvider( new IStructuredContentProvider() {
      public void dispose() {
      }
      public void inputChanged( Viewer viewer, Object oldInput, Object newInput ) {
      }
      public Object[] getElements( Object inputElement ) {
        return ( Object[] )inputElement;
      }
    } );
    viewer.setLabelProvider( new LabelProvider() );
    viewer.setInput( items );
    viewer.addSelectionChangedListener( new ISelectionChangedListener() {
      public void selectionChanged( SelectionChangedEvent event ) {
        String message = "Selected item: " + event.getSelection().toString();
        MessageDialog.openInformation( parent.getShell(), "Info", message, null );
      }} );
    new Label( parent, RWT.NONE ).setText( "Combo box with JFace ComboViewer" );
    registerControl( combo1 );
    registerControl( combo2 );
    registerControl( combo3 );
    registerControl( combo4 );
  }
}
