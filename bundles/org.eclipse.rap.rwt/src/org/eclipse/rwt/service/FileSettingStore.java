/*******************************************************************************
 * Copyright (c) 2002, 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.service;

import java.io.*;
import java.util.*;

import org.eclipse.rwt.RWT;
import org.eclipse.rwt.internal.util.ParamCheck;

/**
 * This {@link ISettingStore} implementation persists all settings on the
 * file system using Java {@link Properties}.
 * <p>
 * @since 1.1
 */
public final class FileSettingStore implements ISettingStore {
  
  /**
   * This key (value "org.eclipse.rwt.service.FileSettingStore.dir") can be
   * used to configure the working directory for file settings stores.
   * See {@link RWTFileSettingStoreFactory} and 
   * <code>WorkbenchFileSettingStoreFactory</code>.
   */
  public static final String FILE_SETTING_STORE_DIR
    = "org.eclipse.rwt.service.FileSettingStore.dir";
  
  private static final Random RANDOM = new Random( System.currentTimeMillis() ); 
  
  private final File workDir;
  private final Properties props = new Properties();
  private final Set listeners = new HashSet();

  private String id;

  /**
   * Create a {@link FileSettingStore} instance. The store will be initialized
   * with a unique random id and will contain no attributes. Use 
   * {@link #loadById(String)} to initialize an existing store with previously
   * persisted attributes.
   * 
   * @param workDir a non-null File instance denoting an existing directory,
   *        which will be used by this class persist its settings.
   * @throws NullPointerException if the argument workDir is <code>null</code>
   * @throws IllegalArgumentException if the argument workDir is not a directory
   * @see #loadById(String)
   */
  public FileSettingStore( final File workDir ) {
    ParamCheck.notNull( workDir, "workDir" );
    if( !workDir.isDirectory() ) {
      String msg = "workDir is not a directory: " + workDir;
      throw new IllegalArgumentException( msg );
    }
    this.workDir = workDir;
    id = String.valueOf( System.currentTimeMillis() ) 
         + "_" 
         + RANDOM.nextInt( Short.MAX_VALUE );
  }
  
  ////////////////////////
  // ISettingStore methods
  
  public String getId() {
    return id;
  }
  
  public synchronized String getAttribute( final String name ) {
    ParamCheck.notNull( name, "name" );
    return props.getProperty( name );
  }

  public synchronized void setAttribute( final String name, 
                                         final String value ) 
    throws SettingStoreException 
  {
    ParamCheck.notNull( name, "name" );
    if( value == null ) {
      removeAttribute( name );
    } else {
      String oldValue = ( String )props.setProperty( name, value );
      if( !value.equals( oldValue ) ) {
        notifyListeners( name, oldValue, value );
        persist();
      }
    }
  }

  public synchronized Enumeration getAttributeNames() {
    return props.keys();
  }

  public synchronized void loadById( final String id ) 
    throws SettingStoreException 
  {
    ParamCheck.notNullOrEmpty( id, "id" );
    this.id = id;
    notifyForEachAttribute( true );
    props.clear();
    
    BufferedInputStream bis = getInputStream( id );
    if( bis != null ) {
      try {
        try {
          props.load( bis );
          notifyForEachAttribute( false );
        } finally {
          bis.close();
        }
      } catch( IOException ioe ) {
        String msg = "Failed to load into file setting store; id= " + id;
        throw new SettingStoreException( msg, ioe );
      }
    }
  }
  
  public synchronized void removeAttribute( final String name ) 
    throws SettingStoreException 
  {
    String oldValue = ( String )props.remove( name );
    if( oldValue != null ) {
      notifyListeners( name, oldValue, null );
      persist();
    }
  }

  public synchronized void addSettingStoreListener( final SettingStoreListener listener ) {
    ParamCheck.notNull( listener, "listener" );
    listeners.add( listener );
  }

  public synchronized void removeSettingStoreListener( final SettingStoreListener listener ) {
    ParamCheck.notNull( listener, "listener" );
    listeners.remove( listener );
  }
  
  
  //////////////////
  // helping methods
  
  /**
   * @return a BufferedInputStream or <code>null</code> if this file 
   *         does not exist
   */
  private BufferedInputStream getInputStream( final String streamId ) {
    BufferedInputStream result = null;
    File file = getStoreFile( streamId );
    if( file.exists() ) {
      try {
        result = new BufferedInputStream( new FileInputStream( file ) );
      } catch( FileNotFoundException fnf ) {
        log( "Should not happen", fnf );
      }
    }
    return result;
  }
  
  private BufferedOutputStream getOutputStream( final String streamId ) 
  throws FileNotFoundException {
    File file = getStoreFile( streamId );
    return new BufferedOutputStream( new FileOutputStream( file ) );
  }
  
  private File getStoreFile( final String fileName ) {
    return new File( workDir, fileName );
  }

  private void log( final String msg, final Throwable throwable ) {
    RWT.getRequest().getSession().getServletContext().log( msg, throwable );
  }
  
  private synchronized void notifyForEachAttribute( final boolean removed ) {
    Enumeration attributes = props.keys();
    while( attributes.hasMoreElements() ) {
      String attribute = ( String )attributes.nextElement();
      String value = props.getProperty( attribute );
      if( removed ) {
        notifyListeners( attribute, value, null );
      } else {
        notifyListeners( attribute, null, value );
      }
    }
  }

  private synchronized void notifyListeners( final String attribute,
                                             final String oldValue, 
                                             final String newValue ) {
    SettingStoreEvent event 
      = new SettingStoreEvent( this, attribute, oldValue, newValue );
    Iterator iter = listeners.iterator();
    while( iter.hasNext() ) {
      SettingStoreListener listener = ( SettingStoreListener )iter.next();
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
  
  private void persist() throws SettingStoreException {
    try {
      BufferedOutputStream bos = getOutputStream( id );
      try {
        props.store( bos, FileSettingStore.class.getName() );
      } finally {
        bos.close();
      }
    } catch( IOException ioe ) {
      String msg = "Failed to persist file setting store; id= " + id;
      throw new SettingStoreException( msg, ioe );
    }
  }

}
