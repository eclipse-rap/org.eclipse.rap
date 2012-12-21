/*******************************************************************************
 * Copyright (c) 2009, 2012 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.WidgetToolTipTest", {
  extend : rwt.qx.Object,
  
  construct : function() {
    this.base( arguments );
    this.TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
    this.TestUtil.prepareTimerUse();
    this.manager = rwt.widgets.util.ToolTipManager.getInstance();
    this.wm = rwt.remote.WidgetManager.getInstance();        
    this.TestUtil.flush();
    this.toolTip = rwt.widgets.base.WidgetToolTip.getInstance();
  },
  
  members : {
    
    TARGETPLATFORM : [ "win", "mac", "unix" ],

    testUpdateWidgetToolTipText : function() {
      this.widget1 = new rwt.widgets.base.Label( "Hello World 1" );
      this.widget2 = new rwt.widgets.base.Label( "Hello World 2" );
      this.widget1.addToDocument();
      this.widget2.addToDocument();
      this.TestUtil.flush();            
      this.wm.setToolTip( this.widget1, "test1" );
      this.wm.setToolTip( this.widget2, "test2" );            
      var widget = this.widget1;
      var event = {
        getTarget : function() { return widget; },
        getType : function() { return "mouseover" }
      };            
      this.manager.handleMouseEvent( event );
      assertEquals( "test1", this.toolTip._atom.getLabel() );
      var widget = this.widget2;
      var event = {
        getTarget : function() { return widget; },
        getType : function() { return "mouseover" }
      };            
      this.manager.handleMouseEvent( event );
      assertEquals( "test2", this.toolTip._atom.getLabel() );
      this.wm.setToolTip( this.widget1, "test3" );
      var widget = this.widget1;
      var event = {
        getTarget : function() { return widget; },
        getType : function() { return "mouseover" }
      };            
      this.manager.handleMouseEvent( event );
      assertEquals( "test3", this.toolTip._atom.getLabel() );
      this.widget1.setParent( null );
      this.widget2.setParent( null );
      this.widget1.dispose();
      this.widget2.dispose();
      this.TestUtil.flush();      
    },
    
    testUpdateWigetToolTipTextWhileToolTipBound : function() {
      this.widget1 = new rwt.widgets.base.Label( "Hello World 1" );
      this.widget1.addToDocument();
      this.TestUtil.flush();      
      this.wm.setToolTip( this.widget1, "test1" );            
      var widget = this.widget1;
      var event = {
        getTarget : function() { return widget; },
        getType : function() { return "mouseover" }
      };            
      this.manager.handleMouseEvent( event );
      assertEquals( "test1", this.toolTip._atom.getLabel() );
      this.wm.setToolTip( this.widget1, "test2" );
      assertEquals( "test2", this.toolTip._atom.getLabel() );      
      this.widget1.setParent( null );
      this.widget1.dispose();
      this.TestUtil.flush();
    }
  }
  
} );
