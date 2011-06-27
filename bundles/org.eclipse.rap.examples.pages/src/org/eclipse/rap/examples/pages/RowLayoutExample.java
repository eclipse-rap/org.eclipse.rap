/*******************************************************************************
 * Copyright (c) 2009, 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.examples.pages;

import org.eclipse.rap.examples.*;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;


public class RowLayoutExample implements IExamplePage {

  private boolean propCenter = false;
  private boolean propFill = false;
  private boolean propJustify = false;
  private boolean propPack = true;
  private boolean propWrap = false;
  private boolean propPrefSize = false;

  public void createControl( Composite parent ) {
    parent.setLayout( ExampleUtil.createMainLayout( 1 ) );
    createLayoutArea( parent );
    createControlButtons( parent );
  }

  public void createLayoutArea( Composite parent ) {
    SashForm sashForm = new SashForm( parent, SWT.NONE );
    sashForm.setLayoutData( ExampleUtil.createFillData() );
    createLayoutComp( sashForm, SWT.HORIZONTAL );
    createLayoutComp( sashForm, SWT.VERTICAL );
  }

  private void createLayoutComp( Composite parent, int style ) {
    Group group = new Group( parent, SWT.NONE );
    group.setLayout( ExampleUtil.createGridLayout( 1, false, 10, 10 ) );
    String text = style == SWT.VERTICAL ? "Vertical RowLayout" : "Horizontal RowLayout";
    group.setText( text );
    Composite layoutComp = new Composite( group, SWT.BORDER );
    if( !propPrefSize ) {
      layoutComp.setLayoutData( ExampleUtil.createFillData() );
    }
    RowLayout layout = createLayout( style );
    layoutComp.setLayout( layout );
    Button button1 = new Button( layoutComp, SWT.PUSH );
    button1.setText( "Add" );
    ClassLoader classLoader = getClass().getClassLoader();
    Image imgAdd = Graphics.getImage( "resources/add_obj.gif", classLoader );
    button1.setImage( imgAdd );
    Button button2 = new Button( layoutComp, SWT.PUSH );
    button2.setText( "Remove" );
    Image imgDelete = Graphics.getImage( "resources/delete_obj.gif", classLoader );
    button2.setImage( imgDelete );
    Button button3 = new Button( layoutComp, SWT.PUSH );
    button3.setText( "Up" );
    Image imgUp = Graphics.getImage( "resources/up.gif", classLoader );
    button3.setImage( imgUp );
    Button button4 = new Button( layoutComp, SWT.PUSH );
    button4.setText( "Down" );
    Image imgDown = Graphics.getImage( "resources/down.gif", classLoader );
    button4.setImage( imgDown );
    Button button5 = new Button( layoutComp, SWT.PUSH );
    button5.setText( "Clear" );
  }

  private void createControlButtons( final Composite parent ) {
    Group group = new Group( parent, SWT.NONE );
    GridData gridData = new GridData( SWT.FILL, SWT.TOP, true, false );
    gridData.horizontalSpan = 2;
    group.setLayoutData( gridData );
    group.setLayout( ExampleUtil.createGridLayout( 1, false, 5, 2 ) );
    final Button centerButton = new Button( group, SWT.CHECK );
    centerButton.setText( "Center all elements in a row" );
    centerButton.addSelectionListener( new SelectionAdapter() {
      
      public void widgetSelected( SelectionEvent e ) {
        propCenter = centerButton.getSelection();
        relayout( parent );
      }
    } );
    final Button fillButton = new Button( group, SWT.CHECK );
    fillButton.setText( "Make all elements the same width / height" );
    fillButton.addSelectionListener( new SelectionAdapter() {
      
      public void widgetSelected( SelectionEvent e ) {
        propFill = fillButton.getSelection();
        relayout( parent );
      }
    } );
    final Button packButton = new Button( group, SWT.CHECK );
    packButton.setText( "Make all elements the same size" );
    packButton.addSelectionListener( new SelectionAdapter() {
      
      public void widgetSelected( SelectionEvent e ) {
        propPack = !packButton.getSelection();
        relayout( parent );
      }
    } );
    final Button justifyButton = new Button( group, SWT.CHECK );
    justifyButton.setText( "Justify elements" );
    justifyButton.addSelectionListener( new SelectionAdapter() {
      
      public void widgetSelected( SelectionEvent e ) {
        propJustify = justifyButton.getSelection();
        relayout( parent );
      }
    } );
    final Button wrapButton = new Button( group, SWT.CHECK );
    wrapButton.setText( "Wrap" );
    wrapButton.addSelectionListener( new SelectionAdapter() {
      
      public void widgetSelected( SelectionEvent e ) {
        propWrap = wrapButton.getSelection();
        relayout( parent );
      }
    } );
    final Button preferredSizeButton = new Button( group, SWT.CHECK );
    preferredSizeButton.setText( "Shrink containers to their preferred size" );
    preferredSizeButton.addSelectionListener( new SelectionAdapter() {
      
      public void widgetSelected( SelectionEvent e ) {
        propPrefSize = preferredSizeButton.getSelection();
        relayout( parent );
      }
    } );
  }

  private void relayout( Composite parent ) {
    Control[] children = parent.getChildren();
    for( Control child : children ) {
      if( child instanceof Composite ) {
        Composite childComp = ( Composite )child;
        relayout( childComp );
      }
    }
    Layout layout = parent.getLayout();
    if( layout instanceof RowLayout ) {
      RowLayout oldLayout = ( RowLayout )layout;
      parent.setLayout( createLayout( oldLayout.type ) );
      parent.layout();
      if( !propPrefSize ) {
        parent.setLayoutData( ExampleUtil.createFillData() );
      } else {
        parent.setLayoutData( null );
      }
      parent.getParent().layout();
    }
  }

  private RowLayout createLayout( int style ) {
    RowLayout layout = new RowLayout( style );
    layout.marginTop = 0;
    layout.marginBottom = 0;
    layout.marginLeft = 0;
    layout.marginRight = 0;
    layout.marginWidth = 5;
    layout.marginHeight = 5;
    layout.center = propCenter;
    layout.fill = propFill ;
    layout.justify = propJustify;
    layout.pack = propPack;
    layout.wrap = propWrap;
    return layout;
  }
}
