/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/

org.eclipse.rwt.test.fixture.Message = function( jsonString ) {
  if( typeof jsonString !== "string" ) {
    throw new Error( "jsonString must be a string" );
  }
  try {
    this._message = JSON.parse( jsonString );
  } catch( ex ) {
    throw new Error( "Could not parse json: " + ( ex.message ? ex.message : ex ) );
  }
  if( !( this._message.operations instanceof Array ) ) {
    throw new Error( "Missing operations array" );
  }
  this._operations = this._message.operations;
};

org.eclipse.rwt.test.fixture.Message.prototype = {

  getOperationCount : function() {
    return this._operations.length;
  },

  getOperation : function( index ) {
    var result = null;
    var op = this._operations[ index ];
    if( op ) {
      result = {
          "type" : op[ 0 ],
          "target" : op[ 1 ]
      };
      if( result.type === "set" ) {
        result.properties = op[ 2 ];
      } else if( result.type === "notify" ) {
        result.eventType = op[ 2 ];
        result.properties = op[ 3 ];
      }
    }
    return result;
  },

  findSetOperation : function( target, property ) {
    var result = null;
    for( var i = 0; i < this.getOperationCount(); i++ ) {
      var op = this.getOperation( i );
      if(    op.type === "set"
          && op.target === target
          && typeof op.properties[ property ] !== "undefined" )
      {
        result = op;
      }
    }
    return result;
  },

  findSetProperty : function( target, property ) {
    var op = this.findSetOperation( target, property );
    return op.properties[ property ];
  },

  findNotifyOperation : function( target, eventType ) {
    var result = null;
    for( var i = 0; i < this.getOperationCount(); i++ ) {
      var op = this.getOperation( i );
      if(    op.type === "notify"
          && op.target === target
          && op.eventType === eventType )
      {
        result = op;
      }
    }
    return result;
  },

  findNotifyProperty : function( target, eventType, property ) {
    var op = this.findNotifyOperation( target, eventType );
    return op.properties[ property ];
  },

  getMeta : function() {
    return this._message.meta;
  },

  toString : function() {
    return JSON.stringify( this._message );
  }

};