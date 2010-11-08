/*******************************************************************************
 *  Copyright: 2006, 2010 STZ-IDA, Germany, http://www.stz-ida.de
 *                        and EclipseSource
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    STZ-IDA and others - original API and implementation
 *    EclipseSource - adaptation for the Eclipse Rich Ajax Platform
 ******************************************************************************/
 
/**
 * An appender that writes all messages to the best possible target in
 * this client e.g. it uses Firebug in Firefox browsers.
 */
qx.Class.define("qx.log.appender.Native",
{
  extend : qx.log.appender.Abstract,




  /*
  *****************************************************************************
     CONSTRUCTOR
  *****************************************************************************
  */

  construct : function()
  {
    this.base(arguments);

    if (typeof console != 'undefined' && (console.debug || console.log) && !console.emu) {
      this._appender = new qx.log.appender.FireBug;
    } else {
      this._appender = new qx.log.appender.Window;
    }
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
      if (this._appender) {
        return this._appender.appendLogEvent(evt);
      }
    }
  },




  /*
  *****************************************************************************
     DESTRUCTOR
  *****************************************************************************
  */

  destruct : function() {
    this._disposeObjects("_appender");
  }
});
