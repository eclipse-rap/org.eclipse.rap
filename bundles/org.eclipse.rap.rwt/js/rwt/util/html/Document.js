/*******************************************************************************
 *  Copyright: 2004, 2012 1&1 Internet AG, Germany, http://www.1und1.de,
 *                        and EclipseSource
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    1&1 Internet AG and others - original API and implementation
 *    EclipseSource - adaptation for the Eclipse Rich Ajax Platform
 *
 *  This class contains code based on the following work:
 *
 *  * Yahoo! UI Library
 *      http://developer.yahoo.com/yui
 *      Version 2.2.0
 *
 *    Copyright:
 *      (c) 2007, Yahoo! Inc.
 *
 *    License:
 *      BSD: http://developer.yahoo.com/yui/license.txt
 *
 ******************************************************************************/

/**
 * Includes library functions to work with the current document.
 */
rwt.qx.Class.define("rwt.util.html.Document",
{
  statics :
  {


    /**
     * Whether the document is in quirks mode (e.g. non XHTML, HTML4 Strict or missing doctype)
     *
     * @type static
     * @param win {Window?window} The window to query
     * @return {Boolean} true when containing document is in quirks mode
     */
    isStandardMode : function(win) { // TODO : use Client.js
      return (win||window).document.compatMode === "CSS1Compat";
    }


  }
});
