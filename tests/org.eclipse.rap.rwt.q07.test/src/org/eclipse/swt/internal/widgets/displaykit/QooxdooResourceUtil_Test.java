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
package org.eclipse.swt.internal.widgets.displaykit;

import org.eclipse.rwt.*;
import org.eclipse.rwt.internal.resources.ResourceManager;
import org.eclipse.rwt.resources.IResourceManager;
import org.eclipse.rwt.resources.IResourceManagerFactory;
import junit.framework.TestCase;

public class QooxdooResourceUtil_Test extends TestCase {

  private final static String CLIENT_JS = "client.js";
  private final static String CORE_JS = "qx/lang/Core.js";

  private final class EmptyInitializer implements Runnable {
    public void run() {
    }
  }

  private final class Test2ResourceManagerFactory implements IResourceManagerFactory {
    public IResourceManager create() {
      return new Test2ResourceManager();
    }
  }

  private final class Test2ResourceManager extends TestResourceManager {
    public void register( String name, String charset, RegisterOptions options ) {
      super.register( name, charset, options );
      if( name.equals( CORE_JS ) ) {
        isCompressed =    options == RegisterOptions.COMPRESS
                       || options == RegisterOptions.VERSION_AND_COMPRESS;
      }
    }
  }

  private boolean isCompressed;

  public void testRegisterResources() throws Exception {
    QooxdooResourcesUtil.registerResources();
    IResourceManager resourceManager = ResourceManager.getInstance();
    assertFalse( resourceManager.isRegistered( CLIENT_JS ) );
    assertTrue( resourceManager.isRegistered( CORE_JS ) );
    assertTrue( isCompressed );
  }

  public void testRegisterResourcesDebug() throws Exception {
    System.setProperty( "org.eclipse.rwt.clientLibraryVariant", "DEBUG" );
    QooxdooResourcesUtil.registerResources();
    IResourceManager resourceManager = ResourceManager.getInstance();
    assertFalse( resourceManager.isRegistered( CLIENT_JS ) );
    assertTrue( resourceManager.isRegistered( CORE_JS ) );
    assertFalse( isCompressed );
  }

  protected void setUp() throws Exception {
    Fixture.createRWTContext( new EmptyInitializer() );
    Fixture.createServiceContext();
    ResourceManager.register( new Test2ResourceManagerFactory() );
    isCompressed = false;
  }

  protected void tearDown() throws Exception {
    Fixture.disposeOfServiceContext();
    Fixture.disposeOfRWTContext();
  }
}
