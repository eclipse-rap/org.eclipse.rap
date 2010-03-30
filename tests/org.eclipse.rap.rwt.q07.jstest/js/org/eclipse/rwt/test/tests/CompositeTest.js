/*******************************************************************************
 * Copyright (c) 2010 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.test.tests.CompositeTest", {
  extend : qx.core.Object,
  
  members : {
    
    TARGETENGINE : [ "mshtml" ],
        
    testCompositeBackgroundInitial : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var composite = new org.eclipse.swt.widgets.Composite();
      composite.setBackgroundColor( null );
      composite.setBackgroundImage( null );
      composite.addToDocument();
      testUtil.flush();
      var image = composite._getTargetNode().style.backgroundImage;
      assertTrue( image.indexOf( "blank.gif" ) != -1 );
      composite.destroy();
      testUtil.flush();
    },
        
    testCompositeBackgroundFromColor : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var composite = new org.eclipse.swt.widgets.Composite();
      composite.setBackgroundColor( "green" );
      composite.setBackgroundImage( null );
      composite.addToDocument();
      testUtil.flush();
      var image = composite._getTargetNode().style.backgroundImage;
      assertEquals( "", image );
      composite.setBackgroundColor( null );
      image = composite._getTargetNode().style.backgroundImage;
      assertTrue( image.indexOf( "blank.gif" ) != -1 );
      composite.destroy();
      testUtil.flush();
    },
        
    testCompositeBackgroundFromImage : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var composite = new org.eclipse.swt.widgets.Composite();
      composite.setBackgroundColor( null );
      composite.setBackgroundImage( "bla.jpg" );
      composite.addToDocument();
      testUtil.flush();
      var image = composite._getTargetNode().style.backgroundImage;
      assertEquals( "url(bla.jpg)", image );
      composite.setBackgroundImage( null );
      image = composite._getTargetNode().style.backgroundImage;
      assertTrue( image.indexOf( "blank.gif" ) != -1 );
      composite.destroy();
      testUtil.flush();
    }

  }
  
} );