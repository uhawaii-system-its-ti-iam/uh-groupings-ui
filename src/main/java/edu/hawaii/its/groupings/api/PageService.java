package edu.hawaii.its.groupings.api;

// import edu.hawaii.its.groupings.api.type.Group;
// import edu.hawaii.its.groupings.api.type.GroupingsServiceResult;
// import edu.hawaii.its.groupings.api.type.Grouping;
import edu.hawaii.its.groupings.api.type.MyGroupings;
// import org.springframework.http.ResponseEntity;

// import edu.internet2.middleware.grouperClient.ws.beans.WsGetGrouperPrivilegesLiteResult;
// import edu.internet2.middleware.grouperClient.ws.beans.WsSubjectLookup;

// import java.util.List;


public interface PageService{
  public MyGroupings getMyGroupings(String username);
}
