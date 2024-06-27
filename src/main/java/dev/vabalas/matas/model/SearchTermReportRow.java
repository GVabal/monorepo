package dev.vabalas.matas.model;

public record SearchTermReportRow(
        String product,
        Long campaignId,
        Long adGroupId,
        Long keywordId,
        Long productTargetingId,
        State state,
        State campaignState,
        Double bid,
        String keywordText,
        MatchType matchType,
        String productTargetingExpression,
        String customerSearchTerm,
        Integer impressions,
        Integer clicks,
        Double clickThroughRate,
        Double spend,
        Double sales,
        Integer orders,
        Integer units,
        Double conversionRate,
        Double acos,
        Double cpc,
        Double roas
) {
    public enum State {
        ENABLED,
        PAUSED
    }

    public enum MatchType {
        BROAD,
        EXACT,
        PHRASE;

        public static MatchType valueOfNullable(String name) {
            try {
                return MatchType.valueOf(name);
            } catch (Exception e) {
                return null;
            }
        }
    }
}
