/*******************************************************************************
 * Copyright (c) 2010 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.test.tests.IFrameTest", {

  extend : qx.core.Object,

  members : {

    testIFrameDimension : function() {
      var platform = qx.core.Client.getPlatform();
      if( platform !== "iphone" && platform !== "ipad" ) {
        var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
        var iframe = new qx.ui.embed.Iframe();
        iframe.addToDocument();
        iframe.setWidth( 300 );
        iframe.setHeight( 400 );
        testUtil.flush();
        var node = iframe.getIframeNode();
        var widgetNode = iframe.getElement();
        assertEquals( "100%", node.width );
        assertEquals( "100%", node.height );
        assertEquals( "", node.style.width );
        assertEquals( "", node.style.height );
        assertEquals( "", node.style.minWidth );
        assertEquals( "", node.style.minHeight );
        assertEquals( "", node.style.maxWidth );
        assertEquals( "", node.style.maxHeight );
        assertEquals( 300, parseInt( widgetNode.style.width ) );
        assertEquals( 400, parseInt( widgetNode.style.height ) );
      }
    }

  }

} );