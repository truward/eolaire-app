package com.truward.orion.eolaire.server.controller;

import com.truward.orion.eolaire.model.EolaireModel;
import com.truward.orion.eolaire.model.EolaireRestService;
import com.truward.orion.eolaire.server.logic.EolaireService;
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
public final class EolaireRestController implements EolaireRestService {
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
    throw new UnsupportedOperationException();
  }

  @Override
  public EolaireModel.GetAllEntityTypesResponse getAllEntities(@RequestBody EolaireModel.GetAllEntityTypesRequest request) {
    throw new UnsupportedOperationException();
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
