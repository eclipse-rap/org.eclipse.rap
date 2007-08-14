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
package org.eclipse.rwt.internal;

/** 
 * <p>This interface provides access to the W4 Toolkit configuration
 * settings.</p>
 * <p>This interface is not intended to be implemented by clients.</p>
 */
public interface IConfiguration {

  IInitialization getInitialization();

  IFileUpload getFileUpload();
}
