/*******************************************************************************
 * Copyright (c) 2002-2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rwt.internal.service;

import java.util.*;

import org.eclipse.rwt.RWT;
import org.eclipse.rwt.internal.util.ParamCheck;
import org.eclipse.rwt.service.*;

/**
 * This {@link ISettingStore} implementation "persists" all settings
 * in memory, for as long the application is running.
 * <p>
 * <b>This strategy results in an ever increasing memory
 * consuption over time</b>. We do <b>*not*</b> recommend using this 
 * implementation in a production environment.
 */
public final class MemorySettingStore implements ISettingStore {

  private static final Map VALUES = new HashMap();
  private static final Set LISTENERS = new HashSet();

  private String id;
  
  /**
   * Create a {@link MemorySettingStore} instance and containing the
   * attributes persisted under the given <code>id</code>.
   * 
   * @param id a non-null; non-empty; non-whitespace-only String
   * @throws NullPointerException if id is <code>null</null>
   * @throws IllegalArgumentException if id is empty or composed
   *         entirely of whitespace
   */
  public MemorySettingStore( final String id ) {
    ParamCheck.notNullOrEmpty( id, "id" );
    this.id = id;
  }
  

  ////////////////////////
  // ISettingStore methods 
  
  public String getId() {
    return id;
  }
  
  public synchronized void loadById( final String id ) {
    ParamCheck.notNullOrEmpty( id, "id" );
    fakeRemoval();
    this.id = id;
    loadAttributes();
  }
  
  public synchronized String getAttribute( final String name ) {
    ParamCheck.notNull( name, "name" );
    String key = id + name;
    return ( String )VALUES.get( key );
  }

  public synchronized Enumeration getAttributeNames() {
    List result = new ArrayList();
    Iterator iter = VALUES.keySet().iterator();
    int nameBeginIndex = id.length();
    while( iter.hasNext() ) {
      String key = ( String )iter.next();
      if( key.startsWith( id ) ) {
        result.add( key.substring( nameBeginIndex ) );
      }
    }
    final Iterator resultIterator = result.iterator();
    return new Enumeration() {
      public boolean hasMoreElements() {
        return resultIterator.hasNext();
      }
      public Object nextElement() {
        return resultIterator.next();
      }
    };
  }

  public synchronized void removeAttribute( final String name ) {
    ParamCheck.notNull( name, "name" );
    String key = id + name;
    String oldValue = ( String )VALUES.remove( key );
    if( oldValue != null ) {
      notifyListeners( name, oldValue, null );
    }
  }

  public synchronized void setAttribute( final String name, 
                                         final String value ) {
    ParamCheck.notNull( name, "name" );
    if( value == null ) {
      removeAttribute( name );
    } else {
      ParamCheck.notNull( value, "value" );
      String key = id + name;
      String oldValue = ( String )VALUES.put( key, value );
      if( !value.equals( oldValue ) ) {
        notifyListeners( name, oldValue, value );
      }
    }
  }

  public synchronized void addSettingStoreListener( 
    final ISettingStoreListener listener ) 
  {
    ParamCheck.notNull( listener, "listener" );
    LISTENERS.add( listener );
  }

  public synchronized void removeSettingStoreListener( 
    final ISettingStoreListener listener ) 
  {
    ParamCheck.notNull( listener, "listener" );
    LISTENERS.remove( listener );
  }
  
  
  //////////////////
  // helping methods
  
  private void fakeRemoval() {
    Enumeration attributes = getAttributeNames();
    while( attributes.hasMoreElements() ) {
      String name = ( String )attributes.nextElement();
      String key = id + name;
      String value = ( String )VALUES.get( key );
      notifyListeners( name, value, null );
    }
  }
  
  private synchronized void loadAttributes() {
    Enumeration attributes = getAttributeNames();
    while( attributes.hasMoreElements() ) {
      String name = ( String )attributes.nextElement();
      String key = id + name;
      String value = ( String )VALUES.get( key );
      notifyListeners( name, null, value );
    }
  }
  
  private void log( final String msg, final Throwable throwable ) {
    RWT.getRequest().getSession().getServletContext().log( msg, throwable );
  }
  
  private synchronized void notifyListeners( final String attribute,
                                             final String oldValue, 
                                             final String newValue ) {
    ISettingStoreEvent event 
      = new SettingStoreEvent( attribute, oldValue, newValue );
    Iterator iter = LISTENERS.iterator();
    while( iter.hasNext() ) {
      ISettingStoreListener listener = ( ISettingStoreListener )iter.next();
      try {
        listener.settingChanged( event );
      } catch( Exception exc ) {
        String msg =   "Exception when invoking listener " 
                     + listener.getClass().getName();
        log( msg, exc );
      } catch( LinkageError le ) {
        String msg =   "Linkage error when invoking listener "
                     + listener.getClass().getName();
        log( msg, le );
      }
    }
  }
}
