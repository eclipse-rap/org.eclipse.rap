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
 * @class RWT Scripting analoge to org.eclipse.swt.widgets.Composite
 * @description This constructor is not available in the global namespace. Instances can only
 * be obtained from {@link rap.getObject}.
 * @name Composite
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
   * @function
   * @memberOf Composite#
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
   * @function
   * @memberOf Composite#
   * @description Returns the client Area of the Composite
   * @returns {int[]} the client area as array [ x, y, width, height ]
   */
  this.getClientArea = function() {
    return composite.getClientArea();
  };

  /**
   * @name addListener
   * @function
   * @memberOf Composite#
   * @description Register the function as a listener of the given type
   * @param {string} type The type of the event (e.g. "Resize").
   * @param {Function} listener The callback function. It is executed in global context.
   */
  this.addListener = function( type, listener ) {
    composite.addEventListener( convertEventType( type ), listener, window );
  };

  /**
   * @name removeListener
   * @function
   * @memberOf Composite#
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
