package net.darktree.jmxl.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(EnvType.CLIENT)
public class JmxlInitializer implements ClientModInitializer {

	public static final Logger LOGGER = LoggerFactory.getLogger("JMXL");

	@Override
	public void onInitializeClient() {
		LOGGER.info("JMXL loaded");
	}

}
