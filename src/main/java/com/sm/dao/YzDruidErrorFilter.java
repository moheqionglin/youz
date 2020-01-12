package com.sm.dao;

import com.alibaba.druid.filter.logging.Slf4jLogFilter;
import com.alibaba.druid.proxy.jdbc.StatementProxy;


public class YzDruidErrorFilter extends Slf4jLogFilter {
    protected void statement_executeErrorAfter(StatementProxy statement, String sql, Throwable error) {
        if (this.isStatementLogErrorEnabled()) {
            statementLogError("{db conn-" + statement.getConnectionProxy().getId() + ", } execute error. " + statement.getRawObject(), null);
        }
        super.statement_executeErrorAfter(statement, sql, error);
    }
}
