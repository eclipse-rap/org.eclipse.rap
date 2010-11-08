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
 * This event handles all focus events.
 *
 *  The four supported types are:
 *  1+2: focus and blur also propagate the target object
 *  3+4: focusout and focusin are bubbling to the parent objects
 */
qx.Class.define("qx.event.type.FocusEvent",
{
  extend : qx.event.type.Event,

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
