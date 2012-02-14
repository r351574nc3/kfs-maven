/*
 * Copyright 2011 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.sys.batch.dataaccess.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.sys.batch.dataaccess.PreparedStatementCachingDao;
import org.kuali.rice.kns.dao.jdbc.PlatformAwareDaoBaseJdbc;
import org.kuali.rice.kns.service.DateTimeService;

public abstract class AbstractPreparedStatementCachingDaoJdbc extends PlatformAwareDaoBaseJdbc implements PreparedStatementCachingDao {
    protected static final String RETRIEVE_PREFIX = "retrieve-";
    protected static final String INSERT_PREFIX = "insert-";
    protected static final String UPDATE_PREFIX = "update-";

    protected abstract class JdbcWrapper<T> {
        protected abstract void populateStatement(PreparedStatement preparedStatement) throws SQLException;

        void update(Class<T> type, PreparedStatement preparedStatement) {
            try {
                populateStatement(preparedStatement);
                preparedStatement.executeUpdate();
            }
            catch (SQLException e) {
                throw new RuntimeException("AbstractUpdatingPreparedStatementCachingDaoJdbc.UpdatingJdbcWrapper encountered exception during getObject method for type: " + type, e);
            }
        }
    }

    protected abstract class RetrievingJdbcWrapper<T> extends JdbcWrapper {
        protected abstract T extractResult(ResultSet resultSet) throws SQLException;

        public T get(Class<T> type) {
            T value = null;
            PreparedStatement statement = preparedStatementCache.get(RETRIEVE_PREFIX + type);
            try {
                populateStatement(statement);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    value = extractResult(resultSet);
                    if (resultSet.next()) {
                        throw new RuntimeException("More that one row returned when selecting by primary key in AbstractRetrievingPreparedStatementCachingDaoJdbc.RetrievingJdbcWrapper for: " + type);
                    }
                }
                resultSet.close();
            }
            catch (SQLException e) {
                throw new RuntimeException("AbstractRetrievingPreparedStatementCachingDaoJdbc.RetrievingJdbcWrapper encountered exception during getObject method for type: " + type, e);
            }
            return (T) value;
        }
    }

    protected abstract class InsertingJdbcWrapper<T> extends JdbcWrapper {
        public void execute(Class<T> type) {
            update(type, preparedStatementCache.get(INSERT_PREFIX + type));
        }
    }

    protected abstract class UpdatingJdbcWrapper<T> extends JdbcWrapper {
        public void execute(Class<T> type) {
            update(type, preparedStatementCache.get(UPDATE_PREFIX + type));
        }
    }

    protected Map<String, PreparedStatement> preparedStatementCache;
    protected DateTimeService dateTimeService;

    protected abstract Map<String, String> getSql();

    public void initialize() {
        preparedStatementCache = new HashMap<String, PreparedStatement>();
        try {
            for (String statementKey : getSql().keySet()) {
                preparedStatementCache.put(statementKey, getConnection().prepareStatement(getSql().get(statementKey)));
            }
        }
        catch (SQLException e) {
            throw new RuntimeException("Caught exception preparing statements in CachingDaoJdbc initialize method", e);
        }
    }

    public void destroy() {
        try {
            for (PreparedStatement preparedStatement : preparedStatementCache.values()) {
                preparedStatement.close();
            }
            preparedStatementCache = null;
        }
        catch (SQLException e) {
            throw new RuntimeException("Caught exception closing statements in CachingDaoJdbc destroy method", e);
        }
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }
}
