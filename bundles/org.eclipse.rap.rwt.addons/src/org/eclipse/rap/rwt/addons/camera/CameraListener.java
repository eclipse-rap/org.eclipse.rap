/*******************************************************************************
 * Copyright (c) 2025 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.addons.camera;

import java.io.Serializable;

import org.eclipse.swt.graphics.Image;


/**
 * <p>
 * The {@link CameraListener} is used to receive notifications from the client when it's done with
 * taking pictures. In the case of a success the picture will be passed as an <code>Image</code> object.
 * </p>
 *
 * @see Image
 * @since 4.4
 */
public interface CameraListener extends Serializable {

  /**
   * <p>
   * Called in the case of successfully taking or selecting a picture from a client's camera.
   * </p>
   *
   * @param image the picture transfered form the client. May be <code>null</code> if there was an error or the user
   *              canceled.
   *
   * @see Image
   */
  void receivedPicture( Image image );

}
