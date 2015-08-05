package com.truward.orion.eolaire.server.controller;

import com.truward.orion.eolaire.model.EolaireModel;
import com.truward.orion.eolaire.model.EolaireRestService;
import com.truward.orion.eolaire.server.logic.EolaireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

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
    throw new UnsupportedOperationException();
  }
}
