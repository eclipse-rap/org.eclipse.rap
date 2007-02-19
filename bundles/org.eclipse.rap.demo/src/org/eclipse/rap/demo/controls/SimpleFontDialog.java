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

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.events.*;
import org.eclipse.rap.rwt.graphics.*;
import org.eclipse.rap.rwt.layout.*;
import org.eclipse.rap.rwt.widgets.*;

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
    this( parent, RWT.DIALOG_TRIM | RWT.APPLICATION_MODAL );
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
    Composite composite = new Composite( parent, RWT.NONE );
    composite.setLayout( new GridLayout( 2, false ) );
    Label lblName = new Label( composite, RWT.NONE );
    lblName.setText( "Name" );
    txtName = new Text( composite, RWT.BORDER );
    txtName.setLayoutData( new GridData( 130, RWT.DEFAULT ) );
    Label lblSize = new Label( composite, RWT.NONE );
    lblSize.setText( "Size" );
    txtSize = new Text( composite, RWT.BORDER );
    txtSize.setLayoutData( new GridData( 50, RWT.DEFAULT ) );
    btnBold = new Button( composite, RWT.CHECK );
    btnBold.addSelectionListener( dummySelectionListener );
    btnBold.setText( "Bold" );
    btnItalic = new Button( composite, RWT.CHECK );
    btnItalic.addSelectionListener( dummySelectionListener );
    btnItalic.setText( "Italic" );
    Button btnApply = new Button( composite, RWT.PUSH );
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
    txtName.setText( font.getName() );
    txtSize.setText( String.valueOf( font.getSize() ) );
    btnBold.setSelection( ( font.getStyle() & RWT.BOLD ) != 0 );
    btnItalic.setSelection( ( font.getStyle() & RWT.ITALIC ) != 0 );
  }

  private void applyPressed() {
    String name = txtName.getText();
    int size;
    try {
      size = Integer.parseInt( txtSize.getText() );
      if( size < 0 ) {
        size = font.getSize();
      }
    } catch( NumberFormatException e ) {
      size = font.getSize();
    }
    int style = RWT.NORMAL;
    if( btnBold.getSelection() ) {
      style |= RWT.BOLD;
    }
    if( btnItalic.getSelection() ) {
      style |= RWT.ITALIC;
    }
    setFont( Font.getFont( name, size, style ) );
    if( callback != null ) {
      callback.run();
    }
//    shell.setVisible( false );
    shell.close();
  }
}
