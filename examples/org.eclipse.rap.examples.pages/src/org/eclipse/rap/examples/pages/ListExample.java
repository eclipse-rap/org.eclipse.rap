/*******************************************************************************
 * Copyright (c) 2009, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.examples.pages;

import java.util.Arrays;

import org.eclipse.rap.examples.ExampleUtil;
import org.eclipse.rap.examples.IExamplePage;
import org.eclipse.rap.examples.pages.internal.ImageUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;


public class ListExample implements IExamplePage {

  private static final int LIST_STYLE = SWT.BORDER | SWT.MULTI | SWT.V_SCROLL;
  private static final String IMG_ADD = "forward_nav.gif";
  private static final String IMG_REMOVE = "backward_nav.gif";
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

  public void createControl( Composite parent ) {
    parent.setLayout( ExampleUtil.createMainLayout( 1 ) );
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayout( ExampleUtil.createGridLayout( 3, false, true, true ) );
    composite.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false ) );
    final List leftList = new List( composite, LIST_STYLE );
    leftList.setLayoutData( ExampleUtil.createFillData() );
    addDragSupport( leftList );
    addDropSupport( leftList );
    Composite buttons = new Composite( composite, SWT.NONE );
    RowLayout layout = new RowLayout( SWT.VERTICAL );
    layout.fill = true;
    buttons.setLayout( layout );
    final Button addButton = createButton( buttons, "Add", IMG_ADD );
    final Button removeButton = createButton( buttons, "Remove", IMG_REMOVE );
    final List rightList = new List( composite, LIST_STYLE );
    rightList.setLayoutData( ExampleUtil.createFillData() );
    addDragSupport( rightList );
    addDropSupport( rightList );
    leftList.setItems( ELEMENTS );
    addButton.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent event ) {
        moveSelectedItems( leftList, rightList );
      }
    } );
    removeButton.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent event ) {
        moveSelectedItems( rightList, leftList );
      }
    } );
  }

  private static void addDragSupport( final List list ) {
    DragSource dragSource = new DragSource( list, DND.DROP_MOVE );
    dragSource.setTransfer( new Transfer[] { new StringArrayTransfer() } );
    dragSource.addDragListener( new DragSourceAdapter() {
      @Override
      public void dragSetData( final DragSourceEvent event ) {
        event.data = list.getSelection();
      }
      @Override
      public void dragFinished( final DragSourceEvent event ) {
        if( event.detail == DND.DROP_MOVE ) {
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
      @Override
      public void drop( final DropTargetEvent event ) {
        String[] items = ( String[] )event.data;
        for( int i = 0; i < items.length; i++ ) {
          list.add( items[ i ] );
        }
      }
    } );
  }

  private Button createButton( Composite parent, String text, String image ) {
    Button result = new Button( parent, SWT.PUSH | SWT.LEFT );
    result.setText( text );
    result.setImage( ImageUtil.getImage( result.getDisplay(), image )  );
    return result;
  }

  private static void moveSelectedItems( List srcList, List dstList ) {
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

  private static void sortItems( List srcList ) {
    String[] items = srcList.getItems();
    Arrays.sort( items );
    srcList.setItems( items );
  }

  ////////////////
  // Inner classes

  private static final class StringArrayTransfer extends ByteArrayTransfer {

    private static final String TYPE_NAME = "string-array";
    private static final int TYPE_ID = registerType( TYPE_NAME );

    @Override
    protected int[] getTypeIds() {
      return new int[] { TYPE_ID };
    }

    @Override
    protected String[] getTypeNames() {
      return new String[] { TYPE_NAME };
    }

    @Override
    public void javaToNative( final Object object,
                              final TransferData transferData )
    {
      transferData.data = object;
    }

    @Override
    public Object nativeToJava( final TransferData transferData ) {
      return transferData.data;
    }
  }
}
