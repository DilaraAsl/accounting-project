package com.cydeo.enums;

public enum InvoiceType {
    PURCHASE, SALES;
    public enum InvoiceStatus {
        PURCHASE("Purchase"), APPROVED("Sales");
        private final String value;

        public String getValue() {
            return value;
        }

        InvoiceStatus(String value) {
            this.value = value;
        }
    }
}
