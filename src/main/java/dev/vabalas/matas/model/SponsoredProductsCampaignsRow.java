package dev.vabalas.matas.model;

public record SponsoredProductsCampaignsRow(
    String product,
    String entity,
    String operation,
    String campaignId,
    String adGroupId,
    String state,
    String keywordText,
    String matchType
) {
}
