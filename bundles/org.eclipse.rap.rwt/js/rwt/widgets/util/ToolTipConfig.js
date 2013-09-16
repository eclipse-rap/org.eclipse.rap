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
      if( widget.getParent() instanceof rwt.widgets.CoolBar ) {
        if( widget.getParent().hasState( "rwt_VERTICAL" ) ) {
          return this._verticalConfig;
        } else {
          return this._horizontalConfig;
        }
      }
      if( widget instanceof rwt.widgets.Label && !widget._rawText ) {
        return this._horizontalConfig;
      }
      switch( widget.classname ) {
        case "rwt.widgets.Text":
        case "rwt.widgets.Spinner":
        case "rwt.widgets.Button":
        case "rwt.widgets.Label":
        case "rwt.widgets.Combo":
        case "rwt.widgets.DateTimeDate":
        case "rwt.widgets.DateTimeTime":
        case "rwt.widgets.DateTimeCalendar":
          return this._formConfig;
        case "rwt.widgets.ToolItem":
        case "rwt.widgets.ProgressBar":
        case "rwt.widgets.ControlDecorator":
        case "rwt.widgets.Scale":
        case "rwt.widgets.Slider":
          if( widget.hasState( "rwt_VERTICAL" ) ) {
            return this._verticalConfig;
          } else {
            return this._horizontalConfig;
          }
        break;
        case "rwt.widgets.base.GridRow":
          return this._rowConfig;
        default:
          return this._defaultConfig;
      }

    },

    _defaultConfig : {
      "position" : "mouse",
      "appearOn" : "rest",
      "disappearOn" : "move",
      "appearDelay" : 1100,
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

    _formConfig : {
      "position" : "align-left",
      "appearOn" : "rest",
      "disappearOn" : "exit",
      "appearDelay" : 700,
      "disappearDelay" : 200
    },

    _rowConfig : {
      "position" : "align-left",
      "appearOn" : "rest",
      "disappearOn" : "move",
      "appearDelay" : 500,
      "disappearDelay" : 200
    }


  };

}());