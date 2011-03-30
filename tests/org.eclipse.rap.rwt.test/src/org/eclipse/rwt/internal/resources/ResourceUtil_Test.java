/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.resources;

import java.io.IOException;

import junit.framework.TestCase;


public class ResourceUtil_Test extends TestCase {

  public void testCompress() {
    StringBuffer javaScript = new StringBuffer(
        "/********************************************************\n"
      + "* Copyright (c) 2008 Innoopract Informationssysteme GmbH.\n"
      + "********************************************************/\n"
      + "\n"
      + "qx.Class.define( \"org.eclipse.swt.widgets.Test\", {\n"
      + "  extend : qx.ui.layout.CanvasLayout,\n"
      + "\n"
      + "  construct : function( style ) {\n"
      + "    this.base( arguments );\n"
      + "  },\n"
      + "\n"
      + "  members : {\n"
      + "    // TODO: Fix me\n"
      + "    setValue : function( value ) {\n"
      + "      this._value = value;\n"
      + "      this._url = \"http://www.eclipse.org\";\n"
      + "      this._comment = \"/* This is a comment inside string*/\";\n"
      + "    }\n"
      + "  }\n"
      + "} );"
    );
    String expected
      = "qx.Class.define(\"org.eclipse.swt.widgets.Test\",{"
      + "extend:qx.ui.layout.CanvasLayout,"
      + "construct:function(a){"
      + "arguments.callee.base.call(this)"
      + "},"
      + "members:{"
      + "setValue:function(a){"
      + "this._value=a;"
      + "this._url=\"http://www.eclipse.org\";"
      + "this._comment=\"/* This is a comment inside string*/\""
      + "}}});";
    try {
      ResourceUtil.compress( javaScript );
    } catch( IOException e ) {
      fail( "Should not throw exception" );
    }
    assertEquals( expected, javaScript.toString() );
  }
}
