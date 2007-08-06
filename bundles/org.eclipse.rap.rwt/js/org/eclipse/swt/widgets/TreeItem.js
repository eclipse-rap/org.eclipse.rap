
/*******************************************************************************
 * Copyright (c) 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

/**
 * This class encapulates the qx.ui.tree.TreeFolder to make it more
 * suitable for usage in RWT.
 */
qx.Class.define( "org.eclipse.swt.widgets.TreeItem", {
  extend : qx.ui.tree.TreeFolder,

  construct : function( parentItem ) {
    this._row = qx.ui.tree.TreeRowStructure.getInstance().newRow();
    // Indentation
    this._row.addIndent();
    // CheckBox
    this._checkBox = null;
    this._checked = false;
    // TODO reactivate
    if ( qx.lang.String.contains( parentItem.getTree().getParent().getRWTStyle(), "check" ) )
    {
      this._checkBox = new qx.ui.basic.Image();
      this._checkBox.setAppearance( "tree-check-box" );
      this._checkBox.addEventListener( "click", this._onCheckBoxClick, this );
      this._checkBox.addEventListener( "dblclick", this._onCheckBoxDblClick, this );
      this._row.addObject( this._checkBox, false );
    }
    // Image
    // TODO [rh] these dummy images are necessary since it is currently not
    //      possible to change images when they were not set here initially
    this._row.addIcon( "widget/tree/folder_closed.gif", 
                 "widget/tree/folder_open.gif" );
    // Text
    this._row.addLabel( "" );
    
    // Construct TreeItem
    this.base( arguments, this._row );
    this.addEventListener( "click", this._onClick, this );
    this.addEventListener( "dblclick", this._onDblClick, this );
    this.addEventListener( "appear", this._onAppear, this );
    this.addEventListener( "changeBackgroundColor", this._onChangeBackgroundColor, this );
    parentItem.add( this );
    
    this._texts = null;
    this._images = new Array();
    this._colLabels = new Array();

    this.getLabelObject().setMode( "html" );
    
    // TODO [bm] need to set the color to prevent inheritance of colors
    this.setBackgroundColor( "transparent" );
  },
  
  destruct : function() {
    if( this._checkBox != null ) {
      this._checkBox.removeEventListener( "click", this._onChangeChecked, this );
      this._checkBox.removeEventListener( "dblclick", this._onCheckBoxDblClick, this );
      this._checkBox.dispose();
    }
    this.removeEventListener( "click", this._onClick, this );
    this.removeEventListener( "dblclick", this._onDblClick, this );
    this.removeEventListener( "appear", this._onAppear, this );
    this.removeEventListener( "changeBackgroundColor", this._onChangeBackgroundColor, this );
  },

  members : {
    
    _onChangeBackgroundColor : function( evt ) {
      if( typeof evt.value == "undefined" ) return;
    	this.getLabelObject().setBackgroundColor( evt.value );
      // we have to go through each column
    	for(var i=0; i<this._colLabels.length; i++) {
        this._colLabels[ i ].setBackgroundColor( evt.value );
      }
    },
    
    // TODO: [bm] needed to override the property setters to apply color to label too
    setTextColor : function ( value ) {
      if( typeof value == "undefined" ) return;
      this.getLabelObject().setTextColor( value );
      // we have to go through each column
      for(var i=0; i<this._colLabels.length; i++) {
       this._colLabels[ i ].setTextColor( value );
      }
    },

    resetBackgroundColor : function ( value ) {
        this.getLabelObject().resetBackgroundColor();
    },

    resetTextColor : function ( value ) {
       this.getLabelObject().resetTextColor();
    },

    setChecked : function( value ) {
      if( this._checkBox != null ) {
        if( value ) {
          this._checkBox.addState( "checked" );
        } else {
          this._checkBox.removeState( "checked" );
        }
      }
    },
    
    setGrayed : function( value ) {
      if( value ) {
          this._checkBox.addState( "grayed" );
      } else {
          this._checkBox.removeState( "grayed" );
      }
    },
    
    setSelection : function( value, focus ) {
      var manager = this.getTree().getManager();
      manager.setItemSelected( this, value );
      if( focus ) {
        manager.setLeadItem( this );
      }
    },

    // TODO [rh] workaround for qx bug #260 (TreeFullControl doesn't update icon
    //      when it is changed)
    setImage : function( image ) {
      this.setIcon( image );
      this.getIconObject().setSource( image );
      this.setIconSelected( image );
    },

    /*
     * Notifies tree of clicks on the item's area.
     */
    _onClick : function( evt ) {
      if( this._checkEventTarget( evt ) ) {
        this.getTree().getParent()._notifyItemClick( this );
      }
    },

    /*
     * Notifies tree of double clicks in the item's area.
     */
    _onDblClick : function( evt ) {
      if( this._checkEventTarget( evt ) ) {
        this.getTree().getParent()._notifyItemDblClick( this );
      }
    },

    /*
     * Checks if a given event should be handled or not. Returns true if the
     * event's original target is either the icon or the label.
     */
    _checkEventTarget : function( evt ) {
      var result = false;
      var target = evt.getOriginalTarget();
      if( target && target == this._iconObject || target == this._labelObject ) {
        result = true;
      }
      return result;
    },

    _onCheckBoxClick : function( evt ) {
      this._checked = !this._checked;
      if( this._checked ) {
          this._checkBox.addState( "checked" );
      } else {
          this._checkBox.removeState( "checked" );
      }
      this._onChangeChecked( evt );
    },
    
    /*
     * Prevent double clicks on check boxes from being propageted to the tree.
     */
    _onCheckBoxDblClick : function( evt ) {
      evt.stopPropagation();
    },

    _onChangeChecked : function( evt ) {
      var wm = org.eclipse.swt.WidgetManager.getInstance();
      var id = wm.findIdByWidget( this );
      var req = org.eclipse.swt.Request.getInstance();
      req.addParameter( id + ".checked", this._checked );
      this.getTree()._notifyChangeItemCheck( this );
    },

    /*
     * Prevent auto expand on click
     */
    _onmouseup : function( evt ) {
    },
    
    _onAppear : function( evt ) {
    	this.updateColumnsWidth();
    },
    
    setTexts : function( texts ) {
      this._texts = texts;
      this.updateItem();
    },
    
    setImages : function( images ) {
    	this._images = images;
    },
    
    columnAdded : function() {
			
    },
    
    updateItem : function() {
	    var colOrder = this.getTree().getParent().getColumnOrder();
	    var colCount = Math.max ( 1, this.getTree().getParent()._columns.length );
    	if( this._texts != null ) {
	    	for( var c = 0; c < colCount; c++ ) {
	    		var col = colOrder[ c ];
	    		var text = this._texts[ col ];
	    		if( text != null && text != "") {
		    		if( c == 0 ) {
		    			this.setLabel( this._texts[ col ] );
		    			// TODO [bm] remove if when image bug is fixed
		    			if( this._images[ col ] != null ) {
  		    		  this.setImage( this._images[ col ] );
		    			}
		    		} else {
		    			if( this._colLabels[ c -1 ] == null ) {
		    				if( this._images[ col ] != null
		    					|| this._colLabels[ c -1 ] instanceof qx.ui.basic.Label ) {
		    					// create new atom / replace label with atom if needed
			    			  var obj = new qx.ui.basic.Atom( "" );
	    						obj.setHorizontalChildrenAlign( "left" );
	    						obj.setHeight( this.getLabelObject().getHeight() );
		    				} else {
		    					var obj = new qx.ui.basic.Label();
		    					// remap function names
		    					obj.setLabel = function( value ) { this.setText( value ); }
		    					obj.setIcon = function( value ) { /* empty cause we have atoms to display images */ }
	    						obj.setHeight( this.getLabelObject().getHeight() );
		    				}
    						this._row.addObject(obj, true);
    						this._colLabels[ c -1 ] = obj;
    						
		    			}
		    		  this._colLabels[ c -1 ].setLabel( this._texts[ col ] );
		    		  this._colLabels[ c -1 ].setIcon( this._images[ col ] );
		    		}
	    		}
	    	}
    	}
    },
    
    updateColumnsWidth : function() {
    	var columnWidth = new Array();
      for( var c = 0; c < this.getTree().getParent()._columns.length; c++ ) {
        columnWidth[ c ] = this.getTree().getParent()._columns[ c ].getWidth();
      }
			if( columnWidth.length > 0 ) {
				var checkboxWidth = (this._checkBox == null ? 0 : 16); // 13 width + 3 checkbox margin 
				this.getLabelObject().setWidth( columnWidth[ 0 ]
					- this.getIconObject().getWidth()
					- ( this.getLevel() * 19)   // TODO: [bm] replace with computed indent width
					- checkboxWidth
					- 3 ); // tree-element-label margin
			  var coLabel;
	    	for( var i=1; i<columnWidth.length; i++ ) {
	    		coLabel = this._colLabels[ i-1 ];
	    		if( coLabel != null ) {
	    			coLabel.setWidth( columnWidth[ i ] );
	    		}
	    	}
			}
    }
    
  }});
