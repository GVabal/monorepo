package dev.vabalas.matas.backend.util;

import dev.vabalas.matas.model.SponsoredProductsCampaignsView;

import java.nio.charset.StandardCharsets;
import java.util.Random;

public class TestDataUtils {

    private static final Random RANDOM = new Random();

    public static SponsoredProductsCampaignsView getRandomSponsoredProductsCampaignsView() {
        return new SponsoredProductsCampaignsView(
                getRandomString(10),
                getRandomString(6),
                getRandomString(20),
                getRandomString(20),
                getRandomString(20),
                getRandomString(6),
                getRandomString(15),
                getRandomString(6)
        );
    }

    public static String getRandomString(int length) {
        byte[] array = new byte[length]; // length is bounded by 7
        RANDOM.nextBytes(array);

        return new String(array, StandardCharsets.UTF_8);
    }
}
