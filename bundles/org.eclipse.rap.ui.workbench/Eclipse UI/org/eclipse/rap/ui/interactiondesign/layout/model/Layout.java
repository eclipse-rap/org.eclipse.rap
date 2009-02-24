/******************************************************************************* 
* Copyright (c) 2009 EclipseSource and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   EclipseSource - initial API and implementation
*******************************************************************************/ 
package org.eclipse.rap.ui.interactiondesign.layout.model;

import java.util.HashMap;
import java.util.Map;

/**
 * This class represents the <code>org.eclipse.ui.presentations.Layouts</code>
 * extension point. This means, that it can be hold any number of <code>
 * {@link LayoutSet}</code> objects. 
 * <p>
 * This class is like a manager for <code>LayoutSet</code> objects.
 * </p>
 * 
 * @since 1.2
 *
 */
public class Layout {

  private Map layoutSets = new HashMap();
  private String id;
  
  /**
   * Instantiate a object of this class and sets the unique id, which is 
   * contributed to the <code>org.eclipse.ui.presentations.Layouts
   * </code> extension point.
   * 
   * @param id the unique <code>Layout</code> id.
   */
  public Layout( final String id ) {
    this.id = id;
  }
  
  /**
   * Adds a existing <code>LayoutSet</code> object to this <code>Layout</code>.
   * 
   * @param set the <code>LayoutSet</code> object to add.
   */
  public void addLayoutSet( final LayoutSet set ) {
    layoutSets.put( set.getId(), set );
  }
    
  /**
   * This method removes a <code>LayoutSet</code> object completely from this 
   * instance.
   * 
   * @param layoutSetId the id of the <code>LayoutSet</code> object to remove.
   */
  public void clearLayoutSet( final String layoutSetId ) {
    LayoutSet set = ( LayoutSet ) layoutSets.get( layoutSetId );
    if( set != null ) {
      layoutSets.remove( layoutSetId );
    }
  }
  
  /**
   * Returns the <code>Layout</code> id, which was set by calling <code>
   * {@link #Layout(String)}</code>.
   * 
   * @return the id.
   */
  public String getId() {
    return id;
  }
  
  /**
   * Returns an instance of a <code>LayoutSet</code> object for the given id. If
   * the instance does not yet exists it will be created.
   * 
   * @param layoutSetId the id of the wanted <code>LayoutSet</code> object.
   * 
   * @return a instance of a <code>LayoutSet</code> object with the given id.
   */
  public LayoutSet getLayoutSet( final String layoutSetId ) {
    LayoutSet result = ( LayoutSet ) layoutSets.get( layoutSetId );
    if( result == null ) {
      result = new LayoutSet( layoutSetId );
      layoutSets.put( layoutSetId, result );
    }
    return result;
  }
  
  /**
   * Returns a <code>Map</code> object that contains all <code>LayoutSet</code>
   * objects for this <code>Layout</code>.
   * 
   * @return all <code>LayoutSet</code> object for this <code>Layout</code>.
   */
  public Map getLayoutSets() {
    return layoutSets;
  }
  
  /**
   * Checks if the object holds a <code>LayoutSet</code> object with the given
   * <code>LayoutSet</code> id.
   * 
   * @param layoutSetId the <code>LayoutSet</code> id to check.
   * 
   * @return <code>true</code> if this object holds a instance of the <code>
   * LayoutSet</code> with the given id.
   * 
   * @see LayoutSet#getId()
   */
  public boolean layoutSetExist( final String layoutSetId ) {
    return layoutSets.containsKey( layoutSetId );
  }
}
