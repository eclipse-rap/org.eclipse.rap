/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

(function(){

var AnimationRenderer = org.eclipse.rwt.AnimationRenderer;
var Animation = org.eclipse.rwt.Animation;

org.eclipse.rwt.AnimationUntil = {

  snapTo : function( widget, time, left, top, hide ) {
    var stop;
    var animation = new Animation();
    animation.setDuration( time );
    animation.setTransition( "easeOut" );
    var converter = AnimationRenderer.converterByRenderType;
    var rendererX = new AnimationRenderer( animation );
    var rendererY = new AnimationRenderer( animation );
    rendererX.setRenderFunction( widget._renderRuntimeLeft, widget );
    rendererY.setRenderFunction( widget._renderRuntimeTop, widget );
    rendererX.setConverter( converter[ "left" ] );
    rendererY.setConverter( converter[ "top" ] );
    var startLeft = parseInt( widget.getLeft(), 10 );
    var startTop = parseInt( widget.getTop(), 10 );
    rendererX.setStartValue( isNaN( startLeft ) ? 0 : startLeft );
    rendererY.setStartValue( isNaN( startTop ) ? 0 : startTop );
    rendererX.setEndValue( left );
    rendererY.setEndValue( top );
    var cleanUp = function() {
      animation.dispose();
      widget.removeEventListener( "dispose", stop );
      widget.removeEventListener( "cancelAnimations", stop );
    };
    stop =  function() {
      animation.cancel();
      cleanUp();
    };
    widget.addEventListener( "dispose", stop );
    widget.addEventListener( "cancelAnimations", stop );
    animation.addEventListener( "finish", function() {
      cleanUp();
      widget.setLeft( left );
      widget.setTop( top );
      if( hide ) {
        widget.setDisplay( false );
      }
    } );
    animation.start();
  }

};

}());