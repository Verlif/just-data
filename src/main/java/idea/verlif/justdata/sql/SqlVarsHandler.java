package idea.verlif.justdata.sql;

import idea.verlif.parser.vars.VarsHandler;

/**
 * @author Verlif
 */
public abstract class SqlVarsHandler implements VarsHandler {

    @Override
    public String handle(int i, String s, String s1) {
        s1 = SqlExecutor.recoveryVar(s1);
        return SqlExecutor.aroundVar(handle(s1));
    }

    protected abstract String handle(String content);
}
