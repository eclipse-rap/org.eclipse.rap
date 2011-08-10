/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.test.tests.ShellProtocolIntegrationTest", {

  extend : qx.core.Object,
  
  members : {

    testCreateShell : function() {
      var shell = this._protocolCreateShell();
      var doc = org.eclipse.rwt.test.fixture.TestUtil.getDocument();
      assertTrue( shell instanceof org.eclipse.swt.widgets.Shell );
      assertIdentical( doc, shell.getParent() );
      assertIdentical( doc, shell.getTopLevelWidget() );
      assertTrue( shell.getUserData( "isControl") );
      assertTrue( shell.hasState( "rwt_BORDER" ) );
      assertTrue( shell.hasState( "rwt_APPLICATION_MODAL" ) );
      assertTrue( shell.hasState( "rwt_ON_TOP" ) );
      assertTrue( shell.hasState( "rwt_TITLE" ) );
      assertTrue( shell.hasState( "rwt_TOOL" ) );
      assertTrue( shell.hasState( "rwt_SHEET" ) );
      assertTrue( shell.getShowMinimize() );
      assertTrue( shell.getAllowMinimize() );
      assertFalse( shell.getShowMaximize() );
      assertFalse( shell.getAllowMaximize() );
      assertFalse( shell.getShowClose() );
      assertFalse( shell.getAllowClose() );
      assertFalse( shell.getResizableWest() );
      assertFalse( shell.getResizableNorth() );
      assertFalse( shell.getResizableEast() );
      assertFalse( shell.getResizableSouth() );
      assertTrue( shell.getShowCaption() );
      assertTrue( shell._onTop );
      assertTrue( shell._appModal );
      org.eclipse.rwt.test.fixture.TestUtil.flush();
      assertTrue( shell.isSeeable() );
      this._disposeShell();
    },

    testAddCustomVariant : function() {
      var shell = this._protocolCreateShell();
      this._protocolSet( { "customVariant" : "variant_blue" } );
      assertTrue( shell.hasState( "variant_blue" ) );
      this._disposeShell();
    },

    testRemoveCustomVariant : function() {
      var shell = this._protocolCreateShell();
      this._protocolSet( { "customVariant" : "variant_blue" } );
      this._protocolSet( { "customVariant" : null } );
      assertFalse( shell.hasState( "variant_blue" ) );
      this._disposeShell();
    },

    testSetImage : function() {
      var shell = this._protocolCreateShell();
      this._protocolSet( { "image" : "blue.jpg" } );
      assertEquals( "blue.jpg", shell.getIcon() );
      this._disposeShell();
    },

    testSetText : function() {
      var shell = this._protocolCreateShell();
      this._protocolSet( { "text" : "blue text" } );
      assertEquals( "blue text", shell.getCaption() );
      this._disposeShell();
    },

    testSetOpacity : function() {
      var shell = this._protocolCreateShell();
      this._protocolSet( { "alpha" : 102 } );
      assertEquals( 0.4, shell.getOpacity() );
      this._disposeShell();
    },

    testSetActive : function() {
      var shell = this._protocolCreateShell();
      this._protocolSet( { "active" : true } );
      assertTrue( shell.getActive() );
      this._disposeShell();
    },

    testSetMode : function() {
      var shell = this._protocolCreateShell();
      this._protocolSet( { "mode" : "maximized" } );
      assertEquals( "maximized", shell.getMode() );
      this._disposeShell();
    },

    testSetFullScreen : function() {
      var shell = this._protocolCreateShell();
      assertTrue( shell.getCaptionBar().getDisplay() );
      this._protocolSet( { "mode" : "fullscreen" } );
      assertEquals( "maximized", shell.getMode() );
      assertFalse( shell.getCaptionBar().getDisplay() );
      this._disposeShell();
    },

    testListenClose : function() {
      var shell = this._protocolCreateShell();
      this._protocolListen( { "shell" : true } );
      assertTrue( shell._hasShellListener );
      this._disposeShell();
    },

    testSetMinimumSize : function() {
      // one property 
      var shell = this._protocolCreateShell();
      this._protocolSet( {
        "minimumSize" : [ 30, 33 ] 
      } );
      assertEquals( 30, shell.getMinWidth() );
      assertEquals( 33, shell.getMinHeight() );
      this._disposeShell();
    },

    testSetDefaultButton : function() {
      var shell = this._protocolCreateShell();
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      var button = new org.eclipse.rwt.widgets.Button( "push" );
      widgetManager.add( button, "wButton", true );
      this._protocolSet( {
        "defaultButton" : "wButton"
      } );
      assertIdentical( button, shell.getDefaultButton() );
      widgetManager.remove( button );
      button.destroy();
      this._disposeShell();
    },

    testSetDefaultButtonToNull : function() {
      var shell = this._protocolCreateShell();
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      var button = new org.eclipse.rwt.widgets.Button( "push" );
      widgetManager.add( button, "wButton", true );
      this._protocolSet( {
        "defaultButton" : "wButton"
      } );

      this._protocolSet( {
        "defaultButton" : null
      } );
      assertNull( shell.getDefaultButton() );
      widgetManager.remove( button );
      button.destroy();
      this._disposeShell();
    },

    testSetDefaultButtonBeforeCreate : function() {
      var shell = this._protocolCreateShell();
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      this._protocolSet( {
        "defaultButton" : "wButton"
      } );
      var button = new org.eclipse.rwt.widgets.Button( "push" );
      widgetManager.add( button, "wButton", true );
      assertIdentical( button, shell.getDefaultButton() );
      widgetManager.remove( button );
      button.destroy();
      this._disposeShell();
    },

    testSetActiveControl : function() {
      var shell = this._protocolCreateShell();
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      var button = new org.eclipse.rwt.widgets.Button( "push" );
      widgetManager.add( button, "wButton", true );
      this._protocolSet( {
        "activeControl" : "wButton"
      } );
      assertIdentical( button, shell._activeControl );
      widgetManager.remove( button );
      button.destroy();
      this._disposeShell();
    },

    testSetActiveControlBeforeCreate : function() {
      var shell = this._protocolCreateShell();
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      this._protocolSet( {
        "activeControl" : "wButton"
      } );
      var button = new org.eclipse.rwt.widgets.Button( "push" );
      widgetManager.add( button, "wButton", true );
      assertIdentical( button, shell._activeControl );
      widgetManager.remove( button );
      button.destroy();
      this._disposeShell();
    },

    testSetDefaultButtonAsActiveControlBeforeCreate : function() {
      var shell = this._protocolCreateShell();
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      this._protocolSet( {
        "defaultButton" : "wButton",
        "activeControl" : "wButton"
      } );
      var button = new org.eclipse.rwt.widgets.Button( "push" );
      widgetManager.add( button, "wButton", true );
      assertIdentical( button, shell._activeControl );
      assertIdentical( button, shell.getDefaultButton() );
      widgetManager.remove( button );
      button.destroy();
      this._disposeShell();
    },

    testSetParentShell : function() {
      var shell = this._protocolCreateShell();
      var shell2 = new org.eclipse.swt.widgets.Shell( { "style" : [] } );
      org.eclipse.rwt.test.fixture.TestUtil.flush();      
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      widgetManager.add( shell2, "wParent", true );
      // done in PushButtonLCA
      this._protocolSet( {
        "parentShell" : "wParent"
      } );
      assertIdentical( shell2, shell.getTopLevelShell() );
      shell2.destroy();
      org.eclipse.rwt.test.fixture.TestUtil.flush();      
      this._disposeShell();
    },

    testSetMenu : function() {
      var shell = this._protocolCreateShell();
      var menu = new org.eclipse.rwt.widgets.Menu();
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      widgetManager.add( menu, "wMenu", true );
      this._protocolSet( { 
        "menu" : "wMenu" 
      } );
      assertIdentical( menu, shell.getContextMenu() );
      assertTrue( shell.hasEventListeners( "contextmenu" ) );
      widgetManager.remove( menu );
      this._disposeShell();
    },

    testSetMenuBeforeCreate : function() {
      var shell = this._protocolCreateShell();
      var menu = new org.eclipse.rwt.widgets.Menu();
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      this._protocolSet( { 
        "menu" : "wMenu" 
      } );
      widgetManager.add( menu, "wMenu", true );
      assertIdentical( menu, shell.getContextMenu() );
      assertTrue( shell.hasEventListeners( "contextmenu" ) );
      widgetManager.remove( menu );
      this._disposeShell();
    },

    testSetBounds : function() {
      var shell = this._protocolCreateShell();
      this._protocolSet( { "bounds" : [ 30, 31, 32, 33 ] } );
      assertEquals( 30, shell.getLeft() );
      assertEquals( 31, shell.getTop() );
      assertEquals( 32, shell.getWidth() );
      assertEquals( 33, shell.getHeight() );
      this._disposeShell();
    },

    testSetZIndex : function() {
      var shell = this._protocolCreateShell();
      this._protocolSet( { "zIndex" : 527 } );
      assertEquals( 527, shell.getZIndex() );
      this._disposeShell();
    },

    testSetToolTipText : function() {
      var shell = this._protocolCreateShell();
      this._protocolSet( { "toolTip" : "hello blue world" } );
      assertEquals( "hello blue world", shell.getUserData( "toolTipText" ) );
      assertTrue( shell.getToolTip() !== null );
      this._disposeShell();
    },

    testSetVisibility : function() {
      // ControlLCAUTil states that visibility is false per default on the shell, 
      // which does not seem to be true?
      var shell = this._protocolCreateShell();
      this._protocolSet( { "visibility" : false } );
      assertFalse( shell.getVisibility() );
      this._disposeShell();
    },

    testSetEnabled : function() {
      var shell = this._protocolCreateShell();
      this._protocolSet( { "enabled" : false } );
      assertFalse( shell.getEnabled() );
      this._disposeShell();
    },

    testSetForeground : function() {
      var shell = this._protocolCreateShell();
      this._protocolSet( { "foreground" : "#0000FF" } );
      assertEquals( "#0000FF", shell.getTextColor() );
      this._disposeShell();
    },

    testSetForegroundTransparent : function() {
      var shell = this._protocolCreateShell();
      this._protocolSet( { "foreground" : "transparent" } );
      assertEquals( "transparent", shell.getTextColor() );
      this._disposeShell();
    },

    testResetForeground : function() {
      var shell = this._protocolCreateShell();
      var orgColor = shell.getTextColor(); // black
      shell.setTextColor( "#0000FF" );
      this._protocolSet( { "foreground" : null } ); // null -> reset
      assertEquals( orgColor, shell.getTextColor() );
      this._disposeShell();
    },

    testSetBackground : function() {
      var shell = this._protocolCreateShell();
      this._protocolSet( { "background" : "#0000FF" } );
      assertEquals( "#0000FF", shell.getBackgroundColor() );
      this._disposeShell();
    },

    testResetBackground : function() {
      var shell = this._protocolCreateShell();
      var orgColor = shell.getBackgroundColor();
      var orgGradient = shell.getBackgroundGradient();
      shell.setBackgroundColor( "red" );
      shell.setBackgroundGradient( [ [ 0, "red" ], [ 1, "yellow" ] ] );
      this._protocolSet( { "background" : null } );
      assertEquals( orgColor, shell.getBackgroundColor() );
      assertEquals( orgGradient, shell.getBackgroundGradient() );
      this._disposeShell();
    },

    testSetBackgroundGradientToNull : function() {
      var shell = this._protocolCreateShell();
      shell.setBackgroundGradient( [ [ 0, "red" ], [ 1, "yellow" ] ] );
      this._protocolSet( { "background" : "#0000FF" } );
      assertNull(shell.getBackgroundGradient() );
      this._disposeShell();
    },

    testSetFont : function() {
      var shell = this._protocolCreateShell();
      var name = "Arial";
      var size = 12;
      var bold = true;
      var italic = false;
      this._protocolSet( { "font" : [ [ name ], size, bold, italic ] } );
      var font = shell.getFont();
      assertEquals( [ name ], font.getFamily() );
      assertEquals( size, font.getSize() );
      assertEquals( bold, font.getBold() );
      assertEquals( italic, font.getItalic() );
      this._disposeShell();
    },

    testSetBackgroundImage : function() {
      var shell = this._protocolCreateShell();
      this._protocolSet( { "backgroundImage" : [ "foo.jpg", 10, 20 ] } );
      assertEquals( "foo.jpg", shell.getBackgroundImage() );
      assertEquals( [ 10, 20 ], shell.getUserData( "backgroundImageSize") );
      this._disposeShell();
    },

    testResetBackgroundImage : function() {
      var shell = this._protocolCreateShell();
      this._protocolSet( { "backgroundImage" : [ "foo.jpg", 10, 20 ] } );
      this._protocolSet( { "backgroundImage" : null } );
      assertNull( shell.getBackgroundImage() );
      assertNull( shell.getUserData( "backgroundImageSize") );
      this._disposeShell();
    },

    testSetCursor : function() {
      var shell = this._protocolCreateShell();
      this._protocolSet( { "cursor" : "wait" } );
      assertEquals( "wait", shell.getCursor() );
      this._disposeShell();
    },

    testResetCursor : function() {
      var shell = this._protocolCreateShell();
      var orgCursor = shell.getCursor();
      shell.setCursor( "wait" );
      this._protocolSet( { "cursor" : null } );
      assertEquals( orgCursor, shell.getCursor() );
      this._disposeShell();
    },
    
    testActivateListener : function() {
      var shell = this._protocolCreateShell();
      this._protocolListen( { "activate" : true } );
      assertEquals( [ shell ], shell._activateListenerWidgets );      
      this._protocolListen( { "activate" : false } );
      assertEquals( [], shell._activateListenerWidgets );      
      this._disposeShell();
    },

    testFocusListener : function() {
      var shell = this._protocolCreateShell();
      assertFalse( shell.hasEventListeners( "focusin" ) );
      assertFalse( shell.hasEventListeners( "focusout" ) );
      this._protocolListen( { "focus" : true } );
      assertTrue( shell.hasEventListeners( "focusin" ) );
      assertTrue( shell.hasEventListeners( "focusout" ) );
      this._disposeShell();
    },

    testMouseListener : function() {
      var shell = this._protocolCreateShell();
      shell.__listeners = {}; // HACK : Remove all listeners for testing
      this._protocolListen( { "mouse" : true } );
      assertTrue( shell.hasEventListeners( "mousedown" ) );
      assertTrue( shell.hasEventListeners( "mouseup" ) );
      this._disposeShell();
    },

    testKeyListener : function() {
      var shell = this._protocolCreateShell();
      this._protocolListen( { "key" : true } );
      assertTrue( shell.getUserData( "keyListener" ) );
      this._disposeShell();
    },

    testTraverseListener : function() {
      var shell = this._protocolCreateShell();
      this._protocolListen( { "traverse" : true } );
      assertTrue( shell.getUserData( "traverseListener" ) );
      this._disposeShell();
    },

    testAllowKeyEvent : function() {
      var util = org.eclipse.rwt.KeyEventUtil.getInstance();
      var allow = false;
      util.allowEvent = function() {
        allow = true;
      };
      var shell = this._protocolCreateShell();
      this._protocolCall( "allowEvent", {} );
      delete util.allowEvent;
      assertTrue( allow );
      this._disposeShell();
    },

    testCancelKeyEvent : function() {
      var util = org.eclipse.rwt.KeyEventUtil.getInstance();
      var cancel = false;
      util.cancelEvent = function() {
        cancel = true;
      };
      var shell = this._protocolCreateShell();
      this._protocolCall( "cancelEvent", {} );
      delete util.cancelEvent;
      assertTrue( cancel );
      this._disposeShell();
    },
    
    testMenuDetectListener : function() {
      var shell = this._protocolCreateShell();
      shell.__listeners = {}; // HACK : Remove all listeners for testing
      this._protocolListen( { "menuDetect" : true } );
      assertTrue( shell.hasEventListeners( "keydown" ) );
      assertTrue( shell.hasEventListeners( "mouseup" ) );
      this._disposeShell();
    },

    testHelpListener : function() {
      var shell = this._protocolCreateShell();
      shell.__listeners = {}; // HACK : Remove all listeners for testing
      this._protocolListen( { "help" : true } );
      assertTrue( shell.hasEventListeners( "keydown" ) );
      this._disposeShell();
    },
    
    testDisposeShell : function() {
      var shell = this._protocolCreateShell();
      assertTrue( shell.getVisibility() );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "destroy"
      } );
      assertFalse( shell.isDisposed() );
      assertFalse( shell.getVisibility() );
      org.eclipse.rwt.test.fixture.TestUtil.flush();
      assertTrue( shell.isDisposed() );
    },

    /////////
    // Helper
    
    _disposeShell : function() {
      org.eclipse.rwt.test.fixture.TestUtil.flush(); // appear to call _beforeDisappear later
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      var shell = widgetManager.findWidgetById( "w3" );
      shell.getWindowManager().setActiveWindow( null ); // remove shell without setting another
      widgetManager.dispose( "w3" );
    },
    
    _protocolCreateShell : function() {
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "org.eclipse.swt.widgets.Shell",
        "properties" : {
          "style" : [ "BORDER", "APPLICATION_MODAL", "ON_TOP", "TITLE", "TOOL", "SHEET", "MIN" ]
        }
      } );
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      var result = widgetManager.findWidgetById( "w3" );
      return result;
    },

    _protocolSet : function( properties ) {
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "set",
        "properties" : properties
      } );
    },

    _protocolCall : function( method, properties ) {
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "call",
        "method" : method,
        "properties" : properties
      } );
    },

    _protocolListen : function( properties ) {
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "listen",
        "properties" : properties
      } );
    }

  }
  
} );