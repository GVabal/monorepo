package dev.vabalas.matas.backend;

import dev.vabalas.matas.model.SearchTermReportRow;
import dev.vabalas.matas.model.SponsoredProductsCampaignsLabel;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import static dev.vabalas.matas.model.SearchTermReportLabel.ACOS;
import static dev.vabalas.matas.model.SearchTermReportLabel.AD_GROUP_ID;
import static dev.vabalas.matas.model.SearchTermReportLabel.BID;
import static dev.vabalas.matas.model.SearchTermReportLabel.CAMPAIGN_ID;
import static dev.vabalas.matas.model.SearchTermReportLabel.CAMPAIGN_STATE;
import static dev.vabalas.matas.model.SearchTermReportLabel.CLICKS;
import static dev.vabalas.matas.model.SearchTermReportLabel.CLICK_THROUGH;
import static dev.vabalas.matas.model.SearchTermReportLabel.CONVERSION_RATE;
import static dev.vabalas.matas.model.SearchTermReportLabel.CPC;
import static dev.vabalas.matas.model.SearchTermReportLabel.CUSTOMER_SEARCH_TERM;
import static dev.vabalas.matas.model.SearchTermReportLabel.IMPRESSIONS;
import static dev.vabalas.matas.model.SearchTermReportLabel.KEYWORD_ID;
import static dev.vabalas.matas.model.SearchTermReportLabel.KEYWORD_TEXT;
import static dev.vabalas.matas.model.SearchTermReportLabel.MATCH_TYPE;
import static dev.vabalas.matas.model.SearchTermReportLabel.ORDERS;
import static dev.vabalas.matas.model.SearchTermReportLabel.PRODUCT;
import static dev.vabalas.matas.model.SearchTermReportLabel.PRODUCT_TARGETING_EXPRESSION;
import static dev.vabalas.matas.model.SearchTermReportLabel.PRODUCT_TARGETING_ID;
import static dev.vabalas.matas.model.SearchTermReportLabel.ROAS;
import static dev.vabalas.matas.model.SearchTermReportLabel.SALES;
import static dev.vabalas.matas.model.SearchTermReportLabel.SPEND;
import static dev.vabalas.matas.model.SearchTermReportLabel.STATE;
import static dev.vabalas.matas.model.SearchTermReportLabel.UNITS;

@Service
public class NegateService {

    private static final String SPONSORED_PRODUCTS_CAMPAIGNS_SHEET_NAME = "Sponsored Products Campaigns";
    private static final String PROCESSED_ROWS_SHEET_NAME = "Processed rows";
    private static final String SEARCH_TERM_REPORT_SHEET_NAME = "SP Search Term Report";
    private static final int TOP_ROW_INDEX = 0;

    public void extractInterestingRows(File excelFile) throws IOException {
        try (var inputStream = new FileInputStream(excelFile);
             var outputStream = new FileOutputStream("./src/main/resources/upload-file.xlsx");
             var workBook = new XSSFWorkbook(inputStream);
             var newWorkBook = new XSSFWorkbook()) {
            var rowIterator = workBook.getSheet(SEARCH_TERM_REPORT_SHEET_NAME).rowIterator();
            rowIterator.next(); // skip label column

            var searchTermReportRows = new ArrayList<SearchTermReportRow>();
            while (rowIterator.hasNext()) {
                searchTermReportRows.add(mapToSearchTermReportRow(rowIterator.next()));
            }

            newWorkBook.createSheet(PROCESSED_ROWS_SHEET_NAME);
            var processedRowsSheet = newWorkBook.getSheet(PROCESSED_ROWS_SHEET_NAME);
            processedRowsSheet.createRow(TOP_ROW_INDEX);

            var sponsoredProductsCampaignsLabelRow = workBook.getSheet(SPONSORED_PRODUCTS_CAMPAIGNS_SHEET_NAME).getRow(TOP_ROW_INDEX);
            for (int index = 0; index < sponsoredProductsCampaignsLabelRow.getLastCellNum(); index++) {
                processedRowsSheet.getRow(TOP_ROW_INDEX).createCell(index, CellType.STRING);
                processedRowsSheet.getRow(TOP_ROW_INDEX).getCell(index).setCellValue(sponsoredProductsCampaignsLabelRow.getCell(index).getStringCellValue());
            }

            searchTermReportRows.stream()
                    .filter(row -> row.state() == SearchTermReportRow.State.ENABLED &&
                            row.campaignState() == SearchTermReportRow.State.ENABLED &&
                            row.matchType() != SearchTermReportRow.MatchType.EXACT &&
                            !row.customerSearchTerm().startsWith("b0") &&
                            row.orders() == 0 &&
                            row.clicks() >= 10)
                    .forEach(row -> addToSheet(row, processedRowsSheet));

            newWorkBook.write(outputStream);
        }
    }

