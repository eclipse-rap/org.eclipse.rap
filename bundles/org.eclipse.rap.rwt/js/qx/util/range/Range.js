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
 * This manager is used by all objects which needs ranges like qx.ui.form.Spinner, ...
 */
qx.Class.define("qx.util.range.Range",
{
  extend : qx.core.Target,



  /*
  *****************************************************************************
     EVENTS
  *****************************************************************************
  */

  events: {
    "change" : "qx.event.type.Event"
  },





  /*
  *****************************************************************************
     PROPERTIES
  *****************************************************************************
  */

  properties :
  {
    /** current value of the Range object */
    value :
    {
      check : "!isNaN(value)&&value>=this.getMin()&&value<=this.getMax()",
      nullable : true,
      event : "change",
      init : 0
    },

    /** maximum fraction digits */
    precision :
    {
      check : "Integer",
      nullable : true,
      event : "change",
      init : 0
    },

    /** minimal value of the Range object */
    min :
    {
      check : "Number",
      apply : "_applyMin",
      event : "change",
      init : 0
    },

    /** maximal value of the Range object */
    max :
    {
      check : "Number",
      apply : "_applyMax",
      event : "change",
      init : 100
    },

    /** whether the value should wrap around */
    wrap :
    {
      check : "Boolean",
      init : false
    }
  },




  /*
  *****************************************************************************
     MEMBERS
  *****************************************************************************
  */

  members :
  {
    /**
     * TODOC
     *
     * @type member
     * @param value {var} Current value
     * @param old {var} Previous value
     */
    _applyMax : function(value, old) {
      this.setValue(Math.min(this.getValue(), value));
    },

    /**
     * TODOC
     *
     * @type member
     * @param value {var} Current value
     * @param old {var} Previous value
     */
    _applyMin : function(value, old) {
      this.setValue(Math.max(this.getValue(), value));
    },

    limit : function(value)
    {
      var precision = this.getPrecision();
      if (precision != null)
        var mover = Math.pow(10, precision);

      if (this.getWrap()) {
        if (precision != null) {
          // round to the precision'th digit
          var value = Math.round(value * mover) / mover;
        }

        if (value < this.getMin()) {
          return (this.getMax() - (this.getMin() - value)) + 1;
        }
        if (value > this.getMax()) {
          return (this.getMin() + (value - this.getMax())) - 1;
        }
      }

      if (value < this.getMin()) {
        return this.getMin();
      }

      if (value > this.getMax()) {
        return this.getMax();
      }

      if (precision != null) {
        return Math.round(value * mover) / mover;
      } else {
        return value;
      }
    }
  }
});
