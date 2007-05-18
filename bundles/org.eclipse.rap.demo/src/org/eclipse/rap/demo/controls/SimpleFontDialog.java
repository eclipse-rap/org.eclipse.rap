/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rap.demo.controls;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

final class SimpleFontDialog extends Dialog {

  // Widgets to set font properties
  private Text txtName;
  private Text txtSize;
  private Button btnBold;
  private Button btnItalic;

  private Font font;
  private Shell shell;
  private Runnable callback;
  
  public SimpleFontDialog( final Shell parent ) {
    this( parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL );
  }
  
  // TODO [rst] When shell close can be prevented, this dialog chould be
  //            implemented as SessionSingleton
  public SimpleFontDialog( final Shell parent, final int style ) {
    super( parent, style );
    title = "Font Dialog";
    shell = new Shell( parent, style );
    createControls( shell );
//    shell.addShellListener( new ShellAdapter() {
//      public void shellClosed( ShellEvent event ) {
//         shell.setVisible( false );
//         event.doit = false;
//      }
//    } );
  }

  public void open( final Runnable callback ) {
    this.callback = callback;
    if( title != null ) {
      shell.setText( title );
    }
    shell.layout();
    shell.pack();
    shell.open();
  }
  
  public Font getFont() {
    return font;
  }
  
  public void setFont( final Font font ) {
    this.font = font;
    updateFontControls();
  }
  
  private void createControls( final Composite parent ) {
    parent.setLayout( new FillLayout() );
    SelectionAdapter dummySelectionListener = new SelectionAdapter() {};
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayout( new GridLayout( 2, false ) );
    Label lblName = new Label( composite, SWT.NONE );
    lblName.setText( "Name" );
    txtName = new Text( composite, SWT.BORDER );
    txtName.setLayoutData( new GridData( 130, SWT.DEFAULT ) );
    Label lblSize = new Label( composite, SWT.NONE );
    lblSize.setText( "Size" );
    txtSize = new Text( composite, SWT.BORDER );
    txtSize.setLayoutData( new GridData( 50, SWT.DEFAULT ) );
    btnBold = new Button( composite, SWT.CHECK );
    btnBold.addSelectionListener( dummySelectionListener );
    btnBold.setText( "Bold" );
    btnItalic = new Button( composite, SWT.CHECK );
    btnItalic.addSelectionListener( dummySelectionListener );
    btnItalic.setText( "Italic" );
    Button btnApply = new Button( composite, SWT.PUSH );
    GridData gridData = new GridData( 80, 23 );
    gridData.horizontalSpan = 2;
    gridData.horizontalAlignment = GridData.END;
    btnApply.setLayoutData( gridData );
    btnApply.setText( "Apply" );
    btnApply.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        applyPressed();
      }
    } );
  }
  
  private void updateFontControls() {
    FontData data = font.getFontData()[ 0 ];
    txtName.setText( data.getName() );
    txtSize.setText( String.valueOf( data.getHeight() ) );
    btnBold.setSelection( ( data.getStyle() & SWT.BOLD ) != 0 );
    btnItalic.setSelection( ( data.getStyle() & SWT.ITALIC ) != 0 );
  }

  private void applyPressed() {
    String name = txtName.getText();
    int height;
    FontData data = font.getFontData()[ 0 ];
    try {
      height = Integer.parseInt( txtSize.getText() );
      if( height < 0 ) {
        height = data.getHeight();
      }
    } catch( NumberFormatException e ) {
      height = data.getHeight();
    }
    int style = SWT.NORMAL;
    if( btnBold.getSelection() ) {
      style |= SWT.BOLD;
    }
    if( btnItalic.getSelection() ) {
      style |= SWT.ITALIC;
    }
    setFont( Font.getFont( name, height, style ) );
    if( callback != null ) {
      callback.run();
    }
//    shell.setVisible( false );
    shell.close();
  }
}
