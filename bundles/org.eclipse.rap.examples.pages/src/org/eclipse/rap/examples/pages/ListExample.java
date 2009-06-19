/*******************************************************************************
 * Copyright (c) 2008, 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.examples.pages;

import java.util.Arrays;

import org.eclipse.rap.examples.viewer.IExamplePage;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.*;


public class ListExample implements IExamplePage {

  private static final String[] ELEMENTS = new String[]{
    "Dansk",
    "Deutsch",
    "English",
    "Espa\u00f1ol",
    "Fran\u00e7ais",
    "Nederlands",
    "\u0420\u0443\u0441\u0441\u043a\u0438\u0439",
    "Suomi",
    "Svenska",
  };

  public void createControl( final Composite parent ) {
    parent.setLayout( ExampleUtil.createGridLayout( 1, false, 10, 20 ) );
    Composite comp = new Composite( parent, SWT.NONE );
    comp.setLayout( ExampleUtil.createGridLayout( 3, false, 5, 20 ) );
    comp.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false ) );
    final List list1 = new List( comp, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL );
    list1.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
    Composite buttonComp = new Composite( comp, SWT.NONE );
    RowLayout layout = new RowLayout( SWT.VERTICAL );
    layout.fill = true;
    buttonComp.setLayout( layout );
    final Button addButton = new Button( buttonComp, SWT.PUSH | SWT.LEFT );
    addButton.setText( "Add" );
    ClassLoader classLoader = getClass().getClassLoader();
    Image imgAdd = Graphics.getImage( "resources/forward_nav.gif",
                                      classLoader );
    addButton.setImage( imgAdd  );
    final Button removeButton = new Button( buttonComp, SWT.PUSH | SWT.LEFT );
    removeButton.setText( "Remove" );
    Image imgRemove = Graphics.getImage( "resources/backward_nav.gif",
                                         classLoader );
    removeButton.setImage( imgRemove );
    final List list2 = new List( comp, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL );
    list2.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
    list1.setItems( ELEMENTS );
    addButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent e ) {
        moveSelectedItems( list1, list2 );
      }
    } );

    removeButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent e ) {
        moveSelectedItems( list2, list1 );
      }
    } );
  }

  private void moveSelectedItems( final List srcList, final List dstList ) {
    String[] selection = srcList.getSelection();
    for( int i = 0; i < selection.length; i++ ) {
      String item = selection[ i ];
      srcList.remove( item );
      dstList.add( item );
    }
    if( selection.length > 0 ) {
      srcList.deselectAll();
      sortItems( dstList );
      dstList.setSelection( selection );
    }
  }

  private void sortItems( final List srcList ) {
    String[] items = srcList.getItems();
    Arrays.sort( items );
    srcList.setItems( items );
  }
}
