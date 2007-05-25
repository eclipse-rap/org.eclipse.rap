
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
    
    initialize : function( widget, wrap ) {
      widget.setVerticalChildrenAlign( qx.constant.Layout.ALIGN_TOP );
      widget.setHorizontalChildrenAlign( qx.constant.Layout.ALIGN_LEFT );
      widget.setAppearance( org.eclipse.swt.LabelUtil.APPEARANCE );
      widget.setOverflow( qx.constant.Style.OVERFLOW_HIDDEN );
      // TODO [rh] workaround for weird getLabelObject behaviour
      widget.setLabel( "(empty)" );
      // end workaround
      var labelObject = widget.getLabelObject();
      labelObject.setMode( org.eclipse.swt.LabelUtil.MODE_TEXT );
      labelObject.setOverflow( qx.constant.Style.OVERFLOW_HIDDEN );
      labelObject.setTextOverflow( false );
      widget.getLabelObject().setWrap( wrap );
      // TODO [rh] workaround for weird getLabelObject behaviour
      widget.setLabel( "" );
      // end workaround
      widget.setHideFocus( true );
    },
    
    setText : function( widget, text ) {
      widget.setLabel( text );
      widget.setShow( org.eclipse.swt.LabelUtil.SHOW_LABEL );
    },

    setImage : function( widget, imagePath ) {
      widget.setIcon( imagePath );
      // TODO [rst] could speed up rendering if available on the server side
      // widget.setIconWidth();
      // widget.setIconHeight();
      if( imagePath != null ) {
        widget.setShow( org.eclipse.swt.LabelUtil.SHOW_ICON );
      } else {
        widget.setShow( org.eclipse.swt.LabelUtil.SHOW_LABEL );
      }
    },

    // TODO [rh] workaround for weird getLabelObject behaviour
    setAlignment : function( widget, align ) {
      if( widget.getLabelObject() != null ) {
        widget.getLabelObject().setTextAlign( align );
      } else {
        var oldLabel = widget.getLabel();
        widget.setLabel( "(empty)" );
        widget.getLabelObject().setTextAlign( align );
        widget.setLabel( oldLabel );
      }
      if( !widget.getLabelObject().getWrap() ) {
        widget.setHorizontalChildrenAlign( align );
      }
    }
  }
});
