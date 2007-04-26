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

package org.eclipse.swt.lifecycle;

import java.io.IOException;
import org.eclipse.swt.widgets.Widget;


/**
 * TODO [rh] JavaDoc
 * <p></p>
 */
public interface IWidgetLifeCycleAdapter extends ILifeCycleAdapter {

  void preserveValues( Widget widget );
  void readData( Widget widget );
  void render( Widget widget ) throws IOException;
}
