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

(function(){

rwt.qx.Class.createNamespace( "rwt.scripting", {} );

/**
 * @private
 * @class RWT Scripting analoge to org.eclipse.swt.widgets.Composite and basis for Custom Widgets.
 * @description The constructor is not public.
 * @exports rwt.scripting.CompositeProxy as Composite
 * @since 2.0
 */
 rwt.scripting.CompositeProxy = function( composite ) {
  var children = null;
  if( !composite.isCreated() ) {
    children = [];
    composite.addEventListener( "create", function() {
      for( var i = 0; i < children.length; i++ ) {
        composite._getTargetNode().appendChild( children[ i ] );
      }
      composite.removeEventListener( "create", arguments.callee );
      children = null;
    } );
  }
  /**
   * @name append
   * @methodOf Composite#
   * @description Adds a given HTMLElement to the Composite.
   * @param {HTMLElement} childElement The element to append.
   */
  this.append = function( childElement ) {
    if( children ) {
      children.push( childElement );
    } else {
      composite._getTargetNode().appendChild( childElement );
    }
  };
  /**
   * @name getClientArea
   * @methodOf Composite#
   * @description Returns the client Area of the Composite
   * @returns {int[]} the client area as array [ x, y, width, height ]
   */
  this.getClientArea = function() {
    return composite.getClientArea();
  };

  /**
   * @name addListener
   * @methodOf Composite#
   * @description Register the function as a listener of the given type
   * @param {string} type The type of the event (e.g. "Resize").
   * @param {Function} listener The callback function. It is executed in global context.
   */
  this.addListener = function( type, listener ) {
    composite.addEventListener( convertEventType( type ), listener, window );
  };

  /**
   * @name removeListener
   * @methodOf Composite#
   * @description De-register the function as a listener of the given type
   * @param {string} type The type of the event (e.g. "Resize").
   * @param {Function} listener The callback function
   */
  this.removeListener = function( type, listener ) {
    composite.removeEventListener( convertEventType( type ), listener, window );
  };

};

/**
 * @event
 * @description Sent when widget changes size.
 * @name Composite#Resize
 */
function convertEventType( type ) {
  var result;
  if( type === "Resize" ) {
    result = "clientAreaChanged"; // works only for Composite
  } else {
    throw new Error( "Unkown event type " + type );
  }
  return result;
}

}());
