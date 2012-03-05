/*******************************************************************************
 * Copyright (c) 2010, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

( function() {

var Animation = org.eclipse.rwt.Animation;
var AnimationRenderer = org.eclipse.rwt.AnimationRenderer;

qx.Mixin.define( "org.eclipse.rwt.VisibilityAnimationMixin", {

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
    this.hide(); // forces _applyVisibility to be called on show() - not a good practice
  },

  destruct : function() {
    if( this._appearAnimation != null ) {
      this._appearAnimation.dispose();
    }
    this._appearAnimation = null;
    if( this._disappearAnimation != null ) {
      this._disappearAnimation.dispose();
    }
    this._disappearAnimation = null;
  },

  members : {
    _appearAnimation : null, // Declaration in constructor would be too late (mixin)
    _disappearAnimation : null, 

    _applyAnimation : function( newValue, oldValue ) {
      this._configureAppearAnimation( newValue );
      this._configureDisappearAnimation( newValue );     
    },
    
    _configureAppearAnimation : function( config ) {
      var type = null;
      for( type in config ) { 
        break; 
      }
      if( type !== null ) {
        var props = config[ type ];
        var animation = this._getAppearAnimation();
        animation.setProperties( props );  
        var renderer = animation.getDefaultRenderer();
        switch( type ) {
          case "fadeIn":
            renderer.animate( this, "opacity", AnimationRenderer.ANIMATION_APPEAR );
          break;
          case "slideIn":
            var animationType = AnimationRenderer.ANIMATION_APPEAR | AnimationRenderer.ANIMATION_CHANGE; 
            renderer.animate( this, "height", animationType );
            animation.addEventListener( "init", this._initSlideAnimation, this );
            animation.addEventListener( "cancel", this._finishSlideAnimation, this );
          break;
          case "flyInTop":
          case "flyInLeft":
          case "flyInRight":
          case "flyInBottom":
            var animationType = AnimationRenderer.ANIMATION_APPEAR; 
            renderer.animate( this, "top", animationType );
            renderer.setInvisibilityGetter( org.eclipse.rwt.VisibilityAnimationMixin.hideTop )
          break;
        }
      } else if( this._appearAnimation != null ) {
        this._appearAnimation.getDefaultRenderer().setActive( false );        
      }
    },
    
    _getAppearAnimation : function() {
      if( this._appearAnimation === null ) {
        this._appearAnimation = new Animation();
      }
      this._appearAnimation.getDefaultRenderer().setActive( true );
      return this._appearAnimation;
    },

    _configureDisappearAnimation : function( config ) {
      if( config.fadeOut ) {
        var animation = this._getDisappearAnimation();
        var renderer = animation.getDefaultRenderer();
        renderer.animate( this, "opacity", AnimationRenderer.ANIMATION_DISAPPEAR );
        animation.setProperties( config.fadeOut );  
      } else if( config.slideOut ) {
        var animation = this._getDisappearAnimation();
        var renderer = animation.getDefaultRenderer();
        renderer.animate( this, "height", AnimationRenderer.ANIMATION_DISAPPEAR );
        animation.addEventListener( "init", this._initSlideAnimation, this );
        animation.addEventListener( "cancel", this._finishSlideAnimation, this );
        animation.setProperties( config.slideOut );  
      } else if( this._disappearAnimation != null ) {
        this._disappearAnimation.getDefaultRenderer().setActive( false );        
      }
    },
    
    _getDisappearAnimation : function() {
      if( this._disappearAnimation === null ) {
        this._disappearAnimation = new Animation();
      }
      this._disappearAnimation.getDefaultRenderer().setActive( true );
      return this._disappearAnimation;
    },

    _initSlideAnimation : function( event ) {
      this.setContainerOverflow( false );
    },
    
    _finishSlideAnimation : function( event ) {
      // TODO : could container overflow just be generally false, or use _applyHeight instead? 
      this.setContainerOverflow( true );
    }

 },
 
 statics : {
   
   hideTop : function( widget ) {
     return parseInt( widget.getHeightValue(), 10 ) * -1;
   }
   
 }

} );

}());