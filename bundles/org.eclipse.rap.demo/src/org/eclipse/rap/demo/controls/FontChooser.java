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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;


final class FontChooser {

  // Widgets to set font properties
  private Text txtName;
  private Text txtSize;
  private Button btnBold;
  private Button btnItalic;

  private Font font;
  private Runnable changeRunnable;
  
  FontChooser( final Composite parent ) {
    createControls( parent );
  }
  
  Font getFont() {
    return font;
  }
  
  void setFont( final Font font ) {
    this.font = font;
    updateFontControls();
  }
  
  Runnable getChangeRunnable() {
    return changeRunnable;
  }
  
  void setChangeRunnable( final Runnable changeRunnable ) {
    this.changeRunnable = changeRunnable;
  }
  
  private void createControls( final Composite parent ) {
    GridData gridData;
    SelectionAdapter dummySelectionListener = new SelectionAdapter() {
    };
    // Due to missing Group, use a labeled composite to 'group' the controls
    Composite composite = new Composite( parent, SWT.BORDER );
    Label lblFont = new Label( composite, SWT.NONE );
    gridData = new GridData( 80, 18 );
    gridData.horizontalSpan = 2;
    lblFont.setLayoutData( gridData );
    lblFont.setFont( boldSystemFont() );
    lblFont.setText( "Font" );
    composite.setLayout( new GridLayout( 2, false ) );
    Label lblName = new Label( composite, SWT.NONE );
    lblName.setLayoutData( new GridData( 80, 18 ) );
    lblName.setText( "Name" );
    txtName = new Text( composite, SWT.BORDER );
    txtName.setLayoutData( new GridData( 130, 18 ) );
    Label lblSize = new Label( composite, SWT.NONE );
    lblSize.setLayoutData( new GridData( 80, 18 ) );
    lblSize.setText( "Size" );
    txtSize = new Text( composite, SWT.BORDER );
    txtSize.setLayoutData( new GridData( 90, 18 ) );
    btnBold = new Button( composite, SWT.CHECK );
    btnBold.addSelectionListener( dummySelectionListener );
    gridData = new GridData( 80, 18 );
    btnBold.setLayoutData( gridData );
    btnBold.setText( "Bold" );
    btnItalic = new Button( composite, SWT.CHECK );
    btnItalic.addSelectionListener( dummySelectionListener );
    gridData = new GridData( 80, 18 );
    btnItalic.setLayoutData( gridData );
    btnItalic.setText( "Italic" );
    Button btnApply = new Button( composite, SWT.PUSH );
    gridData = new GridData( 80, 23 );
    gridData.horizontalSpan = 2;
    btnApply.setLayoutData( gridData );
    btnApply.setText( "Apply" );
    btnApply.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
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
        if( changeRunnable != null ) {
          changeRunnable.run();
        }
      }
    } );
    txtName.setFocus();
  }
  
  private static Font boldSystemFont() {
    Font font = Display.getCurrent().getSystemFont();
    FontData data = font.getFontData()[ 0 ];
    return Font.getFont( data.getName(), data.getHeight(), SWT.BOLD );
  }

  private void updateFontControls() {
    FontData data = font.getFontData()[ 0 ];
    txtName.setText( data.getName() );
    txtSize.setText( String.valueOf( data.getHeight() ) );
    btnBold.setSelection( ( data.getStyle() & SWT.BOLD ) != 0 );
    btnItalic.setSelection( ( data.getStyle() & SWT.ITALIC ) != 0 );
  }
}
