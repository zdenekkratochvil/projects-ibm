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

import com.ibm.rio.log.remote.daos.IViewDAO;
import com.ibm.rio.log.remote.model.View;
import com.ibm.rio.log.remote.model.enums.ViewType;

/**
 * @author Zdenek Kratochvil
 *
 */
public class ViewDAOImpl extends AbstractDAO<View> implements IViewDAO {

	@Override
	public void insert(View view) {
		callInsert(view, view.getType().name(), view.getName(), createDate(view.getStartDate()), view.getDuration(), getEntityId(view.getTransition()), getEntityId(view.getView()));
	}

	@Override
	public String getInsertSQL() {
		return "insert View (type,name,startDate,duration,transitionId,viewId) values (?,?,?,?,?,?)";
	}

	@Override
	public void update(View entity) {
		callUpdate(entity.getDuration());
	}

	@Override
	protected String getUpdateSQL() {
		return "update View set duration=?";
	}

	@Override
	protected void addInsertSQLTypes(PreparedStatementCreatorFactory factory) {
		factory.addParameter(new SqlParameter(Types.VARCHAR));
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
	public List<View> load(List<Long> viewIds) {
		List<View> result = getNamedParameterJdbcTemplate().query("select * from view where id in (:ids)", new MapSqlParameterSource("ids", viewIds), getViewRowMapper());
		return result;
	}

	@Override
	public View load(long id) {
		List<View> list = getNamedParameterJdbcTemplate().query("select * from view where id = :id", new MapSqlParameterSource("id", id), getViewRowMapper());
		return list.isEmpty() == false ? list.get(0) : null;
	}

	private RowMapper<View> getViewRowMapper() {
		return new RowMapper<View>(){
			public View mapRow(ResultSet rs, int rowNum) throws SQLException {
				View row = new View();
				row.setType(ViewType.valueOf(rs.getString("type")));
				row.setName(rs.getString("name"));
				
				remapAbstractEntity(rs, row);
				
				return row;
			}
			
		};
	}

}
