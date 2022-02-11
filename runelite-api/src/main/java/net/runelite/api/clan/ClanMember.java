package net.runelite.api.clan;

/**
 * A member of a clan.
 */
public interface ClanMember
{
  /**
   * The clan member's name
   */
  String getName();

  /**
   * The clan member's rank
   */
  ClanRank getRank();
}