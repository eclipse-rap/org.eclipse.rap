/*******************************************************************************
 * Copyright (c) 2010 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     EclipseSource - initial API and implementation
 ******************************************************************************/

package org.eclipse.jface.tests.viewers;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.FocusCellOwnerDrawHighlighter;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.jface.viewers.TableViewerFocusCellManager;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public class Bug264226TableViewerTest extends ViewerTestCase {

	public Bug264226TableViewerTest( final String name ) {
	  super( name );
	}

	protected StructuredViewer createViewer( final Composite parent ) {
	  final TableViewer viewer = new TableViewer( parent, SWT.NONE );
	  viewer.setContentProvider( new ArrayContentProvider() );
	  CellEditor editor = new TextCellEditor( viewer.getTable() );
	  viewer.setCellEditors( new CellEditor[] { editor } );
	  viewer.setColumnProperties( new String[] { "0" } );
	  ColumnViewerEditorActivationStrategy activationStrategy
        = new ColumnViewerEditorActivationStrategy( viewer );
	  FocusCellOwnerDrawHighlighter highlighter
        = new FocusCellOwnerDrawHighlighter( viewer );
      TableViewerFocusCellManager focusManager
        = new TableViewerFocusCellManager( viewer, highlighter );
      int feature
        = ColumnViewerEditor.TABBING_HORIZONTAL
        | ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR;
      TableViewerEditor.create( viewer,
                                focusManager,
                                activationStrategy,
                                feature );
      new TableColumn( viewer.getTable(), SWT.NONE ).setWidth( 200 );
	  return viewer;
	}

	protected void setUpModel() {
	  // don't do anything here - we are not using the normal fModel and
	  // fRootElement
	}

	protected void setInput() {
	  String[] input = new String[ 10 ];
      for( int i = 0; i < input.length; i++ ) {
        input[ i ] = "item " + i;
      }
      getTableViewer().setInput( input );
	}

	private TableViewer getTableViewer() {
	  return ( TableViewer )fViewer;
	}

	public void testBug264226() {
	  TableViewer viewer = getTableViewer();
	  String[] input = ( String[] )viewer.getInput();
	  Table table = viewer.getTable();
	  IStructuredSelection selection
	    = new StructuredSelection( new Object[] { input[ 5 ] } );
	  viewer.setSelection( selection );
	  // Set SWTFocusCellManager focusCell by firing selection event
	  SelectionEvent event
	    = new SelectionEvent( table,
	                          table.getItem( 5 ),
	                          SelectionEvent.WIDGET_SELECTED );
	  event.processEvent();
	  // Simulate item 5 deleted
	  input = new String[ 9 ];
      for( int i = 0; i < 5; i++ ) {
        input[ i ] = "item " + i;
      }
      for( int i = 5; i < 9; i++ ) {
        input[ i ] = "item " + ( i + 1 );
      }
      viewer.setInput( input );
      String expected = "item 6";
      String text = table.getItem( 5 ).getText();
      assertEquals( expected, text );
	}
}
