/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
 
/**
 * This class contains static functions needed for labels.
 * To represent an RWT Label object, the qx.ui.basic.Atom widget is used.
 */
qx.Class.define( "org.eclipse.swt.LabelUtil", {

  statics : {
    SHOW_LABEL : "label",
    SHOW_ICON : "icon",
    
    APPEARANCE : "label-wrapper",
    
    initialize : function( widget ) {
      widget.setHorizontalChildrenAlign( qx.constant.Layout.ALIGN_LEFT );
      if( !widget.getUserData( "pooled" ) ) {
        widget.setVerticalChildrenAlign( qx.constant.Layout.ALIGN_TOP );
        widget.setAppearance( org.eclipse.swt.LabelUtil.APPEARANCE );
        widget.setOverflow( qx.constant.Style.OVERFLOW_HIDDEN );
        // TODO [rh] workaround for weird getLabelObject behaviour
        widget.setLabel( "(empty)" );
        // end workaround
        var labelObject = widget.getLabelObject();
        labelObject.setMode( qx.constant.Style.LABEL_MODE_HTML );
        labelObject.setTextOverflow( false );
        labelObject.setAppearance( "label-graytext" );
        // TODO [rh] workaround for weird getLabelObject behaviour
        widget.setLabel( "" );
        // end workaround
        widget.setHideFocus( true );
        // track DOM insertion state
        widget.addEventListener( "beforeRemoveDom",
                                 org.eclipse.swt.LabelUtil._onRemoveDom );
        widget.addEventListener( "insertDom",
                                 org.eclipse.swt.LabelUtil._onInsertDom );
      }
    },
    
    _onRemoveDom : function( evt ) {
      var widget = evt.getTarget();
      widget._isInDOM = false;
    },
    
    _onInsertDom : function( evt ) {
      var widget = evt.getTarget();
      widget._isInDOM = true;
    },
    
    setWrap : function( widget, wrap ) {
      widget.getLabelObject().setWrap( wrap );
    },
    
    setAlignment : function( widget, align ) {
      widget.getLabelObject().setTextAlign( align );
      widget.setHorizontalChildrenAlign( align );
    },
    
    setText : function( widget, text ) {
      if( !widget.isCreated() ) {
        widget.setUserData( "setText", text );
        widget.addEventListener( "appear",
                                 org.eclipse.swt.LabelUtil._setTextDelayed );
      }
      // workaround for pooling problems
      else if( !widget._isInDOM && widget.getUserData( "pooled" ) ) {
        widget.setUserData( "setText", text );
        widget.addEventListener( "insertDom",
                                 org.eclipse.swt.LabelUtil._setTextDelayed );
      } else {
        org.eclipse.swt.LabelUtil._doSetText( widget, text );
      }
    },
    
    setImage : function( widget, imagePath ) {
      if( !widget.isCreated() ) {
        widget.setUserData( "setImage", imagePath );
        widget.addEventListener( "appear",
                                 org.eclipse.swt.LabelUtil._setImageDelayed );
      } else if( !widget._isInDOM ) {
        widget.setUserData( "setImage", imagePath );
        widget.addEventListener( "insertDom",
                                 org.eclipse.swt.LabelUtil._setImageDelayed );
      } else {
        org.eclipse.swt.LabelUtil._doSetImage( widget, imagePath );
      }
    },
    
    _setTextDelayed : function( evt ) {
      var widget = evt.getTarget();
      var text = widget.getUserData( "setText" );
      org.eclipse.swt.LabelUtil._doSetText( widget, text );
      widget.removeEventListener( "appear",
                                  org.eclipse.swt.LabelUtil._setTextDelayed );
      widget.removeEventListener( "insertDom",
                                  org.eclipse.swt.LabelUtil._setTextDelayed );
    },
    
    _setImageDelayed : function( evt ) {
      var widget = evt.getTarget();
      var imagePath = widget.getUserData( "setImage" );
      org.eclipse.swt.LabelUtil._doSetImage( widget, imagePath );
      widget.removeEventListener( "appear",
                                  org.eclipse.swt.LabelUtil._setImageDelayed );
      widget.removeEventListener( "insertDom",
                                  org.eclipse.swt.LabelUtil._setImageDelayed );
    },
    
    _doSetText : function( widget, text ) {
      if ( text != null ) {
        widget.setLabel( text );
      } else {
        // TODO [rst] widget.resetLabel() throws JS error
        widget.setLabel( "" );
      }
      widget.setShow( org.eclipse.swt.LabelUtil.SHOW_LABEL );
    },
    
    _doSetImage : function( widget, imagePath ) {
      if( imagePath ) {
        widget.setIcon( imagePath );
        widget.setShow( org.eclipse.swt.LabelUtil.SHOW_ICON );
      } else {
        widget.resetIcon();
        widget.setShow( org.eclipse.swt.LabelUtil.SHOW_LABEL );
      }
    }
  }
});
