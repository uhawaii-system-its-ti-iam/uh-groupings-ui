package edu.hawaii.its.groupings.api;


import edu.hawaii.its.groupings.api.type.MyGroupings;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PageService{
    //so the service has to be... fuuuuuuuuu i am inspored to program.
  public Page<Grouping> listAllByPage(Pageable pageable);
  //public MyGroupings getMyGroupings(String username);
}
