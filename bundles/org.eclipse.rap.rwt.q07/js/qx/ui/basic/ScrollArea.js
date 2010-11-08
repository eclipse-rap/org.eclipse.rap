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
 * The CanvasLayout, which fires scroll events. Widgets which need to react on scroll
 * events should extend thie class.
 */
qx.Class.define("qx.ui.basic.ScrollArea",
{
  extend : qx.ui.layout.CanvasLayout,



  /*
  *****************************************************************************
     CONSTRUCTOR
  *****************************************************************************
  */

  construct : function()
  {
    this.base(arguments);

    this.__onscroll = qx.lang.Function.bindEvent(this._onscroll, this);
  },



  /*
  *****************************************************************************
     EVENTS
  *****************************************************************************
  */

  events :
  {
    /** Fired each time the widget gets scrolled. */
    "scroll" : "qx.event.type.Event"
  },



  /*
  *****************************************************************************
     MEMBERS
  *****************************************************************************
  */

  members :
  {
    _applyElement : function(value, old)
    {
      this.base(arguments, value, old);

      if (value)
      {
        // Register inline event
        if (qx.core.Variant.isSet("qx.client", "mshtml")) {
          value.attachEvent("onscroll", this.__onscroll);
        } else {
          value.addEventListener("scroll", this.__onscroll, false);
        }
      }
    },

    /**
     * Event handler for the scroll event
     *
     * @param e {Event} the event object
     */
    _onscroll : function(e)
    {
      this.createDispatchEvent("scroll");
      org.eclipse.rwt.EventHandlerUtil.stopDomEvent(e);
    }
  },



  /*
  *****************************************************************************
     DESTRUCTOR
  *****************************************************************************
  */

  destruct : function()
  {
    var el = this.getElement();

    if (el)
    {
      // Register inline event
      if (qx.core.Variant.isSet("qx.client", "mshtml")) {
        el.detachEvent("onscroll", this.__onscroll);
      } else {
        el.removeEventListener("scroll", this.__onscroll, false);
      }

      delete this.__onscroll;
    }
  }
});
