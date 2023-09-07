SELECT       MSG_PROVIDER.PRV_LOCAL_ID, MSG_PROVIDER.PRV_AUTHOR_ID,
             MSG_PROVIDER.PRV_ADDR_CITY_TXT, MSG_PROVIDER.PRV_ADDR_COMMENT_TXT, MSG_PROVIDER.PRV_ADDR_COUNTY_CD,
             MSG_PROVIDER.PRV_ADDR_COUNTRY_CD, MSG_PROVIDER.PRV_ADDR_STREET_ADDR1_TXT,
             MSG_PROVIDER.PRV_ADDR_STREET_ADDR2_TXT, MSG_PROVIDER.PRV_ADDR_STATE_CD,
             MSG_PROVIDER.PRV_ADDR_ZIP_CODE_TXT, MSG_PROVIDER.PRV_COMMENT_TXT,
             MSG_PROVIDER.PRV_ID_ALT_ID_NBR_TXT, MSG_PROVIDER.PRV_ID_QUICK_CODE_TXT,
             MSG_PROVIDER.PRV_ID_NBR_TXT, MSG_PROVIDER.PRV_ID_NPI_TXT,
             MSG_PROVIDER.PRV_EFFECTIVE_TIME, MSG_PROVIDER.PRV_EMAIL_ADDRESS_TXT, MSG_PROVIDER.PRV_NAME_DEGREE_CD,
             MSG_PROVIDER.PRV_NAME_FIRST_TXT, MSG_PROVIDER.PRV_NAME_LAST_TXT,
             MSG_PROVIDER.PRV_NAME_MIDDLE_TXT, MSG_PROVIDER.PRV_NAME_PREFIX_CD,
             MSG_PROVIDER.PRV_NAME_SUFFIX_CD, MSG_PROVIDER.PRV_PHONE_COMMENT_TXT,
             MSG_PROVIDER.PRV_PHONE_COUNTRY_CODE_TXT, MSG_PROVIDER.PRV_PHONE_EXTENSION_TXT,
             MSG_PROVIDER.PRV_PHONE_NBR_TXT, MSG_PROVIDER.PRV_ROLE_CD, MSG_PROVIDER.PRV_URL_ADDRESS_TXT
FROM MSG_PROVIDER
WHERE MSG_CONTAINER_UID =:MSG_CONTAINER_UID