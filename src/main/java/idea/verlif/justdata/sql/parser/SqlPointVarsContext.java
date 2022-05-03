package idea.verlif.justdata.sql.parser;

import idea.verlif.parser.vars.VarsContext;

/**
 * @author Verlif
 */
public class SqlPointVarsContext extends VarsContext {

    public SqlPointVarsContext(String context) {
        super(context);

        setAreaTag("{", "}");
    }
}
