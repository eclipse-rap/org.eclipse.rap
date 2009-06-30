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

import org.eclipse.rap.ui.interactiondesign.layout.ElementBuilder;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FormData;

/**
 * A <code>LayoutSet</code> object can hold different components for a <code>
 * {@link ElementBuilder}</code>. These can be <code>Image</code>, <code>Color
 * </code>, <code>Font</code> and <code>FormData</code> objects.
 * <p>
 * Actually this class it's just a container for additional objects.
 * </p>
 * 
 * @since 1.2
 * 
 * @see ElementBuilder
 */
public class LayoutSet {    
  
  private String id;
  private Map imageMap = new HashMap();
  private Map fontMap = new HashMap();
  private Map colorMap = new HashMap();
  private Map positionMap = new HashMap();

  /**
   * Instantiate an object of this class with a given id. This id is contributed
   * in the attributes of the <code>org.eclipse.rap.ui.layouts
   * </code> extension point.
   * 
   * @param id the id of the <code>LayoutSet</code>
   */
  public LayoutSet( final String id ) {
    this.id = id;
  }
  
  /**
   * Adds a <code>Color</code> object. The object is couples to a key, which 
   * is defined by the <code>{@link ElementBuilder}</code> implementation. 
   *  
   * @param key the key for the <code>color</code> object.
   * @param color the <code>Color</code> object to add.
   * 
   * @see ElementBuilder
   */
  public void addColor( final String key, final Color color ) {
    colorMap.put( key, color );
  }
  
  /**
   * Adds a <code>Font</code> object. The object is couples to a key, which 
   * is defined by the <code>{@link ElementBuilder}</code> implementation. 
   *  
   * @param key the key for the <code>Font</code> object.
   * @param font the <code>Font</code> object to add.
   * 
   * @see ElementBuilder
   */
  public void addFont( final String key, final Font font ) {
    fontMap.put( key, font );
  }
  
  /**
   * Adds a path of a image. This path is coupled to a key, which is defined by
   * the <code>{@link ElementBuilder}</code> implementation. The path of the 
   * image should be relative to the plug-in root, which the image belongs to.
   * 
   * @param key the key for the image path. 
   * @param imagePath the relative image path.
   * 
   * @see ElementBuilder
   */
  public void addImagePath( final String key, final String imagePath ) {
    imageMap.put( key, imagePath ); 
  }
  
  /**
   * Adds a <code>FormData</code> object. The object is couples to a key, which 
   * is defined by the <code>{@link ElementBuilder}</code> implementation. 
   * 
   * @param key the key for the <code>FormData</code> object.
   * @param position the <code>FormData</code> object to add.
   * 
   * @see ElementBuilder
   */
  public void addPosition( final String key, final FormData position ) {
    positionMap.put( key, position );
  }
  
  /**
   * Returns the <code>Color</code> object for the given key.
   * 
   * @param key the key for the <code>Color</code> object.
   * 
   * @return the <code>Color</code> object.
   */
  public Color getColor( final String key ) {
    return ( Color ) colorMap.get( key );
  }
  
  /**
   * Returns the <code>Font</code> object for the given key.
   * 
   * @param key the key for the <code>Font</code> object.
   * 
   * @return the <code>Font</code> object.
   */
  public Font getFont( final String key ) {
    return ( Font ) fontMap.get( key );
  }
  
  /**
   * Returns the id of a <code>LayoutSet</code> object.
   * 
   * @return the id.
   */
  public String getId() {
    return id;
  }
  
  /**
   * Returns the image path for the given key.
   * 
   * @param key the key for the image path.
   * 
   * @return the recursive image path.
   */
  public String getImagePath( final String key ) {
    return ( String ) imageMap.get( key );
  }
  
  /**
   * Returns the <code>FormData</code> object for the given key.
   * 
   * @param key the key for the <code>FormData</code> object.
   * 
   * @return the <code>FormData</code> object.
   */
  public FormData getPosition( final String key ) {
    return ( FormData ) positionMap.get( key );
  }

}
