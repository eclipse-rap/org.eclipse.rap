/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing implementation
 *    Frank Appel - replaced singletons and static fields (Bug 337787)
 ******************************************************************************/
package org.eclipse.swt.graphics;

import java.io.InputStream;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.swt.SWT;


public class ImageData_Test extends TestCase {
  
  public void testImageData() {
    ClassLoader loader = Fixture.class.getClassLoader();
    InputStream inputStream = loader.getResourceAsStream( Fixture.IMAGE_100x50 );
    assertNotNull( inputStream );
    ImageData[] datas = ImageDataLoader.load( inputStream );
    assertNotNull( datas );
    assertEquals( 1, datas.length );
    ImageData data = datas[ 0 ];
    assertNotNull( data );
    assertEquals( 100, data.width );
    assertEquals( 50, data.height );
    assertEquals( SWT.IMAGE_PNG, data.type );
  }
}
