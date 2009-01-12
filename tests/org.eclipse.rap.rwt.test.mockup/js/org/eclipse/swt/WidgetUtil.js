/*******************************************************************************
 * Copyright (c) 2007, 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

/**
 * This class contains static helper functions for widgets.
 */
qx.Class.define( "org.eclipse.swt.WidgetUtil", {

  statics : {

    setPropertyParam : function( widget, propertyName, propertyValue ) {
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      var id = widgetManager.findIdByWidget( widget );
      var req = org.eclipse.swt.Request.getInstance();
      req.addParameter( id + "." + propertyName, propertyValue );
    },

    /*
     * workaround for IE bug
     * div's have the height of the font even if they are empty
     */
    fixIEBoxHeight : function( widget ) {
      if( qx.core.Variant.isSet( "qx.client", "mshtml" ) ) {
        widget.setStyleProperty( "fontSize", "0" );
        widget.setStyleProperty( "lineHeight", "0" );
      }
    }
  }
});