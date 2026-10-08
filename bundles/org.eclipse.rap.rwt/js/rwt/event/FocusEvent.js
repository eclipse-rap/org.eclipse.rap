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
 * This event handles all focus events.
 *
 *  The four supported types are:
 *  1+2: focus and blur also propagate the target object
 *  3+4: focusout and focusin are bubbling to the parent objects
 */
rwt.qx.Class.define("rwt.event.FocusEvent",
{
  extend : rwt.event.Event,

  construct : function(type, target)
  {
    this.base(arguments, type);

    this.setTarget(target);

    switch(type)
    {
      case "focusin":
      case "focusout":
        this.setBubbles(true);
        this.setPropagationStopped(false);
    }
  }
});
