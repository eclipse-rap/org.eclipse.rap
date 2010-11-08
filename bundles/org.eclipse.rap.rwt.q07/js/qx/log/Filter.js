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
 * A filter for log events.
 */
qx.Class.define("qx.log.Filter",
{
  extend : qx.core.Object,
  type : "abstract",




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
     STATICS
  *****************************************************************************
  */

  statics :
  {

    /** {int} Specifies that the log event is accepted. */
    ACCEPT  : 1,

    /** {int} Specifies that the log event is denied. */
    DENY    : 2,

    /** {int} Specifies that the filter is neutral to the log event. */
    NEUTRAL : 3
  },




  /*
  *****************************************************************************
     MEMBERS
  *****************************************************************************
  */

  members :
  {
    /**
     * Decidies whether a log event is accepted.
     *
     * @type member
     * @abstract
     * @param evt {Map} The event to check.
     * @return {Integer} {@link #ACCEPT}, {@link #DENY} or {@link #NEUTRAL}.
     * @throws the abstract function warning.
     */
    decide : function(evt) {
      throw new Error("decide is abstract");
    }
  }
});
