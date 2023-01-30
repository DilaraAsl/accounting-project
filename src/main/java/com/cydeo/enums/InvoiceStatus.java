package com.cydeo.enums;

    public enum InvoiceStatus {
        AWAITING_APPROVAL("Awaiting Approval"), APPROVED("Approved");
        private final String value;

        public String getValue() {
            return value;
        }

        InvoiceStatus(String value) {
            this.value = value;
        }
    }

