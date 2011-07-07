/*******************************************************************************
 * Copyright (c) 2010 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Mixin.define( "org.eclipse.rwt.FadeAnimationMixin", {

  properties : {

    animation : {
      check : "Object",
      nullable : false,
      init : null,
      apply : "_applyAnimation",      
      themeable : true
    }

  },
  
  construct : function() {
    this.hide();
    this._renderAppearance();
  },

  destruct : function() {
    if( this._animation != null ) {
      this._animation.dispose();
    }
    this._animation = null;
  },

  members : {
    // NOTE [tb] : This mixin exists for two reasons:
    // 1. The ClientDocumentBlocker can not animated without changing its code.
    // 2. The code would be the same for all widgets supporting fade.
    // TODO [tb] : Should the required qooxdoo classes be adopted by RAP,
    // this could be solved in a more elegant way.  

    _animation : null, // Declaration in constructor would be too late.

    _applyAnimation : function( newValue, oldValue ) {
      var animationType = 0;
      if( newValue[ "fadeIn" ] ) {
        animationType |= org.eclipse.rwt.AnimationRenderer.ANIMATION_APPEAR; 
      } 
      if( newValue[ "fadeOut" ] ) {
        animationType |= org.eclipse.rwt.AnimationRenderer.ANIMATION_DISAPPEAR;        
      }
      if( animationType != 0 ) {
        if( this._animation == null ) {
          this._animation = new org.eclipse.rwt.Animation();
          var renderer = this._animation.getDefaultRenderer() 
          renderer.animate( this, "opacity", animationType );
          this._animation.addEventListener( "init", this._initAnimation, this );
        } 
        this._animation.getDefaultRenderer().setActive( true );
      } else if( this._animation != null ) {
        this._animation.getDefaultRenderer().setActive( false );
      }
    },

    _initAnimation : function( event ) {
      if( event.getData() == "appear" ) {
        this._animation.setProperties( this.getAnimation()[ "fadeIn" ] );
      } else {
        this._animation.setProperties( this.getAnimation()[ "fadeOut" ] );
      }
    }

 }

} );
