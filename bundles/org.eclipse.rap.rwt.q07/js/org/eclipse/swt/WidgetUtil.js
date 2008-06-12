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
 * This class contains static functions for radio buttons and check boxes.
 */
qx.Class.define( "org.eclipse.swt.WidgetUtil", {

  statics : {
    
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