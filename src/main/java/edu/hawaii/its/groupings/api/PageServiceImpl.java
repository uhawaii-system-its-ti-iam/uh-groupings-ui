package edu.hawaii.its.groupings.api;

import edu.hawaii.its.groupings.api.type.*;
import edu.hawaii.its.groupings.controller.*;

import edu.internet2.middleware.grouperClient.ws.beans.*;

import org.springframework.beans.factory.annotation.Autowired;
import edu.hawaii.its.groupings.api.PageService;
import edu.hawaii.its.groupings.api.GroupingsServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;


import org.springframework.stereotype.Service;


@Service
@Transactional
public class PageServiceImpl implements PageService{

  private MyGroupings groupingRepository;

  @Autowired
  protected GroupingsService gs;

  @Autowired
  public void PersonServiceImpl(String username){
      this.groupingRepository = gs.getMyGroupings(username);
  }


  @Override
  //okay so this is something that gives me what i need
  public Page<Grouping> listAllByPage(Pageable pageable){
            //so i am guessing i can switch this out with myGroupings.
      return groupingRepository.findAll(pageabele);
  }

  // @Override
  // public MyGroupings getMyGroupings(String username) {
  //
  //     MyGroupings myGroupings = new MyGroupings();
  //     myGroupings.setGroupingsIn(gs.groupingsIn(username));
  //     myGroupings.setGroupingsOwned(gs.groupingsOwned(username));
  //     myGroupings.setGroupingsToOptInTo(gs.groupingsToOptInto(username));
  //     myGroupings.setGroupingsToOptOutOf(gs.groupingsToOptOutOf(username));
  //     myGroupings.setGroupingsOptedOutOf(gs.groupingsOptedOutOf(username));
  //     myGroupings.setGroupingsOptedInTo(gs.groupingsOptedInto(username));
  //
  //     return myGroupings;
  // }

}
