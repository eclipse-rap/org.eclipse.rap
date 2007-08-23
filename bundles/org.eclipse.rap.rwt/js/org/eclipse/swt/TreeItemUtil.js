
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
    }

  }
});
