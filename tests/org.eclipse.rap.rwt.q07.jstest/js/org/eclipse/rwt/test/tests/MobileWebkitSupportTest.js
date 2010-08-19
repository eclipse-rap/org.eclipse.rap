/*******************************************************************************
 * Copyright (c) 2010 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.test.tests.MobileWebkitSupportTest", {
  extend : qx.core.Object,

  members : {

    TARGETENGINE : [ "webkit" ],
    TARGETPLATFORM : [ "iphone", "ipad" ],
    
    testTabHighlightHidden : function() {
    	var head = document.childNodes[ 0 ].childNodes[ 0 ];
    	var headertext = head.innerHTML;
    	var expected = "* { -webkit-tap-highlight-color: rgba(0,0,0,0); }";
    	assertTrue( headertext.indexOf( expected ) != -1 );
    }

  }

} );