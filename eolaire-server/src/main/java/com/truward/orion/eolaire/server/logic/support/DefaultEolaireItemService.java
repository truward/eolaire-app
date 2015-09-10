package com.truward.orion.eolaire.server.logic.support;

import com.google.protobuf.InvalidProtocolBufferException;
import com.truward.orion.eolaire.model.EolaireModel;
import com.truward.orion.eolaire.server.logic.EolaireItemService;
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
 * Default implementation of {@link EolaireItemService}.
 */
public final class DefaultEolaireItemService implements EolaireItemService {
  private final JdbcOperations db;

  public DefaultEolaireItemService(JdbcOperations jdbcOperations) {
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
  public List<EolaireModel.EntityType> getEntityTypesOrderedById(@Nullable Long startEntityId, int limit) {
    if (startEntityId == null) {
      return db.query("SELECT id, name FROM entity_type ORDER BY id LIMIT ?", new EntityTypeRowMapper(), limit);
    }

    return db.query("SELECT id, name FROM entity_type WHERE id > ? ORDER BY id LIMIT ?", new EntityTypeRowMapper(),
        startEntityId, limit);
  }

  @Nonnull
  @Override
  public List<Long> getItemIdsByType(long itemTypeId, @Nullable Long startEntityId, int limit) {
    return db.queryForList("SELECT id FROM item WHERE type_id=? AND ((? IS NULL) OR (id > ?)) ORDER BY id LIMIT ?",
        Long.class, itemTypeId, startEntityId, startEntityId, limit);
  }

  @Nonnull
  @Override
  public List<Long> getItemIdsByRelation(long itemId,
                                         @Nullable Long relationTypeId,
                                         @Nullable Long relatedItemTypeId,
                                         @Nullable Long startEntityId,
                                         int limit) {
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
        limit);
  }

  @Nonnull
  @Override
  public List<EolaireModel.ItemRelation> getItemRelations(long itemId) {
    return db.query("SELECT rhs, type_id, metadata FROM item_relation WHERE lhs=? ORDER BY rhs",
        new ItemRelationRowMapper(), itemId);
  }

  //
  // Mappers
  //

  private static final class ItemRelationRowMapper implements RowMapper<EolaireModel.ItemRelation> {

    @Override
    public EolaireModel.ItemRelation mapRow(ResultSet rs, int i) throws SQLException {
      return EolaireModel.ItemRelation.newBuilder()
          .setTargetItemId(rs.getLong("rhs"))
          .setRelationTypeId(rs.getLong("type_id"))
          .setMetadata(getMetadata(rs, "metadata"))
          .build();
    }
  }

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
      return EolaireModel.ItemProfile.newBuilder()
          .setItemId(rs.getLong("item_id"))
          .setDescription(rs.getString("description"))
          .setCreated(rs.getTimestamp("date_created").getTime())
          .setUpdated(rs.getTimestamp("date_updated").getTime())
          .setFlags(rs.getLong("flags"))
          .setMetadata(getMetadata(rs, "metadata"))
          .build();
    }
  }

  private static EolaireModel.Metadata getMetadata(ResultSet rs, String name) throws SQLException {
    final byte[] metadataBytes = rs.getBytes(name);
    if (metadataBytes == null) {
      return EolaireModel.Metadata.newBuilder().build();
    }

    try {
      return EolaireModel.Metadata.parseFrom(metadataBytes);
    } catch (InvalidProtocolBufferException e) {
      throw new SQLException("Unable to deserialize metadata", e);
    }
  }
}
