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

var isValid = function( text ) {
  var result =    text.indexOf( " " ) === -1
               && text.indexOf( "@" ) === text.lastIndexOf( "@" );
  return result;
};

var isComplete = function( text ) {
  var regexp = /^\S+@\S+\.[a-zA-Z]{2,5}$/;
  return text.match( regexp ) !== null;
};

var handleEvent = function( event ) {
  var text = event.widget.getText();
  if( text === "" ) {
    event.widget.setBackground( null );
  } else if( !isValid( text ) ) {
    event.widget.setBackground( [ 255, 128, 128 ] );
  } else if( !isComplete( text ) ) {
    event.widget.setBackground( [ 255, 255, 128 ] );
  } else {
    event.widget.setBackground( null );
  }
};
