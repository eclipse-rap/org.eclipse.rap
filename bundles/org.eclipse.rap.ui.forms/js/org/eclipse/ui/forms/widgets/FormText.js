/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.ui.forms.widgets.FormText", {
  extend : qx.ui.layout.CanvasLayout,

  construct : function() {
    this.base( arguments );
    this.setAppearance( "formtext" );
    this._hyperlinks = new Array();
    this._hyperlinkMode = org.eclipse.ui.forms.widgets.FormText.UNDERLINE_ALWAYS;
    this._hyperlinkForeground = "#0000FF";
    this._hyperlinkActiveForeground = "#0000FF";
    this._hyperlinkBackground = null;
    this._hyperlinkActiveBackground = null;
  },

  destruct : function() {
    this.clearContent( true );
  },

  statics : {
    UNDERLINE_NEVER : 1,
    UNDERLINE_HOVER : 2,
    UNDERLINE_ALWAYS : 3
  },

  members : {

    createBullet : function( style, image, text, x, y, width, height ) {
      var bullet = new qx.ui.basic.Atom();
      bullet.setAppearance( "formtext-bullet" );
      switch( style ) {
        case 2:
          bullet.setLabel( text );
          break;
        case 3:
          bullet.setIcon( image );
          break;
        default:
          bullet.setIcon( image );
      }
      bullet.set( {
        top     : y,
        left    : x,
        width   : width,
        height  : height
      } );
      this.add( bullet );
    },

    createTextFragment : function( text,
                                   x, y, width, height,
                                   fontName, fontSize, bold, italic,
                                   color )
    {
      var textFragment = new qx.ui.basic.Label();
      textFragment.setAppearance( "formtext-text" );
      textFragment.set( {
        text    : text,
        top     : y,
        left    : x,
        width   : width,
        height  : height
      } );
      if( fontName != null && fontSize != null && bold != null && italic != null ) {
        var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
        widgetManager.setFont( textFragment, fontName, fontSize, bold, italic );
      }
      if( color != null ) {
        textFragment.setTextColor( color );
      }
      this.add( textFragment );
    },

    createImageSegment : function( source, x, y, width, height ) {
      var image = new qx.ui.basic.Image();
      image.setAppearance( "formtext-image" );
      image.set( {
        source  : source,
        top     : y,
        left    : x,
        width   : width,
        height  : height
      } );
      this.add( image );
    },

    createTextHyperlinkSegment : function( text, toolTip,
                                           x, y, width, height,
                                           fontName, fontSize, bold, italic )
    {
      var textHyperlink = new qx.ui.basic.Label();
      textHyperlink.setAppearance( "formtext-hyperlink" );
      textHyperlink.set( {
        text    : text,
        top     : y,
        left    : x,
        width   : width,
        height  : height
      } );
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      widgetManager.setToolTip( textHyperlink, toolTip );
      if( fontName != null && fontSize != null && bold != null && italic != null ) {
        widgetManager.setFont( textHyperlink, fontName, fontSize, bold, italic );
      }
      textHyperlink.addEventListener( "mousemove", this._onMouseMove, this );
      textHyperlink.addEventListener( "mouseout", this._onMouseOut, this );
      this._hyperlinks[ this._hyperlinks.length ] = textHyperlink;
      this.add( textHyperlink );
    },

    createImageHyperlinkSegment : function( source, toolTip,
                                            x, y, width, height )
    {
      var imageHyperlink = new qx.ui.basic.Image();
      imageHyperlink.setAppearance( "formtext-hyperlink" );
      imageHyperlink.set( {
        source      : source,
        top         : y,
        left        : x,
        width       : width,
        height      : height,
        paddingTop  : 2
      } );
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      widgetManager.setToolTip( imageHyperlink, toolTip );
      this.add( imageHyperlink );
    },

    createControlSegment : function( id ) {
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      var control = widgetManager.findWidgetById( id );
      if( control != null ) {
        this.add( control );
      }
    },

    setHyperlinkSettings : function( mode, foreground, activeForeground ) {
      this._hyperlinkMode = mode;
      if( foreground != null ) {
        this._hyperlinkForeground = foreground;
      }
      if( activeForeground != null ) {
        this._hyperlinkActiveForeground = activeForeground;
      }
      this.updateHyperlinks();
    },

    updateHyperlinks : function() {
      for( var i = 0; i < this._hyperlinks.length; i++ ) {
        this._hyperlinks[ i ].setTextColor( this._hyperlinkForeground );
        if( this._hyperlinkMode == org.eclipse.ui.forms.widgets.FormText.UNDERLINE_ALWAYS ) {
          this._hyperlinks[ i ].setStyleProperty( "textDecoration", "underline");
        }
      }
    },

    clearContent : function( destruct ) {
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      var children = this.getChildren();
      for( var i = 0; i < this._hyperlinks.length; i++ ) {
        this._hyperlinks[ i ].removeEventListener( "mousemove",
                                                   this._onMouseMove,
                                                   this );
        this._hyperlinks[ i ].removeEventListener( "mouseout",
                                                   this._onMouseOut,
                                                   this );
      }
      if( !destruct ) {
        this._hyperlinks = new Array();
        this.removeAll();
      }
      for( var i = 0; i < children.length; i++ ) {
        var id = widgetManager.findIdByWidget( children[i] );
        if( id == null ) {
          children[i].dispose();
        }
      }
    },

    _onMouseMove : function( evt ) {
      var hyperlink = evt.getTarget();
      if( !this.hasState( "hover" ) ) {
        this.addState( "hover" );
        hyperlink.setTextColor( this._hyperlinkActiveForeground );
        if( this._hyperlinkMode == org.eclipse.ui.forms.widgets.FormText.UNDERLINE_HOVER ) {
          hyperlink.setStyleProperty( "textDecoration", "underline");
        }
      }
    },

    _onMouseOut : function( evt ) {
      var hyperlink = evt.getTarget();
      if( this.hasState( "hover" ) ) {
        this.removeState( "hover" );
        hyperlink.setTextColor( this._hyperlinkForeground );
        if( this._hyperlinkMode == org.eclipse.ui.forms.widgets.FormText.UNDERLINE_HOVER ) {
          hyperlink.setStyleProperty( "textDecoration", "none");
        }
      }
    }
  }
} );
