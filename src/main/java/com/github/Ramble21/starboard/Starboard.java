package com.github.Ramble21.starboard;

public class Starboard {
    public long guildId;
    public long channelId;
    public int starboardId;
    public int minReactions;
    public String starboardName;
    public String starboardEmoji;
    public Starboard(long guildId, long channelId, int starboardId, int minReactions, String starboardName, String starboardEmoji) {
        this.guildId = guildId;
        this.channelId = channelId;
        this.starboardId = starboardId;
        this.minReactions = minReactions;
        this.starboardName = starboardName;
        this.starboardEmoji = starboardEmoji;
    }
}
