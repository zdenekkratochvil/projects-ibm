/**
 * 
 */
package com.ibm.rio.log.remote.dao.impls;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.ibm.rio.log.remote.daos.ILogLineDAO;
import com.ibm.rio.log.remote.model.LogLine;

/**
 * @author Zdenek Kratochvil
 *
 */
public class LogLineDAOImpl extends AbstractDAO<LogLine> implements ILogLineDAO {
	
	private static final int MAX_QUEUE_SIZE = 10000;
	private ThreadLocal<List<LogLine>> insertQueueHolder = new ThreadLocal<List<LogLine>>();
	
	@Override
	public void insert(LogLine ll) {
		List<LogLine> insertQueue = getInsertQueue();
		
		insertQueue.add(ll);
		
		if(insertQueue.size() == MAX_QUEUE_SIZE) {
			bulkInsert(insertQueue);
			insertQueue.clear();
		}
//		getNamedParameterJdbcTemplate().update(getInsertSQL(), createParamSource(ll));
	}

	private List<LogLine> getInsertQueue() {
		List<LogLine> insertQueue = insertQueueHolder.get();
		if(insertQueue == null) {
			insertQueue = new LinkedList<LogLine>();
			insertQueueHolder.set(insertQueue);
		}
		return insertQueue;
	}

	private void bulkInsert(List<LogLine> queue) {
		SqlParameterSource[] params = createParams(queue);
		getNamedParameterJdbcTemplate().batchUpdate(getInsertSQL(), params);
	}

	private SqlParameterSource[] createParams(List<LogLine> queue) {
		SqlParameterSource[] params = new MapSqlParameterSource[queue.size()];
		for(int index = 0; index < queue.size(); index++) {
			LogLine ll = queue.get(index);
			
			MapSqlParameterSource source = createParamSource(ll);
			
			params[index] = source;
		}
		return params;
	}

	private MapSqlParameterSource createParamSource(LogLine ll) {
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("logText", ll.getLogText());
		source.addValue("startDate", createDate(ll.getStartDate()));
		source.addValue("clientConnectionId", ll.getConnectionId());
		source.addValue("transitionId", getEntityId(ll.getTransition()));
		source.addValue("viewId", getEntityId(ll.getView()));
		return source;
	}

	@Override
	public String getInsertSQL() {
		return "insert LogLine (logText,startDate,clientConnectionId,transitionId,viewId) values (:logText,:startDate,:clientConnectionId,:transitionId,:viewId)";
	}

	@Override
	protected void addInsertSQLTypes(PreparedStatementCreatorFactory factory) {
		factory.addParameter(new SqlParameter(Types.VARCHAR));
		factory.addParameter(new SqlParameter(Types.VARCHAR));
		factory.addParameter(new SqlParameter(Types.VARCHAR));
		factory.addParameter(new SqlParameter(Types.INTEGER));
		factory.addParameter(new SqlParameter(Types.INTEGER));
	}
	
	@Override
	public void update(LogLine entity) {
		throw new NotImplementedException();
	}

	@Override
	protected String getUpdateSQL() {
		throw new NotImplementedException();
	}

	@Override
	protected void addUpdateSQLTypes(PreparedStatementCreatorFactory factory) {
		throw new NotImplementedException();
	}

	@Override
	public List<LogLine> load(String clientConnection) {
		List<LogLine> result = getJdbcTemplate().query("select * from logline where clientConnectionId = ? order by id asc", new Object[]{clientConnection}, getLogLineRowMapper());
		return result;
	}

	@Override
	public LogLine findLastLogLine(String clientConnection, Date date) {
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("cc", clientConnection);
		params.addValue("date", createDate(date));
		
		List<LogLine> lines = getNamedParameterJdbcTemplate().query("select * from logline where clientConnectionId = :cc and startDate < :date order by startDate desc limit 1", params, getLogLineRowMapper());
		return lines.isEmpty() == false ? lines.get(0) : null;
	}

	private RowMapper<LogLine> getLogLineRowMapper() {
		return new RowMapper<LogLine>(){
			public LogLine mapRow(ResultSet rs, int rowNum) throws SQLException {
				LogLine ll = new LogLine();
				ll.setLogText(rs.getString("logText"));
				ll.setConnectionId(rs.getString("clientConnectionId"));
				
				remapAbstractEntity(rs, ll);
				
				return ll;
			}
		};
	}

	@Override
	public List<String> getConnectionsFor(Date from, Date to) {
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("from", createDate(from));
		params.addValue("to", createDate(to));
		
		List<String> lines = getNamedParameterJdbcTemplate().queryForList("select clientConnectionId from logline where startDate > :from and startDate < :to group by clientConnectionId;", params, String.class);
		return lines;
	}

	@Override
	public void flush() {
		List<LogLine> insertQueue = getInsertQueue();
		if(insertQueue.isEmpty() == false) {
			bulkInsert(insertQueue);
			insertQueue.clear();
		}
	}

}
