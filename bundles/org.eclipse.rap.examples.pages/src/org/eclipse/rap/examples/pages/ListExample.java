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

import org.eclipse.rap.examples.IExamplePage;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.*;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.*;


public class ListExample implements IExamplePage {

  private static final int LIST_STYLE = SWT.BORDER | SWT.MULTI | SWT.V_SCROLL;
  private static final String IMG_ADD = "resources/forward_nav.gif";
  private static final String IMG_REMOVE = "resources/backward_nav.gif";
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
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayout( ExampleUtil.createGridLayout( 3, false, 5, 20 ) );
    composite.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false ) );
    final List leftList = new List( composite, LIST_STYLE );
    leftList.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
    addDragSupport( leftList );
    addDropSupport( leftList );
    Composite buttons = new Composite( composite, SWT.NONE );
    RowLayout layout = new RowLayout( SWT.VERTICAL );
    layout.fill = true;
    buttons.setLayout( layout );
    final Button addButton = createButton( buttons, "Add", IMG_ADD );
    final Button removeButton = createButton( buttons, "Remove", IMG_REMOVE );
    final List rightList = new List( composite, LIST_STYLE );
    rightList.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
    addDragSupport( rightList );
    addDropSupport( rightList );
    leftList.setItems( ELEMENTS );
    addButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        moveSelectedItems( leftList, rightList );
      }
    } );
    removeButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        moveSelectedItems( rightList, leftList );
      }
    } );
  }

  private static void addDragSupport( final List list ) {
    DragSource dragSource = new DragSource( list, DND.DROP_MOVE );
    dragSource.setTransfer( new Transfer[] { new StringArrayTransfer() } );
    dragSource.addDragListener( new DragSourceAdapter() {
      public void dragSetData( final DragSourceEvent event ) {
        event.data = list.getSelection();
      }
      public void dragFinished( final DragSourceEvent event ) {
        if( event.doit ) {
          String[] items = list.getSelection();
          for( int i = 0; i < items.length; i++ ) {
            list.remove( items[ i ] );
          }
        }
      }
    } );
  }

  private static void addDropSupport( final List list ) {
    DropTarget dropTarget = new DropTarget( list, DND.DROP_MOVE );
    dropTarget.setTransfer( new Transfer[] { new StringArrayTransfer() } );
    dropTarget.addDropListener( new DropTargetAdapter() {
      public void drop( final DropTargetEvent event ) {
        String[] items = ( String[] )event.data;
        for( int i = 0; i < items.length; i++ ) {
          list.add( items[ i ] );
        }
      }
    } );
  }

  private Button createButton( final Composite parent,
                                    final String text,
                                    final String image )
  {
    Button result = new Button( parent, SWT.PUSH | SWT.LEFT );
    result.setText( text );
    result.setImage( Graphics.getImage( image, getClass().getClassLoader() )  );
    return result;
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
  
  private static final class StringArrayTransfer extends ByteArrayTransfer {

    private static final String TYPE_NAME = "string-array";
    private static final int TYPE_ID = registerType( TYPE_NAME );
    
    protected int[] getTypeIds() {
      return new int[] { TYPE_ID };
    }

    protected String[] getTypeNames() {
      return new String[] { TYPE_NAME };
    }
    
    public void javaToNative( Object object, TransferData transferData ) {
      transferData.data = object;
    }
    
    public Object nativeToJava( TransferData transferData ) {
      return transferData.data;
    }
  }
}
