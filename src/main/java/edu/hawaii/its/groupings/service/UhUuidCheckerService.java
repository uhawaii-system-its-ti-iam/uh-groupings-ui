package edu.hawaii.its.groupings.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UhUuidCheckerService {

    private static final Log logger = LogFactory.getLog(UhUuidCheckerService.class);

    @Value("${groupings.access.uhuuid_pattern}")
    private String UHUUIDPATTERN;

    public boolean isValidUhUuid(String uhUuid, String uid) {
        logger.info("isValidUhUuid; check for valid uhUuid starting...");
        Pattern pattern = Pattern.compile(UHUUIDPATTERN, Pattern.CASE_INSENSITIVE);

        final Matcher matcher = pattern.matcher(uhUuid);
        return matcher.matches() || uhUuid.equals(uid);
    }

    public boolean isDepartmentAccount(String uhUuid, String uid) {
        logger.info(String.format("isDepartmentAccount; uhUuid: %s; uid: %s", uhUuid, uid));
        return uhUuid.equals(uid);
    }
}
