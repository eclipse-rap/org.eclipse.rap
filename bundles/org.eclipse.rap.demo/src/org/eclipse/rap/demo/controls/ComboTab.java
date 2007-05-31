/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Innoopract Informationssysteme GmbH - initial API and
 * implementation
 ******************************************************************************/

package org.eclipse.rap.demo.controls;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

public class ComboTab extends ExampleTab {
  
  private static final String[] ITEMS = new String[] { 
    "Eiffel", 
    "Java", 
    "Python", 
    "Ruby", 
    "Simula", 
    "Smalltalk" };
  private Combo emptyCombo;
  private Combo filledCombo;
  private Combo preselectedCombo;
  private Combo viewerCombo;

  public ComboTab( final CTabFolder topFolder ) {
    super( topFolder, "Combo" );
  }

  protected void createStyleControls() {
    createStyleButton( "BORDER" );
    createVisibilityButton();
    createEnablementButton();
    createRemoveAllButton();
  }

  private void createRemoveAllButton() {
    Button button = new Button( styleComp, SWT.PUSH );
    button.setText( "Remove All" );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
System.out.println( "selection before removeAll: " + filledCombo.getSelectionIndex() );        
        filledCombo.removeAll();
      }
    } );
  }

  protected void createExampleControls( final Composite parent ) {
    parent.setLayout( new GridLayout( 2, false ) );
    int style = getStyle();
    emptyCombo = new Combo( parent, style );
    Label lblEmptyCombo = new Label( parent, SWT.NONE );
    lblEmptyCombo.setText( "Empty Combo box" );
    filledCombo = new Combo( parent, style );
    filledCombo.setItems( ITEMS );
    Label lblFilledCombo = new Label( parent, SWT.NONE );
    lblFilledCombo.setText( "Filled Combo box" );
    preselectedCombo = new Combo( parent, style );
    preselectedCombo.setItems( ITEMS );
    preselectedCombo.select( 1 );
    preselectedCombo.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        int index = preselectedCombo.getSelectionIndex();
        String message = "Selected Item: " + ITEMS[ index ];
        Shell shell = parent.getShell();
        MessageDialog.openInformation( shell, "Info", message, null );
      }
    } );
    Label lblPreselectionCombo = new Label( parent, SWT.NONE );
    lblPreselectionCombo.setText( "Filled Combo box with preselection" );
    viewerCombo = new Combo( parent, style );
    ComboViewer viewer = new ComboViewer( viewerCombo );
    viewer.setContentProvider( new IStructuredContentProvider() {
      public void dispose() {
      }
      public void inputChanged( final Viewer viewer, final Object oldIn, final Object newIn ) {
      }
      public Object[] getElements( final Object inputElement ) {
        return ( Object[] )inputElement;
      }
    } );
    viewer.setLabelProvider( new LabelProvider() );
    viewer.setInput( ITEMS );
    viewer.addSelectionChangedListener( new ISelectionChangedListener() {
      public void selectionChanged( final SelectionChangedEvent event ) {
        String message = "Selected item: " + event.getSelection().toString();
        Shell shell = parent.getShell();
        MessageDialog.openInformation( shell, "Info", message, null );
      } 
    } );
    Menu menu = new Menu( viewerCombo );
    MenuItem menuItem = new MenuItem( menu, SWT.NONE );
    menuItem.setText( "MenuItem on ComboViewer" );
    viewerCombo.setMenu( menu );
    Label lblViewerCombo = new Label( parent, SWT.NONE );
    String msg = "Combo box with JFace ComboViewer and context menu";
    lblViewerCombo.setText( msg );
    registerControl( emptyCombo );
    registerControl( filledCombo );
    registerControl( preselectedCombo );
    registerControl( viewerCombo );
  }
}
