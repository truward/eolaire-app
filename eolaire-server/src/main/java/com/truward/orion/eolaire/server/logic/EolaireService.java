package com.truward.orion.eolaire.server.logic;

import com.google.protobuf.InvalidProtocolBufferException;
import com.truward.orion.eolaire.model.EolaireModel;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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

    @Nonnull
    List<EolaireModel.EntityType> getEntityTypeByName(@Nonnull String name);

    @Nonnull
    List<EolaireModel.EntityType> getEntityTypesOrderedById(@Nullable Long startEntityId, int size);

    @Nonnull
    List<Long> getItemIdsByType(long itemTypeId, @Nullable Long startEntityId, int size);

    @Nonnull
    List<Long> getItemIdsByRelation(long itemId,
                                    @Nullable Long relationTypeId,
                                    @Nullable Long relatedItemTypeId,
                                    @Nullable Long startEntityId,
                                    int size);
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

    @Nonnull
    @Override
    public List<EolaireModel.EntityType> getEntityTypeByName(@Nonnull String name) {
      return db.query("SELECT id, name FROM entity_type WHERE name=?", new EntityTypeRowMapper(), name);
    }

    @Nonnull
    @Override
    public List<EolaireModel.EntityType> getEntityTypesOrderedById(@Nullable Long startEntityId, int size) {
      if (startEntityId == null) {
        return db.query("SELECT id, name FROM entity_type ORDER BY id LIMIT ?", new EntityTypeRowMapper(), size);
      }

      return db.query("SELECT id, name FROM entity_type WHERE id > ? ORDER BY id LIMIT ?", new EntityTypeRowMapper(),
          startEntityId, size);
    }

    @Nonnull
    @Override
    public List<Long> getItemIdsByType(long itemTypeId, @Nullable Long startEntityId, int size) {
      return db.queryForList("SELECT id FROM item WHERE type_id=? AND ((? IS NULL) OR (id > ?)) ORDER BY id LIMIT ?",
          Long.class, itemTypeId, startEntityId, startEntityId, size);
    }

    @Nonnull
    @Override
    public List<Long> getItemIdsByRelation(long itemId,
                                           @Nullable Long relationTypeId,
                                           @Nullable Long relatedItemTypeId,
                                           @Nullable Long startEntityId,
                                           int size) {
      return db.queryForList("SELECT l.id FROM item AS l\n" +
              "INNER JOIN item_relation AS ir ON ir.lhs=l.id\n" +
              "INNER JOIN item AS r ON ir.rhs=r.id\n" +
              "WHERE r.id=? " +
              "AND ((? IS NULL) OR (ir.type_id=?)) " +
              "AND ((? IS NULL) OR (l.type_id=?)) " +
              "AND ((? IS NULL) OR (l.id > ?)) " +
              "ORDER BY id " +
              "LIMIT ?",
          Long.class,
          itemId,
          relationTypeId, relationTypeId,
          relatedItemTypeId, relatedItemTypeId,
          startEntityId, startEntityId,
          size);
    }
  }

  //
  // Mappers
  //

  private static final class EntityTypeRowMapper implements RowMapper<EolaireModel.EntityType> {

    @Override
    public EolaireModel.EntityType mapRow(ResultSet rs, int rowNum) throws SQLException {
      return EolaireModel.EntityType.newBuilder()
          .setId(rs.getLong("id"))
          .setName(rs.getString("name"))
          .build();
    }
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

  private EolaireService() {} // hidden
}
