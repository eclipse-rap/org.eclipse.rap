/******************************************************************************* 
* Copyright (c) 2009 EclipseSource and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   EclipseSource - initial API and implementation
*******************************************************************************/ 
package org.eclipse.rap.ui.interactiondesign.layout;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.rap.ui.interactiondesign.layout.model.Layout;
import org.eclipse.rap.ui.interactiondesign.layout.model.LayoutSet;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * A <code>ElementBuilder</code> represents the super class for all custom
 * builders. With a custom builder you can construct complex objects e.g. a 
 * header, which needs nine or less images and additional information about
 * placing logos or other images.
 * <p>
 * To fit as much as possible patterns of web components we introduce this
 * <code>ElementBuilder</code>. This concept use the builder design pattern.
 * So, an instantiation can look like: <code>ElementBuilder builder = new
 * HeaderBuilder(param, param)</code>. The benefit of this technique is, that
 * you can work against a defined api and you can change the implementation
 * easily.
 * </p>
 * <p>
 * Every builder needs a <code>{@link LayoutSet}</code>, which can be 
 * contributed to the <code>org.eclipse.ui.presentations.Layouts</code> 
 * extension point.
 * </p>
 * The point is, every builder should regard, that a <code>LayoutSet</code> 
 * can define more or less images depending on the contribution in the 
 * extension. E.g. a header can hold nine images, but i also can hold just 
 * three. So, the builder have to look that the component is build correctly.
 * </p>
 * 
 * @since 1.2
 * 
 * @see LayoutSet
 */
public abstract class ElementBuilder {

  private Composite parent;
  private Layout layout;
  private LayoutSet layoutSet;
  
  /**
   * This constructor stores the parent composite and instantiate a 
   * <code>{@link LayoutSet}</code> for this instance. The <code>LayoutSet
   * </code> can be used to create images, fonts, colors or postion data. 
   * This depends on what is defined for a specific builder.
   * <p>
   * Subclasses have to call this constructor because it register the object
   * in the <code>{@link LayoutRegistry}</code>
   * </p>
   * 
   * @param parent the parent <code>{@link Composite}</code> for the component 
   * to build.
   * @param layoutSetId the id of the <code>LayoutSet</code>, which belongs to 
   * this instance.
   * 
   * @see LayoutSet
   * @see LayoutRegistry#registerBuilder(ElementBuilder)
   * @see Composite
   */
  public ElementBuilder( final Composite parent, final String layoutSetId ) 
  {
    this.parent = parent;
    LayoutRegistry registry = LayoutRegistry.getInstance();
    String savedLayoutId = registry.getSavedLayoutId();
    if( !savedLayoutId.equals( IPreferenceStore.STRING_DEFAULT_DEFAULT ) ) {
      registry.setActiveLayout( savedLayoutId, false );
    }
    layout = registry.getActiveLayout();
    if( layout != null ) {
      layoutSet = layout.getLayoutSet( layoutSetId );
      registry.registerBuilder( this );
    } else {
      throw new IllegalArgumentException( "no layout registered with default " +
      		"id (LayoutRegistry.DEFAULT_LAYOUT_ID) or no layout activated " +
      		"over branding extension." );
    }
  }


  /**
   * Clients can call this method to add non standard components to the builder
   * e.g. a logo placed on a label to show in a header.
   * <p>
   * So, subclasses can implement or ignore this method depending on their 
   * needs.
   * </p>
   * <p>
   * Clients have to call this method before calling 
   * <code>{@link #build()}</code>. 
   * </p>
   * 
   * @param control an instance of a <code>Cotrol</code> e.g. a <code>Composite
   * </code> containing a image.
   * @param layoutData can be any position information for the control. 
   * Usually it's a instance of <code>FormData</code>.
   * 
   * @see FormData
   * @see GridData
   */
  public abstract void addControl( 
    final Control control, final Object layoutData );
  
  /**
   * This method do the same as <code>{@link #addControl(Control, Object)}
   * </code>. The only difference is, that the position information can be 
   * loaded by the <code>{@link LayoutSet#getPosition(String)}</code> 
   * method.
   * 
   * @param control an instance of a <code>Control</code> e.g. a <code>Composite
   * </code> containing an image.
   * @param positionId the unique id of a position data holding by the <code>
   * LayoutSet</code> for this object.
   * 
   * @see LayoutSet#getPosition(String)
   * @see LayoutSet#addPosition(String, FormData)
   */
  public abstract void addControl( 
    final Control control, final String positionId );
  
