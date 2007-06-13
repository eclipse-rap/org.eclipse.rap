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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

public class ComboTab extends ExampleTab {
  
  private static final String[] ITEMS = new String[] { 
    "Eiffel", 
    "Java", 
    "Python", 
    "Ruby", 
    "Simula", 
    "Smalltalk" 
  };
  
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
    createFontChooser();
  }

  private void createRemoveAllButton() {
    Button button = new Button( styleComp, SWT.PUSH );
    button.setText( "Remove All" );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        filledCombo.removeAll();
      }
    } );
  }

  protected void createExampleControls( final Composite parent ) {
    parent.setLayout( new GridLayout( 3, false ) );
    int style = getStyle();
    Label lblEmptyCombo = new Label( parent, SWT.NONE );
    lblEmptyCombo.setText( "Empty Combo" );
    emptyCombo = new Combo( parent, style );
    emptyCombo.setLayoutData( colSpan2() );
    Label lblFilledCombo = new Label( parent, SWT.NONE );
    lblFilledCombo.setText( "Filled Combo" );
    filledCombo = new Combo( parent, style );
    filledCombo.setItems( ITEMS );
    Button btnShowSelection = new Button( parent, SWT.PUSH );
    btnShowSelection.setText( "Show Selection" );
    btnShowSelection.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent event ) {
        String selection = "(nothing)";
        int index = filledCombo.getSelectionIndex();
        if( index != -1 ) {
          selection = filledCombo.getItem( index );
        }
        String msg = "Your Selection: " + selection;
        Shell shell = filledCombo.getShell();
        MessageDialog.openInformation( shell, "Information", msg, null );
      }
    } );
    Label lblPreselectionCombo = new Label( parent, SWT.NONE );
    lblPreselectionCombo.setText( "Filled Combo with preselection" );
    preselectedCombo = new Combo( parent, style );
    preselectedCombo.setLayoutData( colSpan2() );
    preselectedCombo.setItems( ITEMS );
    preselectedCombo.select( 1 );
    preselectedCombo.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        int index = preselectedCombo.getSelectionIndex();
        String message = "Selected Item: " + preselectedCombo.getItem( index );
        Shell shell = parent.getShell();
        MessageDialog.openInformation( shell, "Info", message, null );
      }
    } );
    Label lblViewerCombo = new Label( parent, SWT.NONE );
    String msg = "ComboViewer with context menu";
    lblViewerCombo.setText( msg );
    viewerCombo = new Combo( parent, style );
    viewerCombo.setLayoutData( colSpan2() );
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
    registerControl( emptyCombo );
    registerControl( filledCombo );
    registerControl( preselectedCombo );
    registerControl( viewerCombo );
  }
  
  private static GridData colSpan2() {
    GridData result = new GridData( SWT.BEGINNING, SWT.CENTER, false, false );
    result.horizontalSpan = 2;
    return result;
  }
}
