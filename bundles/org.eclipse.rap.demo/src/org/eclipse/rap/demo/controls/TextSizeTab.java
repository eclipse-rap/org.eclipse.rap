/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/

package org.eclipse.rap.demo.controls;

import java.util.Iterator;

import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.*;

public class TextSizeTab extends ExampleTab {

  private static String[] testStrings;
  private boolean propFixedSize;
  private int nextIndex = 0;
  private String labelText = "";
  private Font font;

  public TextSizeTab( final CTabFolder parent ) {
    super( parent, "TextSize" );
    testStrings = createTestStrings();
    switchText();
  }

  protected void createStyleControls( final Composite parent ) {
    createStyleButton( "BORDER", SWT.BORDER );
    createStyleButton( "WRAP", SWT.WRAP );
    createFontChooser();
    final Button fixedSizeButton = createPropertyButton( "Fixed Size" );
    fixedSizeButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        propFixedSize = fixedSizeButton.getSelection();
        createNew();
      }
    } );
    Button nextButton = createPropertyButton( "Next Text", SWT.PUSH );
    nextButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent e ) {
        switchText();
        createNew();
      }
    } );
  }

  protected void createExampleControls( final Composite parent ) {
    int style = getStyle();

    Label label1 = new Label( parent, style );
    label1.setBackground( Graphics.getColor( 0xcc, 0xb7, 0x91 ) );
    label1.setText( labelText );
    label1.setLocation( 10, 10 );
    registerControl( label1 );

    Text text1 = new Text( parent, style );
    text1.setBackground( Graphics.getColor( 0xcc, 0xb7, 0x91 ) );
    text1.setText( labelText );
    text1.setLocation( 10, 100 );
    registerControl( text1 );

    updateControls();
  }

  private void updateControls() {
    Iterator iter = controls.iterator();
    while( iter.hasNext() ) {
      Control control = ( Control )iter.next();
      control.setFont( font );
      if( propFixedSize ) {
        control.setSize( control.computeSize( 200, SWT.DEFAULT ) );
      } else {
        control.pack();
      }
    }
  }

  private void switchText() {
    labelText = testStrings[ nextIndex ];
    nextIndex = (nextIndex + 1) % testStrings.length;
  }

  private static String[] createTestStrings() {
    String[] result = new String[] {
      "Lorem ipsum",
      "Lorem ipsum dolor sit amet",
      "Lorem ipsum dolor sit amet, consectetur adipisici "
      + "elit, sed do eiusmod tempor incididunt ut labore et "
      + "dolore magna aliqua.",

      "Lorem ipsum dolor sit amet, consectetur adipisici "
      + "elit, sed do eiusmod tempor incididunt ut labore et "
      + "dolore magna aliqua.\n"
      + "Ut enim ad minim veniam, quis nostrud exercitation "
      + "ullamco laboris nisi ut aliquip ex ea commodo "
      + "consequat.\n",

      "Lorem ipsum dolor sit amet, consectetur adipisici "
      + "elit, sed do eiusmod tempor incididunt ut labore et "
      + "dolore magna aliqua. "
      + "Ut enim ad minim veniam, quis nostrud exercitation "
      + "ullamco laboris nisi ut aliquip ex ea commodo "
      + "consequat. ",

      ""
    };
    return result;
  }

  protected Button createFontChooser() {
    final Button button = new Button( styleComp, SWT.PUSH );
    button.setText( "Font" );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        FontDialog fontChooser = new FontDialog( getShell(), SWT.NONE );
        Control control = ( Control )controls.get( 0 );
        fontChooser.setFontList( control.getFont().getFontData() );
        if( fontChooser.open() != null ) {
          font = new Font( control.getDisplay(), fontChooser.getFontList() );
        }
        updateControls();
      }
    } );
    return button;
  }
}
