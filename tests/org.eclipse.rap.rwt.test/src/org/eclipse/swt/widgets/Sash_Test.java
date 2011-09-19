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
package org.eclipse.swt.widgets;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;


public class Sash_Test extends TestCase {

  private Shell shell;

  protected void setUp() throws Exception {
    Fixture.setUp();
    Display display = new Display();
    shell = new Shell( display, SWT.NONE );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testIsSerializable() throws Exception {
    Sash sash = new Sash( shell, SWT.NONE );
    sash.setSize( 1, 2 );
    
    Sash deserializedSash = Fixture.serializeAndDeserialize( sash );
    
    assertEquals( sash.getSize(), deserializedSash.getSize() );
  }
}
