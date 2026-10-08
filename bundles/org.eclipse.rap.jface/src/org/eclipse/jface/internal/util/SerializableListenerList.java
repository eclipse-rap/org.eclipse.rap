/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.jface.internal.util;

import java.io.Serializable;

/*
 * Exists in RAP only.  Serializable version of the ListenerList from core.runtime.
 */
public class SerializableListenerList 
  extends org.eclipse.core.runtime.ListenerList 
  implements Serializable 
{

}
