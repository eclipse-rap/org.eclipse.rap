/*******************************************************************************
 * Copyright (c) 2010 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.examples.pages;

import java.util.ArrayList;

import org.eclipse.rap.examples.IExamplePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.*;


public final class CanvasExample implements IExamplePage {

  private static final int MODE_POLYFORM = 0;
  private static final int MODE_OVAL = 1;

  private static final int SNAP_DISTANCE = 10;
  private static final int LINE_WIDTH = 2;

  private static final RGB[] COLORS = new RGB[]{
    new RGB( 21,184,185 ),
    new RGB( 102,169,58 ),
    new RGB( 71,110,188 ),
    new RGB( 251,113,189 ),
    new RGB( 144,202,215 ),
    new RGB( 254,207,21 ),
    new RGB( 255,83,22 ),
    new RGB( 182,199,66 ),
    new RGB( 254,159,169 ),
    new RGB( 159,122,171 ),
    new RGB( 66,187,134 )
  };

  private Display display;
  private Canvas drawingArea;
  private int mode;
  private ArrayList path;
  private int[] currentParam;
  private Point currentStart;
  private int currentColor;
  private int currentAlpha;


  public CanvasExample() {
    path = new ArrayList();
    currentAlpha = 255;
  }


  public void createControl( final Composite parent ) {
    display = parent.getDisplay();
    parent.setLayout( ExampleUtil.createGridLayout( 1, true, 10, 10 ) );
    createDrawingArea( parent );
    createControlButtons( parent );
    parent.layout();
    clear();
 }

  private void createDrawingArea( final Composite parent ) {
    Label label = new Label( parent, SWT.NONE );
    label.setText( "Drawing Area" );
    GridData labelLayout = new GridData( SWT.LEFT, SWT.TOP, false, false );
    label.setLayoutData( labelLayout );
    drawingArea = new Canvas( parent, SWT.BORDER );
    GridData areaLayout = new GridData( SWT.FILL, SWT.FILL, true, true );
    drawingArea.setLayoutData( areaLayout );
    drawingArea.setBackground( display.getSystemColor( SWT.COLOR_WHITE ) );
    drawingArea.addPaintListener( new DrawingAreaPaintListener() );
    drawingArea.addMouseListener( new DrawingAreaMouseListener() );
  }

  private void createControlButtons( final Composite parent ) {
    Group group = new Group( parent, SWT.NONE );
    GridData gridData = new GridData( SWT.FILL, SWT.BOTTOM, true, false );
    group.setLayoutData( gridData );
    group.setLayout( ExampleUtil.createGridLayout( 5, false, 10, 10 ) );
    Button polyformButton = new Button( group, SWT.RADIO );
    polyformButton.setText( "Polyform" );
    polyformButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent e ) {
        newOperation();
        mode = MODE_POLYFORM;
      }
    } );
    Button ovalButton = new Button( group, SWT.RADIO );
    ovalButton.setText( "Oval" );
    ovalButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent e ) {
        newOperation();
        mode = MODE_OVAL;
      }
    } );
    final Button transparencyButton = new Button( group, SWT.CHECK );
    transparencyButton.setText( "Transparency" );
    transparencyButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent e ) {
        boolean selected = transparencyButton.getSelection();
        currentAlpha = selected ? 128 : 255;
      }
    } );
    Button clearButton = new Button( group, SWT.PUSH );
    clearButton.setText( "Clear" );
    clearButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent e ) {
        clear();
      }
    } );
    group.layout();
    polyformButton.setSelection( true );
  }

  private void addToCurrentParam( final int x, final int y ) {
    if( currentParam == null ) {
      currentParam = new int[]{ x, y };
      currentStart = new Point( x, y );
    } else {
      int length = currentParam.length;
      int[] newParam = new int[ length + 2 ];
      System.arraycopy( currentParam, 0, newParam, 0, length );
      newParam[ length ] = x;
      newParam[ length + 1 ] = y;
      currentParam = newParam;
    }
  }

  private void addOperationToPath() {
    if( mode == MODE_OVAL ) {
      int centerX = currentParam[ 0 ];
      int centerY = currentParam[ 1 ];
      int radiusX = Math.abs( centerX - currentParam[ 2 ] );
      int radiusY = Math.abs( centerY - currentParam[ 3 ] );
      currentParam = new int[]{
        centerX - radiusX,
        centerY - radiusY,
        radiusX * 2,
        radiusY * 2
      };
    }
    Object[] arg = new Object[] {
      new Integer( mode ),
      currentParam,
      new Color( display, COLORS[ currentColor ] ),
      new Integer( currentAlpha )
    };
    path.add( arg );
    currentColor++;
    if( currentColor >= COLORS.length ) {
      currentColor = 0;
    }
  }

  private void clear() {
    path = new ArrayList();
    newOperation();
  }

  private void newOperation() {
    currentParam = null;
    currentStart = null;
    drawingArea.redraw();
  }

  private boolean isNearStart( final int x, final int y ) {
    boolean result = false;
    if( currentStart != null ) {
      int diffX = Math.abs( currentStart.x - x );
      int diffY = Math.abs( currentStart.y - y );
      result = diffX < SNAP_DISTANCE && diffY < SNAP_DISTANCE;
    }
    return result;
  }

  private void drawStartPoint( final GC gc ) {
    int x = currentStart.x - SNAP_DISTANCE;
    int y = currentStart.y - SNAP_DISTANCE;
    int diameter = 2 * SNAP_DISTANCE;
    gc.setBackground( display.getSystemColor( SWT.COLOR_WHITE ) );
    gc.fillOval( x, y, diameter, diameter );
    gc.drawOval( x, y, diameter, diameter );

  }

  private final class DrawingAreaPaintListener implements PaintListener {
    public void paintControl( final PaintEvent event ) {
      GC gc = event.gc;
      gc.setLineWidth( LINE_WIDTH );
      for( int i = 0; i < path.size(); i++ ) {
        Object[] operation = ( Object[] )path.get( i );
        int operationMode = ( ( Integer )operation[ 0 ] ).intValue();
        int[] param = ( int[] )operation[ 1 ];
        gc.setBackground( ( Color ) operation[ 2 ] );
        gc.setAlpha( ( ( Integer )operation[ 3 ] ).intValue() );
        switch( operationMode ) {
          case MODE_POLYFORM:
            gc.fillPolygon( param );
            gc.setAlpha( 255 );
            gc.drawPolygon( param );
          break;
          case MODE_OVAL:
            gc.fillOval( param[ 0 ], param[ 1 ], param[ 2 ], param[ 3 ] );
            gc.setAlpha( 255 );
            gc.drawOval( param[ 0 ], param[ 1 ], param[ 2 ], param[ 3 ] );
          break;
        }
      }
      if( currentParam != null ) {
        gc.setAlpha( 255 );
        drawStartPoint( gc );
        switch( mode ) {
          case MODE_POLYFORM:
            gc.drawPolyline( currentParam );
          break;
        }
      }
    }
  }

  private final class DrawingAreaMouseListener extends MouseAdapter {
    public void mouseDown( final MouseEvent e ) {
      switch( mode ) {
        case MODE_POLYFORM:
          if( isNearStart( e.x, e.y ) ) {
            addOperationToPath();
            newOperation();
          } else {
            addToCurrentParam( e.x, e.y );
          }
        break;
        case MODE_OVAL:
          addToCurrentParam( e.x, e.y );
          if( currentParam.length == 4 ) {
            addOperationToPath();
            newOperation();
          }
        break;
      }
      drawingArea.redraw();
    }
  }
}
