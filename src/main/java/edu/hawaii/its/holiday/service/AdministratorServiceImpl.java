package edu.hawaii.its.holiday.service;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

@Service("administratorService")
public class AdministratorServiceImpl implements AdministratorService {

    @Value("#{'${app.admin.list}'.split(',')}")
    private List<String> admins;

    @PostConstruct
    public void init() {
        Assert.notNull(admins, "property 'app.admin.list' is required.");
    }

    @Override
    @Transactional(readOnly = true)
    public boolean exists(String uhuuid) {
        if (admins != null && !admins.isEmpty()) {
            return admins.contains(uhuuid);
        }
        return false;
    }
}