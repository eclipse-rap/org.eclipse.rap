/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
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

package org.eclipse.ui.internal.services;

import org.eclipse.core.expressions.Expression;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.ui.services.IEvaluationReference;

/**
 * 
 */
public class EvaluationReference extends EvaluationResultCache implements
		IEvaluationReference {

	private IPropertyChangeListener listener;
	private String property;
	private boolean postingChanges = true;

	/**
	 * @param expression
	 */
	public EvaluationReference(Expression expression,
			IPropertyChangeListener listener, String property) {
		super(expression);
		this.listener = listener;
		this.property = property;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.internal.services.IEvaluationReference#getListener()
	 */
	public IPropertyChangeListener getListener() {
		return listener;
	}
	
	public String getProperty() {
		return property;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.internal.services.IEvaluationReference#setFlopping(boolean)
	 */
	public void setPostingChanges(boolean evaluationEnabled) {
		this.postingChanges = evaluationEnabled;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.internal.services.IEvaluationReference#isFlopping()
	 */
	public boolean isPostingChanges() {
		return postingChanges;
	}
}
