package com.github.Ramble21.starboard;

public class Starboard {
    public long guildId;
    public long channelId;
    public int starboardId;
    public String starboardName;
    public String starboardEmoji;
    public Starboard(long guildId, long channelId, int starboardId, String starboardName, String starboardEmoji) {
        this.guildId = guildId;
        this.channelId = channelId;
        this.starboardId = starboardId;
        this.starboardName = starboardName;
        this.starboardEmoji = starboardEmoji;
    }
}
