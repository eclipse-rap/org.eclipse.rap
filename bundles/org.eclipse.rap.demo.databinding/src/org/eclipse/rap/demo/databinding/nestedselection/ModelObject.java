/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html Contributors:
 * IBM Corporation - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.demo.databinding.nestedselection;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.Array;
import java.util.*;

public class ModelObject {

  private final PropertyChangeSupport propertyChangeSupport
    = new PropertyChangeSupport( this );
  private String id;

  public void addPropertyChangeListener( final PropertyChangeListener listener )
  {
    propertyChangeSupport.addPropertyChangeListener( listener );
  }

  public void addPropertyChangeListener( final String propertyName,
                                         final PropertyChangeListener listener )
  {
    propertyChangeSupport.addPropertyChangeListener( propertyName, listener );
  }

  public void removePropertyChangeListener( final PropertyChangeListener listener )
  {
    propertyChangeSupport.removePropertyChangeListener( listener );
  }

  public void removePropertyChangeListener( final String propertyName,
                                            final PropertyChangeListener listener )
  {
    propertyChangeSupport.removePropertyChangeListener( propertyName, listener );
  }

  protected void firePropertyChange( final String propertyName,
                                     final Object oldValue,
                                     final Object newValue )
  {
    propertyChangeSupport.firePropertyChange( propertyName, oldValue, newValue );
  }

  protected void firePropertyChange( final String propertyName,
                                     final int oldValue,
                                     final int newValue )
  {
    propertyChangeSupport.firePropertyChange( propertyName, oldValue, newValue );
  }

  protected void firePropertyChange( final String propertyName,
                                     final boolean oldValue,
                                     final boolean newValue )
  {
    propertyChangeSupport.firePropertyChange( propertyName, oldValue, newValue );
  }

  public void setId( final String string ) {
    Object oldValue = id;
    id = string;
    firePropertyChange( "id", oldValue, id );
  }

  protected Object[] append( final Object[] array, final Object object ) {
    List newList = new ArrayList( Arrays.asList( array ) );
    newList.add( object );
    Class compType = array.getClass().getComponentType();
    int size = newList.size();
    return newList.toArray( ( Object[] )Array.newInstance( compType, size ) );
  }

  protected Object[] remove( final Object[] array, final Object object ) {
    List newList = new ArrayList( Arrays.asList( array ) );
    newList.remove( object );
    Class compType = array.getClass().getComponentType();
    int size = newList.size();
    return newList.toArray( ( Object[] )Array.newInstance( compType, size ) );
  }
}
