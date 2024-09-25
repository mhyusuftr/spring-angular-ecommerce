package com.tujuhsembilan.simple_online_shop.configuration;

public final class Helpers {
    public static final String DASH = "-";
    public static final String PAGE_SIZE_DEFAULT = "5";

    public static final class Modules{
        public static final String ORDER = "Order";
        public static final String CUSTOMER = "Customer";
        public static final String ITEM = "Item";

        public static final String ORDER_PREFIX = "ORD";
        public static final String CUSTOMER_PREFIX = "CST";
    }

    public static final class ResponseMessage{
        public static final String EMPTY_DATA = "%1$s data is empty";
        public static final String NOT_FOUND = "%1$s with the ID %2$s is not found.";
        public static final String DELETE_SUCCESS = "%1$s with the ID %2$s was deleted successfully.";
        public static final String MINIO_UPL_ERROR = "Error while uploading to MinIO";
        public static final String MINIO_DEL_ERROR = "Error while deleting to MinIO";
    }

    public static final String MINIO_URL = "http://localhost:9000/";
}
