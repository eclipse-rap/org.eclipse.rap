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
 * The default filter. Has a minimum level and can be enabled or disabled.
 */
qx.Class.define("qx.log.DefaultFilter",
{
  extend : qx.log.Filter,




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
     PROPERTIES
  *****************************************************************************
  */

  properties :
  {
    /**
     * Whether the filter should be enabled. If set to false all log events
     * will be denied.
     */
    enabled :
    {
      check : "Boolean",
      init : true
    },


    /**
     * The minimum log level. If set only log messages with a level greater or equal
     * to the set level will be accepted.
     */
    minLevel :
    {
      check : "Number",
      nullable : true
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
    /**
     * @return {Integer} TODOC
     */
    decide : function(evt)
    {
      var Filter = qx.log.Filter;

      if (!this.getEnabled()) {
        return Filter.DENY;
      } else if (this.getMinLevel() == null) {
        return Filter.NEUTRAL;
      } else {
        return (evt.level >= this.getMinLevel()) ? Filter.ACCEPT : Filter.DENY;
      }
    }
  }
});
