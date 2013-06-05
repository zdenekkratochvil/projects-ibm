/**
 * 
 */
package com.ibm.rio.log.remote.dao.impls;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.ibm.rio.log.remote.daos.ITransitionDAO;
import com.ibm.rio.log.remote.model.Transition;
import com.ibm.rio.log.remote.model.enums.TransitionType;

/**
 * @author Zdenek Kratochvil
 *
 */
public class TransitionDAOImpl extends AbstractDAO<Transition> implements ITransitionDAO {

	@Override
	public void insert(Transition tr) {
		callInsert(tr, tr.getType().name(), createDate(tr.getStartDate()), tr.getDuration(), getEntityId(tr.getTransition()), getEntityId(tr.getView()));
	}

	@Override
	public String getInsertSQL() {
		return "insert Transition (type,startDate,duration,transitionId,viewId) values(?,?,?,?,?)";
	}

	@Override
	public void update(Transition tr) {
		callUpdate(tr.getDuration());
	}

	@Override
	protected String getUpdateSQL() {
		return "update Transition set duration=?";
	}

	@Override
	protected void addInsertSQLTypes(PreparedStatementCreatorFactory factory) {
		factory.addParameter(new SqlParameter(Types.VARCHAR));
		factory.addParameter(new SqlParameter(Types.VARCHAR));
		factory.addParameter(new SqlParameter(Types.INTEGER));
		factory.addParameter(new SqlParameter(Types.INTEGER));
		factory.addParameter(new SqlParameter(Types.INTEGER));
	}

	@Override
	protected void addUpdateSQLTypes(PreparedStatementCreatorFactory factory) {
		factory.addParameter(new SqlParameter(Types.INTEGER));
	}

	@Override
	public List<Transition> load(List<Long> transitionIds) {
		List<Transition> result = getNamedParameterJdbcTemplate().query("select * from transition where id in (:ids)", new MapSqlParameterSource("ids", transitionIds), getTransitionRowMapper());
		return result;
	}

	@Override
	public Transition load(long id) {
		List<Transition> list = getNamedParameterJdbcTemplate().query("select * from transition where id = :id", new MapSqlParameterSource("id", id), getTransitionRowMapper());
		return list.isEmpty() == false ? list.get(0) : null;
	}

	private RowMapper<Transition> getTransitionRowMapper() {
		return new RowMapper<Transition>(){
			public Transition mapRow(ResultSet rs, int rowNum) throws SQLException {
				Transition row = new Transition();
				row.setType(TransitionType.valueOf(rs.getString("type")));
				
				remapAbstractEntity(rs, row);
				
				return row;
			}
			
		};
	}
	
}
