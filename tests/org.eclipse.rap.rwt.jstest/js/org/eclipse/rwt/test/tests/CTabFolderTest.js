/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.test.tests.CTabFolderTest", {
  extend : qx.core.Object,
  
  members : {
            
    testSetBackgroundImage : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var folder = new org.eclipse.swt.custom.CTabFolder();
      folder.setUserData( "backgroundImageSize", [ 50, 50 ] );
      folder.setBackgroundImage( "bla.jpg" );
      assertEquals( "bla.jpg", folder._body.getBackgroundImage() );
      assertEquals( [ 50, 50 ], folder._body.getUserData( "backgroundImageSize" ) );
    }

  }
  
} );