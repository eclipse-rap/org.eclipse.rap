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

import java.lang.reflect.Modifier;
import java.text.MessageFormat;

import org.eclipse.rwt.AdapterFactory;
import org.eclipse.rwt.internal.util.ClassUtil;
import org.eclipse.rwt.internal.util.ParamCheck;


class AdapterFactoryCreator {
  
  static AdapterFactory create( Class adapterFactoryClass ) {
    return new AdapterFactoryCreator( adapterFactoryClass ).create();
  }
  
  private final Class adapterFactoryClass;

  private AdapterFactoryCreator( Class adapterFactoryClass ) {
    ParamCheck.notNull( adapterFactoryClass, "adapterFactoryClass" );
    this.adapterFactoryClass = adapterFactoryClass;
  }
  
  private AdapterFactory create() {
    checkImplementsAdapterFactory( );
    checkIsNotAbstract();
    checkHasDefaultConstructor();
    return ( AdapterFactory )ClassUtil.newInstance( adapterFactoryClass );
  }

  private void checkImplementsAdapterFactory() {
    if( !AdapterFactory.class.isAssignableFrom( adapterFactoryClass ) ) {
      String text = "The factoryClass must implement {0}.";
      String msg = MessageFormat.format( text, new Object[] { AdapterFactory.class.getName() } );
      throw new IllegalArgumentException( msg );
    }
  }

  private void checkIsNotAbstract() {
    if( Modifier.isAbstract( adapterFactoryClass.getModifiers() ) ) {
      throw new IllegalArgumentException( "The factoryClass must not be abstract." );
    }
  }

  private void checkHasDefaultConstructor() {
    if( !hasDefaultConstructor() ) {
      String msg = "The factoryClass must provide a public default constructor.";
      throw new IllegalArgumentException( msg );
    }
  }
  
  private boolean hasDefaultConstructor() {
    boolean result;
    try {
      adapterFactoryClass.getConstructor( null );
      result = true;
    } catch( NoSuchMethodException nsme ) {
      result = false;
    }
    return result ;
  }
}
