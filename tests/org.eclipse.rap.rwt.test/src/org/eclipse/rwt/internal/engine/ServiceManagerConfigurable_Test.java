/*******************************************************************************
 * Copyright (c) 2011 Frank Appel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Frank Appel - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.engine;

import java.util.HashMap;

import junit.framework.TestCase;

import org.eclipse.rwt.internal.lifecycle.UICallBackServiceHandler;
import org.eclipse.rwt.internal.resources.JSLibraryServiceHandler;
import org.eclipse.rwt.internal.service.ServiceManager;
import org.eclipse.rwt.service.IServiceHandler;


public class ServiceManagerConfigurable_Test extends TestCase {
  private static final String ID_JS_LIBRARIES = JSLibraryServiceHandler.HANDLER_ID;
  private static final String ID_UI_CALL_BACK = UICallBackServiceHandler.HANDLER_ID;
  
  private HashMap log;

  private class TestApplicationContext extends ApplicationContext {
    public ServiceManager getServiceManager() {
      return new ServiceManager( null ) {
        public void registerServiceHandler( String id, IServiceHandler handler ) {
          log.put( id, handler );
        }
        public void unregisterServiceHandler( String id ) {
          log.put( id, null );
        }
      };
    }
  }
  

  public void testConfigure() {
    ServiceManagerConfigurable configurable = new ServiceManagerConfigurable();
    
    configurable.configure( new TestApplicationContext() );
    
    assertTrue( log.get( ID_UI_CALL_BACK ) instanceof UICallBackServiceHandler );
    assertTrue( log.get( ID_JS_LIBRARIES ) instanceof JSLibraryServiceHandler );
  }
  
  public void testReset() {
    ServiceManagerConfigurable configurable = new ServiceManagerConfigurable();

    configurable.reset( new TestApplicationContext() );
    
    assertTrue( log.containsKey( ID_UI_CALL_BACK ) );
    assertTrue( log.containsKey( ID_JS_LIBRARIES ) );
  }
  
  protected void setUp() throws Exception {
    log = new HashMap();
  }
}
