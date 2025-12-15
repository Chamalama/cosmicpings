package com.cham.config;

import com.cham.CosmicpingsClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.client.MinecraftClient;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Modifier;

public class PingConfigStorage {

    private final Gson JSON = new GsonBuilder().setPrettyPrinting().serializeNulls().disableHtmlEscaping().excludeFieldsWithModifiers(Modifier.TRANSIENT).create();
    private final File file;

    private final CosmicpingsClient client = CosmicpingsClient.getINSTANCE();

    public PingConfigStorage() {
        this.file = new File(MinecraftClient.getInstance().runDirectory, "config.json");
        createDefaults();
    }

    private void createDefaults() {
        if(!this.file.getParentFile().exists()) {
            this.file.getParentFile().mkdir();
        }
        if(!this.file.exists()) {
            try {
                this.file.createNewFile();
            }catch (IOException e) {
                throw new RuntimeException(e);
            }
            try(FileWriter writer = new FileWriter(file)) {
                writer.write(JSON.toJson(client.getPingConfig(), PingConfig.class));
            }catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else{
            initConfig();
        }
    }

    public void update() {
        try(FileWriter writer = new FileWriter(file)) {
            writer.write(JSON.toJson(client.getPingConfig(), PingConfig.class));
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void initConfig() {
        client.setPingConfig(getStoredConfig());
    }

    public PingConfig getStoredConfig() {
        try(FileReader reader = new FileReader(file)) {
            return JSON.fromJson(reader, PingConfig.class);
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
