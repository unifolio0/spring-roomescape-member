package roomescape.infrastructure;

import java.util.ArrayList;
import java.util.List;

public class QueryBuilder {
    private String query;
    private boolean hasWhere;
    private List<Object> param;

    private QueryBuilder(String query) {
        this.query = query;
        this.hasWhere = query.contains("WHERE");
        this.param = new ArrayList<>();
    }

    public static QueryBuilder build(String query) {
        return new QueryBuilder(query);
    }

    public QueryBuilder addCondition(Object condition, String sql) {
        if (condition == null) {
            return this;
        }
        param.add(condition);
        if (hasWhere) {
            query += " AND " + sql;
            return this;
        }
        query += " WHERE " + sql;
        hasWhere = true;
        return this;
    }

    public String getQuery() {
        return query;
    }

    public Object[] getParam() {
        return param.toArray();
    }
}
