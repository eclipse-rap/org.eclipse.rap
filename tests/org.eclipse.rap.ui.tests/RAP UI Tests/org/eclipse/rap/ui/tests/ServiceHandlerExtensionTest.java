/*******************************************************************************
 * Copyright (c) 2010 EclipseSource Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     EclipseSource - initial API and implementation
 ******************************************************************************/

package org.eclipse.rap.ui.tests;

import junit.framework.TestCase;

import org.eclipse.rap.ui.tests.impl.ServiceHandler1;
import org.eclipse.rap.ui.tests.impl.ServiceHandler2;
import org.eclipse.rwt.internal.service.ServiceManager;
import org.eclipse.rwt.service.IServiceHandler;

public class ServiceHandlerExtensionTest extends TestCase {
  
  public void testServieHandlerRegistration() {
    IServiceHandler handler = ServiceManager.getCustomHandler( "myHandler1" );
    assertNotNull( handler );
    assertTrue( handler instanceof ServiceHandler1 );
    handler = ServiceManager.getCustomHandler( "myHandler2" );
    assertNotNull( handler );
    assertTrue( handler instanceof ServiceHandler2 );
  }
  
}
