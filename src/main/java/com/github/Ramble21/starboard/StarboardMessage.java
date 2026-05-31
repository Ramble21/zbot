package com.github.Ramble21.starboard;

public class StarboardMessage {
    public long authorId;
    public long messageId;
    public int starboardId;
    public StarboardMessage(long authorId, long messageId, int starboardId) {
        this.authorId = authorId;
        this.messageId = messageId;
        this.starboardId = starboardId;
    }
}