  /**
   * Subclasses can implement this method and process it. E.g. if a client
   * want to add a logo for a header directly and not over the 
   * <code>{@link #addControl(Control, Object)}</code> method in a 
   * <code>Composite</code>.
   * 
   * @param image an instance of a <code>Image</code> to add.
   * @param layoutData can be any position information for the <code>Image</code>. 
   * Usually it's an instance of <code>FormData</code>.
   * 
   * @see FormData
   * @see GridData
   */
  public abstract void addImage( final Image image, final Object layoutData );
  
  /**
   * This method do the same as <code>{@link #addImage(Image, Object)}</code>.
   * The only difference is, that the position information can be 
   * loaded by the <code>{@link LayoutSet#getPosition(String)}</code> 
   * method.
   * 
   * @param image an instance of an <code>Image</code> to add.
   * @param positionId the unique id of a position data holding by the <code>
   * LayoutSet</code> for this object.
   * 
   * @see LayoutSet#getPosition(String)
   * @see LayoutSet#addPosition(String, FormData)
   */
  public abstract void addImage( final Image image, final String positionId );
  
  /**
   * This is the most important method in a builder. If a client call this, the
   * builder have to build the needed component e.g. a header or footer 
   * regarding the defined <code>LayoutSet</code>.
   */
  public abstract void build();
  
  /**
   * Subclasses can use this method to create an image by means of it's path in
   * the <code>LayoutSet</code>.
   * <p>Note that images can only be created for <code>LayoutSets</code> which 
   * were contributed via extensions.</p>
   * 
   * @param path the path for the image to create.
   * @return the created image.
   * 
   * @see LayoutSet#addImagePath(String, String)
   * @see LayoutSet#getImagePath(String)
   */
  protected Image createImage( final String path ) {
    Image result = null;
    if( path != null ) {
      LayoutRegistry layoutRegistry = LayoutRegistry.getInstance();
      String id = layoutRegistry.getPluginIdForLayoutSet( layoutSet.getId() ); 
      if( id != null ) {
        ImageDescriptor descriptor
          = AbstractUIPlugin.imageDescriptorFromPlugin( id, path );
        result = descriptor.createImage();
      }
    }
    return result;
  }
  
  /**
   * Subclasses should dispose all created or rather added <code>Control</code>s 
   * and <code>Image</code>s in this method.
   */
  public abstract void dispose();
  
  /**
   * Returns a <code>Color</code> object by it's id defined in the 
   * <code>LayoutSet</code>
   * 
   * @param colorId the id of the color.
   * 
   * @return the created <code>Color</code> object.
   * 
   * @see LayoutSet#addColor(String, Color)
   * @see LayoutSet#getColor(String)
   */
  public Color getColor( final String colorId ) {
    return layoutSet.getColor( colorId );
  }
  
  /**
   * Subclasses should implement this method to return the component, 
   * which is created during the <code>{@link #build()}</code> call.
   *    
   * @return the <code>{@link Control}</code> created in the 
   * <code>{@link #build()}</code> method.
   */
  public abstract Control getControl();
  
  /**
   * Returns a <code>Font</code> object by it's id defined in the 
   * <code>LayoutSet</code>
   * 
   * @param fontID the id of the font.
   * 
   * @return the created font object.
   * 
   * @see LayoutSet#addFont(String, Font)
   * @see LayoutSet#getFont(String)
   */
  public Font getFont( final String fontID ) {
    return layoutSet.getFont( fontID );
  }
  
  /**
   * Return the image by its id in the <code>LayoutSet</code>. This method
   * just extract the image path and call <code>{@link #createImage(String)}
   * </code>.
   * 
   * @param imageId the id of the image defined in the <code>LayoutSet</code>.
   * 
   * @return the created image.
   * 
   * @see LayoutSet#getImagePath(String)
   * @see LayoutSet#addImagePath(String, String)
   */
  public Image getImage( final String imageId ) {
    Image result = null;
    String imagePath = layoutSet.getImagePath( imageId );
    if( imagePath != null ) {
      result = createImage( imagePath );
    }    
    return result;
  }
  
  /**
   * This returns the <code>LayoutSet</code> for this builder. The layoutset 
   * contains all images, fonts, formdatas and colors, which are relevant for 
   * the whole builder layout.
   * @return the <code>LayoutSet</code> object
   */
  protected LayoutSet getLayoutSet() {
    return layoutSet;
  }
  
  /**
   * Returns the parent, which was set in <code>
   * {@link #ElementBuilder(Composite, String)}</code>.
   * 
   * @return the parent <code>Composite</code>. 
   */
  protected Composite getParent() {
    return parent;
  }
  
  /**
   * Should return the size from the representative component of the builder. 
   *  
   * @return the size as a Point. <code>Point.x</code> means the width and 
   * <code>Point.y</code> means the height of the component.
   */
  public abstract Point getSize();
  
}
