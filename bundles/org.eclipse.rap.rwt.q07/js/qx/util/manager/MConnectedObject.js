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
 * Every object, which whishes to establish a connection to a dynamic value
 * using the value manager must implement the method <code>hasConnectionTo</code>.
 *
 * This mixin provides an implementation for this method and automatically
 * takes care of disconnecting all connections to value managers on object
 * dispose.
 */
qx.Mixin.define("qx.util.manager.MConnectedObject",
{
  /*
  *****************************************************************************
     MEMBERS
  *****************************************************************************
  */

  members :
  {
    /**
     * This method is used by value managers {@link qx.util.manager.Value} to
     * notify the object that a connection to a dynamic value has been
     * established.
     *
     * @param valueManager {qx.util.manager.Value} Value manager, which is connected
     *     to this object.
     */
    hasConnectionTo : function(valueManager)
    {
      if (!this._valueManager) {
        this._valueManager = {};
      }
      this._valueManager[valueManager.toHashCode()] = valueManager;
    }
  },


  /*
  *****************************************************************************
     DESTRUCTOR
  *****************************************************************************
  */

  destruct : function()
  {
    for (var key in this._valueManager)
    {
      var valueManager = this._valueManager[key];
      valueManager.disconnect(this);
    }

    this._disposeFields("_valueManager");
  }
});