package com.truward.orion.eolaire.server.logic;

import com.google.protobuf.InvalidProtocolBufferException;
import com.truward.orion.eolaire.model.EolaireModel;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

/**
 * @author Alexander Shabanov
 */
public final class EolaireService {

  public interface Contract {
    @Nonnull
    EolaireModel.Item getItemById(long id);

    @Nonnull
    List<EolaireModel.ItemProfile> getItemProfile(long itemId);
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

    @Nonnull
    @Override
    public List<EolaireModel.ItemProfile> getItemProfile(long itemId) {
      return db.query("SELECT item_id, description, date_created, date_updated, flags, metadata FROM item_profile " +
          "WHERE item_id=?", new ItemProfileRowMapper(), itemId);
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

    private static final class ItemProfileRowMapper implements RowMapper<EolaireModel.ItemProfile> {

      @Override
      public EolaireModel.ItemProfile mapRow(ResultSet rs, int rowNum) throws SQLException {
        final byte[] metadataBytes = rs.getBytes("metadata");
        final EolaireModel.Metadata metadata;
        try {
          metadata = EolaireModel.Metadata.parseFrom(metadataBytes);
        } catch (InvalidProtocolBufferException e) {
          throw new SQLException("Unable to deserialize metadata", e);
        }

        return EolaireModel.ItemProfile.newBuilder()
            .setItemId(rs.getLong("item_id"))
            .setDescription(rs.getString("description"))
            .setCreated(rs.getTimestamp("date_created").getTime())
            .setUpdated(rs.getTimestamp("date_updated").getTime())
            .setFlags(rs.getLong("flags"))
            .setMetadata(metadata)
            .build();
      }
    }
  }

  private EolaireService() {} // hidden
}
