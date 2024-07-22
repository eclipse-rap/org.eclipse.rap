/*******************************************************************************
 * Copyright (c) 2011, 2014 IBM Corporation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Raymond Augé <raymond.auge@liferay.com> - Bug 436698
 *******************************************************************************/
package org.eclipse.rap.http.servlet.internal.servlet;

import java.io.IOException;
import java.util.List;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.rap.http.servlet.internal.registration.EndpointRegistration;
import org.eclipse.rap.http.servlet.internal.registration.FilterRegistration;

public class FilterChainImpl implements FilterChain {

	private final List<FilterRegistration> matchingFilterRegistrations;
	private final EndpointRegistration<?> registration;
	private final DispatcherType dispatcherType;
	private final int filterCount;
	private int filterIndex = 0;

	public FilterChainImpl(List<FilterRegistration> matchingFilterRegistrations, EndpointRegistration<?> registration,
			DispatcherType dispatcherType) {

		this.matchingFilterRegistrations = matchingFilterRegistrations;
		this.dispatcherType = dispatcherType;
		this.registration = registration;
		this.filterCount = matchingFilterRegistrations.size();
	}

	public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
		if (filterIndex < filterCount) {
			FilterRegistration filterRegistration = matchingFilterRegistrations.get(filterIndex++);

			if (filterRegistration.appliesTo(this)) {
				filterRegistration.doFilter((HttpServletRequest) request, (HttpServletResponse) response, this);

				return;
			}
		}

		registration.service((HttpServletRequest) request, (HttpServletResponse) response);
	}

	public DispatcherType getDispatcherType() {
		return dispatcherType;
	}

}
