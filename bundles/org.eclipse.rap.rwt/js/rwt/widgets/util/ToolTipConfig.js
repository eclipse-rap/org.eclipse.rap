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
        case "rwt.widgets.Text":
        case "rwt.widgets.Spinner":
        case "rwt.widgets.Label":
          return this._textLikeConfig;
        case "rwt.widgets.ToolItem":
          if( widget.hasState( "rwt_VERTICAL" ) ) {
            return this._verticalConfig;
          } else {
            return this._horizontalConfig;
          }
        break;
        default:
          return this._defaultConfig;
      }

    },

    _defaultConfig : {
      "position" : "mouse",
      "appearOn" : "rest",
      "disappearOn" : "move",
      "appearDelay" : 800,
      "disappearDelay" : 200
    },

    _horizontalConfig : {
      "position" : "horizontal-center",
      "appearOn" : "enter",
      "disappearOn" : "exit",
      "appearDelay" : 200,
      "disappearDelay" : 100
    },

    _verticalConfig : {
      "position" : "vertical-center",
      "appearOn" : "enter",
      "disappearOn" : "exit",
      "appearDelay" : 200,
      "disappearDelay" : 100
    },

    _textLikeConfig : {
      "position" : "align-left",
      "appearOn" : "enter",
      "disappearOn" : "exit",
      "appearDelay" : 800,
      "disappearDelay" : 200
    }


  };

}());