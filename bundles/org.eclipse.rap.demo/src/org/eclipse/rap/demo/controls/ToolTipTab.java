/*******************************************************************************
 * Copyright (c) 2011 Rüdiger Herrmann and others. All rights reserved.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rüdiger Herrmann - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.demo.controls;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

public class ToolTipTab extends ExampleTab {

  private ToolTip toolTip;
  private boolean toolTipAutoHide;
  private String toolTipText;
  private String toolTipMessage;
  private Point toolTipLocation;
  private boolean toolTipSelectionListener;
  private final SelectionListener selectionListener;

  public ToolTipTab( final CTabFolder folder ) {
    super( folder, "ToolTip" );
    toolTipText = "";
    toolTipMessage = "";
    selectionListener = new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        ToolTipTab.this.log( "" + event );
      }
    };
  }

  protected void createStyleControls( final Composite parent ) {
    parent.setLayout( new GridLayout( 1, false ) );
    createStyleButton( "BALLOON", SWT.BALLOON );
    createStyleButton( "ICON_ERROR", SWT.ICON_ERROR );
    createStyleButton( "ICON_INFORMATION", SWT.ICON_INFORMATION );
    createStyleButton( "ICON_WARNING", SWT.ICON_WARNING );
    createAutoHideButton();
    createTextInput();
    createMessageInput();
    createLocationInput();
    createSelectionListenerButton();
  }

  protected void createExampleControls( final Composite parent ) {
    parent.setLayout( new GridLayout( 1, false ) );
    Button button = new Button( parent, SWT.PUSH );
    button.setText( "Show ToolTip" );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent event ) {
        if( toolTip != null ) {
          toolTip.dispose();
        }
        toolTip = new ToolTip( parent.getShell(), getStyle() );
        toolTip.setVisible( true );
        toolTip.setAutoHide( toolTipAutoHide );
        toolTip.setText( toolTipText );
        toolTip.setMessage( toolTipMessage );
        if( toolTipLocation != null ) {
          toolTip.setLocation( toolTipLocation );
        }
        updateSelectionListener();
      }
    } );
  }

  private void createTextInput() {
    Composite group = new Composite( styleComp, SWT.NONE );
    group.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false ) );
    group.setLayout( new GridLayout( 2, false ) );
    new Label( group, SWT.NONE ).setText( "Text" );
    Text text = new Text( group, SWT.BORDER );
    text.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false ) );
    text.addModifyListener( new ModifyListener() {
      public void modifyText( ModifyEvent event ) {
        Text text = ( Text )event.widget;
        toolTipText = text.getText();
      }
    } );
  }

  private void createMessageInput() {
    Composite group = new Composite( styleComp, SWT.NONE );
    group.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false ) );
    group.setLayout( new GridLayout( 2, false ) );
    Label label = new Label( group, SWT.NONE );
    label.setLayoutData( new GridData( SWT.FILL, SWT.TOP, false, false ) );
    label.setText( "Message" );
    Text text = new Text( group, SWT.BORDER | SWT.MULTI );
    GridData gridData = new GridData( SWT.FILL, SWT.FILL, true, true );
    gridData.heightHint = 150;
    text.setLayoutData( gridData );
    text.addModifyListener( new ModifyListener() {
      public void modifyText( ModifyEvent event ) {
        Text text = ( Text )event.widget;
        toolTipMessage = text.getText();
      }
    } );
  }
  
  private void createLocationInput() {
    Composite group = new Composite( styleComp, SWT.NONE );
    group.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false ) );
    group.setLayout( new GridLayout( 4, false ) );
    new Label( group, SWT.NONE ).setText( "Location X" );
    final Text textX = new Text( group, SWT.BORDER );
    new Label( group, SWT.NONE ).setText( "Y" );
    final Text textY = new Text( group, SWT.BORDER );
    ModifyListener modifyListener = new ModifyListener() {
      public void modifyText( ModifyEvent event ) {
        int x = parseInt( textX.getText() );
        int y = parseInt( textY.getText() );
        if( x > 0 && y > 0 ) {
          toolTipLocation = new Point( x, y );
        } else {
          toolTipLocation = null;
        }
      }
    };
    textX.addModifyListener( modifyListener );
    textY.addModifyListener( modifyListener );
  }

  private void createAutoHideButton() {
    Button button = new Button( styleComp, SWT.CHECK );
    button.setText( "AutoHide" );
    button.setSelection( toolTipAutoHide );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        Button button = ( Button )event.widget;
        toolTipAutoHide = button.getSelection();
      }
    } );
  }

  private void createSelectionListenerButton() {
    Button button = new Button( styleComp, SWT.CHECK );
    button.setText( "SelectionListener" );
    button.setSelection( toolTipSelectionListener );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        Button button = ( Button )event.widget;
        toolTipSelectionListener = button.getSelection();
        updateSelectionListener();
      }
    } );
  }

  private void updateSelectionListener() {
    if( toolTip != null ) {
      if( toolTipSelectionListener ) {
        toolTip.addSelectionListener( selectionListener );
      } else {
        toolTip.removeSelectionListener( selectionListener );
      }
    }
  }

  private static int parseInt( final String text ) {
    int result;
    try {
      result = Integer.parseInt( text );
    } catch( NumberFormatException e ) {
      result = -1;
    }
    return result;
  }
}
