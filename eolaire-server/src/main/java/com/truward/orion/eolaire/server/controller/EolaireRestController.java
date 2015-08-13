package com.truward.orion.eolaire.server.controller;

import com.truward.brikar.server.controller.AbstractRestController;
import com.truward.orion.eolaire.model.EolaireModel;
import com.truward.orion.eolaire.model.EolaireRestService;
import com.truward.orion.eolaire.server.logic.EolaireService;
import com.truward.orion.eolaire.server.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Objects;

/**
 * Implementation of {@link EolaireRestService} that exposes major operations on catalog.
 *
 * @author Alexander Shabanov
 */
@Controller
@RequestMapping("/rest/eolaire")
public final class EolaireRestController extends AbstractRestController implements EolaireRestService {
  private final EolaireService.Contract eolaireService;

  @Autowired
  public EolaireRestController(EolaireService.Contract eolaireService) {
    this.eolaireService = Objects.requireNonNull(eolaireService);
  }

  @Override
  public EolaireModel.Item getItemById(@PathVariable("id") long id) {
    return eolaireService.getItemById(id);
  }

  @Override
  public EolaireModel.GetItemsResponse getItemsByIds(@RequestBody EolaireModel.GetItemsRequest request) {
    final EolaireModel.GetItemsResponse.Builder builder = EolaireModel.GetItemsResponse.newBuilder();
    for (final long id : request.getItemIdList()) {
      builder.addItem(eolaireService.getItemById(id));
    }
    return builder.build();
  }

  @Override
  public EolaireModel.GetItemProfileResponse getItemProfile(@PathVariable("id") long id) {
    final List<EolaireModel.ItemProfile> profiles = eolaireService.getItemProfile(id); // should contain 0 or 1 entry

    final EolaireModel.GetItemProfileResponse.Builder builder = EolaireModel.GetItemProfileResponse.newBuilder();

    if (!profiles.isEmpty()) {
      assert profiles.size() == 1;
      builder.setProfile(profiles.get(0));
    }

    return builder.build();
  }

  @Override
  public EolaireModel.GetEntityTypeResponse getEntityTypeByName(@RequestParam("name") String name) {
    final EolaireModel.GetEntityTypeResponse.Builder builder = EolaireModel.GetEntityTypeResponse.newBuilder();

    final List<EolaireModel.EntityType> types = eolaireService.getEntityTypeByName(name);
    if (!types.isEmpty()) {
      assert types.size() == 1;
      builder.setType(types.get(0));
    }

    return builder.build();
  }

  @Override
  public EolaireModel.GetAllEntityTypesResponse getAllEntities(@RequestBody EolaireModel.GetAllEntityTypesRequest request) {
    // get size
    final int size = request.getSize();
    if (size > Constants.MAX_SIZE) {
      throw new IllegalArgumentException("Size value exceeded");
    }

    if (size < 0) {
      throw new IllegalArgumentException("Size can't be negative");
    }

    // convert offset token
    Long startEntityId = null;
    if (request.hasOffsetToken()) {
      try {
        startEntityId = Long.parseLong(request.getOffsetToken(), 16);
      } catch (NumberFormatException e) {
        throw new IllegalArgumentException("Invalid offsetToken", e);
      }
    }

    // prepare response
    final EolaireModel.GetAllEntityTypesResponse.Builder builder = EolaireModel.GetAllEntityTypesResponse.newBuilder();
    if (size == 0) {
      return builder.setOffsetToken(request.getOffsetToken()).build();
    }

    // prepare response builder
    final List<EolaireModel.EntityType> entityTypes = eolaireService.getEntityTypesOrderedById(startEntityId, size);
    builder.addAllType(entityTypes);
    if (entityTypes.size() == size) {
      builder.setOffsetToken(Long.toHexString(entityTypes.get(size - 1).getId()));
    }

    return builder.build();
  }

  @Override
  public EolaireModel.GetItemByTypeResponse getItemByType(@RequestBody EolaireModel.GetItemByTypeRequest request) {
    throw new UnsupportedOperationException();
  }

  @Override
  public EolaireModel.GetItemByRelationResponse getItemByRelation(@RequestBody EolaireModel.GetItemByRelationRequest request) {
    throw new UnsupportedOperationException();
  }
}
