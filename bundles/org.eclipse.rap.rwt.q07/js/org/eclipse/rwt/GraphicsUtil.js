/*******************************************************************************
 * Copyright (c) 2010, 2011 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.GraphicsUtil", {

  statics : {
    _renderClass : null,
    
    init : function() {
      if( this._renderClass == null ) {
        if( org.eclipse.rwt.Client.supportsVml() ) {
          this._renderClass = org.eclipse.rwt.VML;
        } else if ( org.eclipse.rwt.Client.supportsSvg() ) {
          this._renderClass = org.eclipse.rwt.SVG;
        }
        if( this._renderClass != null ) {
          this._renderClass.init();
        }
      }
    },
    
    ///////
    // Core
    
    /**
     * Returns a handle for a canvas. This objects members are considered
     * package-private and should not be accessed outside the renderclass.
     * The canvas dimensions will be those of its parent-node.
     * The overflow-behavior is browser-dependant. To enforce a 
     * "overflow:hidden"-behavior, set it on the parent-node of the canvas. 
     */
    createCanvas : function() {
      var result = null;
      result = this._renderClass.createCanvas();
      return result;
    },

    /**
     * Returns the DOM-node for the given canvas to be added to or removed from
     * HTML DOM-nodes. Returns null if no renderClass is found!
     */
    getCanvasNode : function( canvas ) {
      var result = null;
      result = this._renderClass.getCanvasNode( canvas );
      return result;      
    },
    
    /**
     * This must be called after the canvas has been (directly or inderectly)
     * inserted into the DOM, i.e. after it becomes visible. It has not to 
     * be called if the canvas was made visible by other means, e.g. the
     * "visibility" or "opacity" properties, but it won't do any damage.
     */
    handleAppear : function( canvas ) {
      this._renderClass.handleAppear( canvas );
    },
    
    /**
     * Returns a handle for a shape. This objects members are considered
     * package-private and should not be accessed outside the renderclass. 
     * Currently supported types: "rect", "roundrect"
     */
    createShape : function( type ) {
      var result = null;
      if( this._renderClass != null ) {
        result = this._renderClass.createShape( type );
      }
      return result;      
    },
    
    // TODO [tb] : There might currently be a glitch in IE if a shape is added
    //             to an alreadey visible canvas.
    addToCanvas : function( canvas, shape ) {
      this._renderClass.addToCanvas( canvas, shape );
    },
    
    removeFromCanvas : function( canvas, shape ) {
      this._renderClass.removeFromCanvas( canvas, shape );
    },
    
    ////////////
    // Layouting
    
    /**
     * value is a boolean
     */
    setDisplay : function( shape, value ) {
      this._renderClass.setDisplay( shape, value );
    },

    /**
     * returns a boolean
     */
    getDisplay : function( shape ) {
      return this._renderClass.getDisplay( shape );
    },
    
    /**
     * shape must be of type "rect"
     * all other values must be a number (for pixels) or a string with 
     * "%"-postfix for percentage, depending on the layout-mode of the canvas. 
     * If the layout-mode and the format of the value don't match, the shape
     * might be layouted incorrectly.
     * width and height must be positive or 0.
     * Initial values are all 0. 
     */
    setRectBounds : function( shape, x, y, width, height ) {
      this._renderClass.setRectBounds( shape, x, y, width, height );
    },

    /**
     * radii is an array: [ topLeft, topRight, bottomRight, bottomLeft ] 
     * Other paramters as described in "setRectBounds".
     * Using this function in layout-mode "percentage" is not tested.
     * 
     * If the shape is geometrically impossible to draw becuse the 
     * the sum of the radii of any two opposite corners is larger than
     * the corresponding edge, a normal rectagle will be drawn instead
     * (i.e. ALL radii are 0).  
     */    
    setRoundRectLayout : function( shape, x, y, width, height, radii ) {
      this._renderClass.setRoundRectLayout( shape, 
                                            x, 
                                            y, 
                                            width, 
                                            height, 
                                            radii );
    },

    //////////
    // Styling

    /**
     * color is any rgb-value or null (transparent).
     * Initial value is null.
     */
    setFillColor : function( shape, color ) {
      this._renderClass.setFillColor( shape, color );
    },
    
    /**
     * returns a string or null
     */
    getFillColor : function( shape, color ) {
      return this._renderClass.getFillColor( shape );
    },
    
    /**
     * gradient is a two dimensional array [ [ offset, color ] ] or null.
     * the array can also have a "horizontal" boolean as a field, creating
     * a horizontal instead of a vertical gradient if set to true.
     * offset is a number between 0 and 1
     * Iniital value is null (transparent). 
     */
    setFillGradient : function( shape, gradient ) {
      this._renderClass.setFillGradient( shape, gradient );
    },
    
    /**
     * source is a valid URL of an image or null
     * width and height are numbers representing the dimension of image in pixel
     */
    setFillPattern : function( shape, source, width, height ) {
      this._renderClass.setFillPattern( shape, source, width, height );
    },

    /**
     * Returns "color", "gradient", "pattern" or null
     */
    getFillType : function( shape, color ) {
      return this._renderClass.getFillType( shape );
    },

    /**
     * color is any rgb-value or null (transparent)
     * width is any positive number or 0.
     * Initial values are null and 0.
     * Note that strokes are (unlike css-borders) not part of the shapes
     * geometric model, but drawn centered along the shapes path.
     */
    setStroke : function( shape, color, width ) {
      this._renderClass.setStroke( shape, color, width );
    },
    
    /**
     * returns the width of the stroke as a number (pixel)
     */
    getStrokeWidth : function( shape ) {
      return this._renderClass.getStrokeWidth( shape );      
    },
    
    /**
     * opaciy is a value between 0 and 1. 
     */
    setOpacity : function( shape, opacity ) {
      this._renderClass.setOpacity( shape, opacity );
    }

  }

} );