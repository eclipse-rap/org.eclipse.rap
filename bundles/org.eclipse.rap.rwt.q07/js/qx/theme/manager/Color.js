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

qx.Class.define("qx.theme.manager.Color",
{
  type : "singleton",
  extend : qx.util.manager.Value,




  /*
  *****************************************************************************
     PROPERTIES
  *****************************************************************************
  */

  properties :
  {
    /** the currently selected color theme */
    colorTheme :
    {
      check : "Theme",
      nullable : true,
      apply : "_applyColorTheme",
      event : "changeColorTheme"
    }
  },




  /*
  *****************************************************************************
     MEMBERS
  *****************************************************************************
  */

  members :
  {
    _applyColorTheme : function(value)
    {
      var dest = this._dynamic = {};

      if (value)
      {
        var source = value.colors;
        var util = qx.util.ColorUtil;
        var temp;

        for (var key in source)
        {
          temp = source[key];

          if (typeof temp === "string")
          {
            if (!util.isCssString(temp)) {
              throw new Error("Could not parse color: " + temp);
            }
          }
          else if (temp instanceof Array)
          {
            temp = util.rgbToRgbString(temp);
          }
          else
          {
            throw new Error("Could not parse color: " + temp);
          }

          dest[key] = temp;
        }
      }

      if (qx.theme.manager.Meta.getInstance().getAutoSync()) {
        this.syncColorTheme();
      }
    },


    /**
     * Sync dependend objects with internal database
     *
     * @type member
     * @return {void}
     */
    syncColorTheme : function() {
      this._updateObjects();
    }
  }
});
