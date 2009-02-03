/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.theme;

import java.io.*;

import junit.framework.TestCase;


public class AppearancesUtil_Test extends TestCase {

  public void testReadAppearanceFile() throws Exception {
    String input = "some ignored content here {\n"
      + "// any chars before ... BEGIN TEMPLATE ... and after are ok ...\n"
      + "This is the real content, line 1\n"
      + "and line 2\n"
      + "// this does not hurt END TEMPLATE and this does not hurt, too...\n"
      + "} and some more ignored content\n";
    InputStream inStream = new ByteArrayInputStream( input.getBytes() );
    String content = AppearancesUtil.readAppearanceFile( inStream );
    assertEquals( "This is the real content, line 1\nand line 2", content );
  }
}
