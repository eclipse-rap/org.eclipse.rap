/*******************************************************************************
 * Copyright (c) 2011, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

(function(){

namespace( "rwt.client" );

  rwt.widgets.util.ToolTipConfig = {

    getConfig : function( widget ) {
      switch( widget.classname ) {
        case "rwt.widgets.ToolItem":
          return this._horizontalConfig;
        default:
          return this._defaultConfig;
      }

    },

    _defaultConfig : {
      "position" : "mouse"
    },

    _horizontalConfig : {
      "position" : "horizontal-center"
    }

  };

}());