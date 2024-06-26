package dev.vabalas.matas.model;

public enum SponsoredProductsCampaignsLabel {
    PRODUCT(0, "Product"),
    ENTITY(1, "Entity"),
    OPERATION(2, "Operation"),
    CAMPAIGN_ID(3, "Campaign ID"),
    AD_GROUP_ID(4, "Ad Group ID"),
    PORTFOLIO_ID(5, "Portfolio ID"),
    AD_ID(6, "Ad ID"),
    KEYWORD_ID(7, "Keyword ID"),
    PRODUCT_TARGETING_ID(8, "Product Targeting ID"),
    CAMPAIGN_NAME(9, "Campaign Name"),
    AD_GROUP_NAME(10, "Ad Group Name"),
    CAMPAIGN_NAME_INFO(11, "Campaign Name (Informational only)"),
    AD_GROUP_NAME_INFO(12, "Ad Group Name (Informational only)"),
    PORTFOLIO_NAME_INFO(13, "Portfolio Name (Informational only)"),
    START_DATE(14, "Start Date"),
    END_DATE(15, "End Date"),
    TARGETING_TYPE(16, "Targeting Type"),
    STATE(17, "State"),
    CAMPAIGN_STATE_INFO(18, "Campaign State (Informational only)"),
    AD_GROUP_STATE_INFO(19, "Ad Group State (Informational only)"),
    DAILY_BUDGET(20, "Daily Budget"),
    SKU(21, "SKU"),
    ASIN_INFO(22, "ASIN (Informational only)"),
    ELIGIBILITY_STATUS_INFO(23, "Eligibility Status (Informational only)"),
    REASON_FOR_INELIGIBILITY_INFO(24, "Reason for Ineligibility (Informational only)"),
    AD_GROUP_DEFAULT_BID(25, "Ad Group Default Bid"),
    AD_GROUP_DEFAULT_BID_INFO(26, "Ad Group Default Bid (Informational only)"),
    BID(27, "Bid"),
    KEYWORD_TEXT(28, "Keyword Text"),
    NATIVE_LANGUAGE_KEYWORD(29, "Native Language Keyword"),
    NATIVE_LANGUAGE_LOCALE(30, "Native Language Locale"),
    MATCH_TYPE(31, "Match Type"),
    BIDDING_STRATEGY(32, "Bidding Strategy"),
    PLACEMENT(33, "Placement"),
    PERCENTAGE(34, "Percentage"),
    PRODUCT_TARGETING_EXPRESSION(35, "Product Targeting Expression"),
    RESOLVED_PRODUCT_TARGETING_EXPRESSION_INFO(36, "Resolved Product Targeting Expression (Informational only)"),
    IMPRESSIONS(37, "Impressions"),
    CLICKS(38, "Clicks"),
    CLICK_THROUGH_RATE(39, "Click-through Rate"),
    SPEND(40, "Spend"),
    SALES(41, "Sales"),
    ORDERS(42, "Orders"),
    UNITS(43, "Units"),
    CONVERSION_RATE(44, "Conversion Rate"),
    ACOS(45, "ACOS"),
    CPC(46, "CPC"),
    ROAS(47, "ROAS");


    private final int index;
    private final String label;

    SponsoredProductsCampaignsLabel(int index, String label) {
        this.index = index;
        this.label = label;
    }

    public int index() {
        return index;
    }

    public String label() {
        return label;
    }
}
