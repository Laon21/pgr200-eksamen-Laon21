package no.kristiania.pgr200.database.core;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;

@FunctionalInterface
public interface ResultSetMapper<T> {

    T mapResultSet(ResultSet rs) throws SQLException, ParseException;

}
