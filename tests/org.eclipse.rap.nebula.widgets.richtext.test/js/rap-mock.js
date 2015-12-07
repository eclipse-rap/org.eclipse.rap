/*******************************************************************************
 * Copyright (c) 2016 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

RapMock = function() {

  this.fakeRemoteObject = {
    set : function(){},
    notify : function(){},
    call : function(){}
  };

  this.fakeComposite = {
    append : function( node ){
      document.createElement( "div" ).appendChild( node );
    },
    addListener : function(){},
    removeListener : function(){},
    getClientArea : function(){ return [ 0, 0, 0, 0 ]; }
  };

};

RapMock.prototype = {

  on: function() {},

  off: function() {},

  registerTypeHandler : function() {},

  getObject : function() {
    return this.fakeComposite;
  },

  getRemoteObject : function() {
    return this.fakeRemoteObject;
  }

};

rap = new RapMock();

rwt = {

  widgets: {},

  define: function() {}

};
