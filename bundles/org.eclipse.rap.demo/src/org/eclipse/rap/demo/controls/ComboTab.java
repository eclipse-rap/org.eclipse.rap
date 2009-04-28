/*******************************************************************************
 * Copyright (c) 2002, 2009 Innoopract Informationssysteme GmbH.
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
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

public class ComboTab extends ExampleTab {

  private static final String PROP_SELECTION_LISTENER = "selectionListener";

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
  private CCombo cCombo;
  private Button createEmptyComboButton;
  private Button preselectFirstItemButton;
  private boolean empty;
  private boolean preselectItem;

  public ComboTab( final CTabFolder topFolder ) {
    super( topFolder, "Combo" );
  }

  protected void createStyleControls( final Composite parent ) {
    createStyleButton( "BORDER", SWT.BORDER );
    createStyleButton( "READ_ONLY", SWT.READ_ONLY );
    createStyleButton( "FLAT", SWT.FLAT );
    createVisibilityButton();
    createEnablementButton();
    createFgColorButton();
    createBgColorButton();
    createFontChooser();
    createCursorCombo();
    createPropertyCheckbox( "Add Selection Listener", PROP_SELECTION_LISTENER );
    Group group = new Group( parent, SWT.NONE );
    group.setText( "Manipulate First Combo" );
    group.setLayout( new GridLayout() );
    createAddButton( group );
    createSetVisibleItemCountButton( group );
    createRemoveAllButton( group );
    createSelectFirstItemButton( group );
    createEmptyComboButton = new Button( group, SWT.CHECK );
    createEmptyComboButton.setText( "Create Empty Combo" );
    createEmptyComboButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent e ) {
        if( createEmptyComboButton.getSelection() ) {
          empty = true;
        } else {
          empty = false;
        }
        createNew();
      }
    } );
    preselectFirstItemButton = new Button( group, SWT.CHECK );
    preselectFirstItemButton.setText( "Preselect First Item" );
    preselectFirstItemButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent e ) {
        if( preselectFirstItemButton.getSelection() ) {
          preselectItem = true;
        } else {
          preselectItem = false;
        }
        createNew();
      }
    } );
    Group grpManioulateCCombo = new Group( parent, SWT.NONE );
    grpManioulateCCombo.setText( "Manipulate CCombo" );
    grpManioulateCCombo.setLayout( new GridLayout() );
    createSetTextLimitButton( grpManioulateCCombo );
    createChangeSizeButton( grpManioulateCCombo );
    createShowListButton( grpManioulateCCombo );
  }

  protected void createExampleControls( final Composite parent ) {
    parent.setLayout( new GridLayout( 3, false ) );
    int style = getStyle();
    // Standard Combo
    Label lblFilledCombo = new Label( parent, SWT.NONE );
    lblFilledCombo.setText( "Filled Combo" );
    firstCombo = new Combo( parent, style );
    if( !empty ) {
      firstCombo.setItems( ITEMS );
    }
    if( !empty && preselectItem ) {
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
        showSelection( firstCombo.getItems(), firstCombo.getSelectionIndex() );
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
    // Viewer Combo
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
    // Separator
    int separatorStyle = SWT.SEPARATOR | SWT.HORIZONTAL | SWT.SHADOW_OUT;
    Label separator = new Label( parent, separatorStyle );
    separator.setLayoutData( createGridDataWithSpan() );
    // CCombo
    Label lblCCombo = new Label( parent, SWT.NONE );
    lblCCombo.setText( "CCombo" );
    cCombo = new CCombo( parent, style );
    cCombo.setItems( ITEMS );
    if( hasCreateProperty( PROP_SELECTION_LISTENER ) ) {
      cCombo.addSelectionListener( new SelectionAdapter() {
        public void widgetSelected( final SelectionEvent evt ) {
          show( evt );
        }
        public void widgetDefaultSelected( final SelectionEvent evt ) {
          show( evt );
        }
        private void show( final SelectionEvent evt ) {
          Shell shell = cCombo.getShell();
          String msg = "Event: " + evt + "\n"
                       + "Text: " + cCombo.getText() + "\n"
                       + "Selection: " + cCombo.getSelectionIndex();
          MessageDialog.openInformation( shell, "Selection Event", msg );
        }
      } );
    }
    Button btnShowSelectionCCombo = new Button( parent, SWT.PUSH );
    btnShowSelectionCCombo.setText( "Show Selection" );
    btnShowSelectionCCombo.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        showSelection( cCombo.getItems(), cCombo.getSelectionIndex() );
      }
    } );
    // Register controls
    registerControl( firstCombo );
    registerControl( verifyCombo );
    registerControl( viewerCombo );
    registerControl( cCombo );
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
    Button button = new Button( parent , SWT.PUSH );
    button.setText( "Remove All Items" );
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
    final Text text = new Text( composite, SWT.BORDER | SWT.SINGLE );
    text.setText( "3" );
    text.setLayoutData( new GridData( 20, SWT.DEFAULT ) );
    Button button = new Button( composite, SWT.PUSH );
    button.setText( "Set Visible Item Count" );
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
  
  private void createSetTextLimitButton( final Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayout( new GridLayout( 2, false ) );
    final Text text = new Text( composite, SWT.BORDER | SWT.SINGLE );
    text.setText( "5" );
    text.setLayoutData( new GridData( 20, SWT.DEFAULT ) );
    Button button = new Button( composite, SWT.PUSH );
    button.setText( "Set Text Limit" );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        try {
          int textLimit = Integer.parseInt( text.getText() );
          cCombo.setTextLimit( textLimit );
        } catch( NumberFormatException e ) {
        }
      }
    } );
  }
  
  private void createChangeSizeButton( final Composite parent ) {
    Button button = new Button( parent, SWT.PUSH );
    button.setText( "Change Size" );
    button.addSelectionListener( new SelectionAdapter() {
      private boolean customSize;
      public void widgetSelected( final SelectionEvent event ) {
        if ( customSize ) {
          GridData gridData = new GridData( SWT.DEFAULT, SWT.DEFAULT );
          cCombo.setLayoutData( gridData );
          customSize = false;
        } else {
          GridData gridData = new GridData( 100, 100 );
          cCombo.setLayoutData( gridData );
          customSize = true;
        }
        Composite parent = ( ( Control )controls.get( 0 ) ).getParent();
        parent.layout( true, true );
      }
    } );
  }
  
  private void createShowListButton( final Composite parent ) {
    Button button = new Button( parent, SWT.PUSH );
    button.setText( "Show List" );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        boolean listVisible = cCombo.getListVisible();
        cCombo.setListVisible( !listVisible );
      }
    } );
  }
  
  private void showSelection( final String[] items, final int selectionIndex ) {
    String selection = "(nothing)";
    if( selectionIndex != -1 ) {
      selection = items[ selectionIndex ];
    }
    String msg = "Your Selection: " + selection;
    MessageDialog.openInformation( getShell(), "Information", msg );
  }

  private GridData createGridDataWithSpan() {
    GridData gridData = new GridData( GridData.FILL_HORIZONTAL );
    gridData.horizontalSpan = 3;
    return gridData;
  }
}
