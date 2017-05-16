package edu.hawaii.its.groupings.api;

import edu.hawaii.its.groupings.api.type.*;

import edu.internet2.middleware.grouperClient.ws.beans.*;

import org.springframework.beans.factory.annotation.Autowired;
import edu.hawaii.its.groupings.api.PageService;
import edu.hawaii.its.groupings.api.GroupingsServiceImpl;


import org.springframework.stereotype.Service;


@Service
public class PageServiceImpl implements PageService{

  @Autowired
  protected GroupingsService gs;

  @Override
  public MyGroupings getMyGroupings(String username) {
      MyGroupings myGroupings = new MyGroupings();
      myGroupings.setGroupingsIn(gs.groupingsIn(username));
      // myGroupings.setGroupingsOwned(gs.groupingsOwned(username));
      // myGroupings.setGroupingsToOptInTo(gs.groupingsToOptInto(username));
      // myGroupings.setGroupingsToOptOutOf(gs.groupingsToOptOutOf(username));
      // myGroupings.setGroupingsOptedOutOf(gs.groupingsOptedOutOf(username));
      // myGroupings.setGroupingsOptedInTo(gs.groupingsOptedInto(username));

      return myGroupings;
  }

}
