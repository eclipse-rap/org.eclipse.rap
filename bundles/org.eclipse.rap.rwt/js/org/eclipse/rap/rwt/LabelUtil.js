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
 */
qx.OO.defineClass( "org.eclipse.rap.rwt.LabelUtil" );

org.eclipse.rap.rwt.LabelUtil.setText = function( widget, text ) {
  // Weird feature of qx.ui.basic.Atom:
  // getLabelObject() returns null until the label property was set to a non-
  // empty string.
  if( widget.getLabelObject() != null ) {
    widget.getLabelObject().setHtml( text );
  } else {
    widget.setLabel( "(empty)" );
    widget.getLabelObject().setHtml( text );
  }
  widget.setShow( qx.ui.basic.Atom.SHOW_LABEL );
}

org.eclipse.rap.rwt.LabelUtil.setImage = function( widget, imagePath ) {
  widget.setIcon( imagePath );
  // could speed up rendering if available on the server side
  // widget.setIconWidth();
  // widget.setIconHeight();
  if( imagePath != null ) {
    widget.setShow( qx.ui.basic.Atom.SHOW_ICON );
  } else {
    widget.setShow( qx.ui.basic.Atom.SHOW_LABEL );
  }
}

org.eclipse.rap.rwt.LabelUtil.setWrap = function( widget, wrap ) {
  if( widget.getLabelObject() != null ) {
    widget.getLabelObject().setWrap( wrap );
  } else {
    widget.setLabel( "(empty)" );
    widget.getLabelObject().setWrap( wrap );
    widget.getLabelObject().setHtml( "" );
  }
}

org.eclipse.rap.rwt.LabelUtil.setAlignment = function( widget, align ) {
  if( widget.getLabelObject() != null ) {
    widget.getLabelObject().setTextAlign( align );
  } else {
    var oldLabel = widget.getLabel();
    widget.setLabel( "(empty)" );
    widget.getLabelObject().setTextAlign( align );
    widget.setLabel( oldLabel );
  }
  widget.setHorizontalChildrenAlign( align );
}
