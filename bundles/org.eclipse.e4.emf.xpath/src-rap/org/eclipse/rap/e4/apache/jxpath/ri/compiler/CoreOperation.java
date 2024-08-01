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

/**
 * The common subclass for tree elements representing core operations like "+",
 * "- ", "*" etc.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 652845 $ $Date: 2008-05-02 12:46:46 -0500 (Fri, 02 May 2008) $
 */
public abstract class CoreOperation extends Operation {

    /** or precedence */
    protected static final int OR_PRECEDENCE = 0;
    /** and precedence */
    protected static final int AND_PRECEDENCE = 1;
    /** compare precedence */
    protected static final int COMPARE_PRECEDENCE = 2;
    /** relational expression precedence */
    protected static final int RELATIONAL_EXPR_PRECEDENCE = 3;
    /** add/subtract precedence */
    protected static final int ADD_PRECEDENCE = 4;
    /** multiply/divide/mod precedence */
    protected static final int MULTIPLY_PRECEDENCE = 5;
    /** negate precedence */
    protected static final int NEGATE_PRECEDENCE = 6;
    /** union precedence */
    protected static final int UNION_PRECEDENCE = 7;

    /**
     * Create a new CoreOperation.
     * @param args Expression[]
     */
    public CoreOperation(Expression[] args) {
        super(args);
    }

    public Object compute(EvalContext context) {
        return computeValue(context);
    }

    public abstract Object computeValue(EvalContext context);

    /**
     * Returns the XPath symbol for this operation, e.g. "+", "div", etc.
     * @return String symbol
     */
    public abstract String getSymbol();

    /**
     * Returns true if the operation is not sensitive to the order of arguments,
     * e.g. "=", "and" etc, and false if it is, e.g. "&lt;=", "div".
     * @return boolean
     */
    protected abstract boolean isSymmetric();

    /**
     * Computes the precedence of the operation.
     * @return int precedence
     */
    protected abstract int getPrecedence();

    public String toString() {
        if (args.length == 1) {
            return getSymbol() + parenthesize(args[0], false);
        }
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < args.length; i++) {
            if (i > 0) {
                buffer.append(' ');
                buffer.append(getSymbol());
                buffer.append(' ');
            }
            buffer.append(parenthesize(args[i], i == 0));
        }
        return buffer.toString();
    }

    /**
     * Wrap an expression in parens if necessary.
     * @param expression other Expression
     * @param left whether <code>expression</code> is left of this one.
     * @return String
     */
    private String parenthesize(Expression expression, boolean left) {
        String s = expression.toString();
        if (!(expression instanceof CoreOperation)) {
            return s;
        }
        int compared = getPrecedence() - ((CoreOperation) expression).getPrecedence();

        if (compared < 0) {
            return s;
        }
        if (compared == 0 && (isSymmetric() || left)) {
            return s;
        }
        return '(' + s + ')';
    }
}
