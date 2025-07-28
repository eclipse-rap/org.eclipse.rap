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
package org.eclipse.rap.rwt.addons.canvas;

import java.io.Serializable;


/**
 * <p>
 * A <code>ClientDrawListener</code> will be called when a client draws on a <code>ClientCanvas</code>.
 * </p>
 *
 * @see ClientCanvas
 *
 * @since 4.4
 */
public interface ClientDrawListener extends Serializable {

  void receivedDrawing();

}
