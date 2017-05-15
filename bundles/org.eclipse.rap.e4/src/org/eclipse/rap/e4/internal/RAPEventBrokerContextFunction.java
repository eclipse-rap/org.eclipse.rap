package org.eclipse.rap.e4.internal;

import org.eclipse.e4.core.contexts.ContextFunction;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;

public class RAPEventBrokerContextFunction extends ContextFunction {
	@Override
	public Object compute(IEclipseContext context) {
		RAPEventBroker broker = context.getLocal(RAPEventBroker.class);
		if (broker == null) {
            broker = ContextInjectionFactory.make(RAPEventBroker.class, context);
            context.set(RAPEventBroker.class, broker);
		}
		return broker;
	}
}
