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

/** This singleton selects the icon theme to use. */
qx.Class.define("qx.theme.manager.Icon",
{
  type : "singleton",
  extend : qx.core.Target,





  /*
  *****************************************************************************
     PROPERTIES
  *****************************************************************************
  */

  properties :
  {
    /** currently used icon theme */
    iconTheme :
    {
      check : "Theme",
      nullable : true,
      apply : "_applyIconTheme",
      event : "changeIconTheme"
    }
  },




  /*
  *****************************************************************************
     MEMBERS
  *****************************************************************************
  */

  members :
  {
    _applyIconTheme : function(value, old)
    {
      if (qx.theme.manager.Meta.getInstance().getAutoSync()) {
        this.syncIconTheme();
      }
    },

    /**
     * Sync dependend objects with internal database
     *
     * @type member
     * @return {void}
     */
    syncIconTheme : function()
    {
      var value = this.getIconTheme();
      var alias = qx.io.Alias.getInstance();
      value ? alias.add("icon", value.icons.uri) : alias.remove("icon");
    }
  }
});
