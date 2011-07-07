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

/** This singleton selects the widget theme to use. */
qx.Class.define("qx.theme.manager.Widget",
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
    /** currently used widget theme */
    widgetTheme :
    {
      check : "Theme",
      nullable : true,
      apply : "_applyWidgetTheme",
      event : "changeWidgetTheme"
    }
  },




  /*
  *****************************************************************************
     MEMBERS
  *****************************************************************************
  */

  members :
  {
    _applyWidgetTheme : function(value, old)
    {
      if (qx.theme.manager.Meta.getInstance().getAutoSync()) {
        this.syncWidgetTheme();
      }
    },

    /**
     * Sync dependend objects with internal database
     * @type member
     * @return {void}
     */
    syncWidgetTheme : function()
    {
      var value = this.getWidgetTheme();
      var alias = qx.io.Alias.getInstance();
      value ? alias.add("widget", value.widgets.uri) : alias.remove("widget");
    }
  }
});
