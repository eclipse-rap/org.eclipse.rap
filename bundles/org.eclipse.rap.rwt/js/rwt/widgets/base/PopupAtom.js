/*******************************************************************************
 *  Copyright: 2004, 2012 1&1 Internet AG, Germany, http://www.1und1.de,
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

rwt.qx.Class.define("rwt.widgets.base.PopupAtom",
{
  extend : rwt.widgets.base.Popup,




  /*
  *****************************************************************************
     CONSTRUCTOR
  *****************************************************************************
  */

  construct : function(vLabel, vIcon)
  {
    this.base(arguments);

    this._atom = new rwt.widgets.base.Atom(vLabel, vIcon);
    this._atom.setParent(this);
  },




  /*
  *****************************************************************************
     MEMBERS
  *****************************************************************************
  */

  members :
  {
    _isFocusRoot : false,


    /**
     * Accessor method to get the atom sub widget
     *
     * @type member
     * @return {rwt.widgets.base.Atom} atom sub widget
     */
    getAtom : function() {
      return this._atom;
    }
  },




  /*
  *****************************************************************************
     DESTRUCTOR
  *****************************************************************************
  */

  destruct : function() {
    this._disposeObjects("_atom");
  }
});
