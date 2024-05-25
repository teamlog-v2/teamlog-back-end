package com.app.teamlog.global.config;

import com.p6spy.engine.logging.Category;
import com.p6spy.engine.spy.appender.MessageFormattingStrategy;
import org.hibernate.engine.jdbc.internal.FormatStyle;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Configuration
public class PrettyLineFormat implements MessageFormattingStrategy {
    @Override
    public String formatMessage(int connectionId, String now, long elapsed, String category, String prepared, String sql, String url) {
        String formattedSql = formatSql(category, sql);
        SimpleDateFormat format1 = new SimpleDateFormat("yy.MM.dd HH:mm:ss");

        return format1.format(new Date()) + " | " + "OperationTime : " + elapsed + "ms" + formattedSql;
    }

    private String formatSql(String category, String sql) {
        if (sql == null || sql.trim().equals("")) return sql;

        // Only format Statement, distinguish DDL And DML
        if (Category.STATEMENT.getName().equals(category)) {
            String lowerCaseSql = sql.trim().toLowerCase(Locale.ROOT);
            if (lowerCaseSql.startsWith("create") || lowerCaseSql.startsWith("alter") || lowerCaseSql.startsWith("comment")) {
                sql = FormatStyle.DDL.getFormatter().format(sql);
            } else {
                sql = FormatStyle.BASIC.getFormatter().format(sql);
            }
            sql = " | HeFormatSql(P6Spy sql,Hibernate format)" + sql;
        }

        return sql;
    }
}
