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
package org.eclipse.rwt.lifecycle;

import junit.framework.TestCase;


public class DefaultEntryPointFactory_Test extends TestCase {
  
  public static class TestEntryPoint implements IEntryPoint {
    public int createUI() {
      return 0;
    }
  }
  
  public void testConstructorWithNullParam() {
    try {
      new DefaultEntryPointFactory( ( Class<? extends IEntryPoint> )null );
      fail( "null-entrypoint not allowed" );
    } catch( NullPointerException expected ) {
    }
  }
  
  public void testCreate() {
    DefaultEntryPointFactory factory = new DefaultEntryPointFactory( TestEntryPoint.class );
    
    IEntryPoint entryPoint = factory.create();
    
    assertTrue( entryPoint instanceof TestEntryPoint );
  }
}
