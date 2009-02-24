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

/**
 * This interface is implemented by the class defined in the attributes of the
 * <code>org.eclipse.ui.presentations.Layouts</code> extension point.
 * <p>
 * Classes implementing this interface to add components to a 
 * <code>{@link LayoutSet}</code> object. 
 *
 * @since 1.2
 */
public interface ILayoutSetInitializer {
  
  /**
   * This method is automatically called by the <code>LayoutRegistry</code> 
   * instance during the initialization process. This happens on the plug-in
   * activation.
   * <p>
   * Clients should add <code>Image</code>, <code>Font</code>, <code>Color
   * </code> and <code>FormData</code> objects to the given 
   * <code>LayoutSet</code>.
   * </p>
   * 
   * @param layoutSet the <code>LayoutSet</code> instance you should add 
   * components.
   * 
   * @see LayoutSet#addColor(String, org.eclipse.swt.graphics.Color)
   * @see LayoutSet#addFont(String, org.eclipse.swt.graphics.Font)
   * @see LayoutSet#addImagePath(String, String)
   * @see LayoutSet#addPosition(String, org.eclipse.swt.layout.FormData)
   */
  public void initializeLayoutSet( final LayoutSet layoutSet );
  
  
}
