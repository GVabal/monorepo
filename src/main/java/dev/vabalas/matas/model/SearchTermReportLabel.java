package dev.vabalas.matas.model;

public enum SearchTermReportLabel {
        PRODUCT(0),
        CAMPAIGN_ID(1),
        AD_GROUP_ID(2),
        KEYWORD_ID(3),
        PRODUCT_TARGETING_ID(4),
        STATE(8),
        CAMPAIGN_STATE(9),
        BID(10),
        KEYWORD_TEXT(11),
        MATCH_TYPE(12),
        PRODUCT_TARGETING_EXPRESSION(13),
        CUSTOMER_SEARCH_TERM(15),
        IMPRESSIONS(16),
        CLICKS(17),
        CLICK_THROUGH(18),
        SPEND(19),
        SALES(20),
        ORDERS(21),
        UNITS(22),
        CONVERSION_RATE(23),
        ACOS(24),
        CPC(25),
        ROAS(26);

        private final int index;

        SearchTermReportLabel(int index) {
            this.index = index;
        }

        public int index() {
            return index;
        }
    }