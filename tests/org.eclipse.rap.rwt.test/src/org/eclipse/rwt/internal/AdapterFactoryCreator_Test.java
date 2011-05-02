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
package org.eclipse.rwt.internal;

import org.eclipse.rwt.AdapterFactory;

import junit.framework.TestCase;


public class AdapterFactoryCreator_Test extends TestCase {

  public static class ProperAdapterFactory implements AdapterFactory {
    public Object getAdapter( Object adaptable, Class adapter ) {
      return null;
    }
    public Class[] getAdapterList() {
      return null;
    }
  }

  private static class AdapterFactoryWithoutDefaultCtor implements AdapterFactory {
    private AdapterFactoryWithoutDefaultCtor( Object foo ) {
    }
    public Object getAdapter( Object adaptable, Class adapter ) {
      return null;
    }
    public Class[] getAdapterList() {
      return null;
    }
  }

  private static class AdapterFactoryWithPrivateDefaultCtor implements AdapterFactory {
    private AdapterFactoryWithPrivateDefaultCtor() {
    }
    public Object getAdapter( Object adaptable, Class adapter ) {
      return null;
    }
    public Class[] getAdapterList() {
      return new Class[] { Runnable.class };
    }
  }
  
  private static abstract class AbstractAdapterFactory implements AdapterFactory {
  }
  
  public void testCreate() {
    AdapterFactory adapterFactory = AdapterFactoryCreator.create( ProperAdapterFactory.class );
    assertEquals( ProperAdapterFactory.class, adapterFactory.getClass() );
  }

  public void testCreateWithNullFactoryClass() {
    try {
      AdapterFactoryCreator.create( null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  public void testCreateWithInvalidFactoryClass() {
    try {
      AdapterFactoryCreator.create( Object.class );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }
  
  public void testCreateAbstractAdapterFactory() {
    try {
      AdapterFactoryCreator.create( AbstractAdapterFactory.class );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }
  
  public void testCreateAdapterFactoryWithoutDefaultConstructor() {
    try {
      AdapterFactoryCreator.create( AdapterFactoryWithoutDefaultCtor.class );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }

  public void testCreateAdapterFactoryWithPrivateDefaultConstructor() {
    try {
      AdapterFactoryCreator.create( AdapterFactoryWithPrivateDefaultCtor.class );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }
}
