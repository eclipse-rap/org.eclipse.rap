/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/

package org.eclipse.ui.internal.menus;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.ui.internal.services.IEvaluationResultCache;

/**
 *
 */
public interface IMenuActivation extends IEvaluationResultCache {
	/**
	 * @return the IContributionItem for the cache.
	 */
	public IContributionItem getContribution();
}
