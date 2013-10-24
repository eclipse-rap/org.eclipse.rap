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

  var regexp = /^[0-9]*$/;
  var text = event.widget.getText();
  if( text.match( regexp ) === null ) {
    event.widget.setBackground( [ 255, 255, 128 ] );
    event.widget.setToolTipText( "Only digits allowed!" );
  } else {
    event.widget.setBackground( null );
    event.widget.setToolTipText( null );
  }

};
