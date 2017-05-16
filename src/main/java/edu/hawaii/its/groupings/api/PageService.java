package edu.hawaii.its.groupings.api;


import edu.hawaii.its.groupings.api.type.MyGroupings;

public interface PageService{
  public MyGroupings getMyGroupings(String username);
}
