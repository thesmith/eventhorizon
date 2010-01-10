package thesmith.eventhorizon.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import thesmith.eventhorizon.model.Account;
import thesmith.eventhorizon.model.Event;
import thesmith.eventhorizon.model.Status;
import thesmith.eventhorizon.service.EventService;
import thesmith.eventhorizon.service.StatusService;

import com.google.appengine.repackaged.com.google.common.collect.Lists;
import com.google.appengine.repackaged.org.joda.time.DateTime;
import com.google.appengine.repackaged.org.joda.time.Interval;
import com.google.appengine.repackaged.org.joda.time.Period;

/**
 * Implementation of StatusService
 * 
 * @author bens
 */
@Transactional
@Service
public class StatusServiceImpl implements StatusService {
  @PersistenceContext
  private EntityManager em;
  private final Log logger = LogFactory.getLog(this.getClass());
  
  private final Map<String, EventService> eventServices;

  public StatusServiceImpl(Map<String, EventService> eventServices) {
    this.eventServices = eventServices;
  }

  public void create(Status status) {
    if (null == this.find(status.getPersonId(), status.getDomain(), status.getCreated()))
      em.persist(status);
  }

  @SuppressWarnings("unchecked")
  public Status find(String personId, String domain, Date from) {
    List<Status> statuses = em
        .createQuery(
            "select s from Status s where s.personId = :personId and s.domain = :domain and s.created <= :from order by s.created desc")
        .setParameter("personId", personId).setParameter("domain", domain).setParameter("from", from).setMaxResults(1)
        .getResultList();
    if (null != statuses && statuses.size() > 0) {
      Status status = statuses.get(0);
      Status returnStatus = new Status();
      returnStatus.setCreated(status.getCreated());
      returnStatus.setDomain(status.getDomain());
      returnStatus.setPersonId(status.getPersonId());
      returnStatus.setStatus(status.getStatus().replaceAll("\\{ago\\}", this.ago(status.getCreated())));
      return returnStatus;
    }
    return null;
  }

  public List<Status> list(Account account, Date from) {
    EventService service = eventServices.get(account.getDomain());
    if (service == null)
      throw new RuntimeException("Unable to process events from domain: " + account.getDomain());

    List<Status> statuses = Lists.newArrayList();
    for (Event event : service.events(account, from)) {
      Status status = new Status();
      status.setPersonId(account.getPersonId());
      status.setDomain(account.getDomain());
      status.setCreated(event.getCreated());

      String text = account.getTemplate();
      text = text.replaceAll("\\{title\\}", event.getTitle());
      text = text.replaceAll("\\{titleUrl\\}", event.getTitleUrl());
      text = text.replaceAll("\\{domain\\}", account.getDomain());
      text = text.replaceAll("\\{domainUrl\\}", event.getDomainUrl());
      text = text.replaceAll("\\{userUrl\\}", event.getUserUrl());
      if (logger.isInfoEnabled())
        logger.info(text);
      status.setStatus(text);
      statuses.add(status);
    }
    
    return statuses;
  }

  private String ago(Date created) {
    if (null == created)
      return "some time ago";
    
    DateTime now = new DateTime();
    DateTime then = new DateTime(created.getTime());
    Period period = new Interval(then, now).toPeriod();
    int printed = 0;

    StringBuffer ago = new StringBuffer();
    printed = printPeriod(ago, printed, period.getYears(), "year");
    printed = printPeriod(ago, printed, period.getMonths(), "month");
    printed = printPeriod(ago, printed, period.getWeeks(), "week");
    printed = printPeriod(ago, printed, period.getDays(), "day");
    printed = printPeriod(ago, printed, period.getHours(), "hour");
    printed = printPeriod(ago, printed, period.getMinutes(), "minute");
    
    ago.append(" ago");
    return ago.toString();
  }

  private int printPeriod(StringBuffer ago, int printed, int value, String desc) {
    if (printed > 1) {
      return printed;
    } else if (printed == 1) {
      ago.append(" and ");
    }
    
    if (value > 1) {
      ago.append(value + " " + desc + "s");
    } else if (value == 1) {
      ago.append(value + " " + desc);
    } else {
      return printed;
    }
    return printed+1;
  }
}
