/**
 * 
 */
package com.ibm.rio.log.remote.dao.impls;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import com.ibm.rio.log.remote.model.AbstractEntity;
import com.ibm.rio.log.remote.model.Transition;
import com.ibm.rio.log.remote.model.View;

/**
 * @author Zdenek Kratochvil
 *
 */
public abstract class AbstractDAO<T extends AbstractEntity> extends NamedParameterJdbcDaoSupport {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDAO.class);
	protected static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS");

	private PreparedStatementCreatorFactory insertFactory;
	private PreparedStatementCreatorFactory updateFactory;
	
	protected int callInsert(AbstractEntity entity, Object...params) {
		PreparedStatementCreator creator = getInsertFactory().newPreparedStatementCreator(params);
		GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
		int update = getJdbcTemplate().update(creator, keyHolder);
		entity.setId(keyHolder.getKey().longValue());
		return update;
//		return 1;
	}
	
	private PreparedStatementCreatorFactory getInsertFactory() {
		if(insertFactory == null) {
			insertFactory = new PreparedStatementCreatorFactory(getInsertSQL());
			insertFactory.setReturnGeneratedKeys(true);
			addInsertSQLTypes(insertFactory);
		}
		return insertFactory;
	}
	
	protected abstract void addInsertSQLTypes(PreparedStatementCreatorFactory factory);

	protected int callUpdate(Object...params) {
		PreparedStatementCreator creator = getUpdateFactory().newPreparedStatementCreator(params);
		int update = getJdbcTemplate().update(creator);
		return update;
//		return 1;
	}
	
	private PreparedStatementCreatorFactory getUpdateFactory() {
		if(updateFactory == null) {
			updateFactory = new PreparedStatementCreatorFactory(getUpdateSQL());
			addUpdateSQLTypes(updateFactory);
		}
		return updateFactory;
	}
	

	protected abstract void addUpdateSQLTypes(PreparedStatementCreatorFactory factory);

	protected int callUpdate(AbstractEntity entity, Object...params) {
		PreparedStatementCreator creator = getInsertFactory().newPreparedStatementCreator(params);
		GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
		int update = getJdbcTemplate().update(creator, keyHolder);
		entity.setId(keyHolder.getKey().longValue());
		return update;
	}
	
	protected String createDate(java.util.Date date) {
		if(date != null) {
			return DATE_FORMATTER.format(date);
		}
		return null;
	}
	
	protected Long getEntityId(AbstractEntity entity) {
		return entity != null ? entity.getId() : null;
	}

	protected void remapAbstractEntity(ResultSet rs, AbstractEntity entity) throws SQLException {
		entity.setId(rs.getLong("id"));

		String dateStr = rs.getString("startDate");
		if(StringUtils.isNotBlank(dateStr)) {
			try {
				entity.setStartDate(DATE_FORMATTER.parse(dateStr));
			} catch (ParseException e) {
				LOGGER.error(e.getLocalizedMessage());
			}
		}
			
		Object transitionIdObj = rs.getObject("transitionId");
		if(transitionIdObj != null) {
			Transition tr = new Transition();
			tr.setId((Integer)transitionIdObj);
			entity.setParent(tr);
		}
		
		Object viewIdObj = rs.getObject("viewId");
		if(viewIdObj != null) {
			View view = new View();
			view.setId((Integer)viewIdObj);
			entity.setParent(view);
		}
	}
	
	protected abstract String getInsertSQL();
	
	protected abstract String getUpdateSQL();

}
