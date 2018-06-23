package com.ail.ui.client.workflow;

import com.ail.ui.client.common.Style;
import com.ail.ui.client.common.UIUtil;
import com.ail.ui.client.common.component.AilButton;
import com.ail.ui.client.common.component.AilHorizontalPanel;
import com.ail.ui.client.common.component.AilLabel;
import com.ail.ui.client.common.component.AilSimpleFlexTable;
import com.ail.ui.client.common.component.AilTextBox;
import com.ail.ui.client.common.component.AilVerticalPanel;
import com.ail.ui.client.common.i18n.Messages;
import com.ail.ui.shared.model.PolicyDetailDTO;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.RootPanel;

public class ReferralWorflowControlPanel implements EntryPoint {

    private static final String REFERRED = "REFERRED";

    private static final String ON_RISK = "ON_RISK";

    private static final String DECLINED = "DECLINED";

    private final Messages messages = GWT.create(Messages.class);

    protected final WorkflowServiceAsync workflowService = GWT.create(WorkflowService.class);

    private AilVerticalPanel mainPanel = new AilVerticalPanel();

    private AilHorizontalPanel buttonPanel = new AilHorizontalPanel();

    private AilHorizontalPanel linkPanel = new AilHorizontalPanel();

    private AilSimpleFlexTable detailGrid = new AilSimpleFlexTable(0, 0, true);

    private AilLabel policyStatusLabel = new AilLabel();

    private AilLabel policyPremiumLabel = new AilLabel();

    private AilTextBox loadingPctText = new AilTextBox(Style.DATE_TEXTBOX);

    private AilButton acceptButton;

    private AilButton declineButton;

    private AilButton referButton;

    private FocusWidget viewPolicyLink;

    @Override
    public void onModuleLoad() {

        Long policySystemId = Long.valueOf(getPolicySystemId());

        detailGrid.addRow(messages.grossPremium(), policyPremiumLabel);
        detailGrid.addRow(messages.status(), policyStatusLabel);

        if (isUpdateable()) {
            detailGrid.addRow("Loading %", loadingPctText);
        }

        acceptButton = getAcceptWithLoadingButton(policySystemId);
        declineButton = getDeclineButton(policySystemId);
        referButton = getReferButton(policySystemId);
        viewPolicyLink = getViewPolicyButton(policySystemId);

        disableControls();

        buttonPanel.add(acceptButton);
        buttonPanel.add(new AilLabel(Style.SPACER));
        buttonPanel.add(declineButton);
        buttonPanel.add(new AilLabel(Style.SPACER));
        buttonPanel.add(referButton);
        buttonPanel.add(new AilLabel(Style.SPACER));

        linkPanel.add(viewPolicyLink);

        mainPanel.add(detailGrid);

        if (isUpdateable()) {
            mainPanel.add(new AilLabel(Style.SPACER));
            mainPanel.add(buttonPanel);
        }
        if (isViewable()) {
            mainPanel.add(new AilLabel(Style.SPACER));
            mainPanel.add(linkPanel);
        }

        RootPanel.get("gui-referral-workflow-control-container").add(mainPanel);

        setPolicyStatus(policySystemId);

    }

    private void setPolicyStatus(Long policySystemId) {
        workflowService.policyBySystemId(policySystemId, new AsyncCallback<PolicyDetailDTO>() {

            public void onSuccess(PolicyDetailDTO policy) {
                enableControls(policy.getStatus());
                policyPremiumLabel.setText(policy.getPremium());
            }

            @Override
            public void onFailure(Throwable t) {
                policyStatusLabel.setTextRed(messages.failedToLoad());
            }
        });
    }

    private AilButton getDeclineButton(final Long policySystemId) {
        AilButton decline = new AilButton("Decline");

        decline.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent arg0) {

                disableControls();

                workflowService.declineReferral(policySystemId, new AsyncCallback<Void>() {

                    public void onSuccess(Void v) {
                        enableControls(DECLINED);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        UIUtil.showErrorMessage("Error declining referral", t);
                    }

                });
            }
        });

        return decline;
    }

    private AilButton getReferButton(final Long policySystemId) {
        AilButton refer = new AilButton("Reset Referral");

        refer.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent arg0) {

                disableControls();

                workflowService.referReferral(policySystemId, new AsyncCallback<PolicyDetailDTO>() {

                    public void onSuccess(PolicyDetailDTO policy) {

                        enableControls(REFERRED);

                        loadingPctText.setText("0");
                        policyPremiumLabel.setText(policy.getPremium());
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        UIUtil.showErrorMessage("Error re-referring referral", t);
                    }

                });
            }
        });

        return refer;
    }

    private AilButton getAcceptWithLoadingButton(final Long policySystemId) {
        AilButton accept = new AilButton("Accept");

        accept.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent arg0) {

                disableControls();

                Double loading = Double.valueOf(0.0);

                if (!loadingPctText.getText().isEmpty()) {
                    try {
                        loading = Double.parseDouble(loadingPctText.getText());
                    } catch (NumberFormatException nfe) {
                        loadingPctText.validationFailed();
                        enableControls(REFERRED);
                        return;
                    }
                }

                workflowService.acceptWithLoadingReferral(policySystemId, loading, new AsyncCallback<PolicyDetailDTO>() {

                    public void onSuccess(PolicyDetailDTO policy) {
                        enableControls(ON_RISK);
                        policyPremiumLabel.setText(policy.getPremium());
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        UIUtil.showErrorMessage("Error accepting referral", t);
                    }

                });
            }
        });

        return accept;
    }

    private void disableControls() {
        acceptButton.setEnabled(false);
        declineButton.setEnabled(false);
        referButton.setEnabled(false);
        loadingPctText.validationReset();
        disableDone();
    }

    private FocusWidget getViewPolicyButton(final Long policySystemId) {
        FocusWidget refer = new Anchor(" View ");
        refer.addStyleName("icon-circle-arrow-right");

        refer.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent arg0) {
                disableControls();

                UIUtil.showPolicy(policySystemId);

            }
        });

        return refer;
    }

    private void enableControls(String status) {

        policyStatusLabel.setText(status);
        
        if (DECLINED.equals(status) || ON_RISK.equals(status)) {
            referButton.setEnabled(true);
            enableDone();
        }

        if (REFERRED.equals(status)) {
            acceptButton.setEnabled(true);
            declineButton.setEnabled(true);
        }

    }

    private boolean isViewable() {
        return getReferralWorkflowState().startsWith("ASSIGNED_TO_ME");
    }

    private boolean isUpdateable() {
        return "ASSIGNED_TO_ME_OPEN".equals(getReferralWorkflowState());
    }

    private native String getPolicySystemId() /*-{
                                              return $wnd.psysid
                                              }-*/;

    private native String getReferralWorkflowState() /*-{
                                                       return $wnd.referralWorkflowState
                                                       }-*/;

    private native void enableDone() /*-{
                                     $wnd.enableDone();
                                     }-*/;

    private native void disableDone() /*-{
                                      $wnd.disableDone();
                                      }-*/;

}
