/*******************************************************************************
 * Copyright (c) 2007, 2011 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
appearances = {
// BEGIN TEMPLATE //

  "scale" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      return {
        border : tv.getCssBorder( "Scale", "border" ),
        font : tv.getCssFont( "*", "font" ),
        textColor : tv.getCssColor( "*", "color" ),
        backgroundColor : tv.getCssColor( "Scale", "background-color" )
      }
    }
  },

  "scale-line" : {
    include : "image",

    style : function( states ) {
      var result = {};
      if( states.horizontal ) {
        result.left = org.eclipse.swt.widgets.Scale.PADDING;
        result.top = org.eclipse.swt.widgets.Scale.SCALE_LINE_OFFSET;
        result.source = "widget/scale/h_line.gif";
      } else {
        result.left = org.eclipse.swt.widgets.Scale.SCALE_LINE_OFFSET;
        result.top = org.eclipse.swt.widgets.Scale.PADDING;
        result.source = "widget/scale/v_line.gif";
      }
      return result;
    }
  },

  "scale-thumb" : {
    include : "atom",

    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {};
      if( states.horizontal ) {
        result.left = org.eclipse.swt.widgets.Scale.PADDING;
        result.top = org.eclipse.swt.widgets.Scale.THUMB_OFFSET;
        // TODO: make it themable
        result.width = 11;
        result.height = 21;
      } else {
        result.left = org.eclipse.swt.widgets.Scale.THUMB_OFFSET;
        result.top = org.eclipse.swt.widgets.Scale.PADDING;
        // TODO: make it themable
        result.width = 21;
        result.height = 11;
      }
      // TODO: add themable background-image (gradient)
      result.border = tv.getCssBorder( "Scale-Thumb", "border" );
      result.backgroundColor = tv.getCssColor( "Scale-Thumb", "background-color" );
      return result;
    }
  },

  "scale-min-marker" : {
    include : "image",

    style : function( states ) {
      var result = {};
      if( states.horizontal ) {
        result.left =   org.eclipse.swt.widgets.Scale.PADDING
                      + org.eclipse.swt.widgets.Scale.HALF_THUMB;
        result.source = "widget/scale/h_marker_big.gif";
      } else {
        result.top =   org.eclipse.swt.widgets.Scale.PADDING
                     + org.eclipse.swt.widgets.Scale.HALF_THUMB;
        result.source = "widget/scale/v_marker_big.gif";
      }
      return result;
    }
  },

  "scale-max-marker" : {
    include : "image",

    style : function( states ) {
      var result = {};
      if( states.horizontal ) {
        result.source = "widget/scale/h_marker_big.gif";
      } else {
        result.source = "widget/scale/v_marker_big.gif";
      }
      return result;
    }
  },

  "scale-middle-marker" : {
    include : "image",

    style : function( states ) {
      var result = {};
      if( states.horizontal ) {
        result.source = "widget/scale/h_marker_small.gif";
      } else {
        result.source = "widget/scale/v_marker_small.gif";
      }
      return result;
    }
  }

// END TEMPLATE //
};