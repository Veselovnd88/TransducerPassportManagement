package ru.veselov.gateway.security;

public class SecurityConstant {

    public static final String CONTENT_TYPE = "Content-Type";

    public static final String WWW_AUTH_HEADER = "WWW-Authenticate";

    public static final String TEMPLATE_URL = "/api/v1/template/**";

    public static final String GENERATE_PASSPORT_URL = "/api/v1/passport/generate";

    public static final String PASSPORT_URL = "/api/v1/passport/**";

    public static final String CUSTOMER_URL = "/api/v1/customer/**";

    public static final String SERIALS_URL = "/api/v1/serials/**";

    public static final String TRANSDUCER_URL = "/api/v1/transducer/**";


    private SecurityConstant() {
    }


}
