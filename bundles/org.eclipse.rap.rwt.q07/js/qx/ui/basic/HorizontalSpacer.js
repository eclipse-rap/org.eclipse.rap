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
 * This widget can be used to create a horizontal spacing between
 * widgets in e.g. a {@link qx.ui.layout.HorizontalBoxLayout} or in
 * a menu or toolbar.
 *
 * By default it tries to occupy the all the remaining space by setting
 * a flex width of <code>1*</code>.
 */
qx.Class.define("qx.ui.basic.HorizontalSpacer",
{
  extend : qx.ui.basic.Terminator,

  construct : function()
  {
    this.base(arguments);

    this.initWidth();
  },

  properties :
  {
    width :
    {
      refine : true,
      init : "1*"
    }
  }
});
