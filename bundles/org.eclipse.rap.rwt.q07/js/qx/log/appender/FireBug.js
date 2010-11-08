/*******************************************************************************
 *  Copyright: 2006, 2010 David Perez and EclipseSource
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    David Perez and others - original API and implementation
 *    EclipseSource - adaptation for the Eclipse Rich Ajax Platform
 ******************************************************************************/
 
 /**
 * An appender that writes all messages to FireBug, a nice extension for debugging and developing under Firefox.
 * <p>
 * This class does not depend on qooxdoo widgets, so it also works when there
 * are problems with widgets or when the widgets are not yet initialized.
 * </p>
 */
qx.Class.define("qx.log.appender.FireBug",
{
  extend : qx.log.appender.Abstract,




  /*
  *****************************************************************************
     CONSTRUCTOR
  *****************************************************************************
  */

  construct : function() {
    this.base(arguments);
  },




  /*
  *****************************************************************************
     MEMBERS
  *****************************************************************************
  */

  members :
  {

    // overridden
    appendLogEvent : function(evt)
    {
      if (typeof console != 'undefined')
      {
        var log = qx.log.Logger;
        var msg = this.formatLogEvent(evt);

        switch(evt.level)
        {
          case log.LEVEL_DEBUG:
            if (console.debug) {
              console.debug(msg);
            } else if (console.log) {
              console.log(msg);
            }

            break;

          case log.LEVEL_INFO:
            if (console.info) {
              console.info(msg);
            }

            break;

          case log.LEVEL_WARN:
            if (console.warn) {
              console.warn(msg);
            }

            break;

          default:
            if (console.error) {
              console.error(msg);
            }

            break;
        }

        // Force a stack dump, for helping locating the error
        if (evt.level >= log.LEVEL_WARN && (!evt.throwable || !evt.throwable.stack) && console.trace) {
          console.trace();
        }
      }
    }
  }
});
