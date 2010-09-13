/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.KeyEventUtil",
{
  type : "singleton",
  extend : qx.core.Object,

  members : {

    cancelEvent : function() {
      this._getInstance().cancelEvent();
    },

    allowEvent : function() {
      this._getInstance().allowEvent();
    },
    
    intercept : function( eventType, keyCode, charCode, domEvent ) {
      var util = this._getInstance();
      return util.intercept( eventType, keyCode, charCode, domEvent );
    },

    _getInstance : function() {
      var util;
      if( qx.core.Variant.isSet( "qx.client", "gecko" ) ) {
        util = org.eclipse.rwt.AsyncKeyEventUtil.getInstance();
      } else {
        util = org.eclipse.rwt.SyncKeyEventUtil.getInstance();
      }
      return util;
    }

  }
} );

