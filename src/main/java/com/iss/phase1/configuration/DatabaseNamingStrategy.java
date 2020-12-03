package com.iss.phase1.configuration;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DatabaseNamingStrategy extends SpringPhysicalNamingStrategy implements Serializable {
    private static final Pattern pattern = Pattern.compile("(?<=[a-z])[A-Z]");

    public DatabaseNamingStrategy() {
    }

    @Override
    public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment jdbcEnvironment) {
        String text = name.getText().toUpperCase();
        text = text.endsWith("S") ? text : text + "S";
        return new Identifier(text, name.isQuoted());
    }

    @Override
    public Identifier toPhysicalColumnName(Identifier name, JdbcEnvironment jdbcEnvironment) {
        String text = camelCaseToUnderscore(name.getText()).toUpperCase();
        return new Identifier(text, name.isQuoted());
    }

    private static String camelCaseToUnderscore(String text) {
        Matcher m = pattern.matcher(text);
        StringBuffer sb = new StringBuffer();

        while(m.find()) {
            m.appendReplacement(sb, "_" + m.group().toLowerCase());
        }

        m.appendTail(sb);
        return sb.toString();
    }
}
