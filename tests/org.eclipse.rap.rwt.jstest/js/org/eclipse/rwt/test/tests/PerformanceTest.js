/*******************************************************************************
 * Copyright (c) 2011, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.PerformanceTest", {
  extend : rwt.qx.Object,
  
  members : {
    
    testSynchronize : function() {
      this._createDummyTarget( 0 );
      var iterations = 1000;
      var evalTime = this._syncEval( iterations );
      var protocolTime = this._syncProtocol( iterations );
      var mixedTime = this._syncMixed( iterations );
      var logger = org.eclipse.rwt.test.Presenter.getInstance();
      logger.log( "iterations: " + iterations );
      logger.log( "eval: " + evalTime );
      logger.log( "protocol: " + protocolTime );
      logger.log( "protocol-in-eval: " + mixedTime );
    },

    
    testSynchronizeLittleDataOnWidgetWithManyProperties : function() {
      this._createDummyTarget( 50 );
      var iterations = 1000;
      var evalTime = this._syncEval( iterations );
      var protocolTime = this._syncProtocol( iterations );
      var mixedTime = this._syncMixed( iterations );
      var logger = org.eclipse.rwt.test.Presenter.getInstance();
      logger.log( "iterations: " + iterations );
      logger.log( "eval: " + evalTime );
      logger.log( "protocol: " + protocolTime );
      logger.log( "protocol-in-eval: " + mixedTime );
    },

    // NOTE: Values need to change due to potential setter optimizations.
    _syncEval : function( iterations ) {
      var evalText1 = "var wm = rwt.remote.WidgetManager.getInstance();";
      evalText1 += "var w = wm.findWidgetById( \"dummyId\" );";
      evalText1 += "w.setTextColor( \"#c20017\" );";
      evalText1 += "w.setLeft( 100 );";
      evalText1 += "w.setTop( 100 );";
      evalText1 += "w.setLabel( \"foo\" );";
      var evalText2 = "var wm = rwt.remote.WidgetManager.getInstance();";
      evalText2 += "var w = wm.findWidgetById( \"dummyId\" );";
      evalText2 += "w.setTextColor( \"#000000\" );";
      evalText2 += "w.setLeft( 200 );";
      evalText2 += "w.setTop( 200 );";
      evalText2 += "w.setLabel( \"bar\" );";
      var startTime = ( new Date() ).getTime();
      for( var i = 0; i < iterations; i++ ) {
        eval( evalText1 );
        eval( evalText2 );
      }
      var endTime = ( new Date() ).getTime();
      return endTime - startTime;
    },

    // NOTE: Values need to change due to potential setter optimizations.
    _syncProtocol : function( iterations ) {
      var details1 = {
        "textColor" : "#c20017",
        "left" : 100,
        "top" : 100,
        "label" : "foo"
      };
      var details2 = {
        "textColor" : "#000000",
        "left" : 200,
        "top" : 200,
        "label" : "bar"
      };
      var startTime = ( new Date() ).getTime();
      for( var i = 0; i < iterations; i++ ) {
        rwt.remote.MessageProcessor.processSet( "dummyId", details1 );
        rwt.remote.MessageProcessor.processSet( "dummyId", details2 );
      }
      var endTime = ( new Date() ).getTime();
      return endTime - startTime;
    },

    // NOTE: Values need to change due to potential setter optimizations.
    _syncMixed : function( iterations ) {
      var evalProc1 = "rwt.remote.MessageProcessor.processSet( \"dummyId\",";
      evalProc1 += "{ \"textColor\" : \"#c20017\",\"left\" : 100,\"top\" : 100,\"label\" : \"foo\"\ }";
      evalProc1 += ");"
      var evalProc2 = "rwt.remote.MessageProcessor.processSet( \"dummyId\",";
      evalProc2 += "{ \"textColor\" : \"#000000\",\"left\" : 200,\"top\" : 200,\"label\" : \"bar\"\ }";
      evalProc2 += ");"
      var startTime = ( new Date() ).getTime();
      for( var i = 0; i < iterations; i++ ) {
        eval( evalProc1 );
        eval( evalProc2 );
      }      
      var endTime = ( new Date() ).getTime();
      return endTime - startTime;
    },
    
    /////////
    // Helper

    _createDummyTarget : function( fakeProperties ) {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var widgetManager = rwt.remote.WidgetManager.getInstance();
      var registry = rwt.remote.HandlerRegistry;
      var processor = rwt.remote.MessageProcessor;
      var dummy = new rwt.widgets.base.Atom();
      dummy.setLocation( 30, 10 );
      dummy.setDimension( 10, 10 );
      dummy.addToDocument();
      widgetManager.add( dummy, "dummyId", true, "dummyType" );
      TestUtil.flush();
      var props = [ "textColor", "left", "top", "label" ];
      for( var i = 0; i < fakeProperties; i++ ) {
        props.push( "prop" + i );
      }
      registry.add( "dummyType", {
        knownProperties : props
      } );
      return dummy;
    }

  }
  
} );