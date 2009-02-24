/******************************************************************************* 
* Copyright (c) 2009 EclipseSource and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   EclipseSource - initial API and implementation
*******************************************************************************/ 
package org.eclipse.rap.ui.interactiondesign;

import org.eclipse.rap.ui.interactiondesign.internal.ConfigurableStackProxy;

/**
 * This interface can be used to react on configuration changes e.g. the 
 * <code>{@link ConfigurableStack}</code> or the visibility of view toolbar 
 * items. 
 * <p>
 * You can register an instance of this in the 
 * <code>{@link ConfigurationAction}</code> of a <code>ConfigurableStack</code>
 * if it has one.
 * </p>
 * 
 * @since 1.2
 * 
 * @see ConfigurableStack
 * @see ConfigurationAction
 */
public interface IConfigurationChangeListener {
  
  /**
   * This method is called if the <code>{@link ConfigurableStack}</code> of a
   * part has changed. 
   * <p> 
   * An instance of this e.g. can call 
   * <code>{@link ConfigurableStackProxy#setCurrentStackPresentation(String)}
   * <code> with the new id to change the <code>ConfigurableStack</code> on the
   * fly.
   * </p>
   *  
   * @param newStackPresentationId the id of the new 
   * <code>ConfigurableStack</code> for the selected part.
   * 
   * @see ConfigurableStack
   * @see ConfigurableStackProxy#setCurrentStackPresentation(String)
   */
  public void presentationChanged( final String newStackPresentationId );
  
  /**
   * This method is called if the visibility of the views's toolbar items or
   * menu has changed.
   * <p>
   * <code>{@link ConfigurableStack}</code> objects can use this e.g. to refresh 
   * the part toolbar.
   * </p> 
   * 
   * @see ConfigurableStack
   */
  public void toolBarChanged();
  
}
