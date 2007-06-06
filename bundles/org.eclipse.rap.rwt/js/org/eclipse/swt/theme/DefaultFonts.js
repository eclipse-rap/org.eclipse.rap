/*******************************************************************************
 * Copyright (c) 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

qx.Theme.define( "org.eclipse.swt.theme.DefaultFonts",
{
  title : "RAP default fonts",
 
  fonts: {
//    "qx-font" : {
//      size: 11,
//      family: [ "Lucida Grande", "Tahoma", "sans-serif" ]
//    },
    
    // -- defaults --
    
    "default.font" : {
      size: 11,
      family: [ "Segoe UI", "Corbel", "Calibri", "Tahoma", "Lucida Sans Unicode", "sans-serif" ]
    },
 
    "bold.font" : {
      size: 11,
      family: [ "Segoe UI", "Corbel", "Calibri", "Tahoma", "Lucida Sans Unicode", "sans-serif" ],
      bold: true
    },
    
    // -- link --
    
    "link.href.font" : {
      size: 11,
      family: [ "Segoe UI", "Corbel", "Calibri", "Tahoma", "Lucida Sans Unicode", "sans-serif" ],
      decoration: "underline"
    },
    
    // -- button --
    
    "button.font" : {
      size: 11,
      family: [ "Segoe UI", "Corbel", "Calibri", "Tahoma", "Lucida Sans Unicode", "sans-serif" ]
    }
 
  }
});
