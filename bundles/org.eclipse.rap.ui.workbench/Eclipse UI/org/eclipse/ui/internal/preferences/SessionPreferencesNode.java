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

package org.eclipse.ui.internal.preferences;

import java.util.*;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IPreferenceNodeVisitor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.rwt.RWT;
import org.eclipse.rwt.internal.util.ParamCheck;
import org.eclipse.rwt.service.ISettingStore;
import org.eclipse.rwt.service.SettingStoreException;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

/**
 * This node use the RWT setting store to persist its preferences.
 */
final class SessionPreferencesNode 
  implements IEclipsePreferences 
{

  private static final String PATH_SEPARATOR = "/"; //$NON-NLS-1$
  private static final String DOUBLE_PATH_SEPARATOR = "//"; //$NON-NLS-1$
  private static final String TRUE  = "true"; //$NON-NLS-1$
  private static final String FALSE = "false"; //$NON-NLS-1$
  
  private final String name;
  private final IEclipsePreferences parent;
  private boolean isRemoved;
  /* cache the absolutePath once it has been computed */
  private String absolutePath;
  
  private final Map children = new HashMap();    // !thread safe
  
  SessionPreferencesNode( final IEclipsePreferences parent, 
                          final String name ) {
    ParamCheck.notNull( parent, "parent" ); //$NON-NLS-1$
    ParamCheck.notNull( name, "name" ); //$NON-NLS-1$
    checkName( name );
    this.parent = parent;
    this.name = name;
  }
  
  public void accept( final IPreferenceNodeVisitor visitor )
    throws BackingStoreException
  {
    boolean withChildren = visitor.visit( this );
    if( withChildren ) {
      Object[] childrenArray;
      synchronized( this ) {
        childrenArray = children.values().toArray();
      }
      for( int i = 0; i < childrenArray.length; i++ ) {
        IEclipsePreferences child = ( IEclipsePreferences )childrenArray[ i ];
        child.accept( visitor );
      }
    }
  }

  public void addNodeChangeListener( final INodeChangeListener listener ) {
    checkRemoved();
    if( listener != null ) {
      getNodeCore().addNodeChangeListener( listener );
    }
  }

  public void addPreferenceChangeListener( 
    final IPreferenceChangeListener listener )
  {
    checkRemoved();
    getNodeCore().addPreferenceChangeListener( listener );
  }

  public Preferences node( final String path ) {
    checkPath( path );
    checkRemoved();
    Preferences result;
    if( "".equals( path ) ) { // "" //$NON-NLS-1$
      result = this;
    } else if( path.startsWith( PATH_SEPARATOR ) ) { // "/absolute/path"
      result = findRoot().node( path.substring( 1 ) );
    } else if( path.indexOf( PATH_SEPARATOR ) > 0 ) { // "foo/bar/baz"
      int index = path.indexOf( PATH_SEPARATOR );
      String nodeName = path.substring( 0, index );
      String rest = path.substring( index + 1, path.length() ); 
      result = getChild( nodeName, true ).node( rest );
    } else { // "foo"
      result = getChild( path, true );
    }
    return result;
  }

  public synchronized void removeNode() throws BackingStoreException {
    checkRemoved();
    // remove all preferences
    clear(); 
    // remove all children
    Object[] childNodes = children.values().toArray();
    for( int i = 0; i < childNodes.length; i++ ) {
      Preferences child = ( Preferences )childNodes[ i ];
      if( child.nodeExists( "" ) ) { // if !removed //$NON-NLS-1$
        child.removeNode();
      }
    }
    // remove from parent; this is ugly, because the interface 
    // Preference has no API for removing oneself from the parent.
    // In general the parent will be a SessionPreferencesNode.
    // The only case in the workbench where this is not true, is one level
    // below the root (i.e. at /session ), but the scope root must not
    // be removable (see IEclipsePreferences#removeNode())
    if( parent instanceof SessionPreferencesNode ) {
      // this means: 
      // (a) we know what kind of parent we have, and 
      // (b) we are not the scope root, since that has a 
      /// RootPreference as a parent
      SessionPreferencesNode spnParent 
        = ( ( SessionPreferencesNode ) parent );
      spnParent.children.remove( name );
      spnParent.fireNodeEvent( this, false );

      // the listeners are not needed anymore
      getNodeCore().clear();
      children.clear();
      isRemoved = true;
    }
  }

  public void removeNodeChangeListener( final INodeChangeListener listener ) {
    checkRemoved();
    if( listener != null ) {
      getNodeCore().removeNodeChangeListener( listener );
    }
  }

  public void removePreferenceChangeListener( 
    final IPreferenceChangeListener listener )
  {
    checkRemoved();
    getNodeCore().removePreferenceChangeListener( listener );
  }

  public String absolutePath() {
    if( absolutePath == null ) {
      if( parent == null ) {
        absolutePath = name;
      } else {
        String parentPath =  parent.absolutePath();
        absolutePath = parentPath.endsWith( PATH_SEPARATOR ) 
                     ? parentPath + name
                     : parentPath + PATH_SEPARATOR + name;
      }
    }
    return absolutePath;
  }

  public synchronized String[] childrenNames() throws BackingStoreException {
    checkRemoved();
    Set names = children.keySet();
    return ( String[] )names.toArray( new String[ names.size() ] );
  }

  public void clear() throws BackingStoreException {
    checkRemoved();
    String[] keys = internalGetKeys();
    for( int i = 0; i < keys.length; i++ ) {
      remove( keys[ i ] );
    }
  }

  public void flush() throws BackingStoreException {
    checkRemoved();
    // the current implementation persists everytime the preferences 
    // are modified, so there's nothing to do here
  }

  public String get( final String key, final String def ) {
    ParamCheck.notNull( key, "key" ); //$NON-NLS-1$
    checkRemoved();
    String result = internalGet( key );
    return result == null ? def : result;
  }

  public boolean getBoolean( final String key, final boolean def ) {
    ParamCheck.notNull( key, "key" ); //$NON-NLS-1$
    checkRemoved();
    String value = internalGet( key );
    return value == null ? def : Boolean.valueOf( value ).booleanValue();
  }

  public byte[] getByteArray( final String key, final byte[] def ) {
    ParamCheck.notNull( key, "key" ); //$NON-NLS-1$
    checkRemoved();
    String value = internalGet( key );
    return value == null ? def : Base64.decode( value.getBytes() );
  }

  public double getDouble( final String key, final double def ) {
    ParamCheck.notNull( key, "key" ); //$NON-NLS-1$
    checkRemoved();
    String value = internalGet( key );
    double result = def;
    if( value != null ) {
      try {
        result = Double.parseDouble( value );
      } catch( NumberFormatException nfe ) {
        // returns def
      }
    }
    return result;
  }

  public float getFloat( final String key, final float def ) {
    ParamCheck.notNull( key, "key" ); //$NON-NLS-1$
    checkRemoved();
    String value = internalGet( key );
    float result = def;
    if( value != null ) {
      try {
        result = Float.parseFloat( value );
      } catch( NumberFormatException nfe ) {
        // returns def
      }
    }
    return result;
  }

  public int getInt( final String key, final int def ) {
    ParamCheck.notNull( key, "key" ); //$NON-NLS-1$
    checkRemoved();
    String value = internalGet( key );
    int result = def;
    if( value != null ) {
      try {
        result = Integer.parseInt( value );
      } catch( NumberFormatException nfe ) {
        // returns def
      }
    }
    return result;
  }

  public long getLong( final String key, final long def ) {
    ParamCheck.notNull( key, "key" ); //$NON-NLS-1$
    checkRemoved();
    String value = internalGet( key );
    long result = def;
    if( value != null ) {
      try {
        result = Long.parseLong( value );
      } catch( NumberFormatException nfe ) {
        // returns def
      }
    }
    return result;
  }

  public String[] keys() throws BackingStoreException {
    checkRemoved();
    return internalGetKeys();
  }

  public String name() {
    return name;
  }

  public synchronized boolean nodeExists( final String path ) 
    throws BackingStoreException 
  {
    boolean result;
    if( "".equals( path ) ) { //$NON-NLS-1$
      result = !isRemoved;
    } else {
      checkRemoved();
      checkPath( path );
      if( path.startsWith( PATH_SEPARATOR ) ) { // "/absolute/path"
        result = findRoot().nodeExists( path.substring( 1 ) );
      } else if( path.indexOf( PATH_SEPARATOR ) > 0 ) { // "foo/bar/baz"
        int index = path.indexOf( PATH_SEPARATOR );
        String nodeName = path.substring( 0, index );
        String rest = path.substring( index + 1, path.length() ); 
        SessionPreferencesNode child = getChild( nodeName, false );
        result = child == null ? false : child.nodeExists( rest );
      } else { // "foo"
        result = children.containsKey( path );
      }
    }
    return result;
  }

  public Preferences parent() {
    checkRemoved();
    return parent;
  }

  public void put( final String key, final String newValue ) {
    ParamCheck.notNull( key, "key" ); //$NON-NLS-1$
    ParamCheck.notNull( newValue, "newValue" ); //$NON-NLS-1$
    checkRemoved();
    String oldValue = internalPut( key, newValue );
    if( !newValue.equals( oldValue ) ) {
      getNodeCore().firePreferenceEvent( key, oldValue, newValue );
    }
  }

  public void putBoolean( final String key, final boolean value ) {
    ParamCheck.notNull( key, "key" ); //$NON-NLS-1$
    checkRemoved();
    String newValue = value ? TRUE : FALSE;
    String oldValue = internalPut( key, newValue );
    if( !newValue.equals( oldValue ) ) {
      getNodeCore().firePreferenceEvent( key, oldValue, newValue );
    }
  }

  public void putByteArray( final String key, final byte[] value ) {
    ParamCheck.notNull( key, "key" ); //$NON-NLS-1$
    ParamCheck.notNull( value, "newValue" ); //$NON-NLS-1$
    checkRemoved();
    String newValue = new String( Base64.encode( value ) );
    String oldValue = internalPut( key, newValue );
    if( !newValue.equals( oldValue) ) {
      getNodeCore().firePreferenceEvent( key, oldValue, newValue );
    }
  }

  public void putDouble( final String key, final double value ) {
    ParamCheck.notNull( key, "key" ); //$NON-NLS-1$
    checkRemoved();
    String newValue = String.valueOf( value );
    String oldValue = internalPut( key, newValue );
    if( !newValue.equals( oldValue ) ) {
      getNodeCore().firePreferenceEvent( key, oldValue, newValue );
    }
  }

  public void putFloat( final String key, final float value ) {
    ParamCheck.notNull( key, "key" ); //$NON-NLS-1$
    checkRemoved();
    String newValue = String.valueOf( value );
    String oldValue = internalPut( key, newValue );
    if( !newValue.equals( oldValue ) ) {
      getNodeCore().firePreferenceEvent( key, oldValue, newValue );
    }
  }

  public void putInt( final String key, final int value ) {
    ParamCheck.notNull( key, "key" ); //$NON-NLS-1$
    checkRemoved();
    String newValue = String.valueOf( value );
    String oldValue = internalPut( key, newValue );
    if( !newValue.equals( oldValue ) ) {
      getNodeCore().firePreferenceEvent( key, oldValue, newValue );
    }
  }

  public void putLong( final String key, final long value ) {
    ParamCheck.notNull( key, "key" ); //$NON-NLS-1$
    checkRemoved();
    String newValue = String.valueOf( value );
    String oldValue = internalPut( key, newValue );
    if( !newValue.equals( oldValue ) ) {
      getNodeCore().firePreferenceEvent( key, oldValue, newValue );
    }
  }

  public void remove( final String key ) {
    checkRemoved();
    String oldValue = internalGet( key );
    if( oldValue != null ) {
      internalPut( key, null );
      getNodeCore().firePreferenceEvent( key, oldValue, null );
    }
  }

  public void sync() throws BackingStoreException {
    checkRemoved();
    ISettingStore store = RWT.getSettingStore();
    String id = store.getId();
    try {
      store.loadById( id );
    } catch( SettingStoreException sse ) {
      throw new BackingStoreException( "Failed to sync() node", sse ); //$NON-NLS-1$
    }
  }
  
  public String toString() {
    return absolutePath() + "@" + hashCode(); //$NON-NLS-1$
  }
  
  //////////////////
  // helping methods
  
  private void checkName( final String nodeName ) {
    if( nodeName.indexOf( PATH_SEPARATOR ) != -1 ) {
      String unboundMsg = "Name ''{0}'' cannot contain or end with ''{1}''"; //$NON-NLS-1$
      String msg = NLS.bind( unboundMsg, nodeName, PATH_SEPARATOR );
      throw new IllegalArgumentException( msg );
    }
  }

  private void checkPath( final String path ) {
    if( path.indexOf( DOUBLE_PATH_SEPARATOR ) != -1 ) {
      String unboundMsg = "''{0}'' is not allowed in path ''{1}''"; //$NON-NLS-1$
      String msg = NLS.bind( unboundMsg, DOUBLE_PATH_SEPARATOR, path );
      throw new IllegalArgumentException( msg );
    }
    if( path.length() > 1 && path.endsWith( PATH_SEPARATOR ) ) {
      String unboundMsg = "path ''{0}'' cannot end with ''{1}''"; //$NON-NLS-1$
      String msg = NLS.bind( unboundMsg, path, PATH_SEPARATOR );
      throw new IllegalArgumentException( msg );
    }
  }
  
  private synchronized void checkRemoved() {
    if( isRemoved ) {
      String msg = "node ''{0}'' has been removed"; //$NON-NLS-1$
      throw new IllegalStateException( NLS.bind( msg, this.absolutePath() ) );
    }
  }
  
  private synchronized SessionPreferencesNode createChild( 
    final String childName )
  {
    SessionPreferencesNode result 
      = new SessionPreferencesNode( this, childName );
    children.put( childName, result );
    fireNodeEvent( result, true );
    return result;
  }
  
  private synchronized SessionPreferencesNode getChild( 
    final String childName, 
    final boolean doCreate ) 
  {
    SessionPreferencesNode result 
      = ( SessionPreferencesNode )children.get( childName );
    if( result == null && doCreate ) {
      result = createChild( childName );
    }
    return result;
  }
  
  private String[] internalGetKeys() {
    List result = new ArrayList();

    String prefix = absolutePath() + PATH_SEPARATOR;
    int prefixLength = prefix.length();
    
    Enumeration attrNames = RWT.getSettingStore().getAttributeNames();
    while( attrNames.hasMoreElements() ) {
      String attr = ( String )attrNames.nextElement();
      if( attr.startsWith( prefix ) ) {
        String key = attr.substring( prefixLength );
        result.add( key );
      }
    }
    return ( String[] )result.toArray( new String[ result.size() ] );
  }

  private Preferences findRoot() {
    Preferences result = this;
    while( result.parent() != null ) {
      result = result.parent();
    }
    return result;
  }
  
  private String internalGet( final String key ) {
    ISettingStore store = RWT.getSettingStore();
    String uniqueKey = absolutePath() + PATH_SEPARATOR + key;
    return store.getAttribute( uniqueKey );
  }

  private synchronized String internalPut( final String key, 
                                           final String value ) {
    String uniqueKey = absolutePath() + PATH_SEPARATOR + key;
    return getNodeCore().put( uniqueKey, value );
  }
  
  private void fireNodeEvent( final Preferences child,
                              final boolean wasAdded ) {
    getNodeCore().fireNodeEvent( child, wasAdded, this );
  }
  
  private SessionPreferenceNodeCore getNodeCore() {
    SessionPreferenceNodeCore result;
    final String key = absolutePath();
    Object object = RWT.getSessionStore().getAttribute( key );
    if( object instanceof SessionPreferenceNodeCore ) {
      result = ( SessionPreferenceNodeCore )object;
    } else {
      result = new SessionPreferenceNodeCore( this );
      RWT.getSessionStore().setAttribute( key, result );
    }
    return result;
  }
  
}
