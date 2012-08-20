/*******************************************************************************
 * Copyright (c) 2009, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Mixin.define( "org.eclipse.rwt.test.fixture.RAPRequestPatch", {

  "members": {

    send : function() {
      if( this._requestCounter === -1 ) {
        throw new Error( "_requestCounter is -1" );
     }
      this.sendImmediate( true );
    },

    _createRequest : function() {
      var result = new org.eclipse.rwt.test.fixture.DummyRequest();
      result.setSuccessHandler( this._handleSuccess, this );
      return result;
    }

  }
} );
