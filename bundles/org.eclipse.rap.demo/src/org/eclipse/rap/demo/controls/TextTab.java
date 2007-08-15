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
import org.eclipse.rap.demo.controls.DefaultButtonManager.ChangeEvent;
import org.eclipse.rap.demo.controls.DefaultButtonManager.ChangeListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

public class TextTab extends ExampleTab {

  private Text simpleText;
  private Text modifyText;
  private final SelectionListener selectionListener;

  public TextTab( final CTabFolder topFolder ) {
    super( topFolder, "Text" );
    selectionListener = new SelectionAdapter() {
      public void widgetDefaultSelected( final SelectionEvent event ) {
        String msg = "You pressed the Enter key.";
        MessageDialog.openInformation( getShell(), "Information", msg );
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
    createVisibilityButton();
    createEnablementButton();
    final Button editableButton = createPropertyButton( "Editable" );
    editableButton.setSelection( true );
    editableButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent event ) {
        boolean editable = editableButton.getSelection();
        simpleText.setEditable( editable );
        modifyText.setEditable( editable );
      }
    } );
    final Button selectionListenerButton 
      = createPropertyButton( "SelectionListener", SWT.CHECK );
    selectionListenerButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        if( SelectionEvent.hasListener( simpleText ) ) {
          simpleText.removeSelectionListener( selectionListener );
        } else {
          simpleText.addSelectionListener( selectionListener );
        }
      }
    } );
    createFgColorButton();
    createBgColorButton();
    createFontChooser();
    createLimitText( parent );
    createSelectionChooser( parent );
    createSelectionQuery( parent );
  }

  protected void createExampleControls( final Composite parent ) {
    parent.setLayout( new FillLayout( SWT.VERTICAL ) );
    simpleText = createText( parent, getStyle() );
    registerControl( simpleText );
    new Label( parent, SWT.NONE );
    modifyText = createModifyText( parent, getStyle() );
    registerControl( modifyText );
  }

  private static Text createText( final Composite parent, final int style ) {
    Group grpContainer = new Group( parent, SWT.NONE );
    grpContainer.setText( "Simple Text" );
    grpContainer.setLayout( new GridLayout( 3, false ) );
    GridData gridData;
    final Button btnChangeIsDefault = new Button( grpContainer, SWT.CHECK );
    String buttonText = "Make the 'Change' button the default button";
    btnChangeIsDefault.setText( buttonText );
    gridData = new GridData( SWT.FILL, SWT.NONE, true, false );
    gridData.horizontalSpan = 3;
    btnChangeIsDefault.setLayoutData( gridData );
    Label lblEnterText = new Label( grpContainer, SWT.NONE );
    lblEnterText.setText( "Enter some text, please" );
    final Text result = new Text( grpContainer, style );
    result.setText( "used for preferred size" );
    Point preferred = getPreferredSize( result );
    result.setLayoutData( new GridData( preferred.x, preferred.y ) );
    result.setText(   "Lorem ipsum dolor sit amet, consectetur adipisici "
                    + "elit, sed do eiusmod tempor incididunt ut labore et "
                    + "dolore magna aliqua.\n"
                    + "Ut enim ad minim veniam, quis nostrud exercitation "
                    + "ullamco laboris nisi ut aliquip ex ea commodo "
                    + "consequat.\n"
                    + "Duis aute irure dolor in reprehenderit in voluptate "
                    + "velit esse cillum dolore eu fugiat nulla pariatur." );
    result.setSelection( 0, 12 );
    final Button btnChange = new Button( grpContainer, SWT.PUSH );
    final Label lblTextContent = new Label( grpContainer, SWT.WRAP );
    gridData = new GridData( SWT.FILL, SWT.FILL, true, true );
    gridData.horizontalSpan = 3;
    lblTextContent.setLayoutData( gridData );
    lblTextContent.setText( "You entered: " + result.getText() );
    btnChange.setText( "Change" );
    btnChangeIsDefault.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        Shell shell = btnChangeIsDefault.getShell();
        if( btnChangeIsDefault.getSelection() ) {
          DefaultButtonManager.getInstance().change( shell, btnChange );
        } else {
          DefaultButtonManager.getInstance().change( shell, null );
        }
      }
    } );
    btnChange.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        lblTextContent.setText( "You entered: " + result.getText() );        
      }
    } );
    DefaultButtonManager.getInstance().addChangeListener( new ChangeListener() {
      public void defaultButtonChanged( final ChangeEvent event ) {
        Shell shell = ( Shell )event.getSource();
        boolean selection = shell.getDefaultButton() == btnChange;
        btnChangeIsDefault.setSelection( selection );
      }
    } );
    return result;
  }

  private Text createModifyText( final Composite parent, final int style ) {
    Group grpContainer = new Group( parent, SWT.NONE );
    grpContainer.setText( "Text width ModifyListener" );
    grpContainer.setLayout( new GridLayout( 3, false ) );
    GridData gridData;
    final Button btnChangeIsDefault = new Button( grpContainer, SWT.CHECK );
    String buttonText = "Make the 'Change' button the default button";
    btnChangeIsDefault.setText( buttonText );
    gridData = new GridData( SWT.FILL, SWT.NONE, true, false );
    gridData.horizontalSpan = 3;
    btnChangeIsDefault.setLayoutData( gridData );
    Label lblEnterText = new Label( grpContainer, SWT.NONE );
    lblEnterText.setText( "Enter some text, please" );
    final Text result = new Text( grpContainer, style );
    Point preferred = getPreferredSize( result );
    result.setLayoutData( new GridData( preferred.x, preferred.y ) );
    final Button btnChange = new Button( grpContainer, SWT.PUSH );
    final Label lblTextContent = new Label( grpContainer, SWT.WRAP );
    gridData = new GridData( SWT.FILL, SWT.FILL, true, true );
    gridData.horizontalSpan = 3;
    lblTextContent.setLayoutData( gridData );
    lblTextContent.setText( "You entered: " );
    btnChange.setText( "Change" );
    btnChangeIsDefault.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        Shell shell = btnChangeIsDefault.getShell();
        if( btnChangeIsDefault.getSelection() ) {
          DefaultButtonManager.getInstance().change( shell, btnChange );
        } else {
          DefaultButtonManager.getInstance().change( shell, null );
        }
      }
    } );
    result.addModifyListener( new ModifyListener() {
      public void modifyText( final ModifyEvent event ) {
        String msg = "ModifyEvent -> You entered: ";
        lblTextContent.setText( msg + result.getText() );        
      }
    } );
    btnChange.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        String msg = "Change button -> You entered: ";
        lblTextContent.setText( msg + result.getText() );        
      }
    } );
    DefaultButtonManager.getInstance().addChangeListener( new ChangeListener() {
      public void defaultButtonChanged( final ChangeEvent event ) {
        Shell shell = ( Shell )event.getSource();
        boolean selection = shell.getDefaultButton() == btnChange;
        btnChangeIsDefault.setSelection( selection );
      }
    } );
    return result;
  }

  private void createSelectionChooser( final Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayout( new GridLayout( 5, false ) );
    Label lblSelectionFrom = new Label( composite, SWT.NONE );
    lblSelectionFrom.setText( "Selection from" );
    final Text txtSelectionFrom = new Text( composite, SWT.BORDER );
    Util.textSizeAdjustment( lblSelectionFrom, txtSelectionFrom );
    Label lblSelectionTo = new Label( composite, SWT.NONE );
    lblSelectionTo.setText( "to" );
    final Text txtSelectionTo = new Text( composite, SWT.BORDER );
    Util.textSizeAdjustment( lblSelectionTo, txtSelectionTo );
    Button btnChange = new Button( composite, SWT.PUSH );
    btnChange.setText( "Change" );
    btnChange.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        int from = parseInt( txtSelectionFrom.getText() );
        int to = parseInt( txtSelectionTo.getText() );
        if( to >= 0 && from <= to  ) {
          // TODO [rh] remove this as soon as selection for MULTI text works
          if( ( simpleText.getStyle() & SWT.MULTI ) != 0 ) {
            noSelectionForMultiTextInfo();
          } else {
            simpleText.setSelection( from, to );
            modifyText.setSelection( from, to );
          }
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
  }

  private void createSelectionQuery( final Composite parent ) {
    Button btnGetSelection = new Button( parent, SWT.PUSH );
    btnGetSelection.setText( "Get Selection" );
    btnGetSelection.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        // TODO [rh] remove this as soon as selection for MULTI text works
        if( ( simpleText.getStyle() & SWT.MULTI ) != 0 ) {
          noSelectionForMultiTextInfo();
        } else {
          Point simpleTextSelection = simpleText.getSelection();
          Point modifyTextSelection = modifyText.getSelection();
          String msg 
            = "Selection in 'Simple Text' ranges from " 
            + simpleTextSelection.x 
            + " to " 
            + simpleTextSelection.y
            + "\nSelection in 'Text with ModifyListener' ranges from "
            + modifyTextSelection.x
            + " to "
            + modifyTextSelection.y;
          MessageDialog.openInformation( getShell(), "Information", msg );
        }
      }
    } );
  }

  private void createLimitText( final Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayout( new GridLayout( 3, false ) );
    Label label = new Label( composite, SWT.NONE );
    label.setText( "TextLimit" );
    final Text text = new Text( composite, SWT.BORDER );
    Util.textSizeAdjustment( label, text );
    Button button = new Button( composite, SWT.PUSH );
    button.setText( "Change" );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        int currentLimit = simpleText.getTextLimit();
        int limit = currentLimit;
        try {
          limit = Integer.parseInt( text.getText() );
        } catch( NumberFormatException e ) {
          // ignore
        }
        if( limit == 0 ) {
          limit = currentLimit;
        }
        modifyText.setTextLimit( limit );
        simpleText.setTextLimit( limit );
        text.setText( String.valueOf( limit ) );
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

  private void noSelectionForMultiTextInfo() {
    String msg 
      = "Sorry, changing the selection is not yet implemented for " 
      + "Text with style MULTI.";
    MessageDialog.openInformation( getShell(), "Information", msg );
  }

  private static Point getPreferredSize( final Text text ) {
    Point result;
    result = text.computeSize( SWT.DEFAULT, SWT.DEFAULT );
    result = new Point( result.y * 4, result.y );
    if( ( text.getStyle() & SWT.SINGLE ) == 0 ) {
      result = new Point( result.x * 2, result.y * 4 );
    }
    return result;
  }
}
