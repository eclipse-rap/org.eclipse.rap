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
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;


public class TextTab extends ExampleTab {

  private Text text;
  private Label textLabel;
  private Label selectionLabel;
  private Button btnSelectionListener;
  private Button btnBlockingVerifyListener;
  private Button btnNumbersOnlyVerifyListener;
  private Button btnModifyListener;
  private Button btnEditable;
  private final SelectionListener selectionListener;
  private final VerifyListener blockingVerifyListener;
  private final VerifyListener numberOnlyVerifyListener;
  private final ModifyListener modifyListener;

  public TextTab( final CTabFolder topFolder ) {
    super( topFolder, "Text" );
    selectionListener = new SelectionAdapter() {

      public void widgetDefaultSelected( final SelectionEvent event ) {
        String msg = "You pressed the Enter key.";
        MessageDialog.openInformation( getShell(), "Information", msg );
      }
    };
    blockingVerifyListener = new VerifyListener() {

      public void verifyText( final VerifyEvent event ) {
        event.doit = false;
      }
    };
    numberOnlyVerifyListener = new VerifyListener() {

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
    };
    modifyListener = new ModifyListener() {

      public void modifyText( final ModifyEvent event ) {
        Text text = ( Text )event.widget;
        textLabel.setText( text.getText() );
      }
    };
  }

  protected void createStyleControls( final Composite parent ) {
    createStyleButton( "BORDER", SWT.BORDER );
    createStyleButton( "WRAP", SWT.WRAP );
    createStyleButton( "SINGLE", SWT.SINGLE );
    createStyleButton( "MULTI", SWT.MULTI );
    createStyleButton( "PASSWORD", SWT.PASSWORD );
    createStyleButton( "READ_ONLY", SWT.READ_ONLY );
    createStyleButton( "LEFT", SWT.LEFT );
    createStyleButton( "CENTER", SWT.CENTER );
    createStyleButton( "RIGHT", SWT.RIGHT );
    createVisibilityButton();
    createEnablementButton();
    createEditableButton();
    createSelectionListenerButton();
    createBlockingVerifyListenerButton();
    createNumbersOnlyVerifyListenerButton();
    createModifyListenerButton();
    createFgColorButton();
    createBgColorButton();
    createFontChooser();
    createCursorCombo();
    createLimitText( parent );
    createSelectionChooser( parent );
  }

  protected void createExampleControls( final Composite parent ) {
    parent.setLayout( new GridLayout() );
    Composite textComposite = new Composite( parent, SWT.NONE );
    textComposite.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
    textComposite.setLayout( new GridLayout( 1, false ) );
    text = new Text( textComposite, getStyle() );
    text.setText( "Lorem ipsum dolor sit amet" );
    text.setSelection( 0, 5 );
    // button bar
    Composite buttonBar = new Composite( parent, SWT.NONE );
    buttonBar.setLayoutData( new GridData( SWT.FILL, SWT.DEFAULT, true, false ) );
    buttonBar.setLayout( new RowLayout( SWT.HORIZONTAL ) );
    final Button btnGetText = new Button( buttonBar, SWT.PUSH );
    btnGetText.setText( "getText" );
    btnGetText.addSelectionListener( new SelectionAdapter() {

      public void widgetSelected( final SelectionEvent event ) {
        textLabel.setText( text.getText() );
      }
    } );
    final Button btnGetSelection = new Button( buttonBar, SWT.PUSH );
    btnGetSelection.setText( "getSelection" );
    btnGetSelection.addSelectionListener( new SelectionAdapter() {

      public void widgetSelected( final SelectionEvent event ) {
        Point selection = text.getSelection();
        selectionLabel.setText( selection.x + ", " + selection.y );
      }
    } );
    final Button btnFixedSize = new Button( buttonBar, SWT.PUSH );
    btnFixedSize.setText( "200 x 100" );
    btnFixedSize.addSelectionListener( new SelectionAdapter() {

      public void widgetSelected( final SelectionEvent event ) {
        text.setLayoutData( new GridData( 200, 100 ) );
        text.getParent().layout();
      }
    } );
    // output form
    Composite outputForm = new Composite( parent, SWT.NONE );
    outputForm.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
    outputForm.setLayout( new GridLayout( 2, false ) );
    new Label( outputForm, SWT.NONE ).setText( "Text:" );
    textLabel = new Label( outputForm, SWT.BORDER );
    textLabel.setText( "\n\n\n\n\n" );
    textLabel.setLayoutData( new GridData( SWT.FILL, SWT.DEFAULT, true, false ) );
    new Label( outputForm, SWT.NONE ).setText( "Selection:" );
    selectionLabel = new Label( outputForm, SWT.BORDER );
    selectionLabel.setLayoutData( new GridData( SWT.FILL, SWT.DEFAULT, true, false ) );
    updateSelectionListener();
    updateBlockingVerifyListener();
    updateNumbersOnlyVerifyListener();
    updateModifyListener();
    updateEditable();
    registerControl( text );
    createDefaultButton( parent );
  }

