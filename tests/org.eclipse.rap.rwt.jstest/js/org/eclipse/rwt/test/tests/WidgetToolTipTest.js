/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.test.tests.WidgetToolTipTest", {
  extend : qx.core.Object,
  
  construct : function() {
    this.base( arguments );
    this.testUtil = org.eclipse.rwt.test.fixture.TestUtil;
    this.testUtil.prepareTimerUse();
    this.manager = qx.ui.popup.ToolTipManager.getInstance();
    this.wm = org.eclipse.swt.WidgetManager.getInstance();        
    this.testUtil.flush();
    this.toolTip = org.eclipse.rwt.widgets.WidgetToolTip.getInstance();
  },
  
  members : {
    
    testUpdateWidgetToolTipText : function() {
      this.widget1 = new qx.ui.basic.Label( "Hello World 1" );
      this.widget2 = new qx.ui.basic.Label( "Hello World 2" );
      this.widget1.addToDocument();
      this.widget2.addToDocument();
      this.testUtil.flush();            
      this.wm.setToolTip( this.widget1, "test1" );
      this.wm.setToolTip( this.widget2, "test2" );            
      var widget = this.widget1;
      var event = {
        getTarget : function() { return widget; }
      };            
      this.manager.handleMouseOver( event );
      assertEquals( "test1", this.toolTip._atom.getLabel() );
      var widget = this.widget2;
      var event = {
        getTarget : function() { return widget; }
      };            
      this.manager.handleMouseOver( event );
      assertEquals( "test2", this.toolTip._atom.getLabel() );
      this.wm.setToolTip( this.widget1, "test3" );
      var widget = this.widget1;
      var event = {
        getTarget : function() { return widget; }
      };            
      this.manager.handleMouseOver( event );
      assertEquals( "test3", this.toolTip._atom.getLabel() );
      this.widget1.setParent( null );
      this.widget2.setParent( null );
      this.widget1.dispose();
      this.widget2.dispose();
      this.testUtil.flush();      
    },
    
    testUpdateWigetToolTipTextWhileToolTipBound : function() {
      this.widget1 = new qx.ui.basic.Label( "Hello World 1" );
      this.widget1.addToDocument();
      this.testUtil.flush();      
      this.wm.setToolTip( this.widget1, "test1" );            
      var widget = this.widget1;
      var event = {
        getTarget : function() { return widget; }
      };            
      this.manager.handleMouseOver( event );
      assertEquals( "test1", this.toolTip._atom.getLabel() );
      this.wm.setToolTip( this.widget1, "test2" );
      assertEquals( "test2", this.toolTip._atom.getLabel() );      
      this.widget1.setParent( null );
      this.widget1.dispose();
      this.testUtil.flush();
    }
  }
  
} );
