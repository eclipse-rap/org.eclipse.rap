/*******************************************************************************
 * Copyright (c) 2002, 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rap.demo.controls;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

public class ComboTab extends ExampleTab {

  private static final String PROP_PRESELECT_ITEM = "preselectItem";

  private static final String PROP_SELECTION_LISTENER = "selectionListener";

  private static final String PROP_EMPTY = "empty";

  private static final String[] ITEMS = new String[] {
    "Eiffel",
    "Java",
    "Python",
    "Ruby",
    "Simula",
    "Smalltalk"
  };

  private Combo firstCombo;
  private Combo verifyCombo;
  private Combo viewerCombo;

  public ComboTab( final CTabFolder topFolder ) {
    super( topFolder, "Combo" );
  }

  protected void createStyleControls( final Composite parent ) {
    createStyleButton( "BORDER", SWT.BORDER );
    createStyleButton( "READ_ONLY", SWT.READ_ONLY );
    createVisibilityButton();
    createEnablementButton();
    Group group = new Group( parent, SWT.NONE );
    group.setText( "Manipulate First Combo" );
    group.setLayout( new GridLayout() );
    createAddButton( group );
    createRemoveAllButton( group );
    createSelectFirstItemButton( group );
    createSetVisibleItemCountButton( group );
    createFgColorButton();
    createBgColorButton();
    createFontChooser();
    createPropertyCheckbox( "Create Empty Combo", PROP_EMPTY );
    createPropertyCheckbox( "Preselect First Item", PROP_PRESELECT_ITEM );
    createPropertyCheckbox( "Add Selection Listener", PROP_SELECTION_LISTENER );
  }

  protected void createExampleControls( final Composite parent ) {
    parent.setLayout( new GridLayout( 3, false ) );
    int style = getStyle();
    Label lblFilledCombo = new Label( parent, SWT.NONE );
    lblFilledCombo.setText( "Filled Combo" );
    firstCombo = new Combo( parent, style );
    if( !hasCreateProperty( PROP_EMPTY ) ) {
      firstCombo.setItems( ITEMS );
    }
    if( !hasCreateProperty( PROP_EMPTY ) && hasCreateProperty( PROP_PRESELECT_ITEM ) ) {
      firstCombo.select( 0 );
    }
    if( hasCreateProperty( PROP_SELECTION_LISTENER ) ) {
      firstCombo.addSelectionListener( new SelectionAdapter() {
        public void widgetSelected( final SelectionEvent evt ) {
          show( evt );
        }
        public void widgetDefaultSelected( final SelectionEvent evt ) {
          show( evt );
        }
        private void show( final SelectionEvent evt ) {
          Shell shell = firstCombo.getShell();
          String msg = "Event: " + evt + "\n"
                       + "Text: " + firstCombo.getText() + "\n"
                       + "Selection: " + firstCombo.getSelectionIndex();
          MessageDialog.openInformation( shell, "Selection Event", msg );
        }
      } );
    }
    Button btnShowSelection = new Button( parent, SWT.PUSH );
    btnShowSelection.setText( "Show Selection" );
    btnShowSelection.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        String selection = "(nothing)";
        int index = firstCombo.getSelectionIndex();
        if( index != -1 ) {
          selection = firstCombo.getItem( index );
        }
        String msg = "Your Selection: " + selection;
        Shell shell = firstCombo.getShell();
        MessageDialog.openInformation( shell, "Information", msg );
      }
    } );

    // -- verify listener --
    Label lblVerifyCombo = new Label( parent, SWT.NONE );
    lblVerifyCombo.setText( "Combo with VerifyListener (only 0-9 allowed)" );
    verifyCombo = new Combo( parent, style );
    verifyCombo.setLayoutData( colSpan2() );
    verifyCombo.add( "0" );
    verifyCombo.add( "1" );
    verifyCombo.add( "2" );
    verifyCombo.add( "3" );
    verifyCombo.addVerifyListener( new VerifyListener() {
      public void verifyText( final VerifyEvent event ) {
        StringBuffer allowedText = new StringBuffer();
        for( int i = 0; i < event.text.length(); i++ ) {
          char ch = event.text.charAt( i );
          if( ch >= '0' && ch <= '9' ) {
            allowedText.append( ch );
          }
        }
        event.text = allowedText.toString();
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
        MessageDialog.openInformation( shell, "Info", message );
      }
    } );
    Menu menu = new Menu( viewerCombo );
    MenuItem menuItem = new MenuItem( menu, SWT.NONE );
    menuItem.setText( "MenuItem on ComboViewer" );
    viewerCombo.setMenu( menu );
    registerControl( firstCombo );
    registerControl( verifyCombo );
    registerControl( viewerCombo );
  }

  private void createAddButton( final Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayout( new GridLayout( 3, false ) );
    Label lblAddItem = new Label( composite, SWT.NONE );
    lblAddItem.setText( "Add Item" );
    final Text txtAddItem = new Text( composite, SWT.BORDER );
    txtAddItem.setText( "New Item" );
    Button btnAddItem = new Button( composite, SWT.PUSH );
    btnAddItem.setText( "Add" );
    btnAddItem.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        firstCombo.add( txtAddItem.getText() );
      }
    } );
  }

  private void createRemoveAllButton( final Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayout( new GridLayout( 2, false ) );
    Label label = new Label( composite, SWT.NONE );
    label.setText( "Remove All Items " );
    Button button = new Button( composite , SWT.PUSH );
    button.setText( "Remove" );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        firstCombo.removeAll();
      }
    } );
  }

  private void createSelectFirstItemButton( final Composite parent ) {
    Button button = new Button( parent , SWT.PUSH );
    button.setText( "Select First Item" );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        if( firstCombo.getItemCount() > 0 ) {
          firstCombo.select( 0 );
        }
      }
    } );
  }

  private void createSetVisibleItemCountButton( final Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayout( new GridLayout( 3, false ) );
    Label label = new Label( composite, SWT.NONE );
    label.setText( "Set Visible Item Count to " );
    final Text text = new Text( composite, SWT.BORDER | SWT.SINGLE );
    text.setText( "3" );
    text.setLayoutData( new GridData( 100, SWT.DEFAULT ) );
    Button button = new Button( composite, SWT.PUSH );
    button.setText( "Set" );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        try {
          int vic = Integer.parseInt( text.getText() );
          firstCombo.setVisibleItemCount( vic );
        } catch( NumberFormatException e ) {
        }
      }
    } );
  }

  private static GridData colSpan2() {
    GridData result = new GridData( SWT.BEGINNING, SWT.CENTER, false, false );
    result.horizontalSpan = 2;
    return result;
  }
}
