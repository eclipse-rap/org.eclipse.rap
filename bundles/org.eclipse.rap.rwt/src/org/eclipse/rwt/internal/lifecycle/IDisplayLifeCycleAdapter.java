/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rwt.internal.lifecycle;

import java.io.IOException;

import org.eclipse.rwt.lifecycle.ILifeCycleAdapter;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.widgets.Display;


public interface IDisplayLifeCycleAdapter extends ILifeCycleAdapter {

  void preserveValues( Display display );
  void readData( Display display );
  void processAction( Device display );
  void render( Display display ) throws IOException;
}
