package com.truward.orion.eolaire.server.util;

import javax.annotation.Nonnull;

/**
 * Helper for transforming IDs to Tokens and vice versa.
 * TODO: make reusable
 *
 * @author Alexander Shabanov
 */
public final class ListQueryUtil {
  public static final int MAX_SIZE = 16;

  private ListQueryUtil() {} // Hidden

  public static long longFromOffsetToken(@Nonnull String offsetToken) {
    try {
      return Long.parseLong(offsetToken, 16);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Invalid offsetToken", e);
    }
  }

  @Nonnull
  public static String toOffsetToken(long id) {
    return Long.toHexString(id);
  }

  public static int checkLimit(int limit) {
    if (limit > MAX_SIZE) {
      throw new IllegalArgumentException("Size exceeds maximum: " + MAX_SIZE);
    }

    if (limit < 0) {
      throw new IllegalArgumentException("Size can't be negative");
    }

    return limit;
  }
}
