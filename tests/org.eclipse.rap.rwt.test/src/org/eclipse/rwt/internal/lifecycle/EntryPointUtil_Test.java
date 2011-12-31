/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.lifecycle;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rwt.internal.application.RWTFactory;
import org.eclipse.rwt.internal.branding.TestBranding;
import org.eclipse.rwt.internal.service.RequestParams;


public class EntryPointUtil_Test extends TestCase {

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testFindEntryPoint_default() {
    assertEquals( EntryPointManager.DEFAULT, EntryPointUtil.findEntryPoint() );
  }

  public void testFindEntryPoint_withStartupParameter() {
    Fixture.fakeRequestParam( RequestParams.STARTUP, "foo" );

    assertEquals( "foo", EntryPointUtil.findEntryPoint() );
  }

  public void testFindEntryPoint_withBranding() {
    // register a branding with the default servlet name ("rap")
    TestBranding branding = new TestBranding( "rap", null, "foo" );
    RWTFactory.getBrandingManager().register( branding );

    assertEquals( "foo", EntryPointUtil.findEntryPoint() );
  }

}
