/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
 
/**
 * This class contains static functions needed for TreeItems.
 */
// TODO [rh] move remaining methods to TreeItem.js and delete 
qx.Class.define( "org.eclipse.swt.TreeItemUtil", {

  statics : {
    
    createTreeItem : function( widgetId, parent, tree ) {
    	var wm = org.eclipse.swt.WidgetManager.getInstance();
    	
    	var realParent = null;
    	if( parent instanceof org.eclipse.swt.widgets.Tree ) {
    		realParent = parent.getTree();
    	} else {
    		realParent = parent;
    	}
    	
    	var w = new org.eclipse.swt.widgets.TreeItem( realParent, tree );
    	wm.add( w, widgetId, false );
    	
    	this.hijackTreeItemSelection( w );
    },
    
    /**
     * Hijacking to avoid selection bugs.
     * 
     * TODO: Remove this after next qooxdoo update
     */
    hijackTreeItemSelection : function( item ) {
    	item._applySelected = function(value, old)
		    {
		      if (value)
		      {
		        this.addState("selected");
		        this._labelObject.addState("selected");
		      }
		      else
		      {
		        this.removeState("selected");
		        this._labelObject.removeState("selected");
		      }
		
		      var vTree = this.getTree();
		
		      if (!vTree._fastUpdate || (old && vTree._oldItem == this))
		      {
		        this._iconObject.setSource(this._evalCurrentIcon());
		
		        if (value) {
		          this._iconObject.addState("selected");
		        } else {
		          this._iconObject.removeState("selected");
		        }
		      }

          /**
           * Disabled due to selection bugs in qooxdoo
		      var vManager = this.getTree().getManager();
		
		      if (old && vManager.getSelectedItem() == this) {
		        vManager.deselectAll();
		      } else if (value && vManager.getSelectedItem() != this) {
		        //vManager.setSelectedItem(this);
		      }
		      */
		    };
    }
    

  }
});
