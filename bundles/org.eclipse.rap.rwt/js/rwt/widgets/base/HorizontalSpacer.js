/*******************************************************************************
 * Copyright (c) 2004, 2013 1&1 Internet AG, Germany, http://www.1und1.de,
 *                          EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    1&1 Internet AG and others - original API and implementation
 *    EclipseSource - adaptation for the Eclipse Remote Application Platform
 ******************************************************************************/

/**
 * This widget can be used to create a horizontal spacing between
 * widgets in e.g. a {@link rwt.widgets.base.HorizontalBoxLayout} or in
 * a menu or toolbar.
 *
 * By default it tries to occupy the all the remaining space by setting
 * a flex width of <code>1*</code>.
 */
rwt.qx.Class.define("rwt.widgets.base.HorizontalSpacer",
{
  extend : rwt.widgets.base.Terminator,

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
