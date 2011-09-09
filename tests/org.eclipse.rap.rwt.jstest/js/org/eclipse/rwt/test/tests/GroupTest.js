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

qx.Class.define( "org.eclipse.rwt.test.tests.GroupTest", {

  extend : qx.core.Object,

  members : {

    testCreateGroupByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Group",
        "properties" : {
          "style" : [],
          "parent" : "w2"
        }
      } );
      var objectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = objectManager.getObject( "w3" );
      assertTrue( widget instanceof org.eclipse.swt.widgets.Group );
      assertIdentical( shell, widget.getParent() );
      assertTrue( widget.getUserData( "isControl") );
      assertEquals( "group-box", widget.getAppearance() );
      shell.destroy();
      widget.destroy();
    },

    testSetTextByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Group",
        "properties" : {
          "style" : [],
          "parent" : "w2",
          "text" : "foo && <> \" bar"
        }
      } );
      var objectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = objectManager.getObject( "w3" );
      assertEquals( "foo &amp; &lt;&gt; &quot; bar", widget.getLegend() );
      shell.destroy();
      widget.destroy();
    }

  }
  
} );