package dev.vabalas.matas.ui.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.StreamResource;
import dev.vabalas.matas.backend.service.NegateService;
import dev.vabalas.matas.model.SearchTermReportRow;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Route("negate")
@RouteAlias("")
public class NegateView extends VerticalLayout {

    private static final int MAX_FILE_SIZE_BYTES = 52428800; // 50MB

    private final Grid<SearchTermReportRow> grid = new Grid<>(SearchTermReportRow.class, false);

    public NegateView(NegateService negateService) {
        var instructions = new VerticalLayout(
                new Paragraph("1. Upload batch excel file and process it."),
                new Paragraph("2. Records that match filter criteria will be displayed from \"SP Search Term Report\" sheet."),
                new Paragraph("Filter rules are:"),
                new Paragraph("* state is \"enabled\""),
                new Paragraph("* campaign state is \"enabled\""),
                new Paragraph("* match type is all but \"exact\""),
                new Paragraph("* customer search term does not start with \"b0\""),
                new Paragraph("* orders is 0"),
                new Paragraph("* clicks is <Min clicks>"),
                new Paragraph("3. Review filtered rows, select the ones you want to upload"),
                new Paragraph("4. Click \"Save\" to generate upload excel file")
        );
        instructions.setPadding(false);
        instructions.setSpacing(false);
        instructions.getChildren().forEach(paragraph -> paragraph.getStyle().setMargin("0"));
        add(new H1("Negate process"), instructions);

        var button = new Button("Process");
        var button2 = new Button("Save");

        var buffer = new MemoryBuffer();
        var dropEnabledUpload = new Upload(buffer);
        dropEnabledUpload.setDropAllowed(true);
        dropEnabledUpload.setMaxFileSize(MAX_FILE_SIZE_BYTES);
        dropEnabledUpload.addFinishedListener(event -> button.setEnabled(true));
        dropEnabledUpload.addFileRemovedListener(event -> button.setEnabled(false));
        var formLayout = new FormLayout(dropEnabledUpload);
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
        add(formLayout);

        var minClicks = new IntegerField();
        minClicks.setLabel("Min clicks");
        minClicks.setValue(10);
        minClicks.setStepButtonsVisible(true);
        minClicks.setMin(0);
        minClicks.setMax(Integer.MAX_VALUE);
        add(minClicks);

        button.setEnabled(false);
        button.addClickListener(event -> {
            try {
                if (minClicks.getOptionalValue().isEmpty()) {
                    minClicks.setValue(10);
                }
                var result = negateService.extractInterestingRows(buffer.getInputStream(), minClicks.getValue());
                grid.setItems(result);
                result.forEach(it -> grid.getSelectionModel().select(it));
                button2.setEnabled(true);
            } catch (IOException e) {
                var notification = new Notification();
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                notification.add(e.getMessage());
                notification.open();
            }
        });
        add(button);

        grid.setAllRowsVisible(true);
        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        grid.addColumn(SearchTermReportRow::product).setHeader("Product").setAutoWidth(true).setFlexGrow(0).setSortable(true);
        grid.addColumn(SearchTermReportRow::campaignId).setHeader("Campaign ID").setAutoWidth(true).setFlexGrow(0).setSortable(true);
        grid.addColumn(SearchTermReportRow::adGroupId).setHeader("Ad group ID").setAutoWidth(true).setFlexGrow(0).setSortable(true);
        grid.addColumn(SearchTermReportRow::productTargetingId).setHeader("Product Targeting ID").setAutoWidth(true).setFlexGrow(0).setSortable(true);
        grid.addColumn(SearchTermReportRow::state).setHeader("State").setAutoWidth(true).setFlexGrow(0).setSortable(true);
        grid.addColumn(SearchTermReportRow::campaignState).setHeader("Campaign state").setAutoWidth(true).setFlexGrow(0).setSortable(true);
        grid.addColumn(SearchTermReportRow::productTargetingExpression).setHeader("Product Targeting Expression").setAutoWidth(true).setFlexGrow(0).setSortable(true);
        grid.addColumn(SearchTermReportRow::customerSearchTerm).setHeader("Customer Search Term").setAutoWidth(true).setFlexGrow(0).setSortable(true);
        grid.addColumn(SearchTermReportRow::impressions).setHeader("Impressions").setAutoWidth(true).setFlexGrow(0).setSortable(true);
        grid.addColumn(SearchTermReportRow::clicks).setHeader("Clicks").setAutoWidth(true).setFlexGrow(0).setSortable(true);
        grid.addColumn(SearchTermReportRow::clickThroughRate).setHeader("Click-through Rate").setAutoWidth(true).setFlexGrow(0).setSortable(true);
        grid.addColumn(SearchTermReportRow::spend).setHeader("Spend").setAutoWidth(true).setFlexGrow(0).setSortable(true);
        grid.addColumn(SearchTermReportRow::sales).setHeader("Sales").setAutoWidth(true).setFlexGrow(0).setSortable(true);
        grid.addColumn(SearchTermReportRow::orders).setHeader("Orders").setAutoWidth(true).setFlexGrow(0).setSortable(true);
        grid.addColumn(SearchTermReportRow::units).setHeader("Units").setAutoWidth(true).setFlexGrow(0).setSortable(true);
        grid.addColumn(SearchTermReportRow::conversionRate).setHeader("Conversion rate").setAutoWidth(true).setFlexGrow(0).setSortable(true);
        grid.addColumn(SearchTermReportRow::acos).setHeader("ACOS").setAutoWidth(true).setFlexGrow(0).setSortable(true);
        grid.addColumn(SearchTermReportRow::cpc).setHeader("CPC").setAutoWidth(true).setFlexGrow(0).setSortable(true);
        grid.addColumn(SearchTermReportRow::roas).setHeader("ROAS").setAutoWidth(true).setFlexGrow(0).setSortable(true);
        add(grid);

        button2.setEnabled(false);
        button2.addClickListener(event -> {
            try {
                var reportBytes = negateService.generateReport(grid.getSelectedItems());
                var streamResource = new StreamResource("upload-file.xlsx", () -> new ByteArrayInputStream(reportBytes));
                var downloadLink = new Anchor(streamResource, "Download");
                add(downloadLink);
                button2.setEnabled(false);
            } catch (IOException e) {
                var notification = new Notification();
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                notification.add(e.getMessage());
                notification.open();
            }
        });
        add(button2);
    }
}
