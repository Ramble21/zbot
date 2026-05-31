package com.github.Ramble21;

import com.github.Ramble21.command.CommandListener;
import com.github.Ramble21.starboard.StarboardListener;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import java.io.File;

public class Zbot {

    private static ShardManager shardManager;
    private static boolean runningLocally;

    public static void main(String[] args) {

        // Load bot token
        Dotenv config = Dotenv.configure().load();
        String token = config.get("TOKEN");
        if (!validateToken(token)) {
            return;
        }

        // Build shard manager
        DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createDefault(token);
        builder.setStatus(OnlineStatus.ONLINE);
        builder.setActivity(Activity.playing("1738"));

        // User Cache and Retrieval
        builder.setMemberCachePolicy(MemberCachePolicy.ALL);
        builder.setChunkingFilter(ChunkingFilter.ALL);

        // Gateway Intents
        builder.enableIntents(GatewayIntent.MESSAGE_CONTENT);
        builder.enableIntents(GatewayIntent.GUILD_MEMBERS);

        // Register listeners
        builder.addEventListeners(
            new CommandListener(),
            new StarboardListener()
        );

        shardManager = builder.build();

        // Declare global variables
        runningLocally = new File("local.flag").exists();
        System.out.println("Running locally: " + runningLocally);

        // Test PostgreSQL driver
        try {
            Class.forName("org.postgresql.Driver");
            System.out.println("PostgreSQL JDBC driver is PRESENT!");
        } catch (ClassNotFoundException e) {
            System.out.println("PostgreSQL JDBC driver NOT found!");
        }
    }

    public static boolean validateToken(String token) {
        try {
            JDA testJda = JDABuilder.createDefault(token).build();
            testJda.awaitReady();
            System.out.println("Token successfully validated: " + testJda.getSelfUser().getAsTag());
            testJda.shutdown();
            return true;

        } catch (Exception e) {
            System.out.println("Invalid token or failed to connect: " + e.getMessage());
            return false;
        }
    }
}
