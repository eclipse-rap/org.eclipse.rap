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
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

final class SimpleFontDialog extends Dialog {

  // Widgets to set font properties
  private Text txtName;
  private Spinner spinSize;
  private Button chkBold;
  private Button chkItalic;

  private Font font;
  private Shell shell;
  private Runnable callback;
  
  public SimpleFontDialog( final Shell parent ) {
    this( parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.APPLICATION_MODAL );
  }
  
  // TODO [rst] When shell close can be prevented, this dialog should be
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
    shell.setSize( shell.computeSize( 350, SWT.DEFAULT ) );
    Point parentLocation = getParent().getLocation();
    shell.setLocation( parentLocation.x + 20, parentLocation.y + 20 );
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
    parent.setLayout( new GridLayout( 2, false ) );
    SelectionAdapter dummySelectionListener = new SelectionAdapter() {};
    Label lblName = new Label( parent, SWT.NONE );
    lblName.setText( "Name" );
    txtName = new Text( parent, SWT.BORDER );
    txtName.setLayoutData( new GridData( SWT.FILL, SWT.DEFAULT, true, false ) );
    Label lblSize = new Label( parent, SWT.NONE );
    lblSize.setText( "Size" );
    spinSize = new Spinner( parent, SWT.BORDER );
    spinSize.setMinimum( 1 );
    spinSize.setMaximum( 100 );
    spinSize.setLayoutData( new GridData( 50, SWT.DEFAULT ) );
    // check boxes for style
    Composite styleComp = new Composite( parent, SWT.NONE );
    styleComp.setLayout( new RowLayout( SWT.HORIZONTAL ) );
    GridData styleData = new GridData();
    styleData.horizontalSpan = 2;
    styleComp.setLayoutData( styleData );
    chkBold = new Button( styleComp, SWT.CHECK );
    chkBold.addSelectionListener( dummySelectionListener );
    chkBold.setText( "Bold" );
    chkItalic = new Button( styleComp, SWT.CHECK );
    chkItalic.addSelectionListener( dummySelectionListener );
    chkItalic.setText( "Italic" );
    // buttons
    Composite buttonComp = new Composite( parent, SWT.NONE );
    buttonComp.setLayout( new RowLayout( SWT.HORIZONTAL ) );
    GridData buttonData = new GridData();
    buttonData.horizontalSpan = 2;
    buttonData.horizontalAlignment = GridData.END;
    buttonComp.setLayoutData( buttonData );
    Button btnRevert = new Button( buttonComp, SWT.PUSH );
    btnRevert.setText( "Revert" );
    btnRevert.setToolTipText( "Revert to default font" );
    btnRevert.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        revertPressed();
      }
    } );
    Button btnCancel = new Button( buttonComp, SWT.PUSH );
    btnCancel.setText( "Cancel" );
    btnCancel.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        cancelPressed();
      }
    } );
    Button btnApply = new Button( buttonComp, SWT.PUSH );
    btnApply.setText( "Apply" );
    btnApply.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        applyPressed();
      }
    } );
  }
  
  private void updateFontControls() {
    if( font != null ) {
      FontData data = font.getFontData()[ 0 ];
      txtName.setText( data.getName() );
      spinSize.setSelection( data.getHeight() );
      chkBold.setSelection( ( data.getStyle() & SWT.BOLD ) != 0 );
      chkItalic.setSelection( ( data.getStyle() & SWT.ITALIC ) != 0 );      
    } else {
      txtName.setText( "" );
      spinSize.setSelection( 0 );
      chkBold.setSelection( false );
      chkItalic.setSelection( false );      
    }
  }
  
  private void revertPressed() {
    setFont( null );
    if( callback != null ) {
      callback.run();
    }
//    shell.setVisible( false );
    shell.close();
  }

  private void cancelPressed() {
    if( callback != null ) {
      callback.run();
    }
//    shell.setVisible( false );
    shell.close();
  }
  
  private void applyPressed() {
    String name = txtName.getText();
    int height;
    height = spinSize.getSelection();
    int style = SWT.NORMAL;
    if( chkBold.getSelection() ) {
      style |= SWT.BOLD;
    }
    if( chkItalic.getSelection() ) {
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
