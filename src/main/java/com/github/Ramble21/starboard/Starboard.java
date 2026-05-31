package com.github.Ramble21.starboard;

public class Starboard {
    public long guildId;
    public long channelId;
    public int starboardId;
    public int minReactions;
    public String starboardName;
    public String starboardEmoji;
    public String pingRole;
    public Starboard(long guildId, long channelId, int starboardId, int minReactions, String starboardName, String starboardEmoji, String pingRole) {
        this.guildId = guildId;
        this.channelId = channelId;
        this.starboardId = starboardId;
        this.minReactions = minReactions;
        this.starboardName = starboardName;
        this.starboardEmoji = starboardEmoji;
        this.pingRole = pingRole;
    }
}
