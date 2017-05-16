package edu.hawaii.its.groupings.api;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import edu.hawaii.its.groupings.api.type.*;

import edu.internet2.middleware.grouperClient.ws.beans.*;

import edu.hawaii.its.groupings.api.GroupingsService;
import edu.hawaii.its.groupings.api.GroupingsServiceImpl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.stereotype.Service;

import edu.internet2.middleware.grouperClient.api.GcAddMember;
import edu.internet2.middleware.grouperClient.api.GcAssignAttributes;
import edu.internet2.middleware.grouperClient.api.GcAssignGrouperPrivileges;
import edu.internet2.middleware.grouperClient.api.GcDeleteMember;
import edu.internet2.middleware.grouperClient.api.GcGetAttributeAssignments;
import edu.internet2.middleware.grouperClient.api.GcGetGrouperPrivilegesLite;
import edu.internet2.middleware.grouperClient.api.GcGetGroups;
import edu.internet2.middleware.grouperClient.api.GcGetMembers;
import edu.internet2.middleware.grouperClient.api.GcGetMemberships;
import edu.internet2.middleware.grouperClient.api.GcHasMember;
import edu.internet2.middleware.grouperClient.ws.StemScope;

@Service
public class PageServiceImpl implements PageService{

  @Override
  public MyGroupings getMyGroupings(String username) {
      MyGroupings myGroupings = new MyGroupings();

      myGroupings.setGroupingsIn(groupingsIn(username));
      myGroupings.setGroupingsOwned(groupingsOwned(username));
      myGroupings.setGroupingsToOptInTo(groupingsToOptInto(username));
      myGroupings.setGroupingsToOptOutOf(groupingsToOptOutOf(username));
      myGroupings.setGroupingsOptedOutOf(groupingsOptedOutOf(username));
      myGroupings.setGroupingsOptedInTo(groupingsOptedInto(username));

      return myGroupings;
  }

}
