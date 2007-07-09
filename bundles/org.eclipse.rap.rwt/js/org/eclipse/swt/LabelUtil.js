
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
    
    MODE_TEXT : "html",
    
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
        labelObject.setMode( org.eclipse.swt.LabelUtil.MODE_TEXT );
        labelObject.setTextOverflow( false );
        labelObject.setAppearance( "label-graytext" );
        // TODO [rh] workaround for weird getLabelObject behaviour
        widget.setLabel( "" );
        // end workaround
        widget.setHideFocus( true );
      }
    },
    
    setWrap : function( widget, wrap ) {
      widget.getLabelObject().setWrap( wrap );
    },
    
    setText : function( widget, text ) {
      if ( text != null ) {
        widget.setLabel( text );
      } else {
        widget.resetLabel();
      }
      org.eclipse.swt.LabelUtil._showText( widget );
    },
    
    setImage : function( widget, imagePath ) {
      if( imagePath ) {
        widget.setIcon( imagePath );
        org.eclipse.swt.LabelUtil._showImage( widget );
      } else {
        widget.resetIcon();
        org.eclipse.swt.LabelUtil._showText( widget );
      }
    },
    
    _showText : function( widget ) {
        widget.setShow( org.eclipse.swt.LabelUtil.SHOW_LABEL );
        // TODO [rst] Workaround for recycled Atoms which do not clear their
        //            text label
        widget.getLabelObject().setVisibility( true );
    },
    
    _showImage : function( widget ) {
        widget.setShow( org.eclipse.swt.LabelUtil.SHOW_ICON );
        // TODO [rst] Workaround for recycled Atoms which do not clear their
        //            text label
        widget.getLabelObject().setVisibility( false );
    },
    
    setAlignment : function( widget, align ) {
      widget.getLabelObject().setTextAlign( align );
      widget.setHorizontalChildrenAlign( align );
    }
  }
});
