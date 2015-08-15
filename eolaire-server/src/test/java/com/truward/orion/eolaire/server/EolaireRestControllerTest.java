package com.truward.orion.eolaire.server;

import com.truward.orion.eolaire.model.EolaireRestService;
import com.truward.orion.eolaire.server.controller.EolaireRestController;
import com.truward.orion.eolaire.server.logic.EolaireService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import static com.truward.orion.eolaire.model.EolaireModel.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link EolaireRestController}.
 */
@RunWith(MockitoJUnitRunner.class)
public final class EolaireRestControllerTest {
  @Mock EolaireService.Contract eolaireServiceMock;

  EolaireRestService controller;

  @Before
  public void init() {
    controller = new EolaireRestController(eolaireServiceMock);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldFailToOnIllegalSize() {
    // Given:
    final int limit = -1;

    // When:
    controller.getAllEntities(GetAllEntityTypesRequest.newBuilder().setLimit(limit).build());

    // Then:
    fail("IllegalArgumentException expected");
  }

  @Test
  public void shouldGetEmptyEntitiesListWithoutAccessingToService() {
    // Given:
    final int limit = 0;

    // When:
    final GetAllEntityTypesResponse response =  controller.getAllEntities(GetAllEntityTypesRequest.newBuilder()
        .setLimit(limit).build());

    // Then:
    assertFalse(response.hasOffsetToken());
    assertEquals(0, response.getTypesCount());
  }

  @Test
  public void shouldGetAllEntitiesWithOffsetToken() {
    // Given:
    final int limit = 1;
    final long ids[] = {100L, 200L};
    final String name = "name";
    final EntityType entityType1 = EntityType.newBuilder().setId(ids[0]).setName(name).build();
    final List<EntityType> typeList1 = Collections.singletonList(entityType1);

    when(eolaireServiceMock.getEntityTypesOrderedById(null, limit)).thenReturn(typeList1);

    final int limit2 = 2;
    final EntityType entityType2 = EntityType.newBuilder().setId(ids[1]).setName(name).build();
    final List<EntityType> typeList2 = Collections.singletonList(entityType2);
    when(eolaireServiceMock.getEntityTypesOrderedById(ids[0], limit2)).thenReturn(typeList2);

    // When:
    GetAllEntityTypesResponse response =  controller.getAllEntities(GetAllEntityTypesRequest.newBuilder()
        .setLimit(limit).build());

    // Then:
    assertTrue(response.hasOffsetToken());
    assertEquals(typeList1, response.getTypesList());

    response = controller.getAllEntities(GetAllEntityTypesRequest.newBuilder()
        .setOffsetToken(response.getOffsetToken()).setLimit(limit2).build());
    assertFalse(response.hasOffsetToken());
    assertEquals(typeList2, response.getTypesList());
  }

  @Test
  public void shouldGetEmptyItemListByTimeWithoutAccessingToService() {
    // Given:
    final int limit = 0;

    // When:
    final GetItemByTypeResponse response =  controller.getItemByType(GetItemByTypeRequest.newBuilder()
        .setItemTypeId(1L).setLimit(limit).build());

    // Then:
    assertFalse(response.hasOffsetToken());
    assertEquals(0, response.getItemIdsCount());
  }

  @Test
  public void shouldGetEmptyItemListByRelationWithoutAccessingToService() {
    // Given:
    final int limit = 0;

    // When:
    final GetItemByRelationResponse response =  controller.getItemByRelation(GetItemByRelationRequest.newBuilder()
        .setItemId(1L).setLimit(limit).build());

    // Then:
    assertFalse(response.hasOffsetToken());
    assertEquals(0, response.getItemIdsCount());
  }
}
