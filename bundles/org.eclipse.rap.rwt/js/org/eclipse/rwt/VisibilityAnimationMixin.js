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
    this.addEventListener( "beforeAppear", this._blockFocusOnAppear, this );
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
      if( this._appearAnimation != null ) {
        this._appearAnimation.getDefaultRenderer().setActive( false );        
      }
      for( var type in config ) {
        switch( type ) {
          case "fadeIn":
            this._configureFadeIn( config[ type ] );
          break;
          case "slideIn":
            this._configureSlideIn( config[ type ] );
          break;
          case "flyInTop":
          case "flyInLeft":
          case "flyInRight":
          case "flyInBottom":
            this._configureFlyIn( config[ type ], type );
          break;
        }
      }
    },
    
    _configureFadeIn : function( props ) {
      var animation = this._getAppearAnimation();
      animation.setProperties( props );  
      animation.getDefaultRenderer().animate( this, "opacity", AnimationRenderer.ANIMATION_APPEAR );
    },
    
    _configureSlideIn : function( props ) {
      var animation = this._getAppearAnimation();
      animation.setProperties( props );  
      var renderer = animation.getDefaultRenderer();
      var animationType = AnimationRenderer.ANIMATION_APPEAR | AnimationRenderer.ANIMATION_CHANGE;
      renderer.animate( this, "height", animationType );
      animation.addEventListener( "init", this._initSlideAnimation, this );
      animation.addEventListener( "cancel", this._finishSlideAnimation, this );
    },
    
    _configureFlyIn : function( props, type ) {
      var animation = this._getAppearAnimation();
      animation.setProperties( props );  
      var renderer = animation.getDefaultRenderer();
      var animationType = AnimationRenderer.ANIMATION_APPEAR;
      switch( type ) {
        case "flyInTop":
          renderer.animate( this, "top", animationType );
          renderer.setInvisibilityGetter( org.eclipse.rwt.VisibilityAnimationMixin.hideTop );
        break;
        case "flyInBottom":
          renderer.animate( this, "top", animationType );
          renderer.setInvisibilityGetter( org.eclipse.rwt.VisibilityAnimationMixin.hideBottom );
        break;
        case "flyInLeft":
          renderer.animate( this, "left", animationType );
          renderer.setInvisibilityGetter( org.eclipse.rwt.VisibilityAnimationMixin.hideLeft );
        break;
        case "flyInRight":
          renderer.animate( this, "left", animationType );
          renderer.setInvisibilityGetter( org.eclipse.rwt.VisibilityAnimationMixin.hideRight );
        break;
      } 
    },
   
    _getAppearAnimation : function() {
      if( this._appearAnimation === null ) {
        this._appearAnimation = new Animation();
        this._appearAnimation.addEventListener( "cancel", this._onAppearFinish, this );
      }
      this._appearAnimation.getDefaultRenderer().setActive( true );
      return this._appearAnimation;
    },
    
    _blockFocusOnAppear : function() {
      if( this._appearAnimation && this._appearAnimation.getDefaultRenderer().isActive() ) {
        qx.event.handler.FocusHandler.blockFocus = true;
      }
    },

    _onAppearFinish : function() {
      if( qx.event.handler.FocusHandler.blockFocus ) {
        qx.event.handler.FocusHandler.blockFocus = false;
        var focused = this.getFocusRoot() ? this.getFocusRoot().getFocusedChild() : null;
        if( focused ) {
          focused._visualizeFocus();
        }
      }
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
   },

   hideBottom : function( widget ) {
     return widget.getParent().getInnerHeight();
   },

   hideLeft : function( widget ) {
     return parseInt( widget.getWidthValue(), 10 ) * -1;
   },

   hideRight : function( widget ) {
     return widget.getParent().getInnerWidth();
   }
   
 }

} );

}());