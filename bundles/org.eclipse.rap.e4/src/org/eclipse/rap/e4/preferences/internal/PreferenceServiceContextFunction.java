package org.eclipse.rap.e4.preferences.internal;

import org.eclipse.e4.core.contexts.ContextFunction;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;

public class PreferenceServiceContextFunction extends ContextFunction {
	
	@Override
	public Object compute(IEclipseContext context) {
		return ContextInjectionFactory.make(RAPPreferenceService.class, context);
	}
}
