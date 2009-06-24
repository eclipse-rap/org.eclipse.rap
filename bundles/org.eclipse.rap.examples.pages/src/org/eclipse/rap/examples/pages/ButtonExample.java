/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.examples.pages;

import org.eclipse.rap.examples.IExamplePage;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;


public class ButtonExample implements IExamplePage {

  public void createControl( final Composite parent ) {
    parent.setLayout( ExampleUtil.createGridLayout( 1, false, 10, 20 ) );
    createPushButtons( parent );
    createToggleGroup( parent );
    createRadioAndCheckButtons( parent );
  }

  private void createPushButtons( final Composite parent ) {
    Group group = new Group( parent, SWT.NONE );
    group.setText( "Push Buttons" );
    RowLayout layout = new RowLayout( SWT.HORIZONTAL );
    layout.marginWidth = 10;
    layout.marginHeight = 10;
    layout.spacing = 10;
    layout.center = true;
    group.setLayout( layout );
    group.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false ) );
    Button button = new Button( group, SWT.PUSH );
    button.setText( "Cancel" );
    Button button1 = new Button( group, SWT.PUSH );
    button1.setText( "Add" );
    ClassLoader classLoader = getClass().getClassLoader();
    Image imgAdd = Graphics.getImage( "resources/add_obj.gif", classLoader );
    button1.setImage( imgAdd );
    Button button2 = new Button( group, SWT.PUSH );
    button2.setText( "Delete" );
    Image imgDelete = Graphics.getImage( "resources/delete_obj.gif",
                                         classLoader );
    button2.setImage( imgDelete );
    new Label( group, SWT.NONE ).setLayoutData( new RowData( 10, 10 ) );
    Button button3 = new Button( group, SWT.PUSH );
    Image imageDownload = Graphics.getImage( "resources/go-bottom.png",
                                             classLoader );
    button3.setImage( imageDownload );
    button3.setToolTipText( "Download" );
  }

  private void createToggleGroup( final Composite parent ) {
    Group group = new Group( parent, SWT.NONE );
    group.setText( "Toggle Buttons" );
    RowLayout layout = new RowLayout( SWT.HORIZONTAL );
    layout.marginWidth = 10;
    layout.marginHeight = 10;
    layout.spacing = 10;
    layout.center = true;
    group.setLayout( layout );
    group.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false ) );
    Button toggle1 = new Button( group, SWT.TOGGLE );
    ClassLoader classLoader = getClass().getClassLoader();
    Image imgSynced = Graphics.getImage( "resources/synced.gif", classLoader );
    toggle1.setImage( imgSynced );
    toggle1.setToolTipText( "Keep in sync" );
    final Button toggle2 = new Button( group, SWT.TOGGLE | SWT.LEFT );
    toggle2.setText( "Unlocked" );
    final Image imgLocked = Graphics.getImage( "resources/lockedstate.gif",
                                               classLoader );
    final Image imgUnlocked = Graphics.getImage( "resources/unlockedstate.gif",
                                                 classLoader );
    toggle2.setImage( imgUnlocked );
    toggle2.addSelectionListener( new SelectionAdapter() {
      
      public void widgetSelected( SelectionEvent e ) {
        boolean selected = toggle2.getSelection();
        toggle2.setText( selected ? "Locked" : "Unlocked" );
        toggle2.setImage( selected ? imgLocked : imgUnlocked );
      }
    } );
  }

  private void createRadioAndCheckButtons( final Composite parent ) {
    Group group = new Group( parent, SWT.NONE );
    group.setText( "Checkboxes and Radiobuttons" );
    GridLayout layout = new GridLayout( 2, true );
    layout.marginWidth = 10;
    layout.marginHeight = 10;
    layout.horizontalSpacing = 20;
    group.setLayout( layout );
    group.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false ) );
    // Radio buttons
    Composite radioComp = new Composite( group, SWT.NONE );
    RowLayout radioLayout = new RowLayout( SWT.VERTICAL );
    radioLayout.marginWidth = 0;
    radioLayout.marginHeight = 0;
    radioComp.setLayout( radioLayout );
    final Button radio1 = new Button( radioComp, SWT.RADIO );
    radio1.setText( "Salami" );
    final Button radio2 = new Button( radioComp, SWT.RADIO );
    radio2.setText( "Funghi" );
    final Button radio3 = new Button( radioComp, SWT.RADIO );
    radio3.setText( "Calzone" );
    // Check boxes
    Composite checkComp = new Composite( group, SWT.NONE );
    RowLayout checkLayout = new RowLayout( SWT.VERTICAL );
    checkLayout.marginWidth = 0;
    checkLayout.marginHeight = 0;
    checkComp.setLayout( checkLayout );
    Button check1 = new Button( checkComp, SWT.CHECK );
    check1.setText( "Extra Cheese" );
    Button check2 = new Button( checkComp, SWT.CHECK );
    check2.setText( "Extra Hot" );
    Button check3 = new Button( checkComp, SWT.CHECK );
    check3.setText( "King Size" );
  }
}
