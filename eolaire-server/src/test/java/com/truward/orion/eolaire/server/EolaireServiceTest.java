package com.truward.orion.eolaire.server;

import com.truward.orion.eolaire.model.EolaireModel;
import com.truward.orion.eolaire.server.logic.EolaireService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/spring/EolaireServiceTest-context.xml")
@Transactional
public final class EolaireServiceTest {

  @Resource
  private EolaireService.Contract eolaireService;

  @Test
  public void shouldGetItem() {
    final EolaireModel.Item item = eolaireService.getItemById(151);

    assertEquals(151, item.getId());
  }

  @Test
  public void shouldNotGetItemProfile() {
    assertTrue(eolaireService.getItemProfile(151).isEmpty());
  }
}
