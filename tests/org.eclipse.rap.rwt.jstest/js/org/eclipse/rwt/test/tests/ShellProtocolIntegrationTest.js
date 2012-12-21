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

(function(){

var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
var MessageProcessor = rwt.remote.MessageProcessor;
var ObjectRegistry = rwt.remote.ObjectRegistry;

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.ShellProtocolIntegrationTest", {

  extend : rwt.qx.Object,

  members : {

    testCreateShell : function() {
      var shell = this._protocolCreateShell();
      var doc = TestUtil.getDocument();
      assertTrue( shell instanceof rwt.widgets.Shell );
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
      TestUtil.flush();
      assertFalse( shell.isSeeable() );
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
      this._protocolSet( { "image" : [ "blue.jpg", 10, 20 ] } );
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
      var widgetManager = rwt.remote.WidgetManager.getInstance();
      var button = new rwt.widgets.Button( "push" );
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
      var widgetManager = rwt.remote.WidgetManager.getInstance();
      var button = new rwt.widgets.Button( "push" );
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
      var widgetManager = rwt.remote.WidgetManager.getInstance();
      this._protocolSet( {
        "defaultButton" : "wButton"
      } );
      var button = new rwt.widgets.Button( "push" );
      widgetManager.add( button, "wButton", true );
      assertIdentical( button, shell.getDefaultButton() );
      widgetManager.remove( button );
      button.destroy();
      this._disposeShell();
    },

    testSetActiveControl : function() {
      var shell = this._protocolCreateShell();
      var widgetManager = rwt.remote.WidgetManager.getInstance();
      var button = new rwt.widgets.Button( "push" );
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
      var widgetManager = rwt.remote.WidgetManager.getInstance();
      this._protocolSet( {
        "activeControl" : "wButton"
      } );
      var button = new rwt.widgets.Button( "push" );
      widgetManager.add( button, "wButton", true );
      assertIdentical( button, shell._activeControl );
      widgetManager.remove( button );
      button.destroy();
      this._disposeShell();
    },

    testSetDefaultButtonAsActiveControlBeforeCreate : function() {
      var shell = this._protocolCreateShell();
      var widgetManager = rwt.remote.WidgetManager.getInstance();
      this._protocolSet( {
        "defaultButton" : "wButton",
        "activeControl" : "wButton"
      } );
      var button = new rwt.widgets.Button( "push" );
      widgetManager.add( button, "wButton", true );
      assertIdentical( button, shell._activeControl );
      assertIdentical( button, shell.getDefaultButton() );
      widgetManager.remove( button );
      button.destroy();
      this._disposeShell();
    },

    testSetParentShell : function() {
      var parent = this._protocolCreateShell( "wParent" );
      var shell = this._protocolCreateShell( "w3", "wParent" );
      TestUtil.flush();
      assertIdentical( parent, shell.getTopLevelShell() );
      this._disposeShell( "wParent" );
      this._disposeShell();
    },

    // See Bug 354912 - New Shell opens in background, not visible
    testSetParentShellZIndex : function() {
      var parent = this._protocolCreateShell( "wParent" );
      parent.setActive( true ); // otherwise it would not be automatically in front
      TestUtil.protocolSet( "wParent", { "visibility" : true } );
      TestUtil.flush();

      var shell = this._protocolCreateShell( "w3", "wParent" );
      TestUtil.protocolSet( "w3", { "visibility" : true } );

      TestUtil.flush();
      assertTrue( shell.getZIndex() > parent.getZIndex() );
      this._disposeShell( "wParent" );
      this._disposeShell();
    },

    testSetMenu : function() {
      var shell = this._protocolCreateShell();
      var menu = new rwt.widgets.Menu();
      var widgetManager = rwt.remote.WidgetManager.getInstance();
      widgetManager.add( menu, "wMenu", true );
      this._protocolSet( {
        "menu" : "wMenu"
      } );
      assertIdentical( menu, shell.getContextMenu() );
      assertTrue( shell.hasEventListeners( "mouseup" ) );
      widgetManager.remove( menu );
      this._disposeShell();
    },

    testSetMenuBeforeCreate : function() {
      var shell = this._protocolCreateShell();
      var menu = new rwt.widgets.Menu();
      var widgetManager = rwt.remote.WidgetManager.getInstance();
      this._protocolSet( {
        "menu" : "wMenu"
      } );
      widgetManager.add( menu, "wMenu", true );
      assertIdentical( menu, shell.getContextMenu() );
      assertTrue( shell.hasEventListeners( "mouseup" ) );
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

    testSetBoundsWhithMaximized : function() {
      var shell = this._protocolCreateShell();
      this._protocolSet( {
        "bounds" : [ 30, 31, 32, 33 ],
        "mode" : "maximized"
      } );
      assertEquals( "maximized", shell.getMode() );
      assertEquals( "100%", shell.getWidth() );
      assertEquals( "100%", shell.getHeight() );
      this._disposeShell();
    },

    testSetChildren : function() {
      var shell = this._protocolCreateShell();
      var widgetManager = rwt.remote.WidgetManager.getInstance();
      var button1 = new rwt.widgets.Button( "push" );
      widgetManager.add( button1, "w11", true );
      var button2 = new rwt.widgets.Button( "push" );
      widgetManager.add( button2, "w12", true );
      var button3 = new rwt.widgets.Button( "push" );
      widgetManager.add( button3, "w13", true );
      this._protocolSet( { "children" : [ "w12", "w13", "w11" ] } );
      assertEquals( 1, button1.getZIndex() );
      assertEquals( 2, button3.getZIndex() );
      assertEquals( 3, button2.getZIndex() );
      widgetManager.remove( button1 );
      button1.destroy();
      widgetManager.remove( button2 );
      button2.destroy();
      widgetManager.remove( button3 );
      button3.destroy();
      this._disposeShell();
    },

    testSetToolTip : function() {
      var shell = this._protocolCreateShell();
      this._protocolSet( { "toolTip" : "hello\n blue<> world" } );
      assertEquals( "hello<br/> blue&lt;&gt; world", shell.getUserData( "toolTipText" ) );
      assertTrue( shell.getToolTip() !== null );
      this._disposeShell();
    },

    testResetToolTipText : function() {
      var shell = this._protocolCreateShell();
      this._protocolSet( { "toolTip" : "hello blue world" } );
      this._protocolSet( { "toolTip" : "" } );
      assertNull( shell.getToolTip() );
      assertNull( shell.getUserData( "toolTipText" ) );
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
      this._protocolSet( { "foreground" : [ 0, 0, 255, 255 ] } );
      assertEquals( "rgb(0,0,255)", shell.getTextColor() );
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
      this._protocolSet( { "background" : [ 0, 0, 255, 255 ] } );
      assertEquals( "rgb(0,0,255)", shell.getBackgroundColor() );
      this._disposeShell();
    },

    testResetBackground : function() {
      var shell = this._protocolCreateShell();
      var orgColor = shell.getBackgroundColor();
      shell.setBackgroundColor( "red" );
      shell.setBackgroundGradient( [ [ 0, "red" ], [ 1, "yellow" ] ] );
      this._protocolSet( { "background" : null } );
      assertEquals( orgColor, shell.getBackgroundColor() );
      this._disposeShell();
    },

    testSetThemingBackgroundGradientToNull : function() {
      TestUtil.fakeAppearance( "window", {
        style : function( states ) {
          var result = {};
          result.backgroundGradient = [ [ 0, "red" ], [ 1, "yellow" ] ];
          result.minWidth = 80;
          result.minHeight = 25;
          result.opacity = 1;
          return result;
        }
      } );
      var shell = this._protocolCreateShell();
      this._protocolSet( { "background" : [ 0, 0, 255, 255 ] } );
      assertNull( shell.getBackgroundGradient() );
      this._disposeShell();
    },

    testSetThemingBackgroundGradientToNullAndBack : function() {
      TestUtil.fakeAppearance( "window", {
        style : function( states ) {
          var result = {};
          result.backgroundGradient = [ [ 0, "red" ], [ 1, "yellow" ] ];
          result.minWidth = 80;
          result.minHeight = 25;
          result.opacity = 1;
          return result;
        }
      } );
      var shell = this._protocolCreateShell();
      this._protocolSet( { "background" : [ 0, 0, 255, 255 ] } );
      this._protocolSet( { "background" : null } );
      assertNotNull( shell.getBackgroundGradient() );
      this._disposeShell();
    },

    testSetServerBackgroundGradientToNull : function() {
      var shell = this._protocolCreateShell();
      shell.setBackgroundGradient( [ [ 0, "red" ], [ 1, "yellow" ] ] );
      this._protocolSet( { "background" : [ 0, 0, 255, 255 ] } );
      assertNotNull( shell.getBackgroundGradient() );
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

    testResetFont : function() {
      var shell = this._protocolCreateShell();
      var org = shell.getFont();
      this._protocolSet( { "font" : [ [ "Arial" ], 12, false, false ] } );
      this._protocolSet( { "font" : null } );
      assertEquals( org, shell.getFont() );
      this._disposeShell();
    },

    testSetBackgroundImage : function() {
      var shell = this._protocolCreateShell();
      this._protocolSet( { "backgroundImage" : [ "foo.jpg", 10, 20 ] } );
      assertEquals( "foo.jpg", shell.getBackgroundImage() );
      assertEquals( [ 10, 20 ], shell.getUserData( "backgroundImageSize") );
      this._disposeShell();
    },

    testSetBackgroundImageOrder : function() {
      var shell = this._protocolCreateShell();
      var size;
      shell._applyBackgroundImage = function() {
        size = shell.getUserData( "backgroundImageSize");
      };
      this._protocolSet( { "backgroundImage" : [ "foo.jpg", 10, 20 ] } );
      assertEquals( "foo.jpg", shell.getBackgroundImage() );
      assertEquals( [ 10, 20 ], size );
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
      this._protocolListen( { "Activate" : true } );
      assertTrue( shell.getUserData( "activateListener" ) );
      this._protocolListen( { "Activate" : false } );
      assertNull( shell.getUserData( "activateListener" ) );
      this._disposeShell();
    },

    testDeactivateListener : function() {
      var shell = this._protocolCreateShell();
      this._protocolListen( { "Deactivate" : true } );
      assertTrue( shell.getUserData( "deactivateListener" ) );
      this._protocolListen( { "Deactivate" : false } );
      assertNull( shell.getUserData( "deactivateListener" ) );
      this._disposeShell();
    },

    testFocusInListener : function() {
      var shell = this._protocolCreateShell();
      assertFalse( shell.hasEventListeners( "focusin" ) );
      assertFalse( shell.hasEventListeners( "focusout" ) );
      this._protocolListen( { "FocusIn" : true } );
      assertTrue( shell.hasEventListeners( "focusin" ) );
      assertFalse( shell.hasEventListeners( "focusout" ) );
      this._disposeShell();
    },

    testFocusOutListener : function() {
      var shell = this._protocolCreateShell();
      assertFalse( shell.hasEventListeners( "focusin" ) );
      assertFalse( shell.hasEventListeners( "focusout" ) );
      this._protocolListen( { "FocusOut" : true } );
      assertFalse( shell.hasEventListeners( "focusin" ) );
      assertTrue( shell.hasEventListeners( "focusout" ) );
      this._disposeShell();
    },

    testNotifyFocusIn : function() {
      rwt.remote.EventUtil.setSuspended( true );
      var shell = this._protocolCreateShell();
      this._protocolListen( { "FocusIn" : true } );
      shell.open();
      var otherShell = this._protocolCreateShell( "w4" );
      otherShell.open();
      TestUtil.flush();
      otherShell.setFocused( true );
      rwt.remote.EventUtil.setSuspended( false );

      shell.setFocused( true );

      var message = TestUtil.getMessageObject();
      assertNotNull( message.findNotifyOperation( "w3", "FocusIn" ) );
      this._disposeShell();
      this._disposeShell( "w4" );
    },

    testNotifyFocusOut : function() {
      rwt.remote.EventUtil.setSuspended( true );
      var shell = this._protocolCreateShell();
      this._protocolListen( { "FocusOut" : true } );
      shell.open();
      var otherShell = this._protocolCreateShell( "w4" );
      otherShell.open();
      TestUtil.flush();
      shell.setFocused( true );
      rwt.remote.EventUtil.setSuspended( false );

      otherShell.setFocused( true );

      var message = TestUtil.getMessageObject();
      assertNotNull( message.findNotifyOperation( "w3", "FocusOut" ) );
      this._disposeShell();
      this._disposeShell( "w4" );
    },

    testMouseDownListener : function() {
      var shell = this._protocolCreateShell();
      shell.__listeners = {}; // HACK : Remove all listeners for testing
      this._protocolListen( { "MouseDown" : true } );
      assertTrue( shell.hasEventListeners( "mousedown" ) );
      assertFalse( shell.hasEventListeners( "mouseup" ) );
      this._disposeShell();
    },

    testMouseDoubleClickListener : function() {
      var shell = this._protocolCreateShell();
      shell.__listeners = {}; // HACK : Remove all listeners for testing
      this._protocolListen( { "MouseDoubleClick" : true } );
      assertTrue( shell.hasEventListeners( "mousedown" ) );
      assertTrue( shell.hasEventListeners( "mouseup" ) );
      this._disposeShell();
    },

    testMouseUpListener : function() {
      var shell = this._protocolCreateShell();
      shell.__listeners = {}; // HACK : Remove all listeners for testing
      this._protocolListen( { "MouseUp" : true } );
      assertFalse( shell.hasEventListeners( "mousedown" ) );
      assertTrue( shell.hasEventListeners( "mouseup" ) );
      this._disposeShell();
    },

    testNotifyMouseDown : function() {
      rwt.remote.EventUtil.setSuspended( true );
      var shell = this._protocolCreateShell();
      this._protocolListen( { "MouseDown" : true } );
      this._protocolSet( { "visibility" : true } );
      TestUtil.flush();
      rwt.remote.EventUtil.setSuspended( false );

      TestUtil.click( shell, 10, 20 );

      var message = TestUtil.getMessageObject();
      assertEquals( 1, message.findNotifyProperty( "w3", "MouseDown", "button" ) );
      assertEquals( 10, message.findNotifyProperty( "w3", "MouseDown", "x" ) );
      assertEquals( 20, message.findNotifyProperty( "w3", "MouseDown", "y" ) );
      assertNotNull( message.findNotifyProperty( "w3", "MouseDown", "time" ) );
      assertFalse( message.findNotifyProperty( "w3", "MouseDown", "shiftKey" ) );
      assertFalse( message.findNotifyProperty( "w3", "MouseDown", "ctrlKey" ) );
      assertFalse( message.findNotifyProperty( "w3", "MouseDown", "altKey" ) );
      this._disposeShell();
    },

    testNotifyMouseUp : function() {
      rwt.remote.EventUtil.setSuspended( true );
      var shell = this._protocolCreateShell();
      this._protocolListen( { "MouseUp" : true } );
      this._protocolSet( { "visibility" : true } );
      TestUtil.flush();
      rwt.remote.EventUtil.setSuspended( false );

      TestUtil.ctrlClick( shell );

      var message = TestUtil.getMessageObject();
      assertEquals( 1, message.findNotifyProperty( "w3", "MouseUp", "button" ) );
      assertEquals( 0, message.findNotifyProperty( "w3", "MouseUp", "x" ) );
      assertEquals( 0, message.findNotifyProperty( "w3", "MouseUp", "y" ) );
      assertNotNull( message.findNotifyProperty( "w3", "MouseUp", "time" ) );
      assertFalse( message.findNotifyProperty( "w3", "MouseUp", "shiftKey" ) );
      assertTrue( message.findNotifyProperty( "w3", "MouseUp", "ctrlKey" ) );
      assertFalse( message.findNotifyProperty( "w3", "MouseUp", "altKey" ) );
      this._disposeShell();
    },

    testNotifyMouseDoubleClick : function() {
      rwt.remote.EventUtil.setSuspended( true );
      var shell = this._protocolCreateShell();
      this._protocolListen( { "MouseDoubleClick" : true } );
      this._protocolSet( { "visibility" : true } );
      TestUtil.flush();
      rwt.remote.EventUtil.setSuspended( false );

      TestUtil.doubleClick( shell );

      var message = TestUtil.getMessageObject();
      assertEquals( 1, message.findNotifyProperty( "w3", "MouseDoubleClick", "button" ) );
      assertEquals( 0, message.findNotifyProperty( "w3", "MouseDoubleClick", "x" ) );
      assertEquals( 0, message.findNotifyProperty( "w3", "MouseDoubleClick", "y" ) );
      assertNotNull( message.findNotifyProperty( "w3", "MouseDoubleClick", "time" ) );
      assertFalse( message.findNotifyProperty( "w3", "MouseDoubleClick", "shiftKey" ) );
      assertFalse( message.findNotifyProperty( "w3", "MouseDoubleClick", "ctrlKey" ) );
      assertFalse( message.findNotifyProperty( "w3", "MouseDoubleClick", "altKey" ) );
      this._disposeShell();
    },

    testNotifyMouseDoubleClickWithAllListeners : function() {
      rwt.remote.EventUtil.setSuspended( true );
      var shell = this._protocolCreateShell();
      this._protocolListen( { "MouseDown" : true } );
      this._protocolListen( { "MouseDoubleClick" : true } );
      this._protocolListen( { "MouseUp" : true } );
      this._protocolSet( { "visibility" : true } );
      TestUtil.flush();
      rwt.remote.EventUtil.setSuspended( false );

      TestUtil.doubleClick( shell );

      assertEquals( 5, TestUtil.getRequestsSend() );
      var message = TestUtil.getMessageObject( 0 );
      assertNotNull( message.findNotifyOperation( "w3", "MouseDown" ) );
      message = TestUtil.getMessageObject( 1 );
      assertNotNull( message.findNotifyOperation( "w3", "MouseUp" ) );
      message = TestUtil.getMessageObject( 2 );
      assertNotNull( message.findNotifyOperation( "w3", "MouseDown" ) );
      message = TestUtil.getMessageObject( 3 );
      assertNotNull( message.findNotifyOperation( "w3", "MouseDoubleClick" ) );
      message = TestUtil.getMessageObject( 4 );
      assertNotNull( message.findNotifyOperation( "w3", "MouseUp" ) );
      this._disposeShell();
    },

    testKeyListener : function() {
      var shell = this._protocolCreateShell();
      this._protocolListen( { "KeyDown" : true } );
      assertTrue( shell.getUserData( "keyListener" ) );
      this._disposeShell();
    },

    testTraverseListener : function() {
      var shell = this._protocolCreateShell();
      this._protocolListen( { "Traverse" : true } );
      assertTrue( shell.getUserData( "traverseListener" ) );
      this._disposeShell();
    },

    testMenuDetectListener : function() {
      var shell = this._protocolCreateShell();
      shell.__listeners = {}; // HACK : Remove all listeners for testing
      this._protocolListen( { "MenuDetect" : true } );
      assertTrue( shell.hasEventListeners( "keydown" ) );
      assertTrue( shell.hasEventListeners( "mouseup" ) );
      this._disposeShell();
    },

    testNotifyMenuDetect : function() {
      rwt.remote.EventUtil.setSuspended( true );
      var shell = this._protocolCreateShell();
      this._protocolListen( { "MenuDetect" : true } );
      this._protocolSet( { "visibility" : true } );
      TestUtil.flush();
      rwt.remote.EventUtil.setSuspended( false );

      TestUtil.press( shell, "Apps", false, 0 );

      var message = TestUtil.getMessageObject( 1 );
      assertNotNull( message.findNotifyOperation( "w3", "MenuDetect" ) );
      this._disposeShell();
    },

    testHelpListener : function() {
      var shell = this._protocolCreateShell();
      shell.__listeners = {}; // HACK : Remove all listeners for testing
      this._protocolListen( { "Help" : true } );
      assertTrue( shell.hasEventListeners( "keydown" ) );
      this._disposeShell();
    },

    testNotifyHelp : function() {
      rwt.remote.EventUtil.setSuspended( true );
      var shell = this._protocolCreateShell();
      this._protocolListen( { "Help" : true } );
      this._protocolSet( { "visibility" : true } );
      TestUtil.flush();
      rwt.remote.EventUtil.setSuspended( false );

      TestUtil.press( shell, "F1", false, 0 );

      assertEquals( 2, TestUtil.getRequestsSend() );
      var message = TestUtil.getMessageObject( 1 );
      assertNotNull( message.findNotifyOperation( "w3", "Help" ) );
      this._disposeShell();
    },

    testDisposeShell : function() {
      var shell = this._protocolCreateShell();
      shell.setVisibility( true );
      var processor = rwt.remote.MessageProcessor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "destroy"
      } );
      assertFalse( shell.isDisposed() );
      assertFalse( shell.getVisibility() );
      TestUtil.flush();
      assertTrue( shell.isDisposed() );
    },

    testDisposeShellWithChildren : function() {
      var shell = this._protocolCreateShell();
      shell.setVisibility( true );
      MessageProcessor.processOperationArray( [ "create", "w4", "rwt.widgets.Composite", {
          "style" : [ "BORDER" ],
          "parent" : "w3"
        }
      ] );
      var child = ObjectRegistry.getObject( "w4" );

      MessageProcessor.processOperationArray( [ "destroy", "w3" ] );
      TestUtil.flush();

      assertTrue( ObjectRegistry.getObject( "w3" ) == null );
      assertTrue( shell.isDisposed() );
      assertTrue( ObjectRegistry.getObject( "w4" ) == null );
      assertTrue( child.isDisposed() );
    },

    /////////
    // Helper

    _disposeShell : function( id ) {
      TestUtil.flush(); // appear to call _beforeDisappear later
      var widgetManager = rwt.remote.WidgetManager.getInstance();
      var shell = widgetManager.findWidgetById( id ? id : "w3" );
      shell.getWindowManager().setActiveWindow( null ); // remove shell without setting another
      widgetManager.dispose( id ? id : "w3" );
    },

    _protocolCreateShell : function( id, parentId ) {
      var processor = rwt.remote.MessageProcessor;
      var props = {
        "style" : [ "BORDER", "APPLICATION_MODAL", "ON_TOP", "TITLE", "TOOL", "SHEET", "MIN" ]
      };
      if( parentId ) {
        props.parentShell = parentId;
      }
      processor.processOperation( {
        "target" : id ? id : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Shell",
        "properties" : props
      } );
      var widgetManager = rwt.remote.WidgetManager.getInstance();
      var result = widgetManager.findWidgetById( id ? id : "w3" );
      return result;
    },

    _protocolSet : function( properties ) {
      var processor = rwt.remote.MessageProcessor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "set",
        "properties" : properties
      } );
    },

    _protocolCall : function( targetId, method ) {
      var processor = rwt.remote.MessageProcessor;
      processor.processOperation( {
        "target" : targetId,
        "action" : "call",
        "method" : method,
        "properties" : {}
      } );
    },

    _protocolListen : function( properties ) {
      var processor = rwt.remote.MessageProcessor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "listen",
        "properties" : properties
      } );
    }

  }

} );

}());