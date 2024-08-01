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

import org.eclipse.rap.e4.apache.jxpath.ri.EvalContext;
import org.eclipse.rap.e4.apache.jxpath.ri.axes.UnionContext;

/**
 * Implementation of {@link Expression} for the operation "|".
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 652845 $ $Date: 2008-05-02 12:46:46 -0500 (Fri, 02 May 2008) $
 */
public class CoreOperationUnion extends CoreOperation {

    /**
     * Create a new CoreOperationUnion.
     * @param args Expression[]
     */
    public CoreOperationUnion(Expression[] args) {
        super(args);
    }

    public Object computeValue(EvalContext context) {
        EvalContext[] argCtxs = new EvalContext[args.length];
        for (int i = 0; i < args.length; i++) {
            Object value = args[i].compute(context);
            if (value instanceof EvalContext) {
                argCtxs[i] = (EvalContext) value;
            }
            else {
                argCtxs[i] = context.getRootContext().getConstantContext(value);
            }
        }
        return new UnionContext(context.getRootContext(), argCtxs);
    }

    protected int getPrecedence() {
        return UNION_PRECEDENCE;
    }

    protected boolean isSymmetric() {
        return true;
    }

    public String getSymbol() {
        return "|";
    }
}
