/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Innoopract Informationssysteme GmbH - initial API and
 * implementation
 ******************************************************************************/

package org.eclipse.rap.demo.controls;

import org.eclipse.rap.demo.controls.DefaultButtonManager.ChangeEvent;
import org.eclipse.rap.demo.controls.DefaultButtonManager.ChangeListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

public class TextTab extends ExampleTab {

  private Text simpleText;
  private Text modifyText;

  public TextTab( final TabFolder folder ) {
    super( folder, "Text" );
  }

  protected void createStyleControls() {
    createStyleButton( "BORDER" );
    createStyleButton( "WRAP" );
    createStyleButton( "SINGLE" );
    createStyleButton( "MULTI" );
    createStyleButton( "PASSWORD" );
    createStyleButton( "READ_ONLY" );
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
    createFontChooser();
    createLimitText( styleComp );
  }

  protected void createExampleControls( final Composite parent ) {
    parent.setLayout( new RowLayout( SWT.VERTICAL ) );
    simpleText = createText( parent, getStyle() );
    registerControl( simpleText );
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
    Point preferred = getPreferredSize( result );
    result.setLayoutData( new GridData( preferred.x, preferred.y ) );
    grpContainer.setLayoutData( new RowData( 400, 140 + preferred.y ) );
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
    grpContainer.setLayoutData( new RowData( 400, 140 + preferred.y ) );
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

  private void createLimitText( final Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayout( new GridLayout( 3, false ) );
    Label label = new Label( composite, SWT.NONE );
    label.setText( "TextLimit" );
    final Text text = new Text( composite, SWT.BORDER );
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

  private static Point getPreferredSize( final Text text ) {
    Point result;
    if( ( text.getStyle() & SWT.SINGLE ) != 0 ) {
      result = new Point( 200, 20 );
    } else {
      result = new Point( 200, 100 );
    }
    return result;
  }
}
