/*******************************************************************************
 * Copyright (c) 2007, 2008 Innoopract Informationssysteme GmbH.
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
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.*;

class DateTimeTab extends ExampleTab {

  private static final String PROP_CONTEXT_MENU = "contextMenu";
  private static final String PROP_SELECTION_LISTENER = "selectionListener";
  DateTime dateTime1;
  Group group1, group2;

  DateTimeTab( final CTabFolder topFolder ) {
    super( topFolder, "DateTime" );
    setDefaultStyle( SWT.BORDER | SWT.DATE | SWT.MEDIUM );
  }

  protected void createStyleControls( final Composite parent ) {
    group1 = new Group( styleComp, SWT.SHADOW_IN );
    group1.setLayout( new RowLayout( SWT.VERTICAL ) );
    createStyleButton( group1, "DATE", SWT.DATE, SWT.RADIO, true );
    createStyleButton( group1, "TIME", SWT.TIME, SWT.RADIO, false );
    createStyleButton( group1, "CALENDAR", SWT.CALENDAR, SWT.RADIO, false );
    group2 = new Group( styleComp, SWT.SHADOW_IN );
    group2.setLayout( new RowLayout( SWT.VERTICAL ) );
    createStyleButton( group2, "SHORT", SWT.SHORT, SWT.RADIO, false );
    createStyleButton( group2, "MEDIUM", SWT.MEDIUM, SWT.RADIO, true );
    createStyleButton( group2, "LONG", SWT.LONG, SWT.RADIO, false );
    createStyleButton( "BORDER", SWT.BORDER, true );
    createVisibilityButton();
    createEnablementButton();
    createFontChooser();
    createFgColorButton();
    createBgColorButton();
    createPropertyCheckbox( "Add Context Menu", PROP_CONTEXT_MENU );
    createPropertyCheckbox( "Add Selection Listener", PROP_SELECTION_LISTENER );
  }

  protected void createExampleControls( final Composite parent ) {
    parent.setLayout( new RowLayout( SWT.VERTICAL ) );
    int style = getStyle() | getStyle( group1 ) | getStyle( group2 );
    /* Create the example widgets */
    dateTime1 = new DateTime( parent, style );
    if( hasCreateProperty( PROP_CONTEXT_MENU ) ) {
      Menu dateTimeMenu = new Menu( dateTime1 );
      MenuItem dateTimeMenuItem = new MenuItem( dateTimeMenu, SWT.PUSH );
      dateTimeMenuItem.addSelectionListener( new SelectionAdapter() {

        public void widgetSelected( final SelectionEvent event ) {
          String message = "You requested a context menu for the DateTime";
          MessageDialog.openInformation( dateTime1.getShell(),
                                         "Information",
                                         message );
        }
      } );
      dateTimeMenuItem.setText( "DateTime context menu item" );
      dateTime1.setMenu( dateTimeMenu );
    }
    if( hasCreateProperty( PROP_SELECTION_LISTENER ) ) {
      dateTime1.addSelectionListener( new SelectionListener() {

        public void widgetSelected( final SelectionEvent event ) {
          String message = "DateTime WidgetSelected!";
          MessageDialog.openInformation( dateTime1.getShell(),
                                         "Information",
                                         message );
          //log( message );
        }

        public void widgetDefaultSelected( final SelectionEvent event ) {
          String message = "DateTime WidgetDefaultSelected!";
          MessageDialog.openInformation( dateTime1.getShell(),
                                         "Information",
                                         message );
          //log( message );
        }
      } );
    }
    registerControl( dateTime1 );
  }

  protected Button createStyleButton( final Composite parent,
                                      final String name,
                                      final int style,
                                      final int buttonStyle,
                                      final boolean checked )
  {
    Button button = new Button( parent, buttonStyle );
    button.setText( name );
    button.addSelectionListener( new SelectionAdapter() {

      public void widgetSelected( final SelectionEvent event ) {
        createNew();
      }
    } );
    button.setData( "style", new Integer( style ) );
    button.setSelection( checked );
    return button;
  }

  protected int getStyle( final Composite comp ) {
    int result = SWT.NONE;
    if( comp != null ) {
      Control[] ctrls = comp.getChildren();
      if( ctrls.length != 0 ) {
        for( int i = 0; i < ctrls.length; i++ ) {
          if( ctrls[ i ] instanceof Button ) {
            Button button = ( Button )ctrls[ i ];
            if( button.getSelection() ) {
              Object data = button.getData( "style" );
              if( data != null && data instanceof Integer ) {
                int style = ( ( Integer )data ).intValue();
                result |= style;
              }
            }
          }
        }
      }
    }
    return result;
  }
}
