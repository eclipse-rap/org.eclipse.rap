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
 * @appearance menu-separator
 * @appearance menu-separator-line {qx.ui.basic.Terminator}
 */
qx.Class.define("qx.ui.menu.Separator",
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

    this.initHeight();

    // Fix IE Styling Issues
    this.setStyleProperty("fontSize", "0");
    this.setStyleProperty("lineHeight", "0");

    // ************************************************************************
    //   LINE
    // ************************************************************************
    this._line = new qx.ui.basic.Terminator;
    this._line.setAnonymous(true);
    this._line.setAppearance("menu-separator-line");
    this.add(this._line);

    // ************************************************************************
    //   EVENTS
    // ************************************************************************
    // needed to stop the event, and keep the menu showing
    this.addEventListener("mousedown", this._onmousedown);
  },




  /*
  *****************************************************************************
     PROPERTIES
  *****************************************************************************
  */

  properties :
  {
    height :
    {
      refine : true,
      init : "auto"
    },

    appearance :
    {
      refine : true,
      init : "menu-separator"
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
     * Returns <code>false</code> to clarify that the Separator widget has no icon
     *
     * @signature function()
     * @return {Boolean} false
     */
    hasIcon : qx.lang.Function.returnFalse,

    /**
     * Returns <code>false</code> to clarify that the Separator widget has no label
     *
     * @signature function()
     * @return {Boolean} false
     */
    hasLabel : qx.lang.Function.returnFalse,

    /**
     * Returns <code>false</code> to clarify that the Separator widget has no shortcut
     *
     * @signature function()
     * @return {Boolean} false
     */
    hasShortcut : qx.lang.Function.returnFalse,

    /**
     * Returns <code>false</code> to clarify that the Separator widget has no sub menu
     *
     * @signature function()
     * @return {Boolean} false
     */
    hasMenu : qx.lang.Function.returnFalse,


    /**
     * Callback method for the "mouseDown" event<br/>
     * Simply stops the propagation of the event
     *
     * @type member
     * @param e {qx.event.type.MouseEvent} mouseDown event
     * @return {void}
     */
    _onmousedown : function(e) {
      e.stopPropagation();
    }
  },




  /*
  *****************************************************************************
     DESTRUCTOR
  *****************************************************************************
  */

  destruct : function() {
    this._disposeObjects("_line");
  }
});
