/*******************************************************************************
 * Copyright (c) 2012, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

var handleEvent = function( event ) {
  switch( event.type ) {
    case SWT.Modify:
      handleModifyEvent( event );
    break;
    case SWT.Verify:
      handleVerifyEvent( event );
    break;
  }
};

var handleVerifyEvent = function( event ) {

  var input = event.text;
  var text = event.widget.getText();
  var start = event.start;
  var end = event.end;
  var left = text.slice( 0, start );
  var right = text.slice( end );
  var result = left + input + right;
  var points = result.split( "." ).length - 1;

  event.doit = input.match( /^\d$|^\.$|^$/ ) !== null; // allow digits, points, or empty

  if( input !== "" ) { // deleting allows everything

    if( event.doit && result.match( /\d{3,}/ ) !== null ) { // more than 2 numbers together
      event.doit = result.match( /^\d{0,2}\.\d{0,2}\.\d{3,}/ ) !== null; // are only valid after two points
    }

    if( event.doit && result.match( /\d{5,}/ ) !== null ) {
      event.doit = false; // no more than 4 numbers together
    }

    if( event.doit && ( points > 2 || result.match( /\.\./ )  ) ) {
      event.doit = false; // only 2 points, never together
    }

  }

};

var handleModifyEvent = function( event ) {
  var text = event.widget.getText();
  var valid = text.match( /^\d{1,2}\.\d{1,2}\.\d{1,4}$/ ) !== null;
  if( text === "" || valid ) {
    event.widget.setBackground( null );
  } else {
    event.widget.setBackground( [ 255, 255, 128 ] );
  }
};