  private void createDefaultButton( final Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayout( new RowLayout( SWT.HORIZONTAL) );
    final Button defaultButton = new Button( composite, SWT.PUSH );
    defaultButton.setText( "Default" );
    defaultButton.addSelectionListener( new SelectionAdapter() {

      public void widgetSelected( SelectionEvent e ) {
        String message = "Default button triggered";
        MessageDialog.openInformation( parent.getShell(), "Info", message  );
      }
    });
    final Button setDefaultButton = new Button( composite, SWT.CHECK );
    setDefaultButton.setText( "set as defaultButton" );
    setDefaultButton.addSelectionListener( new SelectionAdapter() {

      public void widgetSelected( SelectionEvent e ) {
        if( setDefaultButton.getSelection() ) {
          parent.getShell().setDefaultButton( defaultButton );
        } else {
          parent.getShell().setDefaultButton( null );
        }
      }
    });
  }

  private void createModifyListenerButton() {
    btnModifyListener = createPropertyButton( "ModifyListener" );
    btnModifyListener.addSelectionListener( new SelectionAdapter() {

      public void widgetSelected( final SelectionEvent event ) {
        updateModifyListener();
      }
    } );
  }

  private void createNumbersOnlyVerifyListenerButton() {
    btnNumbersOnlyVerifyListener
      = createPropertyButton( "VerifyListener (numbers only)" );
    btnNumbersOnlyVerifyListener.addSelectionListener( new SelectionAdapter() {

      public void widgetSelected( final SelectionEvent event ) {
        updateNumbersOnlyVerifyListener();
      }
    } );
  }

  private void createBlockingVerifyListenerButton() {
    btnBlockingVerifyListener
      = createPropertyButton( "VerifyListener (reject all)" );
    btnBlockingVerifyListener.addSelectionListener( new SelectionAdapter() {

      public void widgetSelected( final SelectionEvent event ) {
        updateBlockingVerifyListener();
      }
    } );
  }

  private void createSelectionListenerButton() {
    btnSelectionListener = createPropertyButton( "SelectionListener" );
    btnSelectionListener.addSelectionListener( new SelectionAdapter() {

      public void widgetSelected( final SelectionEvent event ) {
        updateSelectionListener();
      }
    } );
  }

  private void createEditableButton() {
    btnEditable = createPropertyButton( "Editable" );
    btnEditable.setSelection( true );
    btnEditable.addSelectionListener( new SelectionAdapter() {

      public void widgetSelected( final SelectionEvent event ) {
        updateEditable();
      }
    } );
  }

