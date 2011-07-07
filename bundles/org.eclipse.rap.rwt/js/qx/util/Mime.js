/*******************************************************************************
 *  Copyright: 2004, 2010 1&1 Internet AG, Germany, http://www.1und1.de,
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

/**
 * Mime type constants
 */
qx.Class.define("qx.util.Mime",
{
  statics :
  {
    JAVASCRIPT : "text/javascript",

    /** this has been changed from text/json to application/json */
    JSON       : "application/json",
    XML        : "application/xml",
    TEXT       : "text/plain",
    HTML       : "text/html"
  }
});
