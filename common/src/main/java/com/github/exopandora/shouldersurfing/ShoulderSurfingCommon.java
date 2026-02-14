package com.github.exopandora.shouldersurfing;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ShoulderSurfingCommon
{
    public static final String MOD_ID = "shouldersurfing";
    public static final Logger LOGGER = LogManager.getLogger("ShoulderSurfing");

    static {
        LOGGER.info("[{}] has been loaded!", MOD_ID);
    }
}
