package dev.vabalas.matas.ui.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.StreamResource;
import dev.vabalas.matas.backend.service.NegateService;
import dev.vabalas.matas.model.SponsoredProductsCampaignsView;

import java.io.IOException;

@Route("negate")
@RouteAlias("")
public class NegateView extends VerticalLayout {

    private static final int MAX_FILE_SIZE_BYTES = 52428800; // 50MB

    private final Grid<SponsoredProductsCampaignsView> grid = new Grid<>(SponsoredProductsCampaignsView.class, false);

    public NegateView(NegateService negateService) {
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

        button.setEnabled(false);
        button.addClickListener(event -> {
            try {
                var result = negateService.extractInterestingRows(buffer.getInputStream())
                        .stream()
                        .map(it -> new SponsoredProductsCampaignsView(it.product(), "negative keyword", "create",
                                String.valueOf(it.campaignId()), String.valueOf(it.adGroupId()), it.state().name().toLowerCase(),
                                it.customerSearchTerm(), "negative exact"))
                        .toList();
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
        grid.addColumn(SponsoredProductsCampaignsView::product).setHeader("Product");
        grid.addColumn(SponsoredProductsCampaignsView::entity).setHeader("Entity");
        grid.addColumn(SponsoredProductsCampaignsView::operation).setHeader("Operation ID");
        grid.addColumn(SponsoredProductsCampaignsView::campaignId).setHeader("Campaign ID");
        grid.addColumn(SponsoredProductsCampaignsView::adGroupId).setHeader("Ad group ID");
        grid.addColumn(SponsoredProductsCampaignsView::state).setHeader("State");
        grid.addColumn(SponsoredProductsCampaignsView::keywordText).setHeader("Keyword text");
        grid.addColumn(SponsoredProductsCampaignsView::matchType).setHeader("Match type");
        add(grid);

        button2.setEnabled(false);
        button2.addClickListener(event -> {
            try {
                var report = negateService.generateReport(grid.getSelectedItems());
                var streamResource = new StreamResource("upload-file.xlsx", () -> null);
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
