/*******************************************************************************
 * Copyright (c) 2010, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.examples.pages;

import java.util.ArrayList;

import org.eclipse.rap.examples.ExampleUtil;
import org.eclipse.rap.examples.IExamplePage;
import org.eclipse.rap.examples.pages.internal.ImageUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;


public final class CanvasExamplePage implements IExamplePage {

  private static final int MODE_INIT = 0;
  private static final int MODE_POLYFORM = 1;
  private static final int MODE_OVAL = 2;
  private static final int MODE_CURVE = 3;
  private static final int MODE_STAMP = 4;

  private static final int SNAP_DISTANCE = 6;
  private static final int LINE_WIDTH = 1;

  private static final RGB[] COLORS = new RGB[] {
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

  private static final String ICON_POLYFORM = "polyform.png";
  private static final String ICON_OVAL = "oval.png";
  private static final String ICON_CURVE = "curve.png";
  private static final String ICON_STAMP = "stamp.png";
  private static final String ICON_TRANSPARENCY = "transparency.png";
  private static final String ICON_CLEAR = "clear.png";

  private Canvas drawingArea;
  private int mode;
  private ArrayList<Object[]> path;
  private int[] currentParam;
  private Point currentStart;
  private int currentColor;
  private Image stampImage;
  private int currentAlpha;

  public CanvasExamplePage() {
    path = new ArrayList<Object[]>();
    currentAlpha = 128;
  }

  public void createControl( Composite parent ) {
    parent.setLayout( ExampleUtil.createMainLayout( 1 ) );
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayoutData( ExampleUtil.createFillData() );
    composite.setLayout( ExampleUtil.createGridLayout( 2, false, true, true ) );
    ExampleUtil.createHeading( composite, "click to draw shapes", 2 );
    createControlToolBar( composite );
    createDrawingArea( composite );
    createStampImage( parent.getDisplay() );
    parent.layout();
    init();
 }

  private void createControlToolBar( Composite parent ) {
    ToolBar toolBar = new ToolBar( parent, SWT.VERTICAL );
    toolBar.setLayoutData( new GridData( SWT.BEGINNING, SWT.TOP, false, true ) );
    createPolyformButton( toolBar ).setSelection( true );
    createOvalButton( toolBar );
    createCurveButton( toolBar );
    createStampButton( toolBar );
    createSeparator( toolBar );
    createTransparencyButton( toolBar ).setSelection( true );
    createSeparator( toolBar );
    createClearButton( toolBar );
  }

  private ToolItem createPolyformButton( ToolBar toolBar ) {
    ToolItem button = createToolButton( toolBar, SWT.RADIO, ICON_POLYFORM, "Polyform" );
    button.addListener( SWT.Selection, new Listener() {
      public void handleEvent( Event event ) {
        if( ( ( ToolItem )event.widget ).getSelection() ) {
          setMode( MODE_POLYFORM );
        }
      }
    } );
    return button;
  }

  private void createOvalButton( ToolBar toolBar ) {
    ToolItem button = createToolButton( toolBar, SWT.RADIO, ICON_OVAL, "Oval" );
    button.addListener( SWT.Selection, new Listener() {
      public void handleEvent( Event event ) {
        if( ( ( ToolItem )event.widget ).getSelection() ) {
          setMode( MODE_OVAL );
        }
      }
    } );
  }

  private void createCurveButton( ToolBar toolBar ) {
    ToolItem button = createToolButton( toolBar, SWT.RADIO, ICON_CURVE, "Curved Line" );
    button.addListener( SWT.Selection, new Listener() {
      public void handleEvent( Event event ) {
        if( ( ( ToolItem )event.widget ).getSelection() ) {
          setMode( MODE_CURVE );
        }
      }
    } );
  }

  private void createStampButton( ToolBar toolBar ) {
    ToolItem button = createToolButton( toolBar, SWT.RADIO, ICON_STAMP, "Stamp" );
    button.addListener( SWT.Selection, new Listener() {
      public void handleEvent( Event event ) {
        if( ( ( ToolItem )event.widget ).getSelection() ) {
          setMode( MODE_STAMP );
        }
      }
    } );
  }

  private ToolItem createTransparencyButton( ToolBar toolBar ) {
    ToolItem button = createToolButton( toolBar, SWT.CHECK, ICON_TRANSPARENCY, "Transparency" );
    button.addListener( SWT.Selection, new Listener() {
      public void handleEvent( Event event ) {
        boolean selected = ( ( ToolItem )event.widget ).getSelection();
        currentAlpha = selected ? 128 : 255;
      }
    } );
    return button;
  }

  private void createClearButton( ToolBar toolBar ) {
    ToolItem toolButton = createToolButton( toolBar, SWT.PUSH, ICON_CLEAR, "Clear" );
    toolButton.addListener( SWT.Selection, new Listener() {
      public void handleEvent( Event event ) {
        clear();
      }
    } );
  }

  private static ToolItem createToolButton( ToolBar parent, int style, String icon, String tooltip )
  {
    ToolItem toolButton = new ToolItem( parent, style );
    toolButton.setImage( ImageUtil.getImage( parent.getDisplay(), icon ) );
    toolButton.setToolTipText( tooltip );
    return toolButton;
  }

  private void createSeparator( ToolBar toolBar ) {
    new ToolItem( toolBar, SWT.SEPARATOR );
  }

  private void createDrawingArea( Composite parent ) {
    drawingArea = new Canvas( parent, SWT.BORDER );
    drawingArea.setLayoutData( ExampleUtil.createFillData() );
    drawingArea.setBackground( parent.getDisplay().getSystemColor( SWT.COLOR_WHITE ) );
    drawingArea.setCursor( parent.getDisplay().getSystemCursor( SWT.CURSOR_CROSS ) );
    drawingArea.addPaintListener( new DrawingAreaPaintListener() );
    drawingArea.addMouseListener( new DrawingAreaMouseListener() );
  }

  private void createStampImage( Display display ) {
    stampImage = ImageUtil.getImage( drawingArea.getDisplay(), "rap.png" );
  }

  private void setMode( int mode ) {
    newOperation();
    this.mode = mode;
  }

  private void addToCurrentParam( int x, final int y ) {
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
    Object fill = getCurrentFill();
    switch( mode ) {
      case MODE_OVAL:
        int x0 = currentParam[ 0 ];
        int y0 = currentParam[ 1 ];
        int x1 = currentParam[ 2 ];
        int y1 = currentParam[ 3 ];
        currentParam = new int[]{ x0, y0, x1 - x0, y1 - y0 };
      break;
      case MODE_STAMP:
        Image stamp = ( Image )fill;
        currentParam[ 0 ] -= stamp.getBounds().width / 2;
        currentParam[ 1 ] -= stamp.getBounds().height / 2;
      break;
    }
    Object[] arg = new Object[] {
      new Integer( mode ),
      currentParam,
      fill,
      new Integer( currentAlpha )
    };
    path.add( arg );
  }

  private Object getCurrentFill() {
    Object result;
    switch( mode ) {
      case MODE_STAMP:
        result = stampImage;
      break;
      default:
        result = new Color( drawingArea.getDisplay(), COLORS[ currentColor ] );
        currentColor++;
        if( currentColor >= COLORS.length ) {
          currentColor = 0;
        }
      break;
    }
    return result;
  }

  private void clear() {
    path = new ArrayList<Object[]>();
    newOperation();
  }

  private void init() {
    path = new ArrayList<Object[]>();
    newOperation();
    mode = MODE_INIT;
    addOperationToPath();
    newOperation();
    mode = MODE_POLYFORM;
  }

  private void newOperation() {
    currentParam = null;
    currentStart = null;
    drawingArea.redraw();
  }

  private boolean isNearStart( int x, final int y ) {
    boolean result = false;
    if( currentStart != null ) {
      int diffX = Math.abs( currentStart.x - x );
      int diffY = Math.abs( currentStart.y - y );
      result = diffX < SNAP_DISTANCE && diffY < SNAP_DISTANCE;
    }
    return result;
  }

  private void drawStartPoint( GC gc ) {
    int x = currentStart.x - SNAP_DISTANCE;
    int y = currentStart.y - SNAP_DISTANCE;
    int diameter = 2 * SNAP_DISTANCE;
    gc.setForeground( gc.getDevice().getSystemColor( SWT.COLOR_BLACK ) );
    gc.setBackground( gc.getDevice().getSystemColor( SWT.COLOR_WHITE ) );
    gc.setLineWidth( LINE_WIDTH );
    gc.fillOval( x, y, diameter, diameter );
    gc.drawOval( x, y, diameter, diameter );
  }

  private final class DrawingAreaPaintListener implements PaintListener {
    public void paintControl( PaintEvent event ) {
      GC gc = event.gc;
      for( int i = 0; i < path.size(); i++ ) {
        Object[] operation = path.get( i );
        int operationMode = ( ( Integer )operation[ 0 ] ).intValue();
        int[] param = ( int[] )operation[ 1 ];
        gc.setForeground( gc.getDevice().getSystemColor( SWT.COLOR_BLACK ) );
        gc.setLineWidth( LINE_WIDTH );
        gc.setAlpha( ( ( Integer )operation[ 3 ] ).intValue() );
        switch( operationMode ) {
          case MODE_INIT:
            gc.setBackground( new Color( drawingArea.getDisplay(), COLORS[ 4 ] ) );
            gc.fillOval( 100, 50, 350, 350 );
            gc.setBackground( new Color( drawingArea.getDisplay(), COLORS[ 5 ] ) );
            gc.fillOval( 300, 150, 400, 300 );
            gc.setBackground( new Color( drawingArea.getDisplay(), COLORS[ 6 ] ) );
            gc.fillOval( 500, 100, 250, 300 );
          break;
          case MODE_POLYFORM:
            gc.setBackground( ( Color )operation[ 2 ] );
            gc.fillPolygon( param );
          break;
          case MODE_OVAL:
            gc.setBackground( ( Color ) operation[ 2 ] );
            gc.fillOval( param[ 0 ], param[ 1 ], param[ 2 ], param[ 3 ] );
          break;
          case MODE_CURVE:
            gc.setForeground( ( Color ) operation[ 2 ] );
            gc.setLineWidth( 3 );
            gc.setAlpha( 255 );
            gc.drawPath( createCurvedPath( drawingArea.getDisplay(), param ) );
          break;
          case MODE_STAMP:
            gc.drawImage( ( Image )operation[ 2 ], param[ 0 ], param[ 1 ] );
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
          case MODE_CURVE:
            gc.setForeground( gc.getDevice().getSystemColor( SWT.COLOR_BLACK ) );
            gc.setLineWidth( LINE_WIDTH );
            gc.drawPath( createCurvedPath( gc.getDevice(), currentParam ) );
          break;
        }
      }
    }
  }

  private final class DrawingAreaMouseListener extends MouseAdapter {
    @Override
    public void mouseDown( MouseEvent e ) {
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
        case MODE_CURVE:
          addToCurrentParam( e.x, e.y );
          if( currentParam.length == 8 ) {
            addOperationToPath();
            newOperation();
          }
        break;
        case MODE_STAMP:
          addToCurrentParam( e.x, e.y );
          addOperationToPath();
          newOperation();
        break;
      }
      drawingArea.redraw();
    }
  }

  public static Path createCurvedPath( Device device, int[] param ) {
    Path path = new Path( device );
    if( param.length >= 2 ) {
      path.moveTo( param[ 0 ], param[ 1 ] );
      for( int i = 2; i < param.length; i += 2 ) {
        int cx = param[ i - 2 ];
        int cy = param[ i - 1 ];
        int x = ( param[ i ] + param[ i - 2 ] ) / 2;
        int y = ( param[ i + 1 ] + param[ i - 1 ] ) / 2;
        path.quadTo( cx, cy, x, y );
      }
      path.lineTo( param[ param.length - 2 ], param[ param.length - 1] );
    }
    return path;
  }

}
