/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.eclipse.rap.e4.apache.jxpath.ri.compiler;

import java.util.Arrays;

import org.eclipse.rap.e4.apache.jxpath.Function;
import org.eclipse.rap.e4.apache.jxpath.JXPathFunctionNotFoundException;
import org.eclipse.rap.e4.apache.jxpath.NodeSet;
import org.eclipse.rap.e4.apache.jxpath.ri.EvalContext;
import org.eclipse.rap.e4.apache.jxpath.ri.QName;
import org.eclipse.rap.e4.apache.jxpath.ri.axes.NodeSetContext;

/**
 * Represents an element of the parse tree representing an extension function
 * call.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 652845 $ $Date: 2008-05-02 12:46:46 -0500 (Fri, 02 May 2008) $
 */
public class ExtensionFunction extends Operation {

    private QName functionName;

    /**
     * Create a new ExtensionFunction.
     * @param functionName name of the function
     * @param args Expression[] of function args
     */
    public ExtensionFunction(QName functionName, Expression[] args) {
        super(args);
        this.functionName = functionName;
    }

    /**
     * Get the function name
     * @return QName
     */
    public QName getFunctionName() {
        return functionName;
    }

    /**
     * An extension function gets the current context, therefore it MAY be
     * context dependent.
     * @return true
     */
    public boolean computeContextDependent() {
        return true;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(functionName);
        buffer.append('(');
        Expression[] args = getArguments();
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                if (i > 0) {
                    buffer.append(", ");
                }
                buffer.append(args[i]);
            }
        }
        buffer.append(')');
        return buffer.toString();
    }

    public Object compute(EvalContext context) {
        return computeValue(context);
    }

    public Object computeValue(EvalContext context) {
        Object[] parameters = null;
        if (args != null) {
            parameters = new Object[args.length];
            for (int i = 0; i < args.length; i++) {
                parameters[i] = convert(args[i].compute(context));
            }
        }

        Function function =
            context.getRootContext().getFunction(functionName, parameters);
        if (function == null) {
            throw new JXPathFunctionNotFoundException("No such function: "
                    + functionName + Arrays.asList(parameters));
        }
        Object result = function.invoke(context, parameters);
        return result instanceof NodeSet ? new NodeSetContext(context,
                (NodeSet) result) : result;
    }

    /**
     * Convert any incoming context to a value.
     * @param object Object to convert
     * @return context value or <code>object</code> unscathed.
     */
    private Object convert(Object object) {
        return object instanceof EvalContext ? ((EvalContext) object).getValue() : object;
    }
}