    private static void addToSheet(SearchTermReportRow row, XSSFSheet sheet) {
        var nextRowIndex = sheet.getLastRowNum() + 1;
        sheet.createRow(nextRowIndex);
        var newRow = sheet.getRow(nextRowIndex);
        newRow.createCell(SponsoredProductsCampaignsLabel.PRODUCT.index()).setCellValue(row.product());
        newRow.createCell(SponsoredProductsCampaignsLabel.ENTITY.index()).setCellValue("negative keyword");
        newRow.createCell(SponsoredProductsCampaignsLabel.OPERATION.index()).setCellValue("create");
        newRow.createCell(SponsoredProductsCampaignsLabel.CAMPAIGN_ID.index()).setCellValue(String.valueOf(row.campaignId()));
        newRow.createCell(SponsoredProductsCampaignsLabel.AD_GROUP_ID.index()).setCellValue(String.valueOf(row.adGroupId()));
        newRow.createCell(SponsoredProductsCampaignsLabel.STATE.index()).setCellValue(row.state().name().toLowerCase());
        newRow.createCell(SponsoredProductsCampaignsLabel.KEYWORD_TEXT.index()).setCellValue(row.customerSearchTerm());
        newRow.createCell(SponsoredProductsCampaignsLabel.MATCH_TYPE.index()).setCellValue("negative exact");
    }

    private static Long nullIfEmpty(String value) {
        return value.isEmpty() ? null : Long.parseLong(value);
    }

    private static Double nullIfNotNumericType(Cell cell) {
        return cell.getCellType() == CellType.NUMERIC ? cell.getNumericCellValue() : null;
    }

    private static SearchTermReportRow mapToSearchTermReportRow(Row row) {
        return new SearchTermReportRow(
                row.getCell(PRODUCT.index()).getStringCellValue(),
                nullIfEmpty(row.getCell(CAMPAIGN_ID.index()).getStringCellValue()),
                nullIfEmpty(row.getCell(AD_GROUP_ID.index()).getStringCellValue()),
                nullIfEmpty(row.getCell(KEYWORD_ID.index()).getStringCellValue()),
                nullIfEmpty(row.getCell(PRODUCT_TARGETING_ID.index()).getStringCellValue()),
                SearchTermReportRow.State.valueOf(row.getCell(STATE.index()).getStringCellValue().toUpperCase()),
                SearchTermReportRow.State.valueOf(row.getCell(CAMPAIGN_STATE.index()).getStringCellValue().toUpperCase()),
                nullIfNotNumericType(row.getCell(BID.index())),
                row.getCell(KEYWORD_TEXT.index()).getStringCellValue(),
                SearchTermReportRow.MatchType.valueOfNullable(row.getCell(MATCH_TYPE.index()).getStringCellValue().toUpperCase()),
                row.getCell(PRODUCT_TARGETING_EXPRESSION.index()).getStringCellValue(),
                row.getCell(CUSTOMER_SEARCH_TERM.index()).getStringCellValue(),
                (int) row.getCell(IMPRESSIONS.index()).getNumericCellValue(),
                (int) row.getCell(CLICKS.index()).getNumericCellValue(),
                row.getCell(CLICK_THROUGH.index()).getNumericCellValue(),
                row.getCell(SPEND.index()).getNumericCellValue(),
                row.getCell(SALES.index()).getNumericCellValue(),
                (int) row.getCell(ORDERS.index()).getNumericCellValue(),
                (int) row.getCell(UNITS.index()).getNumericCellValue(),
                row.getCell(CONVERSION_RATE.index()).getNumericCellValue(),
                row.getCell(ACOS.index()).getNumericCellValue(),
                row.getCell(CPC.index()).getNumericCellValue(),
                row.getCell(ROAS.index()).getNumericCellValue()
        );
    }
}
