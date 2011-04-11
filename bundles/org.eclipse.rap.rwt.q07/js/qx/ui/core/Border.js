/*******************************************************************************
 *  Copyright: 2004, 2011 1&1 Internet AG, Germany, http://www.1und1.de,
 *                        and EclipseSource
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    1&1 Internet AG and others - original API and implementation
 *    EclipseSource - adaptation for the Eclipse Rich Ajax Platform
 ******************************************************************************/

qx.Class.define( "qx.ui.core.Border", {
  extend : qx.core.Object,

  construct : function( width, style, color ) {
    this.base( arguments );
    if( width !== undefined ) {
      this.setWidth( width );
    }
    if( style !== undefined ) {
      this.setStyle( style );
    }
    if( color !== undefined ) {
      this.setColor( color );
    }
  },

  properties : {

    widthTop : {
      check : "Number",
      init : 0,
      apply : "_applyWidthTop"
    },

    widthRight : {
      check : "Number",
      init : 0,
      apply : "_applyWidthRight"
    },

    widthBottom : {
      check : "Number",
      init : 0,
      apply : "_applyWidthBottom"
    },

    widthLeft : {
      check : "Number",
      init : 0,
      apply : "_applyWidthLeft"
    },

    styleTop : {
      nullable : true,
      check : [ "solid", "dotted", "dashed", "double", "outset", "inset", "ridge", "groove" ],
      init : "solid"
    },

    styleRight : {
      nullable : true,
      check : [ "solid", "dotted", "dashed", "double", "outset", "inset", "ridge", "groove" ],
      init : "solid"
    },

    styleBottom : {
      nullable : true,
      check : [ "solid", "dotted", "dashed", "double", "outset", "inset", "ridge", "groove" ],
      init : "solid"
    },

    styleLeft : {
      nullable : true,
      check : [ "solid", "dotted", "dashed", "double", "outset", "inset", "ridge", "groove" ],
      init : "solid"
    },

    colorTop : {
      nullable : true,
      check : "Color",
      apply : "_applyColorTop"
    },

    colorRight : {
      nullable : true,
      check : "Color",
      apply : "_applyColorRight"
    },

    colorBottom : {
      nullable : true,
      check : "Color",
      apply : "_applyColorBottom"
    },

    colorLeft : {
      nullable : true,
      check : "Color",
      apply : "_applyColorLeft"
    },

    colorInnerTop : {
      nullable : true,
      check : "Color",
      apply : "_applyColorInnerTop"
    },

    colorInnerRight : {
      nullable : true,
      check : "Color",
      apply : "_applyColorInnerRight"
    },

    colorInnerBottom : {
      nullable : true,
      check : "Color",
      apply : "_applyColorInnerBottom"
    },

    colorInnerLeft : {
      nullable : true,
      check : "Color",
      apply : "_applyColorInnerLeft"
    },

    width : {
      group : [ "widthTop", "widthRight", "widthBottom", "widthLeft" ],
      mode : "shorthand"
    },

    style : {
      group : [ "styleTop", "styleRight", "styleBottom", "styleLeft" ],
      mode : "shorthand"
    },

    color : {
      group : [ "colorTop", "colorRight", "colorBottom", "colorLeft" ],
      mode : "shorthand"
    },

    innerColor : {
      group : [ "colorInnerTop", "colorInnerRight", "colorInnerBottom", "colorInnerLeft" ],
      mode : "shorthand"
    }
  },


  members : {

    _applyWidthTop : function(value, old) {
      this.__widthTop = value == null ? "0px" : value + "px";
      this.__computeComplexTop();
    },

    _applyWidthRight : function(value, old) {
      this.__widthRight = value == null ? "0px" : value + "px";
      this.__computeComplexRight();
    },

    _applyWidthBottom : function(value, old) {
      this.__widthBottom = value == null ? "0px" : value + "px";
      this.__computeComplexBottom();
    },

    _applyWidthLeft : function(value, old) {
      this.__widthLeft = value == null ? "0px" : value + "px";
      this.__computeComplexLeft();
    },

    _applyColorTop : function(value, old) {
      this.__colorTop = value;
      this.__computeComplexTop();
    },

    _applyColorRight : function(value, old) {
      this.__colorRight = value;
      this.__computeComplexRight();
    },
    
    _applyColorBottom : function(value, old) {
      this.__colorBottom = value;
      this.__computeComplexBottom();
    },

    _applyColorLeft : function(value, old) {
      this.__colorLeft = value;
      this.__computeComplexLeft();
    },

    _applyColorInnerTop : function(value, old) {
      this.__colorInnerTop = value;
      this.__computeComplexTop();
    },

    _applyColorInnerRight : function(value, old) {
      this.__colorInnerRight = value;
      this.__computeComplexRight();
    },

    _applyColorInnerBottom : function(value, old) {
      this.__colorInnerBottom = value;
      this.__computeComplexBottom();
    },

    _applyColorInnerLeft : function(value, old) {
      this.__colorInnerLeft = value;
      this.__computeComplexLeft();
    },

    __computeComplexTop : function() {
      this.__complexTop = this.getWidthTop() === 2 && this.__colorInnerTop != null && this.__colorTop != this.__colorInnerTop;
    },

    __computeComplexRight : function() {
      this.__complexRight = this.getWidthRight() === 2 && this.__colorInnerRight != null && this.__colorRight != this.__colorInnerRight;
    },

    __computeComplexBottom : function() {
      this.__complexBottom = this.getWidthBottom() === 2 && this.__colorInnerBottom != null && this.__colorBottom != this.__colorInnerBottom;
    },

    __computeComplexLeft : function() {
      this.__complexLeft = this.getWidthLeft() === 2 && this.__colorInnerLeft != null && this.__colorLeft != this.__colorInnerLeft;
    },

    renderTop : qx.core.Variant.select("qx.client", {
      "gecko" : function(obj) {
        var style = obj._style;
        style.borderTopWidth = this.__widthTop || "0px";
        style.borderTopColor = this.__colorTop || "";
        if (this.__complexTop) {
          style.borderTopStyle = "solid";
          style.MozBorderTopColors = this.__colorTop + " " + this.__colorInnerTop;
        } else {
          style.borderTopStyle = this.getStyleTop() || "none";
          style.MozBorderTopColors = "";
        }
      },
      "default" : function(obj) {
        var outer = obj._style;
        var inner = obj._innerStyle;
        if (this.__complexTop) {
          if (!inner) {
            obj.prepareEnhancedBorder();
            inner = obj._innerStyle;
          }
          outer.borderTopWidth = inner.borderTopWidth = "1px";
          outer.borderTopStyle = inner.borderTopStyle = "solid";
          outer.borderTopColor = this.__colorTop;
          inner.borderTopColor = this.__colorInnerTop;
        } else {
          outer.borderTopWidth = this.__widthTop || "0px";
          outer.borderTopStyle = this.getStyleTop() || "none";
          outer.borderTopColor = this.__colorTop || "";
          if (inner) {
            inner.borderTopWidth = inner.borderTopStyle = inner.borderTopColor = "";
          }
        }
      }
    } ),

    renderRight : qx.core.Variant.select("qx.client", {
      "gecko" : function(obj) {
        var style = obj._style;
        style.borderRightWidth = this.__widthRight || "0px";
        style.borderRightColor = this.__colorRight || "";
        if (this.__complexRight) {
          style.borderRightStyle = "solid";
          style.MozBorderRightColors = this.__colorRight + " " + this.__colorInnerRight;
        } else {
          style.borderRightStyle = this.getStyleRight() || "none";
          style.MozBorderRightColors = "";
        }
      },
      "default" : function(obj) {
        var outer = obj._style;
        var inner = obj._innerStyle;
        if (this.__complexRight) {
          if (!inner) {
            obj.prepareEnhancedBorder();
            inner = obj._innerStyle;
          }
          outer.borderRightWidth = inner.borderRightWidth = "1px";
          outer.borderRightStyle = inner.borderRightStyle = "solid";
          outer.borderRightColor = this.__colorRight;
          inner.borderRightColor = this.__colorInnerRight;
        } else {
          outer.borderRightWidth = this.__widthRight || "0px";
          outer.borderRightStyle = this.getStyleRight() || "none";
          outer.borderRightColor = this.__colorRight || "";
          if (inner) {
            inner.borderRightWidth = inner.borderRightStyle = inner.borderRightColor = "";
          }
        }
      }
    } ),

    renderBottom : qx.core.Variant.select("qx.client", {
      "gecko" : function(obj) {
        var style = obj._style;
        style.borderBottomWidth = this.__widthBottom || "0px";
        style.borderBottomColor = this.__colorBottom || "";
        if (this.__complexBottom) {
          style.borderBottomStyle = "solid";
          style.MozBorderBottomColors = this.__colorBottom + " " + this.__colorInnerBottom;
        } else {
          style.borderBottomStyle = this.getStyleBottom() || "none";
          style.MozBorderBottomColors = "";
        }
      },
      "default" : function(obj) {
        var outer = obj._style;
        var inner = obj._innerStyle;
        if (this.__complexBottom) {
          if (!inner) {
            obj.prepareEnhancedBorder();
            inner = obj._innerStyle;
          }
          outer.borderBottomWidth = inner.borderBottomWidth = "1px";
          outer.borderBottomStyle = inner.borderBottomStyle = "solid";
          outer.borderBottomColor = this.__colorBottom;
          inner.borderBottomColor = this.__colorInnerBottom;
        } else {
          outer.borderBottomWidth = this.__widthBottom || "0px";
          outer.borderBottomStyle = this.getStyleBottom() || "none";
          outer.borderBottomColor = this.__colorBottom || "";
          if (inner) {
            inner.borderBottomWidth = inner.borderBottomStyle = inner.borderBottomColor = "";
          }
        }
      }
    } ),

    renderLeft : qx.core.Variant.select("qx.client", {
      "gecko" : function(obj) {
        var style = obj._style;
        style.borderLeftWidth = this.__widthLeft || "0px";
        style.borderLeftColor = this.__colorLeft || "";
        if (this.__complexLeft) {
          style.borderLeftStyle = "solid";
          style.MozBorderLeftColors = this.__colorLeft + " " + this.__colorInnerLeft;
        } else {
          style.borderLeftStyle = this.getStyleLeft() || "none";
          style.MozBorderLeftColors = "";
        }
      },
      "default" : function(obj) {
        var outer = obj._style;
        var inner = obj._innerStyle;
        if (this.__complexLeft) {
          if (!inner) {
            obj.prepareEnhancedBorder();
            inner = obj._innerStyle;
          }
          outer.borderLeftWidth = inner.borderLeftWidth = "1px";
          outer.borderLeftStyle = inner.borderLeftStyle = "solid";
          outer.borderLeftColor = this.__colorLeft;
          inner.borderLeftColor = this.__colorInnerLeft;
        } else {
          outer.borderLeftWidth = this.__widthLeft || "0px";
          outer.borderLeftStyle = this.getStyleLeft() || "none";
          outer.borderLeftColor = this.__colorLeft || "";
          if (inner) {
            inner.borderLeftWidth = inner.borderLeftStyle = inner.borderLeftColor = "";
          }
        }
      }
    } )
  }

} );