  private void createSelectionChooser( final Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayout( new RowLayout( SWT.HORIZONTAL ) );
    Label lblSelectionFrom = new Label( composite, SWT.NONE );
    lblSelectionFrom.setText( "selection from" );
    final Text txtSelectionFrom = new Text( composite, SWT.BORDER );
    Util.textSizeAdjustment( lblSelectionFrom, txtSelectionFrom );
    Label lblSelectionTo = new Label( composite, SWT.NONE );
    lblSelectionTo.setText( "to" );
    final Text txtSelectionTo = new Text( composite, SWT.BORDER );
    Util.textSizeAdjustment( lblSelectionTo, txtSelectionTo );
    Button btnChange = new Button( composite, SWT.PUSH );
    btnChange.setText( "set" );
    btnChange.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        int from = parseInt( txtSelectionFrom.getText() );
        int to = parseInt( txtSelectionTo.getText() );
        if( to >= 0 && from <= to  ) {
          text.setSelection( from, to );
        } else {
          String msg
            = "Invalid Selection: "
            + txtSelectionFrom.getText()
            + " - "
            + txtSelectionTo.getText();
          MessageDialog.openError( getShell(), "Error", msg );
        }
      }
    } );
    Button selectAllButton = new Button( composite, SWT.PUSH );
    selectAllButton.setText( "select all" );
    selectAllButton.addSelectionListener( new SelectionAdapter() {

      public void widgetSelected( SelectionEvent e ) {
        text.selectAll();
      }
    } );
  }

  private void createLimitText( final Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayout( new GridLayout( 4, false ) );
    Label label = new Label( composite, SWT.NONE );
    label.setText( "textLimit" );
    final Text limitText = new Text( composite, SWT.BORDER );
    limitText.setLayoutData( new GridData( 200, SWT.DEFAULT ) );
    Button setButton = new Button( composite, SWT.PUSH );
    setButton.setText( "set" );
    Button resetButton = new Button( composite, SWT.PUSH );
    resetButton.setText( "reset" );
    Listener changeListener = new Listener() {

      public void handleEvent( Event event ) {
        try {
          text.setTextLimit( Integer.parseInt( limitText.getText() ) );
          limitText.setText( String.valueOf( text.getTextLimit() ) );
          limitText.setBackground( null );
        } catch( Exception e ) {
          limitText.setBackground( BG_COLOR_BROWN );
        }
      }
    };
    limitText.addListener( SWT.DefaultSelection, changeListener );
    setButton.addListener( SWT.Selection, changeListener  );
    resetButton.addSelectionListener( new SelectionAdapter() {

      public void widgetSelected( SelectionEvent e ) {
        text.setTextLimit( Text.LIMIT );
        limitText.setText( "" );
        limitText.setBackground( null );
      }
    } );
  }

  private int parseInt( final String text ) {
    int result;
    try {
      result = Integer.parseInt( text );
    } catch( NumberFormatException e ) {
      result = -1;
    }
    return result;
  }

  private void updateSelectionListener() {
    if( btnSelectionListener != null ) {
      if( btnSelectionListener.getSelection() ) {
        text.addSelectionListener( selectionListener );
      } else {
        text.removeSelectionListener( selectionListener );
      }
    }
  }

  private void updateBlockingVerifyListener() {
    if( btnBlockingVerifyListener != null ) {
      if( btnBlockingVerifyListener.getSelection() ) {
        text.addVerifyListener( blockingVerifyListener );
      } else {
        text.removeVerifyListener( blockingVerifyListener );
      }
    }
  }

  private void updateNumbersOnlyVerifyListener() {
    if( btnNumbersOnlyVerifyListener != null ) {
      if( btnNumbersOnlyVerifyListener.getSelection() ) {
        text.addVerifyListener( numberOnlyVerifyListener );
      } else {
        text.removeVerifyListener( numberOnlyVerifyListener );
      }
    }
  }

  private void updateModifyListener() {
    if( btnModifyListener != null ) {
      if( btnModifyListener.getSelection() ) {
        text.addModifyListener( modifyListener );
      } else {
        text.removeModifyListener( modifyListener );
      }
    }
  }

  private void updateEditable() {
    if( btnEditable != null ) {
      text.setEditable( btnEditable.getSelection() );
    }
  }
}
