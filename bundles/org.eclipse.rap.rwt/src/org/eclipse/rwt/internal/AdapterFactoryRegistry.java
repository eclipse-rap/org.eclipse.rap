/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    Rüdiger Herrmann - bug 316961: Exception handling may fail in AdapterFactoryRegistry
 *    Frank Appel - replaced singletons and static fields (Bug 337787)
 ******************************************************************************/
package org.eclipse.rwt.internal;

import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.rwt.Adaptable;
import org.eclipse.rwt.AdapterFactory;
import org.eclipse.rwt.internal.service.ServletLog;
import org.eclipse.rwt.internal.util.*;


public class AdapterFactoryRegistry {
  private final List factories;
  
  private final static class FactoryEntry {
    private Class factoryClass;
    private Class adaptableClass;
  }
  
  public AdapterFactoryRegistry() {
    factories = new LinkedList();
  }

  public void add( Class factoryClass, Class adaptableClass ) {
    ParamCheck.notNull( factoryClass, "factoryClass" );
    ParamCheck.notNull( adaptableClass, "adaptableClass" );
    if( !AdapterFactory.class.isAssignableFrom( factoryClass ) ) {
      Object[] params = new Object[] {
        factoryClass.getName(),
        AdapterFactory.class.getName()
      };
      String text = "''{0}'' is not an instance of ''{1}''.";
      String msg = MessageFormat.format( text, params );
      throw new IllegalArgumentException( msg );
    }
    if( !Adaptable.class.isAssignableFrom( adaptableClass ) ) {
      Object[] params = new Object[] {
        adaptableClass.getName(),
        Adaptable.class.getName()
      };
      String text = "''{0}'' is not an instance of ''{1}''.";
      String msg = MessageFormat.format( text, params );
      throw new IllegalArgumentException( msg );
    }
    FactoryEntry[] entries = getEntries();
    for( int i = 0; i < entries.length; i++ ) {
      if(    entries[ i ].factoryClass == factoryClass
          && entries[ i ].adaptableClass == adaptableClass )
      {
        Object[] params = new Object[]{
          factoryClass.getName(),
          adaptableClass.getName()
        };
        String text = "The factory ''{0}'' was already added for the adaptable ''{1}''.";
        String msg = MessageFormat.format( text, params );
        throw new IllegalArgumentException( msg );
      }
    }
    FactoryEntry factoryEntry = new FactoryEntry();
    factoryEntry.factoryClass = factoryClass;
    factoryEntry.adaptableClass = adaptableClass;
    factories.add( factoryEntry );
  }

  public void register() {
    FactoryEntry[] entries = getEntries();
    for( int i = 0; i < entries.length; i++ ) {
      Class clazz = entries[ i ].factoryClass;
      try {
        AdapterFactory factory = ( AdapterFactory )ClassUtil.newInstance( clazz );
        AdapterManagerImpl.getInstance().registerAdapters( factory, entries[ i ].adaptableClass );
      } catch( ClassInstantiationException cie ) {
        String text = "Could not create an instance of ''{0}''.";
        String msg = MessageFormat.format( text, new Object[] { clazz } );
        ServletLog.log( msg, cie );
      }
    }
  }
  
  //////////////////
  // helping methods

  private FactoryEntry[] getEntries() {
    FactoryEntry[] entries = new FactoryEntry[ factories.size() ];
    factories.toArray( entries );
    return entries;
  }
}
