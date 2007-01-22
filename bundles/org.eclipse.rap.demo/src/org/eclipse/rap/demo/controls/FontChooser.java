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
import org.eclipse.rap.rwt.events.SelectionAdapter;
import org.eclipse.rap.rwt.events.SelectionEvent;
import org.eclipse.rap.rwt.graphics.Font;
import org.eclipse.rap.rwt.layout.GridData;
import org.eclipse.rap.rwt.layout.GridLayout;
import org.eclipse.rap.rwt.widgets.*;


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
    Composite composite = new Composite( parent, RWT.BORDER );
    Label lblFont = new Label( composite, RWT.NONE );
    gridData = new GridData( 80, 18 );
    gridData.horizontalSpan = 2;
    lblFont.setLayoutData( gridData );
    lblFont.setFont( boldSystemFont() );
    lblFont.setText( "Font" );
    composite.setLayout( new GridLayout( 2, false ) );
    Label lblName = new Label( composite, RWT.NONE );
    lblName.setLayoutData( new GridData( 80, 18 ) );
    lblName.setText( "Name" );
    txtName = new Text( composite, RWT.BORDER );
    txtName.setLayoutData( new GridData( 130, 18 ) );
    Label lblSize = new Label( composite, RWT.NONE );
    lblSize.setLayoutData( new GridData( 80, 18 ) );
    lblSize.setText( "Size" );
    txtSize = new Text( composite, RWT.BORDER );
    txtSize.setLayoutData( new GridData( 90, 18 ) );
    btnBold = new Button( composite, RWT.CHECK );
    btnBold.addSelectionListener( dummySelectionListener );
    gridData = new GridData( 80, 18 );
    gridData.horizontalSpan = 2;
    btnBold.setLayoutData( gridData );
    btnBold.setText( "Bold" );
    btnItalic = new Button( composite, RWT.CHECK );
    btnItalic.addSelectionListener( dummySelectionListener );
    gridData = new GridData( 80, 18 );
    gridData.horizontalSpan = 2;
    btnItalic.setLayoutData( gridData );
    btnItalic.setText( "Italic" );
    Button btnApply = new Button( composite, RWT.PUSH );
    gridData = new GridData( 80, 23 );
    gridData.horizontalSpan = 2;
    btnApply.setLayoutData( gridData );
    btnApply.setText( "Apply" );
    btnApply.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        String name = txtName.getText();
        int size;
        try {
          size = Integer.parseInt( txtSize.getText() );
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
        if( changeRunnable != null ) {
          changeRunnable.run();
        }
      }
    } );
  }
  
  private static Font boldSystemFont() {
    Font font = Display.getCurrent().getSystemFont();
    Font result = Font.getFont( font.getName(), font.getSize(), RWT.BOLD );
    return result;
  }

  private void updateFontControls() {
    txtName.setText( font.getName() );
    txtSize.setText( String.valueOf( font.getSize() ) );
    btnBold.setSelection( ( font.getStyle() & RWT.BOLD ) != 0 );
    btnItalic.setSelection( ( font.getStyle() & RWT.ITALIC ) != 0 );
  }
}
