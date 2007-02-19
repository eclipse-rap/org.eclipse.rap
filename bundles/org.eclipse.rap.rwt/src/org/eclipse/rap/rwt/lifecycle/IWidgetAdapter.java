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

package org.eclipse.rap.rwt.lifecycle;


/**
 * TODO [rh] JavaDoc
 * <p></p>
 */
public interface IWidgetAdapter {
  
  String getId();

  boolean isInitialized();
  void setInitialized( boolean initialized );
  
  void preserve( String propertyName, Object value );
  Object getPreserved( String propertyName );
  void clearPreserved();

  void setJSParent( String jsParent );
  String getJSParent();

  void setRenderRunnable( IRenderRunnable runnable );
  IRenderRunnable getRenderRunnable();
}
