/*******************************************************************************
 * Copyright (c) 2025 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

var points = [];
var lineWidth = 1;
var foreground = "#000000";
var alpha = 1.0;
var isButtonPressed = false;

var handleEvent = function( event ) {
  switch( event.type ) {
    case SWT.MouseDown:
      isButtonPressed = true;
      points = [];
    break;
    case SWT.MouseUp:
      isButtonPressed = false;
      notifyRemoteObject( event );
    break;
    case SWT.MouseMove:
      if( isButtonPressed ) {
        points.push( [ event.x, event.y ] );
        event.widget.redraw();
      }
    break;
    case SWT.Paint:
      if( isButtonPressed && points.length > 1 ) {
        lineWidth = event.gc.lineWidth;
        foreground = event.gc.strokeStyle;
        alpha = event.gc.globalAlpha;
        event.gc.beginPath();
        event.gc.moveTo( points[ 0 ][ 0 ], points[ 0 ][ 1 ] );
        for( var i = 1; i < points.length; i++ ) {
          event.gc.lineTo( points[ i ][ 0 ] , points[ i ][ 1 ] );
        }
        event.gc.stroke();
      }
    break;
  }
};

var notifyRemoteObject = function( event ) {
  var clientCanvasId = event.widget.getData( "clientCanvas" );
  var clientCanvas = rwt.remote.ObjectRegistry.getEntry( clientCanvasId ).object;
  var remoteObject = rap.getRemoteObject( clientCanvas );
  remoteObject.isListening = rwt.util.Functions.returnTrue;
  remoteObject.notify( "Drawing", createDrawingProperties() );
};

var createDrawingProperties = function() {
  var drawings = [];
  var rgba = rwt.util.Colors.stringToRgb( foreground );
  rgba[ 3 ] = Math.ceil( alpha * 255 );
  drawings.push( [ "foreground", rgba ] );
  drawings.push( [ "lineWidth", [ lineWidth ] ] );
  drawings.push( [ "path", points.flat() ] );
  return { drawings: JSON.stringify( drawings ) };
};
