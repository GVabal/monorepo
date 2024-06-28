package dev.vabalas.matas.backend.service;

import dev.vabalas.matas.model.SearchTermReportLabel;
import dev.vabalas.matas.model.SearchTermReportRow;
import dev.vabalas.matas.model.SponsoredProductsCampaignsLabel;
import dev.vabalas.matas.model.SponsoredProductsCampaignsRow;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class NegateService {

    private static final Logger log = LoggerFactory.getLogger(NegateService.class);
    private static final String PROCESSED_ROWS_SHEET_NAME = "Processed rows";
    private static final String SEARCH_TERM_REPORT_SHEET_NAME = "SP Search Term Report";
    private static final int TOP_ROW_INDEX = 0;

    public List<SearchTermReportRow> extractInterestingRows(File excelFile, int minClicks) {
        try (var inputStream = new FileInputStream(excelFile);
             var workBook = new XSSFWorkbook(inputStream)) {
            var rowIterator = workBook.getSheet(SEARCH_TERM_REPORT_SHEET_NAME).rowIterator();
            var searchTermReportRows = new ArrayList<SearchTermReportRow>();

            rowIterator.next(); // skip label column
            while (rowIterator.hasNext()) {
                searchTermReportRows.add(mapToSearchTermReportRow(rowIterator.next()));
            }

            return searchTermReportRows.stream()
                    .filter(row -> row.state() == SearchTermReportRow.State.ENABLED &&
                            row.campaignState() == SearchTermReportRow.State.ENABLED &&
                            row.matchType() != SearchTermReportRow.MatchType.EXACT &&
                            !row.customerSearchTerm().startsWith("b0") &&
                            row.orders() == 0 &&
                            row.clicks() >= minClicks)
                    .toList();
        } catch (IOException e) {
            log.error("Failed to extract interesting rows: {}", e.getMessage());
            return List.of();
        }
    }

    public byte[] generateReport(Set<SearchTermReportRow> selectedItems) {
        try (var newWorkBook = new XSSFWorkbook();
             var outputStream = new ByteArrayOutputStream()) {
            newWorkBook.createSheet(PROCESSED_ROWS_SHEET_NAME);
            var newWorkBookSheet = newWorkBook.getSheet(PROCESSED_ROWS_SHEET_NAME);
            newWorkBookSheet.createRow(TOP_ROW_INDEX);
            var processedRowsSheetLabelRow = newWorkBookSheet.getRow(TOP_ROW_INDEX);

            var labelEnumValues = SponsoredProductsCampaignsLabel.values();
            for (int index = 0; index < labelEnumValues.length; index++) {
                processedRowsSheetLabelRow.createCell(index, CellType.STRING);
                processedRowsSheetLabelRow.getCell(index).setCellValue(labelEnumValues[index].label());
            }

            selectedItems.stream()
                    .map(row -> new SponsoredProductsCampaignsRow(
                            row.product(),
                            "negative keyword",
                            "create",
                            String.valueOf(row.campaignId()),
                            String.valueOf(row.adGroupId()),
                            row.state().name().toLowerCase(),
                            row.customerSearchTerm(),
                            "negative exact"))
                    .forEach(row -> addToSheet(row, newWorkBookSheet));

            newWorkBook.write(outputStream);

            return outputStream.toByteArray();
        } catch (IOException e) {
            log.error("Failed to generate report: {}", e.getMessage());
            return new byte[]{};
        }
    }

    private static void addToSheet(SponsoredProductsCampaignsRow row, XSSFSheet sheet) {
        var nextRowIndex = sheet.getLastRowNum() + 1;
        sheet.createRow(nextRowIndex);
        var newRow = sheet.getRow(nextRowIndex);
        newRow.createCell(SponsoredProductsCampaignsLabel.PRODUCT.index()).setCellValue(row.product());
        newRow.createCell(SponsoredProductsCampaignsLabel.ENTITY.index()).setCellValue(row.entity());
        newRow.createCell(SponsoredProductsCampaignsLabel.OPERATION.index()).setCellValue(row.operation());
        newRow.createCell(SponsoredProductsCampaignsLabel.CAMPAIGN_ID.index()).setCellValue(row.campaignId());
        newRow.createCell(SponsoredProductsCampaignsLabel.AD_GROUP_ID.index()).setCellValue(row.adGroupId());
        newRow.createCell(SponsoredProductsCampaignsLabel.STATE.index()).setCellValue(row.state());
        newRow.createCell(SponsoredProductsCampaignsLabel.KEYWORD_TEXT.index()).setCellValue(row.keywordText());
        newRow.createCell(SponsoredProductsCampaignsLabel.MATCH_TYPE.index()).setCellValue(row.matchType());
    }

    private static Long nullIfEmpty(String value) {
        return value.isEmpty() ? null : Long.parseLong(value);
    }

    private static Double nullIfNotNumericType(Cell cell) {
        return cell.getCellType() == CellType.NUMERIC ? cell.getNumericCellValue() : null;
    }

    private static SearchTermReportRow mapToSearchTermReportRow(Row row) {
        return new SearchTermReportRow(
                row.getCell(SearchTermReportLabel.PRODUCT.index()).getStringCellValue(),
                nullIfEmpty(row.getCell(SearchTermReportLabel.CAMPAIGN_ID.index()).getStringCellValue()),
                nullIfEmpty(row.getCell(SearchTermReportLabel.AD_GROUP_ID.index()).getStringCellValue()),
                nullIfEmpty(row.getCell(SearchTermReportLabel.KEYWORD_ID.index()).getStringCellValue()),
                nullIfEmpty(row.getCell(SearchTermReportLabel.PRODUCT_TARGETING_ID.index()).getStringCellValue()),
                SearchTermReportRow.State.valueOf(row.getCell(SearchTermReportLabel.STATE.index()).getStringCellValue().toUpperCase()),
                SearchTermReportRow.State.valueOf(row.getCell(SearchTermReportLabel.CAMPAIGN_STATE.index()).getStringCellValue().toUpperCase()),
                nullIfNotNumericType(row.getCell(SearchTermReportLabel.BID.index())),
                row.getCell(SearchTermReportLabel.KEYWORD_TEXT.index()).getStringCellValue(),
                SearchTermReportRow.MatchType.valueOfNullable(row.getCell(SearchTermReportLabel.MATCH_TYPE.index()).getStringCellValue().toUpperCase()),
                row.getCell(SearchTermReportLabel.PRODUCT_TARGETING_EXPRESSION.index()).getStringCellValue(),
                row.getCell(SearchTermReportLabel.CUSTOMER_SEARCH_TERM.index()).getStringCellValue(),
                (int) row.getCell(SearchTermReportLabel.IMPRESSIONS.index()).getNumericCellValue(),
                (int) row.getCell(SearchTermReportLabel.CLICKS.index()).getNumericCellValue(),
                row.getCell(SearchTermReportLabel.CLICK_THROUGH.index()).getNumericCellValue(),
                row.getCell(SearchTermReportLabel.SPEND.index()).getNumericCellValue(),
                row.getCell(SearchTermReportLabel.SALES.index()).getNumericCellValue(),
                (int) row.getCell(SearchTermReportLabel.ORDERS.index()).getNumericCellValue(),
                (int) row.getCell(SearchTermReportLabel.UNITS.index()).getNumericCellValue(),
                row.getCell(SearchTermReportLabel.CONVERSION_RATE.index()).getNumericCellValue(),
                row.getCell(SearchTermReportLabel.ACOS.index()).getNumericCellValue(),
                row.getCell(SearchTermReportLabel.CPC.index()).getNumericCellValue(),
                row.getCell(SearchTermReportLabel.ROAS.index()).getNumericCellValue()
        );
    }
}
