package com.truward.orion.eolaire.server;

import com.truward.orion.eolaire.model.EolaireModel;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

/**
 * Tests serializing and deserializing metadata
 */
public final class MetadataSerializationTest {

  /**
   * NOTE: this test is important as by default metadata will be empty in eolaire-schema tables.
   */
  @Test
  public void shouldSerializeEmptyMetadataToEmptyByteArray() {
    // Given:
    final EolaireModel.Metadata metadata = EolaireModel.Metadata.newBuilder().build();

    // When:
    final byte[] bytes = metadata.toByteArray();

    // Then:
    assertArrayEquals(new byte[0], bytes);
  }
}
