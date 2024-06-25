package dev.vabalas.matas.model;

public enum SponsoredProductsCampaignsLabel {
    PRODUCT(0),
    ENTITY(1),
    OPERATION(2),
    CAMPAIGN_ID(3),
    AD_GROUP_ID(4),
    STATE(17),
    KEYWORD_TEXT(28),
    MATCH_TYPE(31);

    private final int index;

    SponsoredProductsCampaignsLabel(int index) {
        this.index = index;
    }

    public int index() {
        return index;
    }
}
