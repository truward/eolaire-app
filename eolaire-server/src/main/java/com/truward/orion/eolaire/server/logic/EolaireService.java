package com.truward.orion.eolaire.server.logic;

import com.truward.orion.eolaire.model.EolaireModel;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

/**
 * @author Alexander Shabanov
 */
public final class EolaireService {

  public interface Contract {
    @Nonnull
    EolaireModel.Item getItemById(long id);
  }

  @Transactional
  public static final class Impl implements Contract {
    private final JdbcOperations db;

    public Impl(JdbcOperations jdbcOperations) {
      this.db = Objects.requireNonNull(jdbcOperations, "jdbcOperations");
    }

    @Nonnull
    @Override
    @Transactional(readOnly = true)
    public EolaireModel.Item getItemById(long id) {
      return db.queryForObject("SELECT id, name, type_id FROM item WHERE id=?", new ItemRowMapper(), id);
    }

    private static final class ItemRowMapper implements RowMapper<EolaireModel.Item> {

      @Override
      public EolaireModel.Item mapRow(ResultSet rs, int rowNum) throws SQLException {
        return EolaireModel.Item.newBuilder()
            .setId(rs.getLong("id"))
            .setName(rs.getString("name"))
            .setItemTypeId(rs.getLong("type_id"))
            .build();
      }
    }
  }

  private EolaireService() {} // hidden
}
