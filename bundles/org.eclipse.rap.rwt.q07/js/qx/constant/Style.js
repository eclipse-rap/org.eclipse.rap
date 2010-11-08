/*******************************************************************************
 *  Copyright: 2002, 2010 1&1 Internet AG, Germany, http://www.1und1.de,
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

qx.Class.define( "qx.constant.Style", {

  statics : {
    POSITION_ABSOLUTE       : "absolute",
    POSITION_RELATIVE       : "relative",
    POSITION_STATIC         : "static",
    POSITION_FIXED          : "fixed",
    CURSOR_WAIT             : "wait",
    CURSOR_PROGRESS         : "progress",
    CURSOR_DEFAULT          : "default",
    CURSOR_HAND             : "pointer",
    OVERFLOW_AUTO           : "auto",
    OVERFLOW_HIDDEN         : "hidden",
    OVERFLOW_BOTH           : "scroll",
    OVERFLOW_HORIZONTAL     : "scrollX",
    OVERFLOW_VERTICAL       : "scrollY",
    OVERFLOW_ELLIPSIS       : "ellipsis",
    OVERFLOW_VISIBLE        : "visible",
    OVERFLOW_MOZ_NONE       : "-moz-scrollbars-none",
    OVERFLOW_MOZ_HORIZONTAL : "-moz-scrollbars-horizontal",
    OVERFLOW_MOZ_VERTICAL   : "-moz-scrollbars-vertical",
    FOCUS_OUTLINE           : "1px dotted invert",

    // from here on are RWT extensions
    BORDER_SOLID            : "solid",
    
    // Constant for Label#setMode 
    LABEL_MODE_HTML : "html",
    // Constant for qx.ui.toolbar.Button#setShow 
    BUTTON_SHOW_ICON : "icon"
  }
});
