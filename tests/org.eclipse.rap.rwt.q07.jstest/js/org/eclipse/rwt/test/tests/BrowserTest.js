/*******************************************************************************
 * Copyright (c) 2010 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.test.tests.BrowserTest", {
  extend : qx.core.Object,
  
  members : {
    BLANK : "../org.eclipse.rap.rwt.q07/resources/resource/static/html/blank.html",
    
    // TODO [tb] : Real meaningful tests for the browser widget can only be 
    // written after fixing Bug 330583. 
        
    testDispose : function() {
      // See Bug 327440 - Memory leak problem with Iframe in Internet Explorer
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      // We will dispatch the load event ourselves.
      var loadOrg = qx.ui.embed.Iframe.load;
      qx.ui.embed.Iframe.load = function(){};
      var wm = org.eclipse.swt.WidgetManager.getInstance();      
      var browser = new org.eclipse.swt.browser.Browser();
      wm.add( browser, "w6", true );
      browser.addToDocument();
      browser.addState( "rwt_BORDER" );
      browser.setSpace( 10, 576, 57, 529 );
      browser.setSource( this.BLANK );
      testUtil.flush();
      assertTrue( browser.isSeeable() );
      var el = browser._getTargetNode();
      var iframe = browser._iframeNode;
      qx.ui.embed.Iframe.load( iframe );
      assertTrue( iframe.parentNode === el );
      wm.dispose( "w6" );
      testUtil.flush();
      assertFalse( browser.isDisposed() );
      assertTrue( wm.findWidgetById( "w6" ) == null ); /* may be undefined */
      assertEquals( "javascript:false;", browser.getSource() );
      assertIdentical( testUtil.getDocument(), browser.getParent() );
      assertTrue( browser.isSeeable() );
      browser.createDispatchEvent( "load" );
      testUtil.flush();
      assertTrue( "disposed?", browser.isDisposed() );
      assertTrue( el.innerHTML === "" );
      assertTrue( iframe.parentNode == null );
      var loadOrg = qx.ui.embed.Iframe.load;
      browser.destroy();
    }
    
 }
  
} );