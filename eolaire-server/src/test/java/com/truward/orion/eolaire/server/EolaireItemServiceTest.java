package com.truward.orion.eolaire.server;

import com.truward.orion.eolaire.model.EolaireModel;
import com.truward.orion.eolaire.server.logic.EolaireItemService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/spring/EolaireServiceTest-context.xml")
@Transactional
public final class EolaireItemServiceTest {

  private final List<EolaireModel.EntityType> actualEntityTypes = Collections.unmodifiableList(Arrays.asList(
      entityType(1, "author"), entityType(2, "language"), entityType(3, "person"), entityType(5, "book"),
      entityType(6, "movie"), entityType(7, "series")));

  @Resource EolaireItemService itemService;

  @Test
  public void shouldGetItem() {
    final EolaireModel.Item item = itemService.getItemById(151);

    assertEquals(151, item.getId());
  }

  @Test
  public void shouldNotGetItemProfile() {
    assertTrue(itemService.getItemProfile(151).isEmpty());
  }

  @Test
  public void shouldGetItemProfileWithMetadata() {
    assertEquals(Collections.singletonList(EolaireModel.ItemProfile.newBuilder()
            .setItemId(1000L)
            .setDescription("Fine Author")
            .setCreated(1432762974000L)
            .setUpdated(1432811825000L)
            .setFlags(1)
            .setMetadata(EolaireModel.Metadata.newBuilder().build())
            .build()),
        itemService.getItemProfile(1000L));
  }

  @Test
  public void shouldGetItemProfileWithoutMetadata() {
    assertEquals(Collections.singletonList(EolaireModel.ItemProfile.newBuilder()
            .setItemId(1001L)
            .setDescription("Another Fine Author")
            .setCreated(1432804399000L)
            .setUpdated(1432811794000L)
            .setFlags(1)
            .setMetadata(EolaireModel.Metadata.newBuilder().build())
            .build()),
        itemService.getItemProfile(1001L));
  }

  @Test
  public void shouldGetEntityTypeByName() {
    assertEquals(Collections.singletonList(entityType(1, "author")), itemService.getEntityTypeByName("author"));
  }

  @Test
  public void shouldGetNoEntityTypesByNonExistentName() {
    assertTrue(itemService.getEntityTypeByName("unknownName").isEmpty());
  }

  @Test
  public void shouldGetAllEntityTypesInOneTurn() {
    assertEquals(actualEntityTypes, itemService.getEntityTypesOrderedById(null, 10));
  }

  @Test
  public void shouldGetEntityTypesOneByOne() {
    final List<EolaireModel.EntityType> allTypes = new ArrayList<>();
    Long lastEntityId = null;
    do {
      final List<EolaireModel.EntityType> types = itemService.getEntityTypesOrderedById(lastEntityId, 1);
      allTypes.addAll(types);

      if (!types.isEmpty()) {
        lastEntityId = types.get(types.size() - 1).getId();
      } else {
        lastEntityId = null;
      }
    } while (lastEntityId != null);

    assertEquals(actualEntityTypes, allTypes);
  }

  @Test
  public void shouldGetItemIdsByRelationUsingItemId() {
    final List<Long> ids = itemService.getItemIdsByRelation(1000L, null, null, null, 10);
    assertEquals(Collections.singletonList(1100L), ids);
  }

  @Test
  public void shouldGetItemIdsByRelationUsingItemIdAndRelationTypeId() {
    final List<Long> ids = itemService.getItemIdsByRelation(1000L, 1L, null, null, 10);
    assertEquals(Collections.singletonList(1100L), ids);
  }

  @Test
  public void shouldGetItemIdsByRelationUsingItemIdAndRelationTypeIdAndRelatedItemTypeId() {
    final List<Long> ids = itemService.getItemIdsByRelation(1000L, 1L, 5L, null, 10);
    assertEquals(Collections.singletonList(1100L), ids);
  }

  //
  // Private
  //

  private static EolaireModel.EntityType entityType(long id, String name) {
    return EolaireModel.EntityType.newBuilder().setId(id).setName(name).build();
  }
}
